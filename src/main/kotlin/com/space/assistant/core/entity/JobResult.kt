package com.space.assistant.core.entity

data class JobResult private constructor(
        val result: String,
        val jobInfo: JobInfo,
        val redirectToJobs: List<String> = emptyList()
) {
    companion object {
        fun new(result: String, jobInfo: JobInfo) = JobResult(result, jobInfo)
    }
}
