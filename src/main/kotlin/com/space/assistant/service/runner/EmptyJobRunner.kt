package com.space.assistant.service.runner

import com.space.assistant.core.entity.*
import com.space.assistant.core.service.JobRunner
import org.springframework.stereotype.Service

@Service
class EmptyJobRunner : JobRunner {

    companion object {
        const val typeName = "EMPTY"
    }

    data class Info(
            override val type: String = typeName
    ) : JobExecInfo

    override suspend fun runJob(activeJobInfo: ActiveJobInfo): JobResult? {
        if (activeJobInfo.jobInfo?.execInfo !is Info) return null
        return emptyJobResult
    }
}
