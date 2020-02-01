package com.space.assistant.core.entity

import com.space.assistant.core.util.simpleID

typealias Phrase = List<String>

private val spaceRegex = "\\s".toRegex()

data class InputCommand(
        val uuid: String = simpleID(),
        val phrase: Phrase = emptyList(),
        val alternativePhrases: List<Phrase> = emptyList()
) {
    companion object
}

data class CommandAlternative(
        val uuid: String = simpleID(),
        val inputCommandUUID: String,
        val alternativePhrase: Phrase
)

fun InputCommand.Companion.fromText(text: String): InputCommand {
    return InputCommand(phrase = tokenizeText(text))
}

fun InputCommand.Companion.fromTexts(texts: List<String>): InputCommand = when {
    texts.isEmpty() -> InputCommand.fromText("")
    texts.size == 1 -> InputCommand.fromText(texts[0])
    else -> texts
            .map { tokenizeText(it) }
            .let { phrases -> InputCommand(phrase = phrases[0], alternativePhrases = phrases.subList(1, phrases.size)) }
}

private fun tokenizeText(text: String): Phrase = text.trim().split(spaceRegex)
