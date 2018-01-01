package ru.spbau.mit

import java.nio.file.Paths

fun main(args: Array<String>) {
    interpretFile(Paths.get(args.first()), System.out)
}
