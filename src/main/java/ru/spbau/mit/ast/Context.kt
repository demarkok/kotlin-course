package ru.spbau.mit.ast

import java.io.OutputStream


interface ContextInterface {
    fun resolveVariable(name: String): Variable?
    fun resolveFunction(name: String): Function?
    val parentContext: Context?
    val outputStream: OutputStream
}

class Context(
        override val parentContext: Context?,
        override val outputStream: OutputStream,
        private val variables: Map<String, Variable>,
        private val functions: Map<String, Function>
) : ContextInterface {

    override fun resolveVariable(name: String): Variable? = variables[name] ?: parentContext?.resolveVariable(name)

    override fun resolveFunction(name: String): Function? = functions[name] ?: parentContext?.resolveFunction(name)
}

class MutableContext( // TODO: refactor: hide the implementation details (Maps)
        override val parentContext: Context?,
        override val outputStream: OutputStream,
        private val variables: MutableMap<String, Variable> = HashMap(),
        private val functions: MutableMap<String, Function> = HashMap()
) : ContextInterface {

    constructor(parent: MutableContext) : this(parent.toImmutable(), parent.outputStream)

    override fun resolveFunction(name: String): Function? = functions[name] ?: parentContext?.resolveFunction(name)

    override fun resolveVariable(name: String): Variable? = variables[name] ?: parentContext?.resolveVariable(name)

    fun addFunction(name: String, function: Function) {
        functions[name] = function
    }

    fun addVariable(name: String, variable: Variable) {
        variables[name] = variable
    }

    fun toImmutable(): Context {

        val immutableVariables = mapOf(*variables
                .entries
                .map { x -> x.toPair() }
                .toTypedArray())

        val immutableFunctions = mapOf(*functions
                .entries
                .map { x -> x.toPair() }
                .toTypedArray())

        return Context(parentContext, outputStream, immutableVariables, immutableFunctions)
    }
}