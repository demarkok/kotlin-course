package ru.spbau.mit.ast


interface ASTEntity {
    val line: Int
    fun <T> accept(visitor: ASTVisitor<T>): T
}

data class File(override val line: Int, val block: Block) : ASTEntity {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

data class Block(override val line: Int, val statements: List<Statement>) : ASTEntity {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

interface Statement : ASTEntity

data class FunctionDeclaration(
        override val line: Int,
        val name: String,
        val parameterNames: List<String>,
        val body: Block
) : Statement {

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

data class VariableDeclaration(override val line: Int,
                               val name: String,
                               val value: Expression? = null
) : Statement {

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

data class While(override val line: Int,
                 val condition: Expression,
                 val body: Block
) : Statement {

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

data class If(override val line: Int,
              val condition: Expression,
              val body: Block,
              val elseBody: Block?
) : Statement {

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

data class VariableAssignment(override val line: Int, val name: String, val value: Expression) : Statement {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

data class Return(override val line: Int, val expression: Expression) : Statement {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

data class Println(override val line: Int, val arguments: List<Expression>) : Statement {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

data class ExpressionStatement(override val line: Int, val expression: Expression) : Statement {
    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

interface Expression : ASTEntity

data class FunctionCall(override val line: Int,
                        val name: String,
                        val arguments: List<Expression>
) : Expression {

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}


data class BinaryExpression(override val line: Int,
                            val leftOperand: Expression,
                            val operator: BinaryOperator,
                            val rightOperand: Expression
) : Expression {

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

data class VariableIdentifier(override val line: Int, val name: String) : Expression {

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}

data class Literal(override val line: Int, val value: Int) : Expression {

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visit(this)
    }
}
