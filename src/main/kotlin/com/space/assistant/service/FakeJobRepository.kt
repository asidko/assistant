package com.space.assistant.service

import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.Phrase
import com.space.assistant.core.service.JobRepository
import com.space.assistant.service.parser.EmptyJobResultParser
import com.space.assistant.service.parser.JsonPathJobResultParser
import com.space.assistant.service.runner.*
import com.space.assistant.service.search.DirectMatchJobFinder
import com.space.assistant.service.search.EmptyJobFinder
import com.space.assistant.service.search.WildcardJobFinder
import org.springframework.stereotype.Service

@Service
class FakeJobRepository : JobRepository {

    override fun findJobByPhrase(phrase: Phrase): JobInfo? {
        val phraseText = phrase.joinToString(" ")
        return jobs
                .filter { it.finderInfo is DirectMatchJobFinder.Info }
                .find { (it.finderInfo as DirectMatchJobFinder.Info).texts.contains(phraseText) }
    }

    override fun findJobByUuid(uuid: String): JobInfo? {
        return jobs.find { it.uuid == uuid }
    }

    override fun findJobsBySearchType(searchType: String): List<JobInfo> {
        return jobs.filter { it.finderInfo.type == searchType }
    }

    private val jobs = listOf(
            JobInfo(
                    uuid = "SAY_HELLO",
                    finderInfo = DirectMatchJobFinder.Info(texts = listOf("hello")),
                    phraseBefore = listOf("выполняю"),
                    runnerInfo = JustSayJobRunner.Info(text = "Hello world"),
                    resultParserInfo = EmptyJobResultParser.Info(),
                    redirectToJobs = listOf(),
                    phraseAfter = listOf()
            ),
            JobInfo(
                    uuid = "SAY_TEXT",
                    finderInfo = EmptyJobFinder.Info(),
                    phraseBefore = listOf(),
                    runnerInfo = JustSayJobRunner.Info(text = ""),
                    resultParserInfo = EmptyJobResultParser.Info(),
                    redirectToJobs = listOf(),
                    phraseAfter = listOf()
            ),
            JobInfo(
                    uuid = "SAY_WEATHER",
                    finderInfo = DirectMatchJobFinder.Info(texts = listOf("погода")),
                    phraseBefore = listOf("уточняю температуру", "секундочку", "смотрю"),
                    runnerInfo = RequestJobRunner.Info(url = "https://www.metaweather.com/api/location/924938/"),
                    resultParserInfo = JsonPathJobResultParser.Info(
                            jsonPathValues = listOf("\$.consolidated_weather[0].the_temp"),
                            resultFormatString = "Текущая температура $1 градусов"),
                    redirectToJobs = listOf("SAY_TEXT"),
                    phraseAfter = listOf()
            ),
            JobInfo(
                    uuid = "RADIO",
                    finderInfo = DirectMatchJobFinder.Info(texts = listOf("включи радио")),
                    phraseBefore = listOf("включаю", "открываю", "запускаю", "сейчас будет"),
                    runnerInfo = WinCmdJobRunner.Info(cmd = "http://www.hitfm.ua/player/"),
                    resultParserInfo = EmptyJobResultParser.Info(),
                    redirectToJobs = listOf("SAY_TEXT"),
                    phraseAfter = listOf()
            ),
            JobInfo(
                    uuid = "RADIO_OFF",
                    finderInfo = DirectMatchJobFinder.Info(texts = listOf("выключи радио", "выключить радио")),
                    phraseBefore = listOf("выключаю"),
                    runnerInfo = PowershellJobRunner.Info(cmd = "taskkill /F /IM chrome.exe /T"),
                    resultParserInfo = EmptyJobResultParser.Info(),
                    redirectToJobs = listOf(),
                    phraseAfter = listOf()
            ),
            JobInfo(
                    uuid = "GOOGLE_SEARCH",
                    finderInfo = WildcardJobFinder.Info(text = "найти *"),
                    phraseBefore = listOf("выполняю поиск", "окей, ищу"),
                    runnerInfo = WildcardJobRunner.Info(wildcardText = "", resultText = "https://www.google.com/search?q=$1"),
                    resultParserInfo = EmptyJobResultParser.Info(),
                    redirectToJobs = listOf(),
                    phraseAfter = listOf()
            ),
            JobInfo(
                    uuid = "VOLUME_UP",
                    finderInfo = DirectMatchJobFinder.Info(texts = listOf("volume up")),
                    phraseBefore = listOf(),
                    runnerInfo = PowershellJobRunner.Info(cmd = "\$obj = new-object -com wscript.shell; \$obj.SendKeys([char]174)"),
                    resultParserInfo = EmptyJobResultParser.Info(),
                    redirectToJobs = listOf(),
                    phraseAfter = listOf("Звук повышен", "Звук увеличен")
            ),
            JobInfo(
                    uuid = "10_SECONDS",
                    finderInfo = DirectMatchJobFinder.Info(texts = listOf("10 секунд")),
                    phraseBefore = listOf("Засекаю 10 секунд", "Отсчитываю 10 секунд", "Таймер на 10 секунд установлен"),
                    runnerInfo = TimeDelayJobRunner.Info(seconds = "10"),
                    resultParserInfo = EmptyJobResultParser.Info(),
                    phraseAfter = listOf("10 секунд прошло", "Время вышло"),
                    redirectToJobs = listOf()
            ),
            JobInfo(
                    uuid = "TEST",
                    finderInfo = DirectMatchJobFinder.Info(texts = listOf("прием", "прийом", "чуєш", "ти чуєш", "ты тут", "ты здесь", "слышишь", "ты слышишь")),
                    phraseBefore = listOf("Слышу вас", "Я здесь", "Я наместе", "Все нормально", "Да-да", "Работаю"),
                    runnerInfo = EmptyJobRunner.Info(),
                    resultParserInfo = EmptyJobResultParser.Info(),
                    phraseAfter = listOf(),
                    redirectToJobs = listOf()
            )

    )

}
