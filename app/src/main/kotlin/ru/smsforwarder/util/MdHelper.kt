package ru.smsforwarder.util

fun String.escapeMdV2(): String {
    val itemsToEscape = listOf(
        '_',
        '*',
        '[',
        ']',
        '(',
        ')',
        '~',
        '`',
        '>',
        '#',
        '+',
        '-',
        '=',
        '|',
        '{',
        '}',
        '.',
        '!'
    )
    var resultString = this
    itemsToEscape.forEach { char ->
        resultString = resultString.replace(char.toString(), "\\$char")
    }
    return resultString
}

fun String.highlightNumberSequence(minLength: Int = 4): String {
    val regex = "[\\d]{$minLength,}".toRegex()
    return this.replace(regex) { sequence -> "`${sequence.value}`" }
}
