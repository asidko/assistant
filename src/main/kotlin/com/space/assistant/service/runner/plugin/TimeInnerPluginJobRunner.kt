package com.space.assistant.service.runner.plugin

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.service.InnerPluginJobRunner
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component("TimePlugin")
class TimeInnerPluginJobRunner : InnerPluginJobRunner {
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")

    override fun runJob(activeJobInfo: ActiveJobInfo): Mono<String> {
        return Mono.create {
            val time = formatter.format(LocalDateTime.now())
            it.success(time)
        }
    }
}
