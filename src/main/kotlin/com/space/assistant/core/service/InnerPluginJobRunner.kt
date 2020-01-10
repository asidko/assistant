package com.space.assistant.core.service

import com.space.assistant.core.entity.ActiveJobInfo
import reactor.core.publisher.Mono

interface InnerPluginJobRunner {
    fun runJob(activeJobInfo: ActiveJobInfo): Mono<String>
}
