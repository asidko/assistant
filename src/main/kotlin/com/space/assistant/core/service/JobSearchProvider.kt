package com.space.assistant.core.service

import com.space.assistant.core.entity.CommandAlternative
import com.space.assistant.core.entity.JobInfo

interface JobSearchProvider {
    fun findJob(command: CommandAlternative): JobInfo?
}
