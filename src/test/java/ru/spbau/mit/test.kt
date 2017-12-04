package ru.spbau.mit
import org.junit.Test
import kotlin.test.assertEquals

class Test {
    @Test
    fun documentTest() {
        val sb = StringBuilder()
        sb.appendTex().tex {
            document()
        }
        assertEquals("""
            |\begin{document}
            |\end{document}
            |
        """.trimMargin(), sb.toString())
    }

    @Test
    fun documentclassTest() {
        val sb = StringBuilder()
        sb.appendTex().tex {
            documentClass("article")
        }
        assertEquals("""
            |\documentclass{article}
            |
        """.trimMargin(), sb.toString())
    }


    @Test
    fun usepackageTest() {
        val sb = StringBuilder()
        sb.appendTex().tex {
            usePackage("graphicx")
            usePackage("babel", "english", "russian")
        }
        assertEquals("""
            |\usepackage{graphicx}
            |\usepackage[english, russian]{babel}
            |
        """.trimMargin(), sb.toString())
    }

    @Test
    fun frameTest() {
        val sb = StringBuilder()
        sb.appendTex().tex {
            documentClass("beamer")
            usePackage("graphicx")
            document {
                frame {
                    +"Hello world"
                }
                frame()
            }
        }
        assertEquals("""
            |\documentclass{beamer}
            |\usepackage{graphicx}
            |\begin{document}
            |\begin{frame}
            |Hello world
            |\end{frame}
            |\begin{frame}
            |\end{frame}
            |\end{document}
            |
        """.trimMargin(), sb.toString())
    }

    @Test
    fun itemizeTest() {
        val sb = StringBuilder()
        sb.appendTex().tex {
            documentClass("beamer")
            document {
                frame {
                    itemize {
                        item { +"1." }
                        +"first"
                        item { +"3." }
                        +"za mat izveni"
                    }
                }
            }
        }
        assertEquals("""
            |\documentclass{beamer}
            |\begin{document}
            |\begin{frame}
            |\begin{itemize}
            |\item
            |1.
            |first
            |\item
            |3.
            |za mat izveni
            |\end{itemize}
            |\end{frame}
            |\end{document}
            |
        """.trimMargin(), sb.toString())
    }

    @Test
    fun enumerateTest() {
        val sb = StringBuilder()
        sb.appendTex().tex {
            documentClass("article")
            document {
                enumerate {
                    item { +"first" }
                    item { +"second" }
                }

            }
        }
        assertEquals("""
            |\documentclass{article}
            |\begin{document}
            |\begin{enumerate}
            |\item
            |first
            |\item
            |second
            |\end{enumerate}
            |\end{document}
            |
        """.trimMargin(), sb.toString())
    }

    @Test
    fun mathTest() {
        val sb = StringBuilder()
        sb.appendTex().tex {
            documentClass("article")
            document {
                math {
                    +"""\pi^e < e^\pi"""
                }

            }
        }
        assertEquals("""
            |\documentclass{article}
            |\begin{document}
            |\begin{math}
            |\pi^e < e^\pi
            |\end{math}
            |\end{document}
            |
        """.trimMargin(), sb.toString())
    }

    @Test
    fun alignmentTest() {
        val sb = StringBuilder()
        sb.appendTex().tex {
            documentClass("article")
            document {
                center {
                    +"Center"
                }
                flushLeft {
                    +"Left"
                }
                flushRight {
                    +"Right"
                }

            }
        }
        assertEquals("""
            |\documentclass{article}
            |\begin{document}
            |\begin{center}
            |Center
            |\end{center}
            |\begin{flushleft}
            |Left
            |\end{flushleft}
            |\begin{flushright}
            |Right
            |\end{flushright}
            |\end{document}
            |
        """.trimMargin(), sb.toString())
    }

    @Test
    fun customTagsTest() {
        val sb = StringBuilder()
        sb.appendTex().tex {
            documentClass("article")
            usePackage("graphicx")
            document {
                customTag("figure") {
                    customCommand("centering")
                    customCommand("includegraphics",
                            "figure.jpg", """width=0.5\textwidth""", """height=0.6\textwidth""")
                }
            }
        }
        assertEquals("""
            |\documentclass{article}
            |\usepackage{graphicx}
            |\begin{document}
            |\begin{figure}
            |\centering
            |\includegraphics[width=0.5\textwidth, height=0.6\textwidth]{figure.jpg}
            |\end{figure}
            |\end{document}
            |
        """.trimMargin(), sb.toString())
    }

    @Test
    fun complexTest() {
        val rows = listOf("one", "two", "three")

        val sb = StringBuilder()
        sb.appendTex().tex {
            documentClass("beamer")
            usePackage("babel", "russian", "english")
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
        assertEquals("""
            |\documentclass{beamer}
            |\usepackage[russian, english]{babel}
            |\begin{document}
            |\begin{frame}
            |\frametitle{frametitle}
            |\begin{itemize}
            |\item
            |one text
            |\item
            |two text
            |\item
            |three text
            |\end{itemize}
            |\begin{pyglist}[language=kotlin]
            |val a = 1
            |
            |\end{pyglist}
            |\end{frame}
            |\end{document}
            |
        """.trimMargin(), sb.toString())
    }
}
