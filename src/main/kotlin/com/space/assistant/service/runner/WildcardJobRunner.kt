package com.space.assistant.service.runner

import com.space.assistant.core.entity.*
import com.space.assistant.core.service.JobRunner
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class WildcardJobRunner : JobRunner {

    val expressionVariableRegex = "\\$\\d+".toRegex()

    override fun runJob(activeJobInfo: ActiveJobInfo): Mono<JobResult> {
        if (!canRun(activeJobInfo)) return Mono.empty()

        val commandText = activeJobInfo.prevActiveJobInfo?.jobResult?.value
                ?: activeJobInfo.commandAlternativeSucceed?.alternativePhrase?.joinToString(" ")
                ?: return Mono.empty()


        return Mono.create { mono ->

            val execInfoText = (activeJobInfo.jobInfo?.execInfo as? WildcardJobExecInfo)?.text ?: ""
            val execInfoExpression = (activeJobInfo.jobInfo?.execInfo as? WildcardJobExecInfo)?.expression ?: ""
            val searchInfoText = (activeJobInfo.jobInfo?.searchInfo as? WildcardJobSearchInfo)?.text ?: ""

            val wildcardPattern = execInfoText.ifEmpty { searchInfoText }

            val wildcardRegex = wildcardPattern
                    .map { escapeCharForRegexp(it) }
                    .joinToString("")
                    .toRegex()

            val args = findRegexGroupValues(wildcardRegex, commandText) ?: emptyList()


            val resultString =
                    if (execInfoExpression.isNotEmpty()) replaceVariablesByArgs(execInfoExpression, args)
                    else args.joinToString(",")

            val result = JobResult(resultString)
            mono.success(result)
        }
    }

    private fun replaceVariablesByArgs(resultExpression: String, args: List<String>): String {
        return resultExpression.replace(expressionVariableRegex) { matchResult ->
            val expressionVariable = matchResult.value.removeRange(0, 1).toInt() // remove $ char
            args[expressionVariable - 1]
        }
    }

    private fun findRegexGroupValues(wildcardRegex: Regex, text: String): List<String>? {
        return wildcardRegex.find(text)?.groupValues?.drop(1)
    }

    private fun escapeCharForRegexp(it: Char) =
            if (it != '*') "\\$it" else "(.+)"


    private fun canRun(activeJobInfo: ActiveJobInfo) = activeJobInfo.jobInfo?.execInfo?.type == JobExecType.WILDCARD
}
