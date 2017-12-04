package ru.spbau.mit
import org.junit.Test
import kotlin.test.assertEquals

class Test {
    @Test
    fun test1() {
        val sb = StringBuilder()
        sb.appendTex().tex {
            documentClass("article")
            document()
        }
        assertEquals("""
            |\documentclass{article}
            |\begin{document}
            |\end{document}
        """.trimMargin(), sb.toString())
    }

    @Test
    fun test2() {
        val rows = listOf("one", "two", "three")

        val sb = StringBuilder()
        System.out.appendTex().tex {
            documentClass("beamer")
            usePackage("babel", "russian", "english")
            usePackage("")
            document {
                frame {
                    frameTitle("frametitle")
                    itemize {
                        for (row in rows) {
                            item { +"$row text" }
                        }
                    }

                    customTag("pyglist", "language=kotlin") {
                        +"""
                            |val a = 1
                            |
                        """.trimMargin()
                    }
                }
            }
        }
    }
}
