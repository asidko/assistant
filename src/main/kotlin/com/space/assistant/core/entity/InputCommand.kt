package com.space.assistant.core.entity

import java.util.*

typealias Phrase = List<String>
private val spaceRegex = "\\s".toRegex()

data class InputCommand(
        val uuid: String = UUID.randomUUID().toString(),
        val phrase: Phrase = emptyList(),
        val alternativePhrases: List<Phrase> = emptyList()
) {
    companion object
}

data class CommandAlternative(
        val uuid: String = UUID.randomUUID().toString(),
        val inputCommandUUID: String,
        val alternativePhrase: Phrase
)

fun InputCommand.Companion.fromText(text: String): InputCommand {
    return InputCommand(phrase = text.split(spaceRegex))
}
