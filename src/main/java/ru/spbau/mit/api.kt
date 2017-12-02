package ru.spbau.mit

interface TagConsumer<out R> {

    fun onTagStart(tag: Tag)
    fun onTagEnd(tag: Tag)
    fun onCommand(command: Command)
    fun onTagContent(content: CharSequence)
    fun finalize(): R
}

interface Tag : TexEntity

interface Command : TexEntity

interface TexEntity {

    val consumer: TagConsumer<*>
    val name: String
    val parameters: List<String>
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
