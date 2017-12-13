package ru.spbau.mit

@DslMarker
annotation class TexEntityMarker

interface TagConsumer<out R> {
    fun onTagStart(tag: TexTag)
    fun onTagEnd(tag: TexTag)
    fun onCommand(command: TexCommand)
    fun onTagContent(content: CharSequence)
    fun finalize(): R
}

open class TexTag(
        val name: String,
        override val consumer: TagConsumer<*>,
        val parameters: List<String>
) : TexEntity

open class TexCommand(
        val name: String,
        override val consumer: TagConsumer<*>,
        val parameters: List<String>
) : TexEntity

@TexEntityMarker
interface TexEntity {
    val consumer: TagConsumer<*>
}


fun <T : TexTag> T.visit(block: T.() -> Unit) {
    consumer.onTagStart(this)
    this.block()
    consumer.onTagEnd(this)
}

fun <C : TexCommand> C.visit(block: C.() -> Unit) {
    consumer.onCommand(this)
    this.block()
}
