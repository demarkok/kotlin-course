package ru.spbau.mit.debug

import ru.spbau.mit.exceptions.UnknownCommandException
import ru.spbau.mit.exceptions.WrongArgumentsException

interface CommandExecutor {
    fun execute(command: Load, arguments: List<String>)
    fun execute(command: Breakpoint, arguments: List<String>)
    fun execute(command: Run, arguments: List<String>)
    fun execute(command: Line, arguments: List<String>)
    fun execute(command: Continue, arguments: List<String>)
    fun execute(command: Evaluate, arguments: List<String>)
    fun execute(command: Condition, arguments: List<String>)
    fun execute(command: ListCommand, arguments: List<String>)
    fun execute(command: Remove, arguments: List<String>)
    fun execute(command: Stop, arguments: List<String>)
}

interface Command {
    fun acceptExecutor(executor: CommandExecutor, arguments: String)
}

fun String.toCommand(): Command {
    return when (this) {
        "load" -> Load
        "breakpoint" -> Breakpoint
        "run" -> Run
        "line" -> Line
        "continue" -> Continue
        "evaluate" -> Evaluate
        "condition" -> Condition
        "list" -> ListCommand
        "remove" -> Remove
        "stop" -> Stop
        else -> throw UnknownCommandException()
    }
}

object Load : Command {
    override fun acceptExecutor(executor: CommandExecutor, arguments: String) {
        executor.execute(this, listOf(arguments))
    }
}

object Breakpoint : Command {
    override fun acceptExecutor(executor: CommandExecutor, arguments: String) {
        executor.execute(this, listOf(arguments))
    }
}

object Run : Command {
    override fun acceptExecutor(executor: CommandExecutor, arguments: String) {
        executor.execute(this, emptyList())
    }
}

object Line : Command {
    override fun acceptExecutor(executor: CommandExecutor, arguments: String) {
        executor.execute(this, emptyList())
    }
}

object Continue : Command {
    override fun acceptExecutor(executor: CommandExecutor, arguments: String) {
        executor.execute(this, emptyList())
    }
}

object Evaluate : Command {
    override fun acceptExecutor(executor: CommandExecutor, arguments: String) {
        executor.execute(this, listOf(arguments))
    }
}

object Condition : Command {
    override fun acceptExecutor(executor: CommandExecutor, arguments: String) {
        if (arguments.split(' ').size < 2) {
            throw WrongArgumentsException()
        }
        executor.execute(this, arguments.split(' ').let { listOf(it[0], it.drop(1).joinToString(" ")) })
    }
}

object ListCommand : Command {
    override fun acceptExecutor(executor: CommandExecutor, arguments: String) {
        executor.execute(this, emptyList())
    }
}

object Remove : Command {
    override fun acceptExecutor(executor: CommandExecutor, arguments: String) {
        executor.execute(this, listOf(arguments))
    }
}

object Stop : Command {
    override fun acceptExecutor(executor: CommandExecutor, arguments: String) {
        executor.execute(this, emptyList())
    }
}
