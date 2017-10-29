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


fun solve(k: Int, string: String, queries: List<Pair<Int, Char>>): String {
    val len = string.length

    fun Char.inAlphabet(): Int = this - 'a'
//    val letterOccurrences: IntArray = kotlin.IntArray(26, { c1 -> string.count { c2 -> c2 - 'a' == c1 } })
    val letterOccurrences: Array<IntArray> = kotlin.Array(26, { c1 ->
        string.withIndex()
                .filter { x -> x.value.inAlphabet() == c1 }
                .map { x -> x.index }
                .toIntArray()
    })

    val removed: BooleanArray = kotlin.BooleanArray(k * len)
    val letterChanges: Array<FenwickTree> = Array(26, { FenwickTree(200000) }) // letter -> fenwick tree of changes


    for ((position, symbol) in queries) {

        var l = 0
        var r = 200000

        while (r - l > 1) {
            val m: Int = (l + r) / 2
            if (m - letterChanges[symbol.inAlphabet()].getPrefixSum(m) < position) {
                l = m
            } else {
                r = m
            }
        }

        val realPosition = r - 1
        letterChanges[symbol.inAlphabet()].inc(realPosition + 1)
        val perWord: Int = letterOccurrences[symbol.inAlphabet()].size
        removed[(realPosition / perWord) * len + letterOccurrences[symbol.inAlphabet()][realPosition % perWord]] = true
    }

    return string.repeat(k).filterIndexed { index, _ -> !removed[index] }
}

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)

    val k = scanner.nextInt()
    scanner.nextLine()
    val string = scanner.nextLine().orEmpty()

    val n = scanner.nextInt()
    scanner.nextLine()

    val queries: List<Pair<Int, Char>> = List(n, { _ ->
        val tokenizer = StringTokenizer(scanner.nextLine())
        Pair(Integer.parseInt(tokenizer.nextToken()), tokenizer.nextToken().first()) })

    println(solve(k, string, queries))
}