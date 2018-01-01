package ru.spbau.mit.debug

import ru.spbau.mit.ast.*
import ru.spbau.mit.evaluation.*
import ru.spbau.mit.evaluation.Function
import ru.spbau.mit.exceptions.DebuggerIsNotRunningException
import ru.spbau.mit.exceptions.RedeclarationException
import ru.spbau.mit.exceptions.UnexpectedReturnException
import ru.spbau.mit.parse
import java.io.BufferedWriter
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.experimental.*

interface Debugger {
    fun addBreakpoint(line: Int)
    fun addConditionalBreakpoint(line: Int, condition: String)
    fun list(): List<Pair<Int, String?>>
    fun removeBreakpoint(line: Int)
    fun run()
    fun evaluate(expressionText: String): Int
    fun next()
    fun line(): Int
    fun isRunning(): Boolean
}


class DebuggerImpl(source: String, private val output: BufferedWriter) : Debugger {

    private var isRunning = false
    private val root = parse(source)
    private var currentContinuation: Continuation<Unit>? = null
    private var currentNode: Statement? = null
    private val breakpoints: HashMap<Int, String?> = hashMapOf()

    private val visitor = DebugVisitor() // TODO: decompose

    override fun addBreakpoint(line: Int) {
        breakpoints.put(line, null)
    }

    override fun addConditionalBreakpoint(line: Int, condition: String) {
        breakpoints.put(line, condition)
    }

    override fun removeBreakpoint(line: Int) {
        breakpoints.remove(line)
    }

    override fun list() = breakpoints.entries.toList().map { x -> x.toPair() }
    override fun line(): Int {
        throwIfNotRunning()
        return currentNode!!.line
    }

    override fun evaluate(expressionText: String): Int {
        throwIfNotRunning()
        val expression = (parse(expressionText).block.statements[0] as ExpressionStatement).expression
        return expression.accept(EvaluationVisitor(MutableContext(visitor.topContext()))).value!!
    }

    override fun run() {
        isRunning = true
        root.accept(visitor).thenRun { isRunning = false }
    }

    override fun next() {
        throwIfNotRunning()
        val continuation = currentContinuation!!
        currentContinuation = null
        continuation.resume(Unit)
    }

    override fun isRunning() = isRunning

    private fun throwIfNotRunning() {
        if (!isRunning()) {
            throw DebuggerIsNotRunningException()
        }
    }

    private suspend fun suspendEvaluation(node: Statement) {
        currentNode = node
        suspendCoroutine<Unit> { continuation ->
            currentContinuation = continuation
        }
    }

    suspend fun suspendIfBreakpointIsSet(node: Statement) {
        if (breakpoints.containsKey(node.line) && (breakpoints[node.line]?.let { evaluate(it) } ?: 1) != 0) {
            suspendEvaluation(node)
        }
    }

    suspend fun await(future: CompletableFuture<EvaluationResult>): EvaluationResult {
        return suspendCoroutine { continuation ->
            future.whenComplete { value, throwable ->
                if (throwable == null) {
                    continuation.resume(value)
                } else {
                    continuation.resumeWithException(throwable)
                }
            }
        }
    }

    fun async(body: suspend DebuggerImpl.() -> EvaluationResult): CompletableFuture<EvaluationResult> {
        val result = CompletableFuture<EvaluationResult>()
        body.startCoroutine(this, object : Continuation<EvaluationResult> {
            override val context: CoroutineContext
                get() = EmptyCoroutineContext

            override fun resume(value: EvaluationResult) {
                result.complete(value)
            }

            override fun resumeWithException(exception: Throwable) {
                result.completeExceptionally(exception)
            }
        })

        return result
    }


    inner class DebugVisitor : ASTVisitor<CompletableFuture<EvaluationResult>> {

        private val contextStack: MutableList<MutableContext> = mutableListOf(MutableContext(null, output))
        fun topContext() = contextStack.last()

        override fun visit(file: File): CompletableFuture<EvaluationResult> = async {
            val block = file.block
            val result = await(visit(block))
            if (result.isPresent()) {
                throw UnexpectedReturnException()
            }
            return@async None
        }

        override fun visit(block: Block): CompletableFuture<EvaluationResult> = async {
            val statements = block.statements
            @Suppress("LoopToCallChain")
            for (statement in statements) {
                val result = await(statement.accept(this@DebugVisitor))
                if (result.isPresent()) {
                    return@async result
                }
            }
            return@async None
        }

        override fun visit(expressionStatement: ExpressionStatement): CompletableFuture<EvaluationResult> = async {
            suspendIfBreakpointIsSet(expressionStatement)
            await(expressionStatement.expression.accept(this@DebugVisitor))
            return@async None
        }

        override fun visit(functionDeclaration: FunctionDeclaration): CompletableFuture<EvaluationResult> = async {
            suspendIfBreakpointIsSet(functionDeclaration)
            val name = functionDeclaration.name
            val parameterNames = functionDeclaration.parameterNames
            val body = functionDeclaration.body
            topContext().addFunction(name, Function(body, parameterNames, topContext().toImmutable()))
            return@async None
        }

        override fun visit(variableDeclaration: VariableDeclaration): CompletableFuture<EvaluationResult> = async {
            suspendIfBreakpointIsSet(variableDeclaration)
            val name = variableDeclaration.name
            val value = variableDeclaration.value

            if (topContext().resolveVariable(name) != null) {
                throw RedeclarationException()
            }
            topContext().addVariable( // Sorry, probably it's too intricate...
                    name,
                    Variable(value
                            ?.accept(this@DebugVisitor)
                            ?.let { await(it) }
                            ?.value
                            ?: 0)
            )
            return@async None
        }

        override fun visit(whileStatement: While): CompletableFuture<EvaluationResult> = async {
            suspendIfBreakpointIsSet(whileStatement)
            val condition = whileStatement.condition
            val body = whileStatement.body

            while (await(condition.accept(this@DebugVisitor)).value != 0) {
                val result = withNewContext(MutableContext(topContext())) { await(body.accept(this@DebugVisitor)) }
                if (result.isPresent()) {
                    return@async result
                }
            }
            return@async None
        }

        override fun visit(ifStatement: If): CompletableFuture<EvaluationResult> = async {
            suspendIfBreakpointIsSet(ifStatement)
            val condition = ifStatement.condition
            val body = ifStatement.body
            val elseBody = ifStatement.elseBody

            val actualBlock = if (await(condition.accept(this@DebugVisitor)).value != 0) body else elseBody
            return@async withNewContext(MutableContext(topContext())) { actualBlock?.accept(this@DebugVisitor)?.let { await(it) } ?: None } // TODO: think about it
        }

        override fun visit(variableAssignment: VariableAssignment): CompletableFuture<EvaluationResult> = async {
            suspendIfBreakpointIsSet(variableAssignment)
            val name = variableAssignment.name
            val value = variableAssignment.value

            val variable = topContext().resolveVariableOrThrow(name)
            variable.value = await(value.accept(this@DebugVisitor)).value!!
            return@async None
        }

        override fun visit(returnStatement: Return): CompletableFuture<EvaluationResult> = async {
            suspendIfBreakpointIsSet(returnStatement)
            val expression = returnStatement.expression
            return@async await(expression.accept(this@DebugVisitor))
        }

        override fun visit(println: Println): CompletableFuture<EvaluationResult> = async {
            suspendIfBreakpointIsSet(println)
            val arguments = println.arguments
            val result = arguments.map { await(it.accept(this@DebugVisitor)).value!! } // thank God map is inline!
                    .toIntArray()
                    .joinToString(" ")
                    .plus("\n")
            topContext().outputStream.write(result)
            return@async None
        }

        override fun visit(binaryExpression: BinaryExpression): CompletableFuture<EvaluationResult> = async {
            val leftOperand = binaryExpression.leftOperand
            val operator = binaryExpression.operator
            val rightOperand = binaryExpression.rightOperand

            val leftValue = await(leftOperand.accept(this@DebugVisitor)).value!!
            val rightValue = await(rightOperand.accept(this@DebugVisitor)).value!!
            return@async Value(operator(leftValue, rightValue))
        }

        override fun visit(functionCall: FunctionCall): CompletableFuture<EvaluationResult> = async {
            val name = functionCall.name
            val arguments = functionCall.arguments

            val function = topContext().resolveFunctionOrThrow(name)

            val callContext = MutableContext(function.declarationContext, topContext().outputStream)
            callContext.addFunction(name, function)

            arguments.map { await(it.accept(this@DebugVisitor)) }
                    .zip(function.arguments)
                    .forEach { callContext.addVariable(it.second, Variable(it.first.value!!)) }

            return@async withNewContext(callContext) {
                await(function.functionBlock.accept(this@DebugVisitor)) as? Value ?: Value(0)
            }
        }

        override fun visit(variableIdentifier: VariableIdentifier): CompletableFuture<EvaluationResult> = async {
            val name = variableIdentifier.name
            val variable = topContext().resolveVariableOrThrow(name)
            return@async Value(variable.value)
        }

        override fun visit(literal: Literal): CompletableFuture<EvaluationResult> = async {
            val value = literal.value
            return@async Value(value)
        }

        private inline fun <T> withNewContext(newContext: MutableContext, block: () -> T): T {
            contextStack.add(newContext)
            val result = block()
            contextStack.removeAt(contextStack.lastIndex)
            return result
        }
    }
}
