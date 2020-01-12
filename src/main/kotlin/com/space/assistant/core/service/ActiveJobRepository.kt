package com.space.assistant.core.service

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.CommandAlternative
import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.JobResult

interface ActiveJobRepository {
    fun addAlternatives(activeJobInfo: ActiveJobInfo, commandAlternatives: List<CommandAlternative>): ActiveJobInfo
    fun setJobInfo(activeJobInfo: ActiveJobInfo, jobInfo: JobInfo): ActiveJobInfo
    fun setAlternativeSucceed(activeJobInfo: ActiveJobInfo, commandAlternative: CommandAlternative): ActiveJobInfo
    fun setRawResult(activeJobInfo: ActiveJobInfo, result: JobResult?): ActiveJobInfo
    fun setResult(activeJobInfo: ActiveJobInfo, result: JobResult?): ActiveJobInfo
    fun saveActiveJob(activeJobInfo: ActiveJobInfo): ActiveJobInfo
    fun getActiveJob(activeJobId: String): ActiveJobInfo?
}