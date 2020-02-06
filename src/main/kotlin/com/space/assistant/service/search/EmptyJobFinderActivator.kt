package com.space.assistant.service.search

import com.space.assistant.core.entity.CommandAlternative
import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.JobActivatorInfo
import com.space.assistant.core.service.JobActivator
import org.springframework.stereotype.Service


@Service
class EmptyJobFinderActivator : JobActivator {
    companion object {
        const val typeName = "EMPTY"
    }

    data class Info(
            override val type: String = typeName
    ) : JobActivatorInfo

    override fun activateJob(command: CommandAlternative): JobInfo? = null
}
