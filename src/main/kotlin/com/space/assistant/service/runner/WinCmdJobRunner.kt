package com.space.assistant.service.runner

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobExecInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.service.JobRunner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class WinCmdJobRunner : JobRunner {

    companion object {
        const val typeName = "WIN_CMD"
    }

    data class Info(
            val cmd: String,
            override val type: String = typeName
    ) : JobExecInfo

    override suspend fun runJob(activeJobInfo: ActiveJobInfo): JobResult? {
        val execInfo = activeJobInfo.jobInfo?.execInfo as? Info ?: return null

        val prevJobResult = activeJobInfo.prevActiveJobInfo?.jobResult
        val command = prevJobResult?.value
                ?: (execInfo as? Info)?.cmd
                ?: return null

        return withContext(Dispatchers.IO) {
            val process = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler $command")

            val pid = process.pid()

            JobResult(pid.toString())
        }

    }

}
