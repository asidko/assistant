package com.space.assistant.service

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
                val wildcardIndex = wildcardText.indexOf("*")
                val prefix = wildcardText.substring(0, wildcardIndex)
                val suffix = wildcardText.subSequence(wildcardIndex + 1, wildcardText.length)

                val isJobFound = phraseText.startsWith(prefix) && phraseText.endsWith(suffix)
                if (isJobFound) return job
            }
        }

        return null
    }
}
