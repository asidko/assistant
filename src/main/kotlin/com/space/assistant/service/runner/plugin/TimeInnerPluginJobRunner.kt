package com.space.assistant.service.runner.plugin

import com.space.assistant.core.entity.RunJobInfo
import com.space.assistant.core.service.InnerPluginJobRunner
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component("TimePlugin")
class TimeInnerPluginJobRunner : InnerPluginJobRunner {
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")

    override fun runJob(runJobInfo: RunJobInfo): Mono<String> {
        return Mono.create { mono ->
            val time = formatter.format(LocalDateTime.now())
            mono.success(time)
        }
    }
}
