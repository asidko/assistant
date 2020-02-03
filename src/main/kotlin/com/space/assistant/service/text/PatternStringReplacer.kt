package com.space.assistant.service.text

import com.space.assistant.core.service.StringProcessor
import com.space.assistant.service.util.applyAsPipe
import org.springframework.stereotype.Service

@Service
class PatternStringReplacer(
        val stringProcessors: List<StringProcessor>
) {
    fun replacePattern(pattern: String, values: List<Any>?): String {
        if (values == null || values.isEmpty()) return pattern

        var resultString = pattern
        for (i in 0..values.lastIndex)
            resultString = resultString.replace("$${i + 1}", values[i].toString())

        resultString = stringProcessors.applyAsPipe(resultString)

        return resultString
    }
}
