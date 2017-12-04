package ru.spbau.mit

@DslMarker
annotation class TexEntityMarker

interface TagConsumer<out R> {
    fun onTagStart(tag: Tag)
    fun onTagEnd(tag: Tag)
    fun onCommand(command: Command)
    fun onTagContent(content: CharSequence)
    fun finalize(): R
}

interface Tag : TexEntity {
    val name: String
    val parameters: List<String>
}

interface Command : TexEntity {
    val name: String
    val parameters: List<String>
}

@TexEntityMarker
interface TexEntity {
    val consumer: TagConsumer<*>
}


fun <T : Tag> T.visit(block: T.() -> Unit) {
    consumer.onTagStart(this)
    this.block()
    consumer.onTagEnd(this)
}

fun <C : Command> C.visit(block: C.() -> Unit) {
    consumer.onCommand(this)
    this.block()
}
