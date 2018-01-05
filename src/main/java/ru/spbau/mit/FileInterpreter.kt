package ru.spbau.mit

import ru.spbau.mit.evaluation.EvaluationVisitor
import ru.spbau.mit.evaluation.MutableContext
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path

fun interpretFile(file: Path, output: OutputStream) {
    val source = String(Files.readAllBytes(file))
    val root = parse(source)

    val visitor = EvaluationVisitor(MutableContext(null, output.bufferedWriter()))
    root.accept(visitor)
}