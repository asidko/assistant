package com.space.assistant.service

import EmptyJobResultParseInfo
import JsonPathJobResultParseInfo
import com.space.assistant.core.entity.*
import com.space.assistant.core.service.JobRepository
import org.springframework.stereotype.Service

@Service
class FakeJobRepository : JobRepository {

    override fun findJobByPhrase(phrase: Phrase): JobInfo? {
        val phraseText = phrase.joinToString(" ")
        return jobs
                .filter { it.searchInfo.type == JobSearchType.DIRECT_MATCH }
                .find { (it.searchInfo as DirectMatchJobSearchInfo).text == phraseText }
    }

    override fun findJobByUuid(uuid: String): JobInfo? {
        return jobs.find { it.uuid == uuid }
    }

    override fun findJobsBySearchType(searchType: String): List<JobInfo> {
        return jobs.filter { it.searchInfo.type == searchType }
    }

    private val jobs = listOf(
            JobInfo(
                    uuid = "SAY_HELLO",
                    searchInfo = DirectMatchJobSearchInfo(text = "hello"),
                    execInfo = JustSayJobExecInfo(text = "Hello world"),
                    resultParseInfo = EmptyJobResultParseInfo()
            ),
            JobInfo(
                    uuid = "SAY_TEXT",
                    searchInfo = EmptyJobSearchInfo(),
                    execInfo = JustSayJobExecInfo(text = ""),
                    resultParseInfo = EmptyJobResultParseInfo()
            ),
            JobInfo(
                    uuid = "SAY_THE_WEATHER",
                    searchInfo = DirectMatchJobSearchInfo(text = "weather"),
                    execInfo = RequestJobExecInfo(url = "https://www.metaweather.com/api/location/924938/"),
                    resultParseInfo = JsonPathJobResultParseInfo(
                            jsonPathValues = listOf("\$.consolidated_weather[0].the_temp"),
                            resultFormatString = "Current temperature is $1 degrees"),
                    redirectToJobs = listOf("SAY_TEXT")
            ),
            JobInfo(
                    uuid = "RUN_CHROME",
                    searchInfo = DirectMatchJobSearchInfo(text = "chrome"),
                    execInfo = WinCmdJobExecInfo(cmd = "chrome.exe"),
                    resultParseInfo = EmptyJobResultParseInfo(),
                    redirectToJobs = listOf("SAY_TEXT")
            ),
            JobInfo(
                    uuid = "GOOGLE_SEARCH",
                    searchInfo = WildcardJobSearchInfo(text = "найти *"),
                    execInfo = JustSayJobExecInfo(text = ""),
                    resultParseInfo = EmptyJobResultParseInfo()
            )
    )

}
