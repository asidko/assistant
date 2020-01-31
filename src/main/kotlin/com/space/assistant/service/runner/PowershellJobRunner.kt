package com.space.assistant.service.runner

import com.profesorfalken.jpowershell.PowerShell
import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.PowerShellJobExecInfo
import com.space.assistant.core.service.JobRunner
import org.springframework.stereotype.Service


@Service
class PowershellJobRunner : JobRunner {

    override suspend fun runJob(activeJobInfo: ActiveJobInfo): JobResult? {
        val execInfo = activeJobInfo.jobInfo?.execInfo as? PowerShellJobExecInfo ?: return null

        val prevJobResult = activeJobInfo.prevActiveJobInfo?.jobResult
        val command = prevJobResult?.value
                ?: execInfo.cmd
                ?: return null

        val shellResponse = PowerShell.openSession().executeCommand(command)
        val output = shellResponse.commandOutput

        return JobResult(output)
    }
}
