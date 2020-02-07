package com.space.assistant.web.rest

import com.space.assistant.core.service.ActiveJobManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class CommandResource(
        private val activeJobManager: ActiveJobManager
) {
    @GetMapping("command/{text}")
    fun handleCommand(@PathVariable text: String) {
        activeJobManager.activateJob(text)
    }
}
