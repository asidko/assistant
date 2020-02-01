package com.space.assistant.service.runner

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.WinCmdJobExecInfo
import com.space.assistant.core.service.JobRunner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class WinCmdJobRunner : JobRunner {

    override suspend fun runJob(activeJobInfo: ActiveJobInfo): JobResult? {
        val execInfo = activeJobInfo.jobInfo?.execInfo as? WinCmdJobExecInfo ?: return null

        val prevJobResult = activeJobInfo.prevActiveJobInfo?.jobResult
        val command = prevJobResult?.value
                ?: (execInfo as? WinCmdJobExecInfo)?.cmd
                ?: return null

        withContext(Dispatchers.IO) {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler $command")
        }

        return JobResult("")
    }

}
