package ru.spbau.mit.ast

fun Boolean.toInt(): Int = if (this) 1 else 0

fun Int.toBoolean(): Boolean = this != 0

enum class BinaryOperator {
    GT,
    LT,
    EQ,
    LE,
    GE,
    NEQ,
    AND,
    OR,

    ADD,
    SUB,
    MUL,
    DIV,
    MOD;

    operator fun invoke(x: Int, y: Int): Int {
        return when (this) {
            GT -> (x > y).toInt()
            LT -> (x < y).toInt()
            EQ -> (x == y).toInt()
            LE -> (x <= y).toInt()
            GE -> (x >= y).toInt()
            NEQ -> (x != y).toInt()
            AND -> (x.toBoolean() && y.toBoolean()).toInt()
            OR -> (x.toBoolean() || y.toBoolean()).toInt()

            ADD -> x + y
            SUB -> x - y
            MUL -> x * y
            DIV -> x / y
            MOD -> x % y
        }
    }

    companion object {
        fun byString(string: String): BinaryOperator = when (string) {
            ">" -> GT
            "<" -> LT
            "==" -> EQ
            "<=" -> LE
            ">=" -> GE
            "!=" -> NEQ
            "&&" -> AND
            "||" -> OR
            "+" -> ADD
            "-" -> SUB
            "*" -> MUL
            "/" -> DIV
            "%" -> MOD
            else -> throw RuntimeException()
        }
    }
}