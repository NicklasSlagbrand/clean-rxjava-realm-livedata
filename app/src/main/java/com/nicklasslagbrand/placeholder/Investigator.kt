package com.nicklasslagbrand.placeholder

import timber.log.Timber

/**
 * Logs the calling instance and method name.
 *
 *
 * **Example**
 * <br></br>Code:
 * <br></br>`Investigator.log(this);`
 * <br></br>Log:
 * <br></br>`D/Investigator: MainActivity@788dc5c.onCreate()`
 *
 * @param instance the calling object instance
 */
fun log(instance: Any) {
    Investigator.doLog(instance)
}

/**
 * Logs the calling instance and method name, and the comment.
 *
 *
 * **Example**
 * <br></br>Code:
 * <br></br>`Investigator.log(this, "some comment");`
 * <br></br>Log:
 * <br></br>`D/Investigator: MainActivity@788dc5c.onCreate() | some comment`
 *
 * @param instance the calling object instance
 * @param comment extra comment message
 */
fun log(instance: Any, comment: String) {
    Investigator.doLog(instance, comment = comment)
}

/**
 * Logs the calling instance and method name, and the variable names and values.
 *
 *
 * **Example**
 * <br></br>Code:
 * <br></br>`Investigator.log(this, "fruit", fruit);`
 * <br></br>`Investigator.log(this, "fruit", fruit, "color", color);`
 * <br></br>Log:
 * <br></br>`D/Investigator: MainActivity@788dc5c.onCreate() | fruit = cherry`
 * <br></br>`D/Investigator: MainActivity@788dc5c.onCreate() | fruit = cherry | color = red`
 *
 * @param instance the calling object instance
 * @param variableNamesAndValues variable name and value pairs
 */
fun log(instance: Any, vararg variableNamesAndValues: Any) {
    Investigator.doLog(instance, namesAndValues = variableNamesAndValues)
}

class Investigator {
    companion object {
        var customLogger: InvestigatorLogger = object : InvestigatorLogger {
            override fun logMessage(message: String) {
                Timber.d(message)
            }
        }

        private const val anonymousClassHighlightWord = "_ANONYMOUS_"

        private const val STACKTRACE_INDEX_OF_CALLING_METHOD =
            3 // fixed value, need to update only if the 'location' of the stack trace fetching code changes
        private const val ANONYMOUS_CLASS_TO_STRING_SYMBOL = "$"

        internal fun doLog(instance: Any, comment: String = "", namesAndValues: Array<out Any> = emptyArray()) {
            val stackTrace = Exception().stackTrace
            val msg = StringBuilder()

            msg.append(threadName())

            msg.append(instanceAndMethodName(instance, stackTrace))

            if (comment.isNotEmpty()) {
                msg.append(commentMessage(comment))
            }

            if (namesAndValues.isNotEmpty()) {
                msg.append(variablesMessage(namesAndValues))
            }

            customLogger.logMessage(msg.toString())
        }

        private fun instanceAndMethodName(instance: Any, stackTrace: Array<StackTraceElement>): String {
            val methodName = stackTrace[STACKTRACE_INDEX_OF_CALLING_METHOD].methodName
            var instanceName = instance.toString()

            instanceName = removePackageName(instanceName)

            instanceName = checkAndHighlightAnonymousClass(instanceName)

            return "$instanceName.$methodName()"
        }

        private fun removePackageName(instanceName: String): String {
            val lastDotIndex = instanceName.lastIndexOf(".")

            return if (lastDotIndex == -1) {
                instanceName
            } else {
                instanceName.substring(lastDotIndex + 1)
            }
        }

        private fun checkAndHighlightAnonymousClass(instanceName: String): String {
            val symbolIndex = instanceName.indexOf(ANONYMOUS_CLASS_TO_STRING_SYMBOL)
            val hasSymbolPlusDigit =
                symbolIndex > 0 &&
                    instanceName.length > symbolIndex + 1 &&
                    Character.isDigit(instanceName[symbolIndex + 1])

            return if (hasSymbolPlusDigit) {
                StringBuilder(instanceName)
                    .deleteCharAt(symbolIndex)
                    .insert(symbolIndex, anonymousClassHighlightWord)
                    .toString()
            } else {
                instanceName
            }
        }

        private fun commentMessage(comment: String) = " | $comment"

        private fun threadName() = "[${Thread.currentThread().name}] "

        private fun variablesMessage(variableNamesAndValues: Array<out Any>): StringBuilder {
            if (variableNamesAndValues.size % 2 != 0) {
                throw IllegalArgumentException(
                    "Missed to add variable names and values in pairs? " +
                        "There has to be an even number of the 'variableNamesAndValues' varargs parameters)."
                )
            }

            val result = StringBuilder()
            variableNamesAndValues.toList().chunked(2).map {
                Pair(it[0], it[1])
            }.forEach {
                val variableName = it.first
                val variableValue = it.second
                val variableMessage = " | $variableName = $variableValue"

                result.append(variableMessage)
            }

            return result
        }
    }
}

interface InvestigatorLogger {
    fun logMessage(message: String)
}
