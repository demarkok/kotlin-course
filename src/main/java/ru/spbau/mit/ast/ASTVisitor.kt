package ru.spbau.mit.ast

interface ASTVisitor<out T> {
    fun visit(file: File): T
    fun visit(block: Block): T
    fun visit(functionDeclaration: FunctionDeclaration): T
    fun visit(variableDeclaration: VariableDeclaration): T
    fun visit(whileStatement: While): T
    fun visit(ifStatement: If): T
    fun visit(variableAssignment: VariableAssignment): T
    fun visit(returnStatement: Return): T
    fun visit(println: Println): T
    fun visit(binaryExpression: BinaryExpression): T
    fun visit(functionCall: FunctionCall): T
    fun visit(variableIdentifier: VariableIdentifier): T
    fun visit(literal: Literal): T
}