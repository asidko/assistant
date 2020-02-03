package com.space.assistant.service.parser

import com.jayway.jsonpath.JsonPath
import com.space.assistant.core.entity.*
import com.space.assistant.core.service.JobResultParser
import com.space.assistant.service.text.VariablesInTextReplacer
import org.springframework.stereotype.Service

@Service
class JsonPathJobResultParser(
        val variablesInTextReplacer: VariablesInTextReplacer
) : JobResultParser {
    companion object {
        const val typeName = "JSON_PATH"
    }

    data class Info(
            val jsonPathValues: List<String>,
            val resultFormatString: String,
            override val type: String = typeName
    ) : JobResultParserInfo

    override suspend fun parseResult(activeJobInfo: ActiveJobInfo): JobResult? {
        val resultParseInfo = activeJobInfo.jobInfo?.resultParserInfo as? Info ?: return null

        val jobRawResult = activeJobInfo.jobRawResult ?: return emptyJobResult

        val json = jobRawResult.value
        val jsonPathList = resultParseInfo.jsonPathValues
        val resultFormatString = resultParseInfo.resultFormatString

        val jsonPathValues = jsonPathList.map { path -> JsonPath.read<Any>(json, path) }

        val resultString = variablesInTextReplacer.replacePattern(resultFormatString, jsonPathValues)

        return JobResult(resultString)
    }
}
