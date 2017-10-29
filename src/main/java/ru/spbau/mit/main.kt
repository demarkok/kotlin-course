package ru.spbau.mit

import java.util.*

/**
 * An implementation of fenwick tree which enables to increase an array element and to calculate the sum of elements
 * on each array prefix. Both of them take O(log n) operations.
 */
class FenwickTree(private val size: Int) {

    private val array: IntArray = IntArray(size + 1)

    /**
     * Increase the array element on the given position.
     * The elements are indexed from 1 to size (inclusive).
     * Time complexity: O(log n)
     */
    fun inc(position: Int) {
        var i: Int = position
        while (i <= size) {
            array[i]++
            i += i and (-i)
        }
    }

    /**
     * Calculate the sum of array element from 1 to rightBorder (inclusive).
     * Time complexity: O(log n)
     */
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

/**
 * An editor of nickName. Enables to delete i-th occurrence of a letter in
 * some string repeated k times.
 */
class NameEditor(private val basicString: String, private val numberOfCopies: Int) {

    private val alphabetSize = 26
    private val maxLength = 200000
    private fun Char.inAlphabet(): Int = this - 'a'
    private val letterOccurrences: Array<IntArray> = Array(alphabetSize, { c1 ->
        basicString.withIndex()
                .filter { x -> x.value.inAlphabet() == c1 }
                .map { x -> x.index }
                .toIntArray()
    })
    private val isRemoved: BooleanArray = BooleanArray(numberOfCopies * basicString.length)
    private val letterChanges: Array<FenwickTree> = Array(alphabetSize, { FenwickTree(maxLength) }) // letter -> fenwick tree of changes

    /**
     * Perform the query: remove a symbol.
     */
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

    /**
     * Get the resulting string after performing the queries.
     */
    fun getResult(): String {
        return basicString.repeat(numberOfCopies).filterIndexed { index, _ -> !isRemoved[index] }
    }

    /**
     * A class describing deletion query.
     * @param index - number of the symbol occurrence to delete.
     * @param symbol - letter to delete
     */
    data class Query(val index: Int, val symbol: Char)
}

/**
 * Solve the problem: perform given queries and return the resulting string.
 */
fun solve(k: Int, string: String, queries: List<NameEditor.Query>): String {
    val editor = NameEditor(string, k)
    queries.forEach { editor.edit(it) }
    return editor.getResult()
}

fun main(args: Array<String>) {
    val k = Integer.parseInt(readLine())
    val string = readLine().orEmpty()
    val n = Integer.parseInt(readLine())
    val queries: List<NameEditor.Query> = List(n, { _ ->
        val tokenizer = StringTokenizer(readLine())
        NameEditor.Query(Integer.parseInt(tokenizer.nextToken()), tokenizer.nextToken().first())
    })

    println(solve(k, string, queries))
}