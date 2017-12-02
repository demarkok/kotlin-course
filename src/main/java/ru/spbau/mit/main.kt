package ru.spbau.mit

import kotlinx.html.*
import kotlinx.html.stream.appendHTML


fun getGreeting(): String {
    val words = mutableListOf<String>()
    words.add("Hello,")
    
    words.add("world!")

    return words.joinToString(separator = " ")

}

fun main(args: Array<String>) {
    println(getGreeting())

    System.out.appendHTML().html {
        body {
            div {
                a("http://kotlinlang.org") {
                    target = ATarget.blank
                    +"Main site"
                }
            }
        }
    }
}
