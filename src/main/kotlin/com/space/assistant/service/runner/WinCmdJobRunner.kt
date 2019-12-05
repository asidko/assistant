package com.space.assistant.service.runner

import com.space.assistant.core.entity.*
import com.space.assistant.core.service.JobRunner
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class WinCmdJobRunner : JobRunner {

    override fun runJob(runJobInfo: RunJobInfo): Mono<JobResult> {
        if (!canRun(runJobInfo.jobInfo)) return Mono.empty()

        val command = runJobInfo.previousJobResult?.result ?: (runJobInfo.jobInfo.execInfo as WinCmdJobExecInfo).cmd

        return Mono.create {
            val process = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler $command")
            val pid = process.pid().toString()

            val result = runJobInfo.previousJobResult?.copy(result = pid)
                    ?: JobResult.new(pid, runJobInfo.jobInfo)
            it.success(result)
        }
    }


    private fun canRun(jobInfo: JobInfo) = jobInfo.execInfo.type == JobExecType.WIN_CMD
}
