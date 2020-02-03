package com.space.assistant.service.search

import com.space.assistant.core.entity.CommandAlternative
import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.JobFinderInfo
import com.space.assistant.core.service.JobRepository
import com.space.assistant.core.service.JobSearchProvider
import org.springframework.stereotype.Service

@Service
class WildcardJobFinder(
        private val jobRepository: JobRepository
) : JobSearchProvider {

    companion object {
        const val typeName = "WILDCARD"
    }

    data class Info(
            val text: String,
            override val type: String = typeName
    ) : JobFinderInfo

    override fun findJob(command: CommandAlternative): JobInfo? {
        val phrase = command.alternativePhrase
        if (phrase.isEmpty()) return null
        val phraseText = phrase.joinToString(" ")

        val allWildcardJobs = jobRepository.findJobsBySearchType(typeName)

        for (job in allWildcardJobs) {
            if (job.finderInfo is Info) {
                val wildcardText = job.finderInfo.text
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
