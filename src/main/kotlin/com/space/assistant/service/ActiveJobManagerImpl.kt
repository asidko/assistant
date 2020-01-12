package com.space.assistant.service

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.InputCommand
import com.space.assistant.core.entity.fromText
import com.space.assistant.core.entity.fromTexts
import com.space.assistant.core.event.JobProvidedEvent
import com.space.assistant.core.event.NewCommandProvidedEvent
import com.space.assistant.core.service.ActiveJobManager
import com.space.assistant.core.service.ActiveJobRepository
import com.space.assistant.core.service.InputCommandFilter
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.util.*

@Service
class ActiveJobManagerImpl(
        private val eventPublisher: ApplicationEventPublisher,
        private val filter: InputCommandFilter,
        private val jobRepository: FakeJobRepository,
        private val activeJobRepository: InMemoryActiveJobRepository
) :
        ActiveJobManager,
        ActiveJobRepository by activeJobRepository {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun tryNewJob(text: String) {
        val command = InputCommand.fromText(text)

        val filtered = filter.apply(command)
        filtered ?: log.debug("Command with text {} was filtered out", text)
        filtered ?: return

        val activeJob = registerActiveJob(command)

        eventPublisher.publishEvent(NewCommandProvidedEvent(activeJob))
    }

    override fun tryNewJobs(texts: List<String>) {
        if (texts.isEmpty()) return
        if (texts.size == 1) tryNewJob(texts.first())
        val command = InputCommand.fromTexts(texts)

        val filtered = filter.apply(command)
        filtered ?: log.debug("Command with texts {} was filtered out", command.toString())
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
}