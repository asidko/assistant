package com.space.assistant.core.entity

import java.util.*

typealias Phrase = List<String>

class InputCommand(
        val uuid: String = UUID.randomUUID().toString(),
        val phrase: Phrase = emptyList(),
        val alternativePhrases: List<Phrase> = emptyList()
) {
    companion object
}

class CommandAlternative(
        val uuid: String = UUID.randomUUID().toString(),
        val inputCommandUUID: UUID,
        val alternativePhrase: Phrase
)

fun InputCommand.Companion.fromText(text: String) = InputCommand(phrase = text.split("\\s"))
