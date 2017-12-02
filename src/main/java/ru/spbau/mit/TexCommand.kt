package ru.spbau.mit

open class TexCommand(
        override val name: String,
        override val consumer: TagConsumer<*>,
        override val parameters: List<String>
) : Command