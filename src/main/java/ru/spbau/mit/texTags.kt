package ru.spbau.mit

interface InDocumentEntity : WithText

interface WithText : TexEntity {
    operator fun String.unaryPlus() {
        text(this)
    }
}

class Tex(override val consumer: TagConsumer<*>) : TexEntity

class Document(
        override val consumer: TagConsumer<*>
) : TexTag("document", consumer, emptyList()), InDocumentEntity

class DocumentClass(
        override val consumer: TagConsumer<*>,
        override val parameters: List<String>
) : TexCommand("documentclass", consumer, parameters)

class UsePackage(
        override val consumer: TagConsumer<*>,
        override val parameters: List<String>
) : TexCommand("usepackage", consumer, parameters)

class Frame(
        override val consumer: TagConsumer<*>,
        override val parameters: List<String>
) : TexTag("frame", consumer, parameters), InDocumentEntity

class FrameTitle(
        override val name: String,
        override val consumer: TagConsumer<*>
) : TexCommand("frametitle", consumer, listOf(name))

class Itemize(
        override val consumer: TagConsumer<*>
) : TexTag("itemize", consumer, emptyList()), InDocumentEntity

class Enumerate(
        override val consumer: TagConsumer<*>
) : TexTag("enumerate", consumer, emptyList()), InDocumentEntity

class Item(
        override val consumer: TagConsumer<*>
) : TexCommand("item", consumer, emptyList()), WithText

class Math(
        override val consumer: TagConsumer<*>
) : TexTag("math", consumer, emptyList()), WithText

class Center(
        override val consumer: TagConsumer<*>
) : TexTag("center", consumer, emptyList()), InDocumentEntity

class FlushLeft(
        override val consumer: TagConsumer<*>
) : TexTag("flashleft", consumer, emptyList()), InDocumentEntity

class FlushRight(
        override val consumer: TagConsumer<*>
) : TexTag("flashright", consumer, emptyList()), InDocumentEntity

class CustomTag(
        override val name: String,
        override val consumer: TagConsumer<*>,
        override val parameters: List<String>
) : TexTag(name, consumer, parameters), InDocumentEntity


fun <T, C : TagConsumer<T>> C.tex(block: Tex.() -> Unit = {}): T = Tex(this).run {
    block()
    return this@tex.finalize()
}

fun Tex.usePackage(name: String, vararg parameters: String) =
        UsePackage(consumer, listOf(name) + parameters).visit {}

fun Document.frame(vararg parameters: String, block: Frame.() -> Unit = {}) = Frame(consumer, parameters.asList()).visit(block)

fun Frame.frameTitle(name: String) = FrameTitle(name, consumer).visit {}

fun Tex.document(block: Document.() -> Unit = {}) = Document(consumer).visit(block)

fun Tex.documentClass(name: String, vararg parameters: String) =
        DocumentClass(consumer, listOf(name) + parameters).visit {}

fun InDocumentEntity.itemize(block: Itemize.() -> Unit) = Itemize(consumer).visit(block)

fun Itemize.item(block: Item.() -> Unit) = Item(consumer).visit(block)

fun Enumerate.item(block: Item.() -> Unit) = Item(consumer).visit(block)

fun InDocumentEntity.math(block: Math.() -> Unit) = Math(consumer).visit(block)

fun InDocumentEntity.center(block: Center.() -> Unit) = Center(consumer).visit(block)

fun InDocumentEntity.flushLeft(block: FlushLeft.() -> Unit) = FlushLeft(consumer).visit(block)

fun InDocumentEntity.flushRight(block: FlushRight.() -> Unit) = FlushRight(consumer).visit(block)

fun TexEntity.customTag(name: String, vararg parameters: String, block: CustomTag.() -> Unit) =
        CustomTag(name, consumer, parameters.asList()).visit(block)

fun WithText.text(string: String) = consumer.onTagContent(string)

