package com.space.assistant.service.search

import com.space.assistant.core.entity.CommandAlternative
import com.space.assistant.core.entity.JobActivatorInfo
import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.emptyJobResult
import com.space.assistant.core.service.JobActivator
import org.springframework.stereotype.Service

@Service
class CronJobActivator : JobActivator {
    companion object {
        const val typeName = "CRON"
    }

    data class Info(
            val cron: String,
            override val type: String = typeName
    ) : JobActivatorInfo

    override fun activateJob(command: CommandAlternative): JobInfo? {
        return null
    }
}
