package ru.spbau.mit.exceptions


sealed class DebuggerException : Exception()

class DebuggerIsNotRunningException : DebuggerException()

class WrongArgumentsException : DebuggerException()

class FileIsNotLoadedException : DebuggerException()

class UnknownCommandException : DebuggerException()
