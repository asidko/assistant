package com.space.assistant.core.service

import com.space.assistant.core.entity.CommandAlternative
import com.space.assistant.core.entity.JobInfo

interface JobActivator {
    fun activateJob(command: CommandAlternative): JobInfo?
}
