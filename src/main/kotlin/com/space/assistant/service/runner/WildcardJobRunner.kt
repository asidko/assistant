package com.space.assistant.service.runner

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobRunnerInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.service.JobRunner
import com.space.assistant.service.search.WildcardJobFinder
import org.springframework.stereotype.Service

@Service
class WildcardJobRunner : JobRunner {

    val expressionVariableRegex = "\\$\\d+".toRegex()

    companion object {
        const val typeName = "WILDCARD"
    }

    data class Info(
            val wildcardText: String,
            val resultText: String,
            override val type: String = typeName
    ) : JobRunnerInfo

    override suspend fun runJob(activeJobInfo: ActiveJobInfo): JobResult? {
        val execInfo = activeJobInfo.jobInfo?.runnerInfo as? Info ?: return null

        val prevJobResult = activeJobInfo.prevActiveJobInfo?.jobResult
        val commandText = prevJobResult?.value
                ?: activeJobInfo.commandAlternativeSucceed?.alternativePhrase?.joinToString(" ")
                ?: return null

        val wildcardSearchText = (activeJobInfo.jobInfo.finderInfo
                as? WildcardJobFinder.Info)?.text ?: ""
        val wildcardPattern = execInfo.wildcardText.ifEmpty { wildcardSearchText }
        val wildcardRegex = wildcardPattern
                .map { it.escapeForRegexp() }
                .joinToString("")
                .toRegex()

        val wildcardValues = findRegexGroupValues(wildcardRegex, commandText)

        val resultString =
                if (execInfo.resultText.isNotEmpty()) replaceVariablesByArgs(execInfo.resultText, wildcardValues)
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
