package ru.spbau.mit
import org.junit.Test
import kotlin.test.assertEquals

class TestSource {

    @Test
    fun sampleTest1() {
        val k = 2
        val string = "bac"

        val queries: List<NameEditor.Query> = listOf<NameEditor.Query>(
                NameEditor.Query(2, 'a'),
                NameEditor.Query(1, 'b'),
                NameEditor.Query(2, 'c'))

        assertEquals("acb", solve(k, string, queries))
    }

    @Test
    fun sampleTest2() {
        val k = 1
        val string = "abacaba"

        val queries: List<NameEditor.Query> = listOf<NameEditor.Query>(
                NameEditor.Query(1, 'a'),
                NameEditor.Query(1, 'a'),
                NameEditor.Query(1, 'c'),
                NameEditor.Query(2, 'b'))

        assertEquals("baa", solve(k, string, queries))
    }

    @Test
    fun sampleTest3() {
        val k = 4
        val string = "db"

        val queries: List<NameEditor.Query> = listOf<NameEditor.Query>(
                NameEditor.Query(1, 'd'),
                NameEditor.Query(2, 'd'),
                NameEditor.Query(2, 'b'),
                NameEditor.Query(1, 'd'),
                NameEditor.Query(2, 'b'))

        assertEquals("bdb", solve(k, string, queries))
    }
}
