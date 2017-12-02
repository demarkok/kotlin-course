package ru.spbau.mit

open class TexTag(
        override val name: String,
        override val consumer: TagConsumer<*>,
        override val parameters: List<String>
) : Tag