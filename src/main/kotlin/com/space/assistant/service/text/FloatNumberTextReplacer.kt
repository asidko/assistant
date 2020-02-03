package com.space.assistant.service.text

import com.space.assistant.core.service.TextReplacer
import org.springframework.stereotype.Service
import kotlin.math.roundToInt

@Service
class FloatNumberTextReplacer : TextReplacer {
    private val decimalNumberRegex = "-?\\d*\\.\\d+".toRegex()

    override fun apply(text: String) = text.replace(decimalNumberRegex) { matchResult ->
        matchResult.value
                // Round to 2 decimals
                .let { num -> ((num.toFloat() * 100).roundToInt() / 100.0).toString() }
    }
}
