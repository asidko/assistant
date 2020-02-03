package com.space.assistant.service.runner

import com.profesorfalken.jpowershell.PowerShell
import com.space.assistant.core.entity.*
import com.space.assistant.core.service.JobRunner
import org.springframework.stereotype.Service


@Service
class PowershellJobRunner : JobRunner {

    companion object {
        const val typeName = "POWERSHELL"
    }

    data class Info(
            val cmd: String,
            override val type: String = typeName
    ) : JobRunnerInfo

    override suspend fun runJob(activeJobInfo: ActiveJobInfo): JobResult? {
        val execInfo = activeJobInfo.jobInfo?.runnerInfo as? Info ?: return null

        val prevJobResult = activeJobInfo.prevActiveJobInfo?.jobResult
        val command = prevJobResult?.value
                ?: execInfo.cmd
                ?: return null

        val shellResponse = PowerShell.openSession().executeCommand(command)
        val output = shellResponse.commandOutput

        return JobResult(output)
    }
}
