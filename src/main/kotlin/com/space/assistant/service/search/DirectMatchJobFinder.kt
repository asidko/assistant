package com.space.assistant.service.search

import com.space.assistant.core.entity.CommandAlternative
import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.JobFinderInfo
import com.space.assistant.core.service.JobRepository
import com.space.assistant.core.service.JobSearchProvider
import org.springframework.stereotype.Service


@Service
class DirectMatchJobFinder(
        private val jobRepository: JobRepository
) : JobSearchProvider {

    companion object {
        const val typeName = "WILDCARD"
    }

    data class Info(
            val texts: List<String>,
            override val type: String = typeName
    ) : JobFinderInfo

    override fun findJob(command: CommandAlternative): JobInfo? =
            jobRepository.findJobByPhrase(command.alternativePhrase)
}
