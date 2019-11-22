package com.space.assistant

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy
class AssistantApplication


fun main(args: Array<String>) {
	runApplication<AssistantApplication>(*args)
	printBanner();
}

fun printBanner() {
	val exampleUrl = "http://localhost:8080/command/hello"

	println("\n")
	println("Application started!")
	println("Example url: $exampleUrl")
	println("\n")
}
