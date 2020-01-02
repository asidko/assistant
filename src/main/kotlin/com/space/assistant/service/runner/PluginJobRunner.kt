package com.space.assistant.service.runner

import com.space.assistant.core.entity.*
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

    override fun runJob(runJobInfo: RunJobInfo): Mono<JobResult> {
        if (!canRun(runJobInfo.jobInfo)) return Mono.empty()

        val name = (runJobInfo.jobInfo.execInfo as PluginJobExecInfo).name

        val pluginRunner = getInnerPluginJobRunner(name) ?: return Mono.empty()

        return pluginRunner.runJob(runJobInfo).map { pluginResult ->
            runJobInfo.previousJobResult?.copy(result = pluginResult)
                    ?: JobResult.new(pluginResult, runJobInfo.jobInfo)
        }
    }

    private fun getInnerPluginJobRunner(name: String): InnerPluginJobRunner? {
        return try {
            context.getBean(name, InnerPluginJobRunner::class.java)
        } catch (ex: Exception) {
            log.error(ex.message)
            null
        }
    }

    private fun canRun(jobInfo: JobInfo) = jobInfo.execInfo.type == JobExecType.PLUGIN
}
