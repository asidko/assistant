package com.space.assistant.core.service

import com.space.assistant.core.entity.*

interface ActiveJobManager {
    fun activateJob(text: String)
    fun activateJob(texts: List<String>)
    fun activateJob(jobInfo: JobInfo)
    fun activateNextJob(currentActiveJobInfo: ActiveJobInfo)
    fun addAlternatives(activeJobInfo: ActiveJobInfo, commandAlternatives: List<CommandAlternative>): ActiveJobInfo
    fun setJobInfo(activeJobInfo: ActiveJobInfo, jobInfo: JobInfo): ActiveJobInfo
    fun setAlternativeSucceed(activeJobInfo: ActiveJobInfo, commandAlternative: CommandAlternative): ActiveJobInfo
    fun setRawResult(activeJobInfo: ActiveJobInfo, result: JobResult?): ActiveJobInfo
    fun setResult(activeJobInfo: ActiveJobInfo, result: JobResult?): ActiveJobInfo
    fun saveActiveJob(activeJobInfo: ActiveJobInfo): ActiveJobInfo
    fun getActiveJob(activeJobId: String): ActiveJobInfo?
}
