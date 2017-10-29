package ru.spbau.mit

import java.util.*


class FenwickTree(private val size: Int) {
    private val array: IntArray = kotlin.IntArray(size + 1)

    fun inc(position: Int) {
        var i: Int = position
        while (i <= size) {
            array[i]++
            i += i and (-i)
        }
    }

    fun getPrefixSum(rightBorder: Int): Int {
        var result = 0
        var i: Int = rightBorder
        while (i >= 1) {
            result += array[i]
            i -= i and (-i)
        }
        return result
    }
}

class NameEditor(private val basicString: String, private val numberOfCopies: Int) {

    private val alphabetSize = 26
    private val maxLength = 200000

    private fun Char.inAlphabet(): Int = this - 'a'

    private val letterOccurrences: Array<IntArray> = kotlin.Array(alphabetSize, { c1 ->
        basicString.withIndex()
                .filter { x -> x.value.inAlphabet() == c1 }
                .map { x -> x.index }
                .toIntArray()
    })

    private val isRemoved: BooleanArray = kotlin.BooleanArray(numberOfCopies * basicString.length)

    private val letterChanges: Array<FenwickTree> = Array(alphabetSize, { FenwickTree(maxLength) }) // letter -> fenwick tree of changes

    fun edit(query: Query) {

        val (index, symbol) = query

        var l = 0
        var r = maxLength

        while (r - l > 1) {
            val m: Int = (l + r) / 2
            if (m - letterChanges[symbol.inAlphabet()].getPrefixSum(m) < index) {
                l = m
            } else {
                r = m
            }
        }

        val truePosition = r - 1
        letterChanges[symbol.inAlphabet()].inc(truePosition + 1)
        val perWord: Int = letterOccurrences[symbol.inAlphabet()].size
        val inResultingWord = (truePosition / perWord) * basicString.length +
                letterOccurrences[symbol.inAlphabet()][truePosition % perWord]
        isRemoved[inResultingWord] = true
    }

    fun getResult(): String {
        return basicString.repeat(numberOfCopies).filterIndexed { index, _ -> !isRemoved[index] }
    }

    data class Query(val index: Int, val symbol: Char)
}

fun solve(k: Int, string: String, queries: List<NameEditor.Query>): String {
    val editor = NameEditor(string, k)
    queries.forEach { editor.edit(it) }
    return editor.getResult()
}



fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)

    val k = scanner.nextInt()
    scanner.nextLine()
    val string = scanner.nextLine().orEmpty()

    val n = scanner.nextInt()
    scanner.nextLine()

    val queries: List<NameEditor.Query> = List(n, { _ ->
        val tokenizer = StringTokenizer(scanner.nextLine())
        NameEditor.Query(Integer.parseInt(tokenizer.nextToken()), tokenizer.nextToken().first())
    })

    println(solve(k, string, queries))
}