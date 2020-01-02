package com.space.assistant.service.text

import com.space.assistant.config.LangConstants
import com.space.assistant.core.service.StringProcessor
import org.springframework.stereotype.Service
import kotlin.math.roundToInt

@Suppress("NestedLambdaShadowedImplicitParameter")
@Service
class NumberTextProcessor : StringProcessor {
    private val decimalNumberRegex = "-?\\d*\\.\\d+".toRegex()

    override fun apply(text: String) = text.replace(decimalNumberRegex) {
        it.value
                // Round to 2 decimals
                .let { ((it.toFloat() * 100).roundToInt() / 100.0).toString() }
                // Replace . in number on 'and' word
                .replace(".", " ${LangConstants.end} ")
    }
}
