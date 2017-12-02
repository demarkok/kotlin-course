package ru.spbau.mit

class TexStreamBuilder<out O : Appendable>(val out: O) : TagConsumer<O> {

    override fun onCommand(command: Command) {
        out.append("""\${command.name}""")
        command.parameters.let {
            printParameters(it.subList(1, it.size))
            out.append("""{${it.first()}}""")
        }
        out.appendln()
    }

    override fun onTagStart(tag: Tag) {
        out.append("""\begin{${tag.name}}""")
        printParameters(tag.parameters)
        out.appendln()
    }

    override fun onTagEnd(tag: Tag) {
        out.append("""\end{${tag.name}}""")
        out.appendln()
    }

    override fun onTagContent(content: CharSequence) {
        out.append(content)
    }

    override fun finalize(): O = out

    private fun printParameters(parameters: List<CharSequence>) {
        if (parameters.isNotEmpty()) {
            out.append(parameters.joinToString(prefix = "[", postfix = "]"))
        }
    }
}

fun <O : Appendable> O.appendTex(): TagConsumer<O> = TexStreamBuilder(this)


