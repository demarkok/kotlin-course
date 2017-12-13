package ru.spbau.mit

interface WithText : TexEntity {
    operator fun String.unaryPlus() {
        consumer.onTagContent(this + "\n")
    }
}

interface InDocumentEntity : WithText

class Tex(override val consumer: TagConsumer<*>) : TexEntity

class Document(
        consumer: TagConsumer<*>
) : TexTag("document", consumer, emptyList()), InDocumentEntity

class DocumentClass(
        consumer: TagConsumer<*>,
        parameters: List<String>
) : TexCommand("documentclass", consumer, parameters)

class UsePackage(
        consumer: TagConsumer<*>,
        parameters: List<String>
) : TexCommand("usepackage", consumer, parameters)

class Frame(
        consumer: TagConsumer<*>,
        parameters: List<String>
) : TexTag("frame", consumer, parameters), InDocumentEntity

class FrameTitle(
        name: String,
        consumer: TagConsumer<*>
) : TexCommand("frametitle", consumer, listOf(name))

class Itemize(
        consumer: TagConsumer<*>
) : TexTag("itemize", consumer, emptyList()), InDocumentEntity

class Enumerate(
        consumer: TagConsumer<*>
) : TexTag("enumerate", consumer, emptyList()), InDocumentEntity

class Item(
        consumer: TagConsumer<*>,
        parameters: List<String>
) : TexCommand("item", consumer, parameters), WithText

class Math(
        consumer: TagConsumer<*>
) : TexTag("math", consumer, emptyList()), WithText

class Center(
        consumer: TagConsumer<*>
) : TexTag("center", consumer, emptyList()), InDocumentEntity

class FlushLeft(
        consumer: TagConsumer<*>
) : TexTag("flushleft", consumer, emptyList()), InDocumentEntity

class FlushRight(
        consumer: TagConsumer<*>
) : TexTag("flushright", consumer, emptyList()), InDocumentEntity

class CustomTag(
        name: String,
        consumer: TagConsumer<*>,
        parameters: List<String>
) : TexTag(name, consumer, parameters), InDocumentEntity

class CustomCommand(
        name: String,
        consumer: TagConsumer<*>,
        parameters: List<String>
) : TexCommand(name, consumer, parameters), InDocumentEntity


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

fun InDocumentEntity.enumerate(block: Enumerate.() -> Unit) = Enumerate(consumer).visit(block)

fun Itemize.item(parameters: List<String> = emptyList(), block: Item.() -> Unit = {}) = Item(consumer, parameters).visit(block)

fun Enumerate.item(parameters: List<String> = emptyList(), block: Item.() -> Unit = {}) = Item(consumer, parameters).visit(block)

fun InDocumentEntity.math(block: Math.() -> Unit) = Math(consumer).visit(block)

fun InDocumentEntity.center(block: Center.() -> Unit) = Center(consumer).visit(block)

fun InDocumentEntity.flushLeft(block: FlushLeft.() -> Unit) = FlushLeft(consumer).visit(block)

fun InDocumentEntity.flushRight(block: FlushRight.() -> Unit) = FlushRight(consumer).visit(block)

fun TexEntity.customTag(name: String, vararg parameters: String, block: CustomTag.() -> Unit) =
        CustomTag(name, consumer, parameters.asList()).visit(block)

fun TexEntity.customCommand(name: String, vararg parameters: String, block: CustomCommand.() -> Unit = {}) =
        CustomCommand(name, consumer, parameters.asList()).visit(block)
