package ru.spbau.mit

import org.junit.Test
import ru.spbau.mit.ast.*
import ru.spbau.mit.ast.BinaryOperator.*
import kotlin.test.assertEquals

class ParsingTest {

    @Test
    fun emptyTest() {
        val s = " "

        val root = parse(s)
        assertEquals(File(1, Block(1, emptyList())), root)
    }

    @Test
    fun variableDeclarationTest() {
        val s = "var a = 10"

        val root = parse(s)
        val expectedRoot = File(1, Block(1, listOf(VariableDeclaration(1, "a", Literal(1, 10)))))
        assertEquals(expectedRoot, root)
    }

    @Test
    fun functionDeclarationTest() {
        val s = """
            |
            |fun f(x) {
            |   return 4
            |}
            |
            |
            |""".trimMargin()

        val root = parse(s)
        val expectedRoot = File(2, Block(2, listOf(
                FunctionDeclaration(2, "f", listOf("x"), Block(3, listOf(
                        Return(3, Literal(3, 4))
                )))
        )))
        assertEquals(expectedRoot, root)
    }

    @Test
    fun functionDeclarationWithoutParametersTest() {
        val s = "fun run() { }"
        val root = parse(s)
        val expected = File(1, Block(1, listOf(FunctionDeclaration(1, "run", emptyList(), Block(1, emptyList())))))
        assertEquals(expected, root)
    }

    @Test
    fun whileTest() {
        val s = """
            |
            |while(1) {
            |   return 4
            |}
            |
            |
            |""".trimMargin()

        val root = parse(s)
        val expectedRoot = File(2, Block(2, listOf(
                While(2, Literal(2, 1), Block(3, listOf(
                        Return(3, Literal(3, 4))
                )))
        )))
        assertEquals(expectedRoot, root)
    }

    @Test
    fun ifWithoutElseTest() {
        val s = """
            |
            |if(1) {
            |   return 4
            |}
            |
            |
            |""".trimMargin()

        val root = parse(s)
        val expectedRoot = File(2, Block(2, listOf(
                If(2, Literal(2, 1), Block(3, listOf(
                        Return(3, Literal(3, 4))
                )), null)
        )))
        assertEquals(expectedRoot, root)
    }

    @Test
    fun ifWithElseTest() {
        val s = """
            |
            |if(1) {
            |   return 4
            |} else {
            |   return 5
            |}
            |
            |""".trimMargin()

        val root = parse(s)
        val expectedRoot = File(2, Block(2, listOf(
                If(2, Literal(2, 1), Block(3, listOf(
                        Return(3, Literal(3, 4))
                )), Block(5, listOf(
                        Return(5, Literal(5, 5))
                )))
        )))
        assertEquals(expectedRoot, root)
    }

    @Test
    fun variableAssignmentTest() {
        val s = "a = 10"

        val root = parse(s)
        val expectedRoot = File(1, Block(1, listOf(VariableAssignment(1, "a", Literal(1, 10)))))
        assertEquals(expectedRoot, root)
    }

    @Test
    fun functionCallTest() {
        val s = "f(239, x)"

        val root = parse(s)
        val expectedRoot = File(1, Block(1, listOf(
                ExpressionStatement(1, FunctionCall(1, "f", listOf(Literal(1, 239), VariableIdentifier(1, "x"))))
        )))
        assertEquals(expectedRoot, root)
    }

    @Test
    fun functionCallWithoutParametersTest() {
        val s = "run()"
        val root = parse(s)
        val expected = File(1, Block(1, listOf(ExpressionStatement(1, FunctionCall(1, "run", emptyList())))))
        assertEquals(expected, root)
    }

    @Test
    fun printlnTest() {
        val s = "println(239, x)"

        val root = parse(s)
        val expectedRoot = File(1, Block(1, listOf(
                Println(1, listOf(Literal(1, 239), VariableIdentifier(1, "x")))
        )))
        assertEquals(expectedRoot, root)
    }

    @Test
    fun emptyPrintlnTest() {
        val s = "println()"
        val root = parse(s)
        val expected = File(1, Block(1, listOf(Println(1, emptyList()))))
        assertEquals(expected, root)
    }

    @Test
    fun binaryExpressionTest() {
        val s = """
            |1 > 0
            |1 < 0
            |1 == 0
            |1 <= 0
            |1 >= 0
            |1 != 0
            |1 && 0
            |1 || 0
            |1 + 0
            |1 - 0
            |1 * 0
            |1 / 0
            |1 % 0
            |
            |""".trimMargin()

        val root = parse(s)


        val expectedRoot = File(1, Block(1, listOf(
                ExpressionStatement(1, BinaryExpression(1, Literal(1, 1), GT, Literal(1, 0))),
                ExpressionStatement(2, BinaryExpression(2, Literal(2, 1), LT, Literal(2, 0))),
                ExpressionStatement(3, BinaryExpression(3, Literal(3, 1), EQ, Literal(3, 0))),
                ExpressionStatement(4, BinaryExpression(4, Literal(4, 1), LE, Literal(4, 0))),
                ExpressionStatement(5, BinaryExpression(5, Literal(5, 1), GE, Literal(5, 0))),
                ExpressionStatement(6, BinaryExpression(6, Literal(6, 1), NEQ, Literal(6, 0))),
                ExpressionStatement(7, BinaryExpression(7, Literal(7, 1), AND, Literal(7, 0))),
                ExpressionStatement(8, BinaryExpression(8, Literal(8, 1), OR, Literal(8, 0))),
                ExpressionStatement(9, BinaryExpression(9, Literal(9, 1), ADD, Literal(9, 0))),
                ExpressionStatement(10, BinaryExpression(10, Literal(10, 1), SUB, Literal(10, 0))),
                ExpressionStatement(11, BinaryExpression(11, Literal(11, 1), MUL, Literal(11, 0))),
                ExpressionStatement(12, BinaryExpression(12, Literal(12, 1), DIV, Literal(12, 0))),
                ExpressionStatement(13, BinaryExpression(13, Literal(13, 1), MOD, Literal(13, 0)))
        )))
        assertEquals(expectedRoot, root)
    }


    @Test
    fun commentsTest() {
        val s = """
            |
            |// ignore it!
            |var a = 10 // ignore it!!
            |// ignore it!!!
            |
            |""".trimMargin()

        val root = parse(s)
        val expectedRoot = File(3, Block(3, listOf(VariableDeclaration(3, "a", Literal(3, 10)))))
        assertEquals(expectedRoot, root)
    }

    @Test
    fun varAndFunWithSameName() {
        val s = """
            |
            |fun f(f) {
            |   return f + 1
            |}
            |var f = 239
            |println(f(f), f)
            |
        """.trimMargin()

        val root = parse(s)
        val expectedRoot = File(2, Block(2, listOf(
                FunctionDeclaration(2, "f", listOf("f"), Block(3, listOf(
                        Return(3, BinaryExpression(3, VariableIdentifier(3, "f"), ADD, Literal(3, 1)))
                ))),
                VariableDeclaration(5, "f", Literal(5, 239)),
                Println(6, listOf(FunctionCall(6, "f", listOf(VariableIdentifier(6, "f"))),
                        VariableIdentifier(6, "f"))))))

        assertEquals(expectedRoot, root)
    }


    @Test
    fun complexTest() {

        val s = """
            |
            |fun fib(n) {
            |   if (n <= 1) {
            |       return 1
            |   }
            |   return fib(n - 1) + fib(n - 2)
            |}
            |var i = 1
            |while (i <= 5) {
            |   println(i, fib(i))
            |   i = i + 1
            |}
            |
            |""".trimMargin()


        val root = parse(s)
        val expectedRoot = File(2, Block(2, listOf(
                FunctionDeclaration(2, "fib", listOf("n"), Block(3, listOf(
                        If(3, BinaryExpression(3, VariableIdentifier(3, "n"), LE, Literal(3, 1)), Block(4, listOf(
                                Return(4, Literal(4, 1))
                        )), null),
                        Return(6, BinaryExpression(6,
                                FunctionCall(6, "fib",
                                        listOf(BinaryExpression(6, VariableIdentifier(6, "n"), SUB, Literal(6, 1)))),
                                ADD,
                                FunctionCall(6, "fib",
                                        listOf(BinaryExpression(6, VariableIdentifier(6, "n"), SUB, Literal(6, 2)))))
                        )
                ))),
                VariableDeclaration(8, "i", Literal(8, 1)),
                While(9, BinaryExpression(9, VariableIdentifier(9, "i"), LE, Literal(9, 5)), Block(10, listOf(
                        Println(10, listOf(
                                VariableIdentifier(10, "i"),
                                FunctionCall(10, "fib", listOf(VariableIdentifier(10, "i"))))),
                        VariableAssignment(11, "i", BinaryExpression(11, VariableIdentifier(11, "i"), ADD, Literal(11, 1))
                        )))))))

        assertEquals(expectedRoot, root)
    }
}
