package ru.spbau.mit.debug

import org.antlr.v4.runtime.misc.ParseCancellationException
import ru.spbau.mit.exceptions.*
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class Repl(private val input: BufferedReader, private val output: BufferedWriter) : CommandExecutor {

    private val WONG_ARGUMENTS_MSG = "Wrong arguments.\n"
    private val FILE_LOADING_ERROR_MSG = "Cannot load the file.\n"
    private val NOT_LOADED_ERROR_MSG = "File isn't loaded. Use \"load <path>\" to load a file.\n"
    private val NOT_RUNNING_ERROR_MSG = "The execution wasn't started or already ended. Use \"run\" to start the execution.\n"
    private val PARSER_ERROR_MSG = "Cannot parse the file.\n"
    private val EXECUTION_ERROR_MSG = "Execution error.\n"
    private val UNKNOWN_COMMAND_MSG = "Unknown command.\n"
    private val COMMON_ERROR_MSG = "Error.\n"

    private var debugger: Debugger? = null

    private fun throwIfNotLoaded() {
        if (debugger == null) {
            throw FileIsNotLoadedException()
        }
    }

    fun run() {
        while (true) {
            output.flush()

            try {
                val line = input.readLine()
                val command: Command = line.split(' ')[0].toCommand()
                val arguments: String = line.split(' ').drop(1).joinToString(" ")
                command.acceptExecutor(this, arguments)
            } catch (e: WrongArgumentsException) {
                output.write(WONG_ARGUMENTS_MSG)
            } catch (e: IOException) {
                output.write(FILE_LOADING_ERROR_MSG)
            } catch (e: FileIsNotLoadedException) {
                output.write(NOT_LOADED_ERROR_MSG)
            } catch (e: DebuggerIsNotRunningException) {
                output.write(NOT_RUNNING_ERROR_MSG)
            } catch (e: ParseCancellationException) {
                output.write(PARSER_ERROR_MSG)
            } catch (e: LanguageRuntimeException) {
                output.write(EXECUTION_ERROR_MSG)
            } catch (e: UnknownCommandException) {
                output.write(UNKNOWN_COMMAND_MSG)
            } catch (e: Exception) {
                output.write(COMMON_ERROR_MSG)
            }

            /* try {
                 when (args[0]) {
                     "load" -> {
                         if (args.size != 2) { throw WrongArgumentsException() }
                         debugger = DebuggerImpl(String(Files.readAllBytes(Paths.get(args[1]))), output)
                     }
                     "breakpoint" -> {
                         if (args.size != 2) { throw WrongArgumentsException() }
                         throwIfNotLoaded()
                         debugger!!.addBreakpoint(args[1].toInt())
                     }
                     "run" -> {
                         if (args.size != 1) { throw WrongArgumentsException() }
                         throwIfNotLoaded()
                         debugger!!.run()
                     }
                     "line" -> {
                         if (args.size != 1) { throw WrongArgumentsException() }
                         throwIfNotLoaded()
                         output.write(debugger!!.line().toString())
                         output.newLine()
                     }
                     "continue" -> {
                         if (args.size != 1) { throw WrongArgumentsException() }
                         throwIfNotLoaded()
                         debugger!!.next()
                     }
                     "evaluate" -> {
                         if (args.size < 2) { throw WrongArgumentsException() }
                         throwIfNotLoaded()
                         val expression = args.drop(1).joinToString(" ")
                         output.write(debugger!!.evaluate(expression).toString())
                         output.newLine()
                     }
                     "condition" -> {
                         if (args.size < 3) { throw WrongArgumentsException() }
                         throwIfNotLoaded()
                         val line = args[1].toInt()
                         val expression = args.drop(2).joinToString(" ")
                         debugger!!.addConditionalBreakpoint(line, expression)
                     }
                     "list" -> {
                         if (args.size != 1) { throw WrongArgumentsException() }
                         throwIfNotLoaded()
                         debugger!!.list()
                                 .sortedBy(Pair<Int, String?>::first)
                                 .joinToString("\n") { (line, condition) ->
                                     "$line: " + (condition ?: "always")
                                 }
                                 .let { output.write(it) }
                         output.newLine()

                     }
                     "remove" -> {
                         if (args.size != 2) { throw WrongArgumentsException() }
                         throwIfNotLoaded()
                         debugger!!.removeBreakpoint(args[1].toInt())
                     }
                     "stop" -> {
                         if (args.size != 1) { throw WrongArgumentsException() }
                         debugger = null
                     }
                     else -> {
                         output.write("unknown command\n")
                     }
                 }

             }
             */
        }
    }


    override fun execute(command: Load, arguments: List<String>) {
        debugger = DebuggerImpl(String(Files.readAllBytes(Paths.get(arguments[0]))), output)
    }

    override fun execute(command: Breakpoint, arguments: List<String>) {
        throwIfNotLoaded()
        debugger!!.addBreakpoint(arguments[0].toInt())
    }

    override fun execute(command: Run, arguments: List<String>) {
        throwIfNotLoaded()
        debugger!!.run()
        if (!debugger!!.isRunning()) {
            debugger = null
        }
    }

    override fun execute(command: Line, arguments: List<String>) {
        throwIfNotLoaded()
        output.write(debugger!!.line().toString())
        output.newLine()
    }

    override fun execute(command: Continue, arguments: List<String>) {
        throwIfNotLoaded()
        debugger!!.next()
        if (!debugger!!.isRunning()) {
            debugger = null
        }
    }

    override fun execute(command: Evaluate, arguments: List<String>) {
        throwIfNotLoaded()
        val expression = arguments[0]
        output.write(debugger!!.evaluate(expression).toString())
        output.newLine()
    }

    override fun execute(command: Condition, arguments: List<String>) {
        throwIfNotLoaded()
        val line = arguments[0].toIntOrNull() ?: throw WrongArgumentsException()
        val expression = arguments[1]
        debugger!!.addConditionalBreakpoint(line, expression)
    }

    override fun execute(command: ListCommand, arguments: List<String>) {
        throwIfNotLoaded()
        debugger!!.list()
                .sortedBy(Pair<Int, String?>::first)
                .joinToString("\n") { (line, condition) ->
                    "$line: " + (condition ?: "always")
                }
                .let { output.write(it) }
        output.newLine()
    }

    override fun execute(command: Remove, arguments: List<String>) {
        throwIfNotLoaded()
        debugger!!.removeBreakpoint(arguments[0].toIntOrNull() ?: throw WrongArgumentsException())
    }

    override fun execute(command: Stop, arguments: List<String>) {
        debugger = null
    }
}