package com.space.assistant.service.runner

import com.space.assistant.core.entity.*
import com.space.assistant.core.service.JobRunner
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class WildcardJobRunner : JobRunner {

    val expressionVariableRegex = "\\$\\d+".toRegex()

    override fun runJob(runJobInfo: RunJobInfo): Mono<JobResult> {
        if (!canRun(runJobInfo.jobInfo)) return Mono.empty()

        return Mono.create { mono ->

            val wildcardPattern = (runJobInfo.jobInfo.execInfo as WildcardJobExecInfo).text
                    .ifEmpty { (runJobInfo.jobInfo.searchInfo as? WildcardJobSearchInfo)?.text ?: "" }

            val wildcardRegex = wildcardPattern
                    .map { escapeCharForRegexp(it) }
                    .joinToString("")
                    .toRegex()

            val commandText = runJobInfo.previousJobResult?.result
                    ?: runJobInfo.command.alternativePhrase.joinToString(" ")

            val args = findRegexGroupValues(wildcardRegex, commandText) ?: emptyList()

            val resultExpression = runJobInfo.jobInfo.execInfo.expression

            val resultString = if (resultExpression.isNotEmpty())
                replaceVariablesByArgs(resultExpression, args)
            else
                args.joinToString(",")

            val result = runJobInfo.previousJobResult?.copy(result = resultString)
                    ?: JobResult.new(resultString, runJobInfo.jobInfo)

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


    private fun canRun(jobInfo: JobInfo) = jobInfo.execInfo.type == JobExecType.WILDCARD
}
