package com.space.assistant.service.search

import com.space.assistant.core.entity.CommandAlternative
import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.JobFinderInfo
import com.space.assistant.core.service.JobSearchProvider
import org.springframework.stereotype.Service


@Service
class EmptyJobFinder : JobSearchProvider {
    companion object {
        const val typeName = "EMPTY"
    }

    data class Info(
            override val type: String = typeName
    ) : JobFinderInfo

    override fun findJob(command: CommandAlternative): JobInfo? = null
}
