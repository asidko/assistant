package com.space.assistant.service.search

import com.space.assistant.core.entity.CommandAlternative
import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.JobActivatorInfo
import com.space.assistant.core.service.JobRepository
import com.space.assistant.core.service.JobActivator
import org.springframework.stereotype.Service


@Service
class DirectMatchJobActivator(
        private val jobRepository: JobRepository
) : JobActivator {

    companion object {
        const val typeName = "WILDCARD"
    }

    data class Info(
            val texts: List<String>,
            override val type: String = typeName
    ) : JobActivatorInfo

    override fun activateJob(command: CommandAlternative): JobInfo? =
            jobRepository.findJobByPhrase(command.alternativePhrase)
}
