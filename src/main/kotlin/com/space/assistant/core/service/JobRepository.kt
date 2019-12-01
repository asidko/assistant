package com.space.assistant.core.service

import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.Phrase

interface JobRepository {
    fun findJobByPhrase(phrase: Phrase): JobInfo?
    fun findJobByUuid(uuid: String): JobInfo?
    fun findJobsBySearchType(searchType: String): List<JobInfo>
}
