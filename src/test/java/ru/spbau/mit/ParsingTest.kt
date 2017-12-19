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
        assertEquals(File(Block(emptyList())), root)
    }

    @Test
    fun variableDeclarationTest() {
        val s = "var a = 10"

        val root = parse(s)
        val expectedRoot = File(Block(listOf(VariableDeclaration("a", Literal(10)))))
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
        val expectedRoot = File(Block(listOf(
                FunctionDeclaration("f", listOf("x"), Block(listOf(
                        Return(Literal(4))
                )))
        )))
        assertEquals(expectedRoot, root)
    }

    @Test
    fun functionDeclarationWithoutParametersTest() {
        val s = "fun run() { }"
        val root = parse(s)
        val expected = File(Block(listOf(FunctionDeclaration("run", emptyList(), Block(emptyList())))))
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
        val expectedRoot = File(Block(listOf(
                While(Literal(1), Block(listOf(
                        Return(Literal(4))
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
        val expectedRoot = File(Block(listOf(
                If(Literal(1), Block(listOf(
                        Return(Literal(4))
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
        val expectedRoot = File(Block(listOf(
                If(Literal(1), Block(listOf(
                        Return(Literal(4))
                )), Block(listOf(
                        Return(Literal(5))
                )))
        )))
        assertEquals(expectedRoot, root)
    }

    @Test
    fun variableAssignmentTest() {
        val s = "a = 10"

        val root = parse(s)
        val expectedRoot = File(Block(listOf(VariableAssignment("a", Literal(10)))))
        assertEquals(expectedRoot, root)
    }

    @Test
    fun functionCallTest() {
        val s = "f(239, x)"

        val root = parse(s)
        val expectedRoot = File(Block(listOf(
                FunctionCall("f", listOf(Literal(239), VariableIdentifier("x")))
        )))
        assertEquals(expectedRoot, root)
    }

    @Test
    fun functionCallWithoutParametersTest() {
        val s = "run()"
        val root = parse(s)
        val expected = File(Block(listOf(FunctionCall("run", emptyList()))))
        assertEquals(expected, root)
    }

    @Test
    fun printlnTest() {
        val s = "println(239, x)"

        val root = parse(s)
        val expectedRoot = File(Block(listOf(
                Println(listOf(Literal(239), VariableIdentifier("x")))
        )))
        assertEquals(expectedRoot, root)
    }

    @Test
    fun emptyPrintlnTest() {
        val s = "println()"
        val root = parse(s)
        val expected = File(Block(listOf(Println(emptyList()))))
        assertEquals(expected, root)
    }

    @Test
    fun binaryExpressionTest() {
        val s = """
            |
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


        val expectedRoot = File(Block(listOf(
                BinaryExpression(Literal(1), GT, Literal(0)),
                BinaryExpression(Literal(1), LT, Literal(0)),
                BinaryExpression(Literal(1), EQ, Literal(0)),
                BinaryExpression(Literal(1), LE, Literal(0)),
                BinaryExpression(Literal(1), GE, Literal(0)),
                BinaryExpression(Literal(1), NEQ, Literal(0)),
                BinaryExpression(Literal(1), AND, Literal(0)),
                BinaryExpression(Literal(1), OR, Literal(0)),
                BinaryExpression(Literal(1), ADD, Literal(0)),
                BinaryExpression(Literal(1), SUB, Literal(0)),
                BinaryExpression(Literal(1), MUL, Literal(0)),
                BinaryExpression(Literal(1), DIV, Literal(0)),
                BinaryExpression(Literal(1), MOD, Literal(0))
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
        val expectedRoot = File(Block(listOf(VariableDeclaration("a", Literal(10)))))
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
        val expectedRoot = File(Block(listOf(
                FunctionDeclaration("f", listOf("f"), Block(listOf(
                        Return(BinaryExpression(VariableIdentifier("f"), ADD, Literal(1)))
                ))),
                VariableDeclaration("f", Literal(239)),
                Println(listOf(FunctionCall("f", listOf(VariableIdentifier("f"))),
                        VariableIdentifier("f"))))))

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
        val expectedRoot = File(Block(listOf(
                FunctionDeclaration("fib", listOf("n"), Block(listOf(
                        If(BinaryExpression(VariableIdentifier("n"), LE, Literal(1)), Block(listOf(
                                Return(Literal(1))
                        )), null),
                        Return(BinaryExpression(
                                FunctionCall("fib",
                                        listOf(BinaryExpression(VariableIdentifier("n"), SUB, Literal(1)))),
                                ADD,
                                FunctionCall("fib",
                                        listOf(BinaryExpression(VariableIdentifier("n"), SUB, Literal(2)))))
                        )
                ))),
                VariableDeclaration("i", Literal(1)),
                While(BinaryExpression(VariableIdentifier("i"), LE, Literal(5)), Block(listOf(
                        Println(listOf(
                                VariableIdentifier("i"),
                                FunctionCall("fib", listOf(VariableIdentifier("i"))))),
                        VariableAssignment("i", BinaryExpression(VariableIdentifier("i"), ADD, Literal(1))
                        )))))))

        assertEquals(expectedRoot, root)
    }
}
