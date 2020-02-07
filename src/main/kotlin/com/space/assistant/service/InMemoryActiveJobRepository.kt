package com.space.assistant.service

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.CommandAlternative
import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.service.ActiveJobRepository
import com.space.assistant.service.error.ActiveJobNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class InMemoryActiveJobRepository : ActiveJobRepository {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val activeJobs: MutableMap<String, ActiveJobInfo> = ConcurrentHashMap()

    override fun saveActiveJob(activeJobInfo: ActiveJobInfo): ActiveJobInfo {
        activeJobs[activeJobInfo.uuid] = activeJobInfo
        return activeJobInfo
    }

    override fun addAlternatives(activeJobInfo: ActiveJobInfo, commandAlternatives: List<CommandAlternative>): ActiveJobInfo {
        val currentActiveJob = getById(activeJobInfo)
        val updatedAlternatives = currentActiveJob.commandAlternatives + commandAlternatives
        val updatedActiveJob = currentActiveJob.copy(commandAlternatives = updatedAlternatives)
        activeJobs[activeJobInfo.uuid] = updatedActiveJob

        return updatedActiveJob
    }

    override fun setJobInfo(activeJobInfo: ActiveJobInfo, jobInfo: JobInfo): ActiveJobInfo {
        val currentActiveJob = getById(activeJobInfo)
        val updatedActiveJob = currentActiveJob.copy(jobInfo = jobInfo)
        activeJobs[activeJobInfo.uuid] = updatedActiveJob

        return updatedActiveJob
    }

    override fun setAlternativeSucceed(activeJobInfo: ActiveJobInfo, commandAlternative: CommandAlternative): ActiveJobInfo {
        val currentActiveJob = getById(activeJobInfo)
        val updatedActiveJob = currentActiveJob.copy(commandAlternativeSucceed = commandAlternative)
        activeJobs[activeJobInfo.uuid] = updatedActiveJob

        return updatedActiveJob
    }

    override fun setRawResult(activeJobInfo: ActiveJobInfo, result: JobResult?): ActiveJobInfo {
        val currentActiveJob = getById(activeJobInfo)
        val updatedActiveJob = currentActiveJob.copy(jobRawResult = result)
        activeJobs[activeJobInfo.uuid] = updatedActiveJob

        return updatedActiveJob
    }

    override fun setResult(activeJobInfo: ActiveJobInfo, result: JobResult?): ActiveJobInfo {
        val currentActiveJob = getById(activeJobInfo)
        val updatedActiveJob = currentActiveJob.copy(jobResult = result)
        activeJobs[activeJobInfo.uuid] = updatedActiveJob

        return updatedActiveJob
    }

    private fun getById(activeJobInfo: ActiveJobInfo): ActiveJobInfo {
        return activeJobs[activeJobInfo.uuid] ?: throw ActiveJobNotFoundException(activeJobInfo)
    }

    override fun getActiveJob(activeJobId: String) = activeJobs[activeJobId]
}
