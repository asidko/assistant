package com.space.assistant.service.runner

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobExecInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.service.JobRunner
import com.space.assistant.service.search.WildcardJobSearchProvider
import org.springframework.stereotype.Service

@Service
class WildcardJobRunner : JobRunner {

    val expressionVariableRegex = "\\$\\d+".toRegex()

    companion object {
        const val typeName = "WILDCARD"
    }

    data class Info(
            val pattern: String,
            val resultExpression: String,
            override val type: String = typeName
    ) : JobExecInfo

    override suspend fun runJob(activeJobInfo: ActiveJobInfo): JobResult? {
        val execInfo = activeJobInfo.jobInfo?.execInfo as? Info ?: return null

        val prevJobResult = activeJobInfo.prevActiveJobInfo?.jobResult
        val commandText = prevJobResult?.value
                ?: activeJobInfo.commandAlternativeSucceed?.alternativePhrase?.joinToString(" ")
                ?: return null

        val wildcardSearchText = (activeJobInfo.jobInfo.searchInfo
                as? WildcardJobSearchProvider.Info)?.text ?: ""
        val wildcardPattern = execInfo.pattern.ifEmpty { wildcardSearchText }
        val wildcardRegex = wildcardPattern
                .map { it.escapeForRegexp() }
                .joinToString("")
                .toRegex()

        val wildcardValues = findRegexGroupValues(wildcardRegex, commandText)

        val resultString =
                if (execInfo.resultExpression.isNotEmpty()) replaceVariablesByArgs(execInfo.resultExpression, wildcardValues)
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
