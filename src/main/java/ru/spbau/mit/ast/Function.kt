package ru.spbau.mit.ast

data class Function(val functionBlock: Block,
                    val arguments: List<String>,
                    val declarationContext: Context)
