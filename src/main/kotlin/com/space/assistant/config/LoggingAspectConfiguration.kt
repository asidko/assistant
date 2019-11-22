package com.space.assistant.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.space.assistant.aop.LoggingAspect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@Configuration
@EnableAspectJAutoProxy
class LoggingAspectConfiguration {

    @Bean
    fun loggingAspect(objectMapper: ObjectMapper): LoggingAspect {
        return LoggingAspect(objectMapper)
    }
}
