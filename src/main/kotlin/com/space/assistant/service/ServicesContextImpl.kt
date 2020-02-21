package com.space.assistant.service

import com.space.assistant.core.service.JobResultParser
import com.space.assistant.core.service.JobRunner
import com.space.assistant.core.service.ServicesContext
import org.springframework.stereotype.Service

@Service
class ServicesContextImpl(jobRunners: List<JobRunner>,
                          jobResultParser: List<JobResultParser>) : ServicesContext {

    private val infoClassToServiceMap: Map<Class<*>, Any> = listOf(
            jobRunners,
            jobResultParser
    )
            .map { it.toInfoClassPairs() }
            .flatten()
            .associate { it }

    override fun getServiceByInfo(info: Any) = infoClassToServiceMap[info::class.java]

    private fun List<Any>.toInfoClassPairs(): List<Pair<Class<*>, Any>> =
            this.mapNotNull { service -> getInfoInnerClass(service) }

    private fun getInfoInnerClass(service: Any): Pair<Class<*>, Any>? = try {
        Class.forName(service.getInfoInnerClassFullName()) to service
    } catch (e: Exception) {
        null
    }

    private fun Any.getInfoInnerClassFullName(): String {
        val serviceClassName = this::class.java.name.split("$$")[0]
        return "$serviceClassName\$Info"
    }
}
