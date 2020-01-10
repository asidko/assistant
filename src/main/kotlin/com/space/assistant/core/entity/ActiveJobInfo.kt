package com.space.assistant.core.entity

data class ActiveJobInfo(
        val uuid: String,
        val inputCommand: InputCommand? = null,
        val commandAlternatives: List<CommandAlternative> = emptyList(),
        val commandAlternativeSucceed: CommandAlternative? = null,
        val jobInfo: JobInfo? = null,
        val jobRawResult: JobResult? = null,
        val jobResult: JobResult? = null,
        val prevActiveJobInfo: ActiveJobInfo? = null,
        val nexJobs: List<String> = emptyList()
)

