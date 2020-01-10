package com.space.assistant.service.runner

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobExecType
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.WinCmdJobExecInfo
import com.space.assistant.core.service.JobRunner
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class WinCmdJobRunner : JobRunner {

    override fun runJob(activeJobInfo: ActiveJobInfo): Mono<JobResult> {
        if (!canRun(activeJobInfo)) return Mono.empty()

        val command = activeJobInfo.prevActiveJobInfo?.jobResult?.value
                ?: (activeJobInfo.jobInfo?.execInfo as? WinCmdJobExecInfo)?.cmd
                ?: return Mono.empty()

        return Mono.create {
            val process = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler $command")
            val pid = process.pid().toString()
            val result = JobResult(pid)
            it.success(result)
        }
    }


    private fun canRun(activeJobInfo: ActiveJobInfo) = activeJobInfo.jobInfo?.execInfo?.type == JobExecType.WIN_CMD
}
