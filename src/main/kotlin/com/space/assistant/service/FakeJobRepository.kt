package com.space.assistant.service

import com.space.assistant.core.entity.*
import com.space.assistant.core.service.JobRepository
import com.space.assistant.service.parser.EmptyJobResultParser
import com.space.assistant.service.parser.JsonPathJobResultParser
import com.space.assistant.service.search.DirectMatchJobSearchProvider
import com.space.assistant.service.search.EmptyJobSearchProvider
import com.space.assistant.service.search.WildcardJobSearchProvider
import org.springframework.stereotype.Service

@Service
class FakeJobRepository : JobRepository {

    override fun findJobByPhrase(phrase: Phrase): JobInfo? {
        val phraseText = phrase.joinToString(" ")
        return jobs
                .filter { it.searchInfo is DirectMatchJobSearchProvider.Info }
                .find { (it.searchInfo as DirectMatchJobSearchProvider.Info).texts.contains(phraseText) }
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
                    searchInfo = DirectMatchJobSearchProvider.Info(texts = listOf("hello")),
                    preExecPhrase = listOf("выполняю"),
                    execInfo = JustSayJobExecInfo(text = "Hello world"),
                    resultParseInfo = EmptyJobResultParser.Info(),
                    redirectToJobs = emptyList(),
                    postExecPhrase = emptyList()
            ),
            JobInfo(
                    uuid = "SAY_TEXT",
                    searchInfo = EmptyJobSearchProvider.Info(),
                    preExecPhrase = emptyList(),
                    execInfo = JustSayJobExecInfo(text = ""),
                    resultParseInfo = EmptyJobResultParser.Info(),
                    redirectToJobs = emptyList(),
                    postExecPhrase = emptyList()
            ),
            JobInfo(
                    uuid = "SAY_WEATHER",
                    searchInfo = DirectMatchJobSearchProvider.Info(texts = listOf("погода")),
                    preExecPhrase = listOf("уточняю температуру", "секундочку", "смотрю"),
                    execInfo = RequestJobExecInfo(url = "https://www.metaweather.com/api/location/924938/"),
                    resultParseInfo = JsonPathJobResultParser.Info(
                            jsonPathValues = listOf("\$.consolidated_weather[0].the_temp"),
                            resultFormatString = "Текущая температура $1 градусов"),
                    redirectToJobs = listOf("SAY_TEXT"),
                    postExecPhrase = emptyList()
            ),
            JobInfo(
                    uuid = "RUN_CHROME",
                    searchInfo = DirectMatchJobSearchProvider.Info(texts = listOf("включи радио")),
                    preExecPhrase = listOf("включаю", "открываю", "запускаю", "сейчас будет"),
                    execInfo = WinCmdJobExecInfo(cmd = "http://www.hitfm.ua/player/"),
                    resultParseInfo = EmptyJobResultParser.Info(),
                    redirectToJobs = emptyList(),
                    postExecPhrase = emptyList()
            ),
            JobInfo(
                    uuid = "GOOGLE_SEARCH",
                    searchInfo = WildcardJobSearchProvider.Info(text = "найти *"),
                    preExecPhrase = listOf("выполняю поиск", "окей, ищу"),
                    execInfo = WildcardJobExecInfo(expression = "https://www.google.com/search?q=$1"),
                    resultParseInfo = EmptyJobResultParser.Info(),
                    redirectToJobs = emptyList(),
                    postExecPhrase = emptyList()
            ),
            JobInfo(
                    uuid = "VOLUME_UP",
                    searchInfo = DirectMatchJobSearchProvider.Info(texts = listOf("volume up")),
                    preExecPhrase = emptyList(),
                    execInfo = PowerShellJobExecInfo(cmd = "\$obj = new-object -com wscript.shell; \$obj.SendKeys([char]174)"),
                    resultParseInfo = EmptyJobResultParser.Info(),
                    redirectToJobs = emptyList(),
                    postExecPhrase = listOf("Звук повышен", "Звук увеличен")
            ),
            JobInfo(
                    uuid = "10_SECONDS",
                    searchInfo = DirectMatchJobSearchProvider.Info(texts = listOf("10 секунд")),
                    preExecPhrase = listOf("Засекаю 10 секунд", "Отсчитываю 10 секунд", "Таймер на 10 секунд установлен"),
                    execInfo = TimeDelayJobExecInfo(seconds = "10"),
                    resultParseInfo = EmptyJobResultParser.Info(),
                    postExecPhrase = listOf("10 секунд прошло", "Время вышло"),
                    redirectToJobs = emptyList()
            ),
            JobInfo(
                    uuid = "CHARLIE",
                    searchInfo = DirectMatchJobSearchProvider.Info(texts = listOf("прием", "прийом", "чуєш", "ти чуєш", "слышишь", "ты слышишь")),
                    preExecPhrase = listOf("Слышу вас", "Я здесь", "Я наместе", "Все нормально", "Да-да", "Работаю"),
                    execInfo = EmptyJobExecInfo(),
                    resultParseInfo = EmptyJobResultParser.Info(),
                    postExecPhrase = listOf(),
                    redirectToJobs = emptyList()
            )

    )

}
