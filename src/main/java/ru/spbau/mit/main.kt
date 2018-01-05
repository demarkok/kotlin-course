package ru.spbau.mit

import ru.spbau.mit.debug.Repl
import java.io.BufferedWriter
import java.io.OutputStreamWriter

fun main(args: Array<String>) {
    Repl(System.`in`.bufferedReader(), BufferedWriter(OutputStreamWriter(System.out))).run()
}

