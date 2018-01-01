package ru.spbau.mit.evaluation

import org.junit.Test
import ru.spbau.mit.ast.*
import ru.spbau.mit.ast.BinaryOperator.*
import ru.spbau.mit.exceptions.FunctionIsNotDefinedException
import ru.spbau.mit.exceptions.RedeclarationException
import ru.spbau.mit.exceptions.UnexpectedReturnException
import ru.spbau.mit.exceptions.VariableIsNotDefinedException
import java.io.BufferedWriter
import java.io.StringWriter
import kotlin.test.assertEquals


class InterpretationTest {

    @Test
    fun emptyTest() {

        val root = File(0, Block(0, emptyList()))
        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)

        val result = root.accept(EvaluationVisitor(MutableContext(null, stream)))
        stream.flush()
        val output = stringWriter.toString()

        assertEquals(None, result)
        assertEquals("", output)
    }

    @Test
    fun variableDeclarationTest() {
        val root = VariableDeclaration(0, "a", Literal(0, 10))
        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)
        val result = root.accept(EvaluationVisitor(MutableContext(null, stream)))
        stream.flush()
        val output = stringWriter.toString()

        assertEquals(None, result)
        assertEquals("", output)
    }

    @Test
    fun defaultVariableValueTest() {
        val root = Block(0, listOf(
                VariableDeclaration(0, "a"),
                Return(0, VariableIdentifier(0, "a"))
        ))
        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)

        val result = root.accept(EvaluationVisitor(MutableContext(null, stream)))
        stream.flush()
        val output = stringWriter.toString()

        assertEquals(Value(0), result)
        assertEquals("", output)
    }


    @Test
    fun functionDeclarationTest() {
        val root = FunctionDeclaration(0, "f", listOf("x"), Block(0, listOf(
                Println(0, listOf(Literal(0, 4))),
                Return(0, Literal(0, 2)))))
        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)

        val result = root.accept(EvaluationVisitor(MutableContext(null, stream)))
        stream.flush()
        val output = stringWriter.toString()

        assertEquals(None, result)
        assertEquals("", output)
    }

    @Test
    fun whileTest() {
        val root = File(0, Block(0, listOf(
                VariableDeclaration(0, "i"),
                While(0, BinaryExpression(0, VariableIdentifier(0, "i"), LT, Literal(0, 3)), Block(0, listOf(
                        Println(0, listOf(VariableIdentifier(0, "i"))),
                        VariableAssignment(0, "i", BinaryExpression(0, VariableIdentifier(0, "i"), ADD, Literal(0, 1)))
                )))
        )))

        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)


        val result = root.accept(EvaluationVisitor(MutableContext(null, stream)))
        stream.flush()
        val output = stringWriter.toString()

        assertEquals(None, result)
        assertEquals("0\n1\n2\n", output)
    }

    @Test
    fun ifWithoutElseTest() {
        val root = Block(0, listOf(
                If(0, Literal(0, 1), Block(0, listOf(Return(0, Literal(0, 4)))), null),
                Return(0, Literal(0, 0))

        ))
        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)

        val result = root.accept(EvaluationVisitor(MutableContext(null, stream)))
        stream.flush()
        val output = stringWriter.toString()

        assertEquals(Value(4), result)
        assertEquals("", output)
    }

    @Test
    fun ifWithElseTest() {
        val root = Block(0, listOf(
                If(0, Literal(0, 0), Block(0, listOf(
                        Return(0, Literal(0, 4))
                )), Block(0, listOf(
                        Return(0, Literal(0, 5))
                )))
        ))
        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)

        val result = root.accept(EvaluationVisitor(MutableContext(null, stream)))
        stream.flush()
        val output = stringWriter.toString()

        assertEquals(Value(5), result)
        assertEquals("", output)
    }

    @Test
    fun variableAssignmentTest() {
        val root = Block(0, listOf(
                VariableDeclaration(0, "a"),
                VariableAssignment(0, "a", Literal(0, 10)),
                Return(0, VariableIdentifier(0, "a"))
        ))
        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)

        val result = root.accept(EvaluationVisitor(MutableContext(null, stream)))
        stream.flush()
        val output = stringWriter.toString()

        assertEquals(Value(10), result)
        assertEquals("", output)
    }

    @Test
    fun functionCallTest() {
        val root = Block(0, listOf(
                FunctionDeclaration(0, "f", listOf("a"), Block(0, listOf(
                        Return(0, VariableIdentifier(0, "a"))
                ))),
                Return(0, FunctionCall(0, "f", listOf(Literal(0, 239))))
        ))
        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)

        val result = root.accept(EvaluationVisitor(MutableContext(null, stream)))
        stream.flush()
        val output = stringWriter.toString()

        assertEquals(Value(239), result)
        assertEquals("", output)
    }

    @Test
    fun binaryExpressionTest() {
        val root = Block(0, listOf(
                Println(0, listOf(
                        BinaryExpression(0, Literal(0, 10), GT, Literal(0, 20)),
                        BinaryExpression(0, Literal(0, 10), LT, Literal(0, 20)),
                        BinaryExpression(0, Literal(0, 10), EQ, Literal(0, 20)),
                        BinaryExpression(0, Literal(0, 10), LE, Literal(0, 20)),
                        BinaryExpression(0, Literal(0, 10), GE, Literal(0, 20)),
                        BinaryExpression(0, Literal(0, 10), NEQ, Literal(0, 20)),
                        BinaryExpression(0, Literal(0, 10), AND, Literal(0, 20)),
                        BinaryExpression(0, Literal(0, 10), OR, Literal(0, 20)),
                        BinaryExpression(0, Literal(0, 10), ADD, Literal(0, 20)),
                        BinaryExpression(0, Literal(0, 10), SUB, Literal(0, 20)),
                        BinaryExpression(0, Literal(0, 10), MUL, Literal(0, 20)),
                        BinaryExpression(0, Literal(0, 10), DIV, Literal(0, 20)),
                        BinaryExpression(0, Literal(0, 10), MOD, Literal(0, 20))
                ))
        ))
        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)

        val result = root.accept(EvaluationVisitor(MutableContext(null, stream)))
        stream.flush()
        val output = stringWriter.toString()

        assertEquals(None, result)
        assertEquals("0 1 0 1 0 1 1 1 30 -10 200 0 10\n", output)
    }


    @Test
    fun varAndFunWithSameName() {
        val root = Block(0, listOf(
                FunctionDeclaration(0, "f", listOf("f"), Block(0, listOf(
                        Return(0, BinaryExpression(0, VariableIdentifier(0, "f"), ADD, Literal(0, 1)))
                ))),
                VariableDeclaration(0, "f", Literal(0, 239)),
                Println(0, listOf(FunctionCall(0, "f", listOf(VariableIdentifier(0, "f"))),
                        VariableIdentifier(0, "f")
                ))
        ))
        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)

        val result = root.accept(EvaluationVisitor(MutableContext(null, stream)))
        stream.flush()
        val output = stringWriter.toString()

        assertEquals(None, result)
        assertEquals("240 239\n", output)
    }

    @Test
    fun emptyPrintlnTest() {
        val root = Println(0, emptyList())
        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)

        val result = root.accept(EvaluationVisitor(MutableContext(null, stream)))
        stream.flush()
        val output = stringWriter.toString()


        assertEquals(None, result)
        assertEquals("\n", output)
    }

    @Test(expected = UnexpectedReturnException::class)
    fun unexpectedReturnExceptionTest() {
        val root = File(0, Block(0, listOf(Return(0, Literal(0, 1)))))
        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)

        root.accept(EvaluationVisitor(MutableContext(null, stream)))
    }

    @Test(expected = RedeclarationException::class)
    fun redeclarationExceptionTest() {
        val root = File(0, Block(0, listOf(
                VariableDeclaration(0, "a"),
                VariableDeclaration(0, "a")
        )))
        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)

        root.accept(EvaluationVisitor(MutableContext(null, stream)))
    }

    @Test(expected = VariableIsNotDefinedException::class)
    fun variableIsNotDefinedExceptionTest() {
        val root = File(0, Block(0, listOf(
                Println(0, listOf(VariableIdentifier(0, "x")))
        )))
        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)

        root.accept(EvaluationVisitor(MutableContext(null, stream)))
    }


    @Test(expected = FunctionIsNotDefinedException::class)
    fun functionIsNotDefinedExceptionTest() {
        val root = File(0, Block(0, listOf(
                ExpressionStatement(0, FunctionCall(0, "x", emptyList()))
        )))
        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)

        root.accept(EvaluationVisitor(MutableContext(null, stream)))
    }

}