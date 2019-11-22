package com.space.assistant.service

import RequestJobExecInfo
import JsonPathJobResultParseInfo
import JustSayJobExecInfo
import PlainTextJobResultParseInfo
import com.space.assistant.core.entity.*
import com.space.assistant.core.service.JobRepository
import org.springframework.stereotype.Service

@Service
class FakeJobRepository : JobRepository {

    override fun findJobByPhrase(phrase: Phrase): JobInfo? {
        val phraseText = phrase.joinToString(" ")
        return jobs
                .filter { it.searchInfo.type == JobParseType.DIRECT_MATCH }
                .find { (it.searchInfo as DirectMatchJobSearchInfo).text == phraseText }
    }

    private val jobs = listOf(
            JobInfo(
                    searchInfo = DirectMatchJobSearchInfo("hello"),
                    execInfo = JustSayJobExecInfo("Hello world"),
                    resultParseInfo = PlainTextJobResultParseInfo()
            ),
            JobInfo(
                    searchInfo = DirectMatchJobSearchInfo("weather"),
                    execInfo = RequestJobExecInfo("https://www.metaweather.com/api/location/924938/"),
                    resultParseInfo = JsonPathJobResultParseInfo(
                            jsonPathValues = listOf("\$.consolidated_weather[0].the_temp"),
                            resultFormatString = "Current temperature is $1 degrees")
            )
    )

}
