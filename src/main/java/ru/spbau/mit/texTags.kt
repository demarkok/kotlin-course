package ru.spbau.mit

fun <T, C : TagConsumer<T>> C.tex(block: Tex.() -> Unit = {}): T = Tex(this).block()

class Tex