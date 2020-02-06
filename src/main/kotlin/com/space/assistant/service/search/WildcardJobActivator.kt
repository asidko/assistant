package com.space.assistant.service.search

import com.space.assistant.core.entity.CommandAlternative
import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.JobActivatorInfo
import com.space.assistant.core.service.JobRepository
import com.space.assistant.core.service.JobActivator
import org.springframework.stereotype.Service

@Service
class WildcardJobActivator(
        private val jobRepository: JobRepository
) : JobActivator {

    companion object {
        const val typeName = "WILDCARD"
    }

    data class Info(
            val text: String,
            override val type: String = typeName
    ) : JobActivatorInfo

    override fun activateJob(command: CommandAlternative): JobInfo? {
        val phrase = command.alternativePhrase
        if (phrase.isEmpty()) return null
        val phraseText = phrase.joinToString(" ")

        val allWildcardJobs = jobRepository.findJobsBySearchType(typeName)

        for (job in allWildcardJobs) {
            if (job.activatorInfo is Info) {
                val wildcardText = job.activatorInfo.text
                val wildcardRegexp = wildcardText
                        .map { if (it != '*') "\\" + it else "(.+?)" }
                        .joinToString("")

                val isJobFound = Regex(wildcardRegexp).matches(phraseText)

                if (isJobFound) return job
            }
        }

        return null
    }
}
