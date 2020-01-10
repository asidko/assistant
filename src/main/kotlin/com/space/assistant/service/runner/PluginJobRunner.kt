package com.space.assistant.service.runner

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobExecType
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.PluginJobExecInfo
import com.space.assistant.core.service.InnerPluginJobRunner
import com.space.assistant.core.service.JobRunner
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PluginJobRunner(
        private val context: ApplicationContext
) : JobRunner {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun runJob(activeJobInfo: ActiveJobInfo): Mono<JobResult> {
        if (!canRun(activeJobInfo)) return Mono.empty()

        val name = (activeJobInfo.jobInfo?.execInfo as? PluginJobExecInfo)?.name ?: ""

        val pluginRunner = getInnerPluginJobRunner(name) ?: return Mono.empty()

        return pluginRunner.runJob(activeJobInfo)
                .map { pluginResult -> JobResult(pluginResult) }
    }

    private fun getInnerPluginJobRunner(name: String): InnerPluginJobRunner? {
        return try {
            context.getBean(name, InnerPluginJobRunner::class.java)
        } catch (ex: Exception) {
            log.error(ex.message)
            null
        }
    }

    private fun canRun(activeJobInfo: ActiveJobInfo) = activeJobInfo.jobInfo?.execInfo?.type == JobExecType.PLUGIN
}
