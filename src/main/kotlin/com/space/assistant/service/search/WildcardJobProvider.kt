package com.space.assistant.service.search

import com.space.assistant.core.entity.CommandAlternative
import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.JobSearchType
import com.space.assistant.core.entity.WildcardJobSearchInfo
import com.space.assistant.core.service.JobProvider
import com.space.assistant.core.service.JobRepository
import org.springframework.stereotype.Service

@Service
class WildcardJobProvider(
        private val jobRepository: JobRepository
) : JobProvider {

    override fun findJob(command: CommandAlternative): JobInfo? {
        val phrase = command.alternativePhrase
        if (phrase.isEmpty()) return null
        val phraseText = phrase.joinToString(" ")

        val allWildcardJobs = jobRepository.findJobsBySearchType(JobSearchType.WILDCARD)

        for (job in allWildcardJobs) {
            if (job.searchInfo is WildcardJobSearchInfo) {
                val wildcardText = job.searchInfo.text
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
