package com.space.assistant.core.entity

data class JobResult(
        val value: String
)

fun JobResult.asArgs(): List<String> = this.value.split(",").map(String::trim)


val emptyJobResult = JobResult("")


