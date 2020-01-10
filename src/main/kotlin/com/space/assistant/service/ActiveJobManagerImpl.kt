package com.space.assistant.service

import com.space.assistant.core.entity.*
import com.space.assistant.core.event.JobProvidedEvent
import com.space.assistant.core.event.NewCommandProvidedEvent
import com.space.assistant.core.service.ActiveJobManager
import com.space.assistant.core.service.InputCommandFilter
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.util.*

@Service
class ActiveJobManagerImpl(
        private val eventPublisher: ApplicationEventPublisher,
        private val filter: InputCommandFilter,
        private val jobRepository: FakeJobRepository
) : ActiveJobManager {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val activeJobs: MutableMap<String, ActiveJobInfo> = mutableMapOf()

    override fun tryNewJob(text: String) {
        val command = InputCommand.fromText(text)

        val filtered = filter.apply(command)
        filtered ?: log.debug("Command with text={} was filtered out", text)
        filtered ?: return

        val activeJob = registerActiveJob(command)

        eventPublisher.publishEvent(NewCommandProvidedEvent(activeJob))
    }

    override fun tryNextJob(currentActiveJobInfo: ActiveJobInfo) {
        val currentNextJobs = currentActiveJobInfo.nexJobs
        val nextJobsFromJobInfo = currentActiveJobInfo.jobInfo?.redirectToJobs ?: emptyList()
        val allNextJobs = nextJobsFromJobInfo + currentNextJobs

        if (allNextJobs.isEmpty()) {
            log.debug("There is no more job redirects. Return.")
            return
        }

        val nextJobUuid = allNextJobs.first()
        val remainingNextJobs = allNextJobs.subList(1, allNextJobs.size)
        log.debug("Looking for next job with uuid {}", nextJobUuid)
        val nextJobInfo = jobRepository.findJobByUuid(nextJobUuid) ?: return

        val nextActiveJobInfo = ActiveJobInfo(
                uuid = UUID.randomUUID().toString(),
                inputCommand = currentActiveJobInfo.inputCommand,
                commandAlternatives = currentActiveJobInfo.commandAlternatives,
                commandAlternativeSucceed = currentActiveJobInfo.commandAlternativeSucceed,
                jobInfo = nextJobInfo,
                prevActiveJobInfo = currentActiveJobInfo,
                nexJobs = remainingNextJobs
        )

        saveActiveJob(nextActiveJobInfo)

        val event = JobProvidedEvent(nextActiveJobInfo)
        eventPublisher.publishEvent(event)
    }

    override fun registerActiveJob(command: InputCommand): ActiveJobInfo {
        val activeJobInfo = ActiveJobInfo(
                uuid = UUID.randomUUID().toString(),
                inputCommand = command
        )

        return saveActiveJob(activeJobInfo)
    }

    override fun saveActiveJob(activeJobInfo: ActiveJobInfo): ActiveJobInfo {
        activeJobs + (activeJobInfo.uuid to activeJobInfo)
        return activeJobInfo
    }

    override fun addAlternatives(activeJobInfo: ActiveJobInfo, commandAlternatives: List<CommandAlternative>): ActiveJobInfo {
        val currentActiveJob = activeJobs[activeJobInfo.uuid]!!
        val updatedAlternatives = currentActiveJob.commandAlternatives + commandAlternatives
        val updatedActiveJob = currentActiveJob.copy(commandAlternatives = updatedAlternatives)
        activeJobs[activeJobInfo.uuid] = updatedActiveJob

        return updatedActiveJob
    }

    override fun setJobInfo(activeJobInfo: ActiveJobInfo, jobInfo: JobInfo): ActiveJobInfo {
        val currentActiveJob = activeJobs[activeJobInfo.uuid]!!
        val updatedActiveJob = currentActiveJob.copy(jobInfo = jobInfo)
        activeJobs[activeJobInfo.uuid] = updatedActiveJob

        return updatedActiveJob
    }

    override fun setAlternativeSucceed(activeJobInfo: ActiveJobInfo, commandAlternative: CommandAlternative): ActiveJobInfo {
        val currentActiveJob = activeJobs[activeJobInfo.uuid]!!
        val updatedActiveJob = currentActiveJob.copy(commandAlternativeSucceed = commandAlternative)
        activeJobs[activeJobInfo.uuid] = updatedActiveJob

        return updatedActiveJob
    }

    override fun setRawResult(activeJobInfo: ActiveJobInfo, result: JobResult?): ActiveJobInfo {
        val currentActiveJob = activeJobs[activeJobInfo.uuid]!!
        val updatedActiveJob = currentActiveJob.copy(jobRawResult = result)
        activeJobs[activeJobInfo.uuid] = updatedActiveJob

        return updatedActiveJob
    }

    override fun setResult(activeJobInfo: ActiveJobInfo, result: JobResult?): ActiveJobInfo {
        val currentActiveJob = activeJobs[activeJobInfo.uuid]!!
        val updatedActiveJob = currentActiveJob.copy(jobResult = result)
        activeJobs[activeJobInfo.uuid] = updatedActiveJob

        return updatedActiveJob
    }
}