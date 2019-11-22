package com.space.assistant.service

import com.space.assistant.core.entity.CommandAlternative
import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.service.JobProvider
import com.space.assistant.core.service.JobRepository
import org.springframework.stereotype.Service

@Service
class RepositoryJobProvider(
        private val jobRepository: JobRepository
) : JobProvider {

    override fun findJob(command: CommandAlternative): JobInfo? =
            jobRepository.findJobByPhrase(command.alternativePhrase)
}
