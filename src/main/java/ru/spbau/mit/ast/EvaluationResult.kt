package ru.spbau.mit.ast


interface EvaluationResult {
    fun isPresent(): Boolean = value != null

    val value: Int?
}

data class Value(override val value: Int) : EvaluationResult

object None : EvaluationResult {
    override val value: Int? = null
}
