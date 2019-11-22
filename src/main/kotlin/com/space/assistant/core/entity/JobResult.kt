package com.space.assistant.core.entity

data class JobResult(
        val result: String,
        val jobInfo: JobInfo,
        val redirectToJobs: List<String> = emptyList()
)
