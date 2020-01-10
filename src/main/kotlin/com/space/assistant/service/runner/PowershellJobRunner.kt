package com.space.assistant.service.runner

import com.profesorfalken.jpowershell.PowerShell
import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobExecType
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.PowerShellJobExecInfo
import com.space.assistant.core.service.JobRunner
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class PowershellJobRunner : JobRunner {

    override fun runJob(activeJobInfo: ActiveJobInfo): Mono<JobResult> {
        if (!canRun(activeJobInfo)) return Mono.empty()

        val command = activeJobInfo.prevActiveJobInfo?.jobResult?.value
                ?: (activeJobInfo.jobInfo?.execInfo as? PowerShellJobExecInfo)?.cmd
                ?: return Mono.empty()

        return Mono.create {
            val shellResponse = PowerShell.openSession().executeCommand(command)
            val result = JobResult(shellResponse.commandOutput)
            it.success(result)
        }
    }


    private fun canRun(activeJobInfo: ActiveJobInfo) = activeJobInfo.jobInfo?.execInfo?.type == JobExecType.POWERSHELL
}
