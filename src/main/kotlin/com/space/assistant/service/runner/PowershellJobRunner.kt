package com.space.assistant.service.runner

import com.profesorfalken.jpowershell.PowerShell
import com.space.assistant.core.entity.*
import com.space.assistant.core.service.JobRunner
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class PowershellJobRunner : JobRunner {

    override fun runJob(runJobInfo: RunJobInfo): Mono<JobResult> {
        if (!canRun(runJobInfo.jobInfo)) return Mono.empty()

        val command = runJobInfo.previousJobResult?.result ?: (runJobInfo.jobInfo.execInfo as PowerShellJobExecInfo).cmd

        return Mono.create {
            val shellResponse = PowerShell.openSession().executeCommand(command)

            val result = runJobInfo.previousJobResult?.copy(result = shellResponse.commandOutput)
                    ?: JobResult.new(shellResponse.commandOutput ?: "", runJobInfo.jobInfo)

            it.success(result)
        }
    }


    private fun canRun(jobInfo: JobInfo) = jobInfo.execInfo.type == JobExecType.POWERSHELL
}
