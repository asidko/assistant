package com.space.assistant.service.runner

import com.space.assistant.core.entity.JobExecType
import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.WinCmdJobExecInfo
import com.space.assistant.core.service.JobRunner
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class WinCmdJobRunner : JobRunner {

    override fun runJob(jobInfo: JobInfo, previousJobResult: JobResult?): Mono<JobResult> {
        if (!canRun(jobInfo)) return Mono.empty()

        val command = previousJobResult?.result ?: (jobInfo.execInfo as WinCmdJobExecInfo).cmd

        return Mono.create {
            val process = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler $command")
            val pid = process.pid().toString()

            val result = previousJobResult?.copy(result = pid)
                    ?: JobResult.new(pid, jobInfo)
            it.success(result)
        }
    }


    private fun canRun(jobInfo: JobInfo) = jobInfo.execInfo.type == JobExecType.WIN_CMD
}
