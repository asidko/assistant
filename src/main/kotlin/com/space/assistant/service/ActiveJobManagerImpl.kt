package com.space.assistant.service

import com.space.assistant.core.entity.*
import com.space.assistant.core.event.JobProvidedEvent
import com.space.assistant.core.event.NewCommandProvidedEvent
import com.space.assistant.core.service.ActiveJobManager
import com.space.assistant.core.service.ActiveJobRepository
import com.space.assistant.core.service.EventPublisher
import com.space.assistant.core.service.InputCommandFilter
import com.space.assistant.core.util.simpleID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ActiveJobManagerImpl(
        private val eventPublisher: EventPublisher,
        private val filter: InputCommandFilter,
        private val jobRepository: FakeJobRepository,
        private val activeJobRepository: InMemoryActiveJobRepository
) :
        ActiveJobManager,
        ActiveJobRepository by activeJobRepository {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun activateJob(text: String) {
        val inputCommand = InputCommand.fromText(text)
        applyFilterAndRegister(inputCommand)
    }

    override fun activateJob(jobInfo: JobInfo) {
        val activeJobInfo = ActiveJobInfo(
                uuid = simpleID(),
                jobInfo = jobInfo
        )

        saveActiveJob(activeJobInfo)

        val event = JobProvidedEvent(activeJobInfo)

        eventPublisher.publishEvent(event)
    }

    override fun activateJob(texts: List<String>) {
        if (texts.isEmpty()) return
        if (texts.size == 1) activateJob(texts.first())
        val inputCommand = InputCommand.fromTexts(texts)

        applyFilterAndRegister(inputCommand)
    }

    private fun applyFilterAndRegister(inputCommand: InputCommand) {
        val filtered = filter.apply(inputCommand)
        filtered ?: log.debug("Command {} was filtered out", inputCommand.toString())
        filtered ?: return

        val activeJobInfo = ActiveJobInfo(
                uuid = simpleID(),
                inputCommand = inputCommand
        )

        val activeJob = saveActiveJob(activeJobInfo)
        val event = NewCommandProvidedEvent(activeJob)

        eventPublisher.publishEvent(event)
    }

    override fun activateNextJob(currentActiveJobInfo: ActiveJobInfo) {
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
                uuid = simpleID(),
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
}
