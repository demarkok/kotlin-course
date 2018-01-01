package ru.spbau.mit.evaluation

import ru.spbau.mit.ast.Block

data class Function(val functionBlock: Block,
                    val arguments: List<String>,
                    val declarationContext: Context)
