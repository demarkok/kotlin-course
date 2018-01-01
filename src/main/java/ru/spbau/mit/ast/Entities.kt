package ru.spbau.mit.ast

import ru.spbau.mit.exceptions.FunctionIsNotDefinedException
import ru.spbau.mit.exceptions.VariableIsNotDefinedException


fun ContextInterface.resolveVariableOrThrow(name: String) =
        this.resolveVariable(name) ?: throw VariableIsNotDefinedException()

fun ContextInterface.resolveFunctionOrThrow(name: String) =
        this.resolveFunction(name) ?: throw FunctionIsNotDefinedException()

interface ASTEntity {
    fun <T> accept(visitor: ASTVisitor<T>): T
}

data class File(val block: Block) : ASTEntity {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

data class Block(val statements: List<Statement>) : ASTEntity {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

interface Statement : ASTEntity

data class FunctionDeclaration(
        val name: String,
        val parameterNames: List<String>,
        val body: Block
) : Statement {

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

data class VariableDeclaration(val name: String,
                               val value: Expression? = null
) : Statement {

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

data class While(val condition: Expression,
                 val body: Block
) : Statement {

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

data class If(val condition: Expression,
              val body: Block,
              val elseBody: Block?
) : Statement {

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

data class VariableAssignment(val name: String, val value: Expression) : Statement {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

data class Return(val expression: Expression) : Statement {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

data class Println(val arguments: List<Expression>) : Statement {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

interface Expression : Statement

data class FunctionCall(val name: String,
                        val arguments: List<Expression>
) : Expression {

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}


data class BinaryExpression(val leftOperand: Expression,
                            val operator: BinaryOperator,
                            val rightOperand: Expression
) : Expression {

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

data class VariableIdentifier(val name: String) : Expression {

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

data class Literal(val value: Int) : Expression {

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}
