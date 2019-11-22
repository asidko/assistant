package com.space.assistant.service

import com.space.assistant.core.entity.*
import com.space.assistant.core.service.JobRepository
import org.springframework.stereotype.Service

@Service
class FakeJobRepository : JobRepository {

    override fun findJobByPhrase(phrase: Phrase): JobInfo? {
        val text = phrase.joinToString(" ")
        return jobs.find { it.parseValue == text}
    }

    private val jobs = listOf(
            JobInfo(
                    parseInfo = DirectMatchJobParseInfo("hello"),
                    execType = JobExecType.JUST_SAY,
                    execValue = "Hello world"
            ),
            JobInfo(
                    parseInfo = DirectMatchJobParseInfo("weather"),
                    execType = JobExecType.GET_REQUEST,
                    execValue = "https://www.metaweather.com/api/location/924938/",
                    resultParseType = JobResultParseType.JSON_PATH,
                    resultParseArgs = "\$.consolidated_weather[0].the_temp",
                    shouldSayResult = true
            )
    )
}
