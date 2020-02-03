package com.space.assistant.service.runner

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.WildcardJobExecInfo
import com.space.assistant.core.service.JobRunner
import com.space.assistant.service.search.WildcardJobSearchProvider
import org.springframework.stereotype.Service

@Service
class WildcardJobRunner : JobRunner {

    val expressionVariableRegex = "\\$\\d+".toRegex()

    override suspend fun runJob(activeJobInfo: ActiveJobInfo): JobResult? {
        val execInfo = activeJobInfo.jobInfo?.execInfo as? WildcardJobExecInfo ?: return null

        val prevJobResult = activeJobInfo.prevActiveJobInfo?.jobResult
        val commandText = prevJobResult?.value
                ?: activeJobInfo.commandAlternativeSucceed?.alternativePhrase?.joinToString(" ")
                ?: return null

        val wildcardSearchText = (activeJobInfo.jobInfo.searchInfo
                as? WildcardJobSearchProvider.Info)?.text ?: ""
        val wildcardPattern = execInfo.text.ifEmpty { wildcardSearchText }
        val wildcardRegex = wildcardPattern
                .map { it.escapeForRegexp() }
                .joinToString("")
                .toRegex()

        val wildcardValues = findRegexGroupValues(wildcardRegex, commandText)

        val resultString =
                if (execInfo.expression.isNotEmpty()) replaceVariablesByArgs(execInfo.expression, wildcardValues)
                else wildcardValues.joinToString(",")

        return JobResult(resultString)
    }


    private fun replaceVariablesByArgs(resultExpression: String, args: List<String>): String {
        return resultExpression.replace(expressionVariableRegex) { matchResult ->
            val expressionVariable = matchResult.value.removeRange(0, 1).toInt() // remove $ char
            args[expressionVariable - 1]
        }
    }

    private fun findRegexGroupValues(wildcardRegex: Regex, text: String): List<String> =
            wildcardRegex.find(text)?.groupValues?.drop(1) ?: emptyList()

    private fun Char.escapeForRegexp() = if (this != '*') "\\$this" else "(.+)"
}
