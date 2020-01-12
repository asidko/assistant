package com.space.assistant.service

import EmptyJobResultParseInfo
import JsonPathJobResultParseInfo
import PatternStringResultParseInfo
import com.space.assistant.core.entity.*
import com.space.assistant.core.service.JobRepository
import org.springframework.stereotype.Service

@Service
class FakeJobRepository : JobRepository {

    override fun findJobByPhrase(phrase: Phrase): JobInfo? {
        val phraseText = phrase.joinToString(" ")
        return jobs
                .filter { it.searchInfo.type == JobSearchType.DIRECT_MATCH }
                .find { (it.searchInfo as DirectMatchJobSearchInfo).texts.contains(phraseText) }
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
                    searchInfo = DirectMatchJobSearchInfo(texts = listOf("hello")),
                    preExecPhrase = listOf("выполняю"),
                    execInfo = JustSayJobExecInfo(text = "Hello world"),
                    resultParseInfo = EmptyJobResultParseInfo(),
                    redirectToJobs = emptyList(),
                    postExecPhrase = emptyList()
            ),
            JobInfo(
                    uuid = "SAY_TEXT",
                    searchInfo = EmptyJobSearchInfo(),
                    preExecPhrase = emptyList(),
                    execInfo = JustSayJobExecInfo(text = ""),
                    resultParseInfo = EmptyJobResultParseInfo(),
                    redirectToJobs = emptyList(),
                    postExecPhrase = emptyList()
            ),
            JobInfo(
                    uuid = "SAY_WEATHER",
                    searchInfo = DirectMatchJobSearchInfo(texts = listOf("погода")),
                    preExecPhrase = listOf("уточняю температуру", "секундочку", "смотрю"),
                    execInfo = RequestJobExecInfo(url = "https://www.metaweather.com/api/location/924938/"),
                    resultParseInfo = JsonPathJobResultParseInfo(
                            jsonPathValues = listOf("\$.consolidated_weather[0].the_temp"),
                            resultFormatString = "Текущая температура $1 градусов"),
                    redirectToJobs = listOf("SAY_TEXT"),
                    postExecPhrase = emptyList()
            ),
            JobInfo(
                    uuid = "TIME",
                    searchInfo = DirectMatchJobSearchInfo(texts = listOf("время")),
                    preExecPhrase = emptyList(),
                    execInfo = PluginJobExecInfo(name = "TimePlugin"),
                    resultParseInfo = PatternStringResultParseInfo(text = "Текущее время $1"),
                    redirectToJobs = listOf("SAY_TEXT"),
                    postExecPhrase = emptyList()
            ),
            JobInfo(
                    uuid = "RUN_CHROME",
                    searchInfo = DirectMatchJobSearchInfo(texts = listOf("включи радио")),
                    preExecPhrase = listOf("включаю", "открываю", "запускаю", "сейчас будет"),
                    execInfo = WinCmdJobExecInfo(cmd = "http://www.hitfm.ua/player/"),
                    resultParseInfo = EmptyJobResultParseInfo(),
                    redirectToJobs = emptyList(),
                    postExecPhrase = emptyList()
            ),
            JobInfo(
                    uuid = "GOOGLE_SEARCH",
                    searchInfo = WildcardJobSearchInfo(text = "найти *"),
                    preExecPhrase = listOf("выполняю поиск", "окей, ищу"),
                    execInfo = WildcardJobExecInfo(expression = "https://www.google.com/search?q=$1"),
                    resultParseInfo = EmptyJobResultParseInfo(),
                    redirectToJobs = emptyList(),
                    postExecPhrase = emptyList()
            ),
            JobInfo(
                    uuid = "VOLUME_UP",
                    searchInfo = DirectMatchJobSearchInfo(texts = listOf("volume up")),
                    preExecPhrase = emptyList(),
                    execInfo = PowerShellJobExecInfo(cmd = "\$obj = new-object -com wscript.shell; \$obj.SendKeys([char]174)"),
                    resultParseInfo = EmptyJobResultParseInfo(),
                    redirectToJobs = emptyList(),
                    postExecPhrase = listOf("Звук повышен", "Звук увеличен")
            ),
            JobInfo(
                    uuid = "10_SECONDS",
                    searchInfo = DirectMatchJobSearchInfo(texts = listOf("10 секунд")),
                    preExecPhrase = listOf("Засекаю 10 секунд", "Отсчитываю 10 секунд", "Таймер на 10 секунд установлен"),
                    execInfo = TimeDelayJobExecInfo(seconds = "10"),
                    resultParseInfo = EmptyJobResultParseInfo(),
                    postExecPhrase = listOf("10 секунд прошло", "Время вышло"),
                    redirectToJobs = emptyList()
            ),
            JobInfo(
                    uuid = "CHARLIE",
                    searchInfo = DirectMatchJobSearchInfo(texts = listOf("прием", "прийом", "как слышно", "слышишь")),
                    preExecPhrase = listOf("Слышу вас", "Я здесь", "Я наместе", "Прием, все нормально", "Да-да, слышу", "Все работает, слышу вас"),
                    execInfo = EmptyJobExecInfo(),
                    resultParseInfo = EmptyJobResultParseInfo(),
                    postExecPhrase = listOf(),
                    redirectToJobs = emptyList()
            )

    )

}
