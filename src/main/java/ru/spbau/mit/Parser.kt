package ru.spbau.mit

import com.google.common.primitives.Ints
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.spbau.mit.ast.*
import ru.spbau.mit.ast.BinaryOperator.Companion.byString
import ru.spbau.mit.parser.LanguageBaseVisitor
import ru.spbau.mit.parser.LanguageLexer
import ru.spbau.mit.parser.LanguageParser
import ru.spbau.mit.parser.LanguageParser.*


fun parse(sourceCode: String): File {
    val charStream = CharStreams.fromString(sourceCode)
    val lexer = LanguageLexer(charStream)
    val tokens = CommonTokenStream(lexer)
    val parser = LanguageParser(tokens)
    val fileVisitor = FileVisitor
    return fileVisitor.visit(parser.file())
}


private object FileVisitor : LanguageBaseVisitor<File>() {
    override fun visitFile(ctx: FileContext): File? {
        val blockVisitor = BlockVisitor
        return File(ctx.block().accept(blockVisitor))
    }
}


private object BlockVisitor : LanguageBaseVisitor<Block>() {
    override fun visitBlock(ctx: BlockContext): Block {
        val statementVisitor = StatementVisitor
        return Block(ctx.statement().map { it.accept(statementVisitor) })
    }
}


private object StatementVisitor : LanguageBaseVisitor<Statement>() {
    override fun visitFunctionDeclarationStatement(ctx: FunctionDeclarationStatementContext): Statement {
        val functionDeclaration = ctx.functionDeclaration()

        val name: String = functionDeclaration.Identifier().text
        val parameters: List<String> = functionDeclaration.parameterNames()?.Identifier()?.map { it.text } ?: emptyList()
        val body: Block = functionDeclaration.blockWithBraces().block().accept(BlockVisitor)

        return FunctionDeclaration(name, parameters, body)
    }

    override fun visitVariableDeclarationStatement(ctx: VariableDeclarationStatementContext): Statement {
        val variableDeclaration = ctx.variableDeclaration()

        val name: String = variableDeclaration.Identifier().text
        val value: Expression? = variableDeclaration.expression()?.accept(ExpressionVisitor)

        return VariableDeclaration(name, value)
    }

    override fun visitExpressionStatement(ctx: ExpressionStatementContext): Statement =
            ctx.expression().accept(ExpressionVisitor)

    override fun visitWhileStatement(ctx: WhileStatementContext): Statement {
        val condition: Expression = ctx.parExpression().expression().accept(ExpressionVisitor)
        val body: Block = ctx.blockWithBraces().block().accept(BlockVisitor)

        return While(condition, body)
    }

    override fun visitIfStatement(ctx: IfStatementContext): Statement {
        val condition = ctx.parExpression().expression().accept(ExpressionVisitor)

        val blocks: List<Block> = ctx.blockWithBraces().map { it.block().accept(BlockVisitor) }

        val body: Block = blocks[0]
        val elseBody: Block? = blocks.elementAtOrNull(1)

        return If(condition, body, elseBody)
    }

    override fun visitAssignmentStatement(ctx: AssignmentStatementContext): Statement {
        val name: String = ctx.assignment().variableAccess().Identifier().text
        val value: Expression = ctx.assignment().expression().accept(ExpressionVisitor)
        return VariableAssignment(name, value)
    }

    override fun visitReturnStatement(ctx: ReturnStatementContext): Statement =
            Return(ctx.expression().accept(ExpressionVisitor))

    override fun visitPrintlnStatement(ctx: PrintlnStatementContext): Statement =
            Println(ctx.arguments()?.expression()?.map { it.accept(ExpressionVisitor) } ?: emptyList())
}


private object ExpressionVisitor : LanguageBaseVisitor<Expression>() {
    override fun visitVariableAccessExpression(ctx: VariableAccessExpressionContext): Expression {
        val name: String = ctx.variableAccess().Identifier().text
        return VariableIdentifier(name)
    }

    override fun visitBinaryExpression(ctx: BinaryExpressionContext): Expression {
        val leftOperand: Expression = ctx.leftOperand.accept(ExpressionVisitor)
        val rightOperand: Expression = ctx.rightOperand.accept(ExpressionVisitor)
        val operator = byString(ctx.operator.text)

        return BinaryExpression(leftOperand, operator, rightOperand)
    }

    override fun visitParenthesesExpression(ctx: ParenthesesExpressionContext): Expression =
            ctx.parExpression().expression().accept(ExpressionVisitor)

    override fun visitFunctionCallExpression(ctx: FunctionCallExpressionContext): Expression {
        val functionCall = ctx.functionCall()
        val name: String = functionCall.Identifier().text
        val arguments: List<Expression> = functionCall
                .arguments()
                ?.expression()
                ?.map { it.accept(ExpressionVisitor) } ?: emptyList()

        return FunctionCall(name, arguments)
    }

    override fun visitLiteralExpression(ctx: LiteralExpressionContext): Expression {
        val value = Ints.tryParse(ctx.Literal().text) ?: throw RuntimeException()
        return ru.spbau.mit.ast.Literal(value)
    }
}
