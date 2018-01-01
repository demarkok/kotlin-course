package ru.spbau.mit.debugger

import org.junit.Test
import ru.spbau.mit.debug.DebuggerImpl
import java.io.BufferedWriter
import java.io.StringWriter
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DebuggerTest {

    private val source = """
        |fun f(x) {         // 1
        |   var y = 8       // 2
        |   y = y + 1       // 3
        |   return 239      // 4
        |}                  // 5
        |                   // 6
        |f(4)               // 7
        |f(2)               // 8
        |
        |""".trimMargin()

    @Test
    fun listAddAndRemoveTest() {
        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)
        val debugger = DebuggerImpl(source, stream)

        assert(debugger.list().isEmpty())

        debugger.addBreakpoint(3)

        assertEquals(listOf(3 to null), debugger.list())

        debugger.addConditionalBreakpoint(4, "y == 9")

        assertEquals(mapOf(3 to null, 4 to "y == 9"), debugger.list().toMap())

        debugger.removeBreakpoint(3)

        assertEquals(listOf(4 to "y == 9"), debugger.list())
    }

    @Test
    fun breakpointTest() {

        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)
        val debugger = DebuggerImpl(source, stream)

        debugger.addBreakpoint(3)
        debugger.addBreakpoint(4)

        debugger.run()
        assertTrue(debugger.isRunning())
        assertEquals(true, debugger.isRunning())
        assertEquals(3, debugger.line())
        debugger.next()
        assertEquals(4, debugger.line())
        debugger.next()
        assertEquals(3, debugger.line())
        debugger.next()
        assertEquals(4, debugger.line())
        debugger.next()
        assertEquals(false, debugger.isRunning())
    }


    @Test
    fun conditionTest() {
        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)
        val debugger = DebuggerImpl(source, stream)


        debugger.addConditionalBreakpoint(4, "x == 2")

        debugger.run()
        assertTrue(debugger.isRunning())
        assertEquals(4, debugger.line())
        assertEquals(2, debugger.evaluate("x"))
        debugger.next()
        assertFalse(debugger.isRunning())
    }

    @Test
    fun evaluateTest() {
        val stringWriter = StringWriter()
        val stream = BufferedWriter(stringWriter)
        val debugger = DebuggerImpl(source, stream)

        debugger.addBreakpoint(3)
        debugger.addBreakpoint(4)

        debugger.run()
        assertTrue(debugger.isRunning())
        assertEquals(true, debugger.isRunning())
        assertEquals(8, debugger.evaluate("y"))
        assertEquals(12, debugger.evaluate("x + y"))
        debugger.next()
        assertEquals(9, debugger.evaluate("y"))
        debugger.next()
        assertEquals(10, debugger.evaluate("x + y"))
        debugger.next()
        assertEquals(11, debugger.evaluate("x + y"))
        debugger.next()
        assertEquals(false, debugger.isRunning())
    }

}