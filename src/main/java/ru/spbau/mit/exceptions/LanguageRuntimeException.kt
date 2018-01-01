package ru.spbau.mit.exceptions

sealed class LanguageRuntimeException : Exception()

class UnexpectedReturnException : LanguageRuntimeException()

class RedeclarationException : LanguageRuntimeException()

class VariableIsNotDefinedException : LanguageRuntimeException()

class FunctionIsNotDefinedException : LanguageRuntimeException()
