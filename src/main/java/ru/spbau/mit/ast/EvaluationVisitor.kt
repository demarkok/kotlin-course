package ru.spbau.mit.ast

import ru.spbau.mit.exceptions.RedeclarationException
import ru.spbau.mit.exceptions.UnexpectedReturnException

class EvaluationVisitor(context: MutableContext) : ASTVisitor<EvaluationResult> {

    private val contextStack: MutableList<MutableContext> = mutableListOf(context)

    private fun topContext() = contextStack.last()


    override fun visit(file: File): EvaluationResult {
        val block = file.block
        val result = visit(block)
        if (result.isPresent()) {
            throw UnexpectedReturnException()
        }
        return None
    }

    override fun visit(block: Block): EvaluationResult {
        val statements = block.statements
        @Suppress("LoopToCallChain")
        for (statement in statements) {
            val result = statement.accept(this)
            if (result.isPresent()) {
                return result
            }
        }
        return None
    }

    override fun visit(functionDeclaration: FunctionDeclaration): EvaluationResult {
        val name = functionDeclaration.name
        val parameterNames = functionDeclaration.parameterNames
        val body = functionDeclaration.body
        topContext().addFunction(name, Function(body, parameterNames, topContext().toImmutable()))
        return None
    }

    override fun visit(variableDeclaration: VariableDeclaration): EvaluationResult {
        val name = variableDeclaration.name
        val value = variableDeclaration.value

        if (topContext().resolveVariable(name) != null) {
            throw RedeclarationException()
        }
        topContext().addVariable(name, Variable(value?.accept(this)?.value ?: 0))
        return None
    }

    override fun visit(whileStatement: While): EvaluationResult {
        val condition = whileStatement.condition
        val body = whileStatement.body

        while (condition.accept(this).value != 0) {
            val result = withNewContext(MutableContext(topContext())) { body.accept(this) }
            if (result.isPresent()) {
                return result
            }
        }
        return None
    }

    override fun visit(ifStatement: If): EvaluationResult {
        val condition = ifStatement.condition
        val body = ifStatement.body
        val elseBody = ifStatement.elseBody

        val actualBlock = if (condition.accept(this).value != 0) body else elseBody
        return withNewContext(MutableContext(topContext())) { actualBlock?.accept(this) ?: None }
    }

    override fun visit(variableAssignment: VariableAssignment): EvaluationResult {
        val name = variableAssignment.name
        val value = variableAssignment.value

        val variable = topContext().resolveVariableOrThrow(name)
        variable.value = value.accept(this).value!!
        return None
    }

    override fun visit(returnStatement: Return): EvaluationResult {
        val expression = returnStatement.expression
        return expression.accept(this)
    }

    override fun visit(println: Println): EvaluationResult {
        val arguments = println.arguments
        val result = arguments.map { it.accept(this).value!! }
                .toIntArray()
                .joinToString(" ")
                .plus("\n")
        topContext().outputStream.write(result.toByteArray())
        return None
    }

    override fun visit(binaryExpression: BinaryExpression): EvaluationResult {
        val leftOperand = binaryExpression.leftOperand
        val operator = binaryExpression.operator
        val rightOperand = binaryExpression.rightOperand

        val leftValue = leftOperand.accept(this).value!!
        val rightValue = rightOperand.accept(this).value!!
        return Value(operator(leftValue, rightValue))

    }

    override fun visit(functionCall: FunctionCall): EvaluationResult {
        val name = functionCall.name
        val arguments = functionCall.arguments

        val function = topContext().resolveFunctionOrThrow(name)

        val callContext = MutableContext(function.declarationContext, topContext().outputStream)
        callContext.addFunction(name, function)

        arguments.map { it.accept(this) }
                .zip(function.arguments)
                .forEach { callContext.addVariable(it.second, Variable(it.first.value!!)) }

        return withNewContext(callContext) {
            function.functionBlock.accept(this) as? Value ?: Value(0)
        }
    }

    override fun visit(variableIdentifier: VariableIdentifier): EvaluationResult {
        val name = variableIdentifier.name
        val variable = topContext().resolveVariableOrThrow(name)
        return Value(variable.value)
    }

    override fun visit(literal: Literal): EvaluationResult {
        val value = literal.value
        return Value(value)
    }

    private fun <T> withNewContext(newContext: MutableContext, block: () -> T): T {
        contextStack.add(newContext)
        val result = block()
        contextStack.removeAt(contextStack.lastIndex)
        return result
    }


}