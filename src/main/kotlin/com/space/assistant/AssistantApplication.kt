package com.space.assistant

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.event.ApplicationEventMulticaster
import org.springframework.context.event.SimpleApplicationEventMulticaster
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.scheduling.support.TaskUtils


@SpringBootApplication
@EnableAspectJAutoProxy
class AssistantApplication


fun main(args: Array<String>) {
	runApplication<AssistantApplication>(*args)
	printBanner()
}

fun printBanner() {
	val exampleUrl = "http://localhost:8080/command/hello"

	println("\n")
	println("Application started!")
	println("Example url: $exampleUrl")
	println("\n")
}

@Bean
fun applicationEventMulticaster(): ApplicationEventMulticaster {
	val eventMulticaster = SimpleApplicationEventMulticaster()
	eventMulticaster.setTaskExecutor(SimpleAsyncTaskExecutor())
	eventMulticaster.setErrorHandler(TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER)
	return eventMulticaster
}
