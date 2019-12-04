package com.space.assistant.aop


import com.fasterxml.jackson.databind.ObjectMapper
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono


/**
 * Aspect for logging execution of service and repository Spring components.
 *
 * By default, it only runs with the "dev" profile.
 */
@Aspect
class LoggingAspect(private val objectMapper: ObjectMapper) {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val basePackageName = "com.space.assistant"
    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.stereotype.Component *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)")
    fun springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut("within(com.space.assistant..*)")
    fun applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Advice that logs methods throwing exceptions.
     *
     * @param joinPoint join point for advice.
     * @param e exception.
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    fun logAfterThrowing(joinPoint: JoinPoint, e: Throwable) {
        log.error("Exception in {}.{}() with cause = {}", joinPoint.signature.declaringTypeName,
                joinPoint.signature.name, if (e.cause != null) e.cause else "NULL")
    }

    /**
     * Advice that logs when a method is entered and exited.
     *
     * @param joinPoint join point for advice.
     * @return result.
     * @throws Throwable throws [IllegalArgumentException].
     */
    @Around("applicationPackagePointcut() && springBeanPointcut()")
    @Throws(Throwable::class)
    fun logAround(joinPoint: ProceedingJoinPoint): Any? {
        if (log.isDebugEnabled) {
            val arguments = joinPoint.args.joinToString(", ") { convertResultToString(it) }
            log.debug("⮡ Enter {}.{}() with arguments = {}",
                    joinPoint.signature.declaringTypeName.prettifyClassName(),
                    joinPoint.signature.name.prettifyMethodName(),
                    arguments)
        }
        try {
            val result = joinPoint.proceed()
            if (log.isDebugEnabled) {
                val returnType = (joinPoint.signature as MethodSignature).returnType
                if (returnType.name != "void") {
                    log.debug("⮤ Exit {}.{}() with result = {}",
                            joinPoint.signature.declaringTypeName.prettifyClassName(),
                            joinPoint.signature.name.prettifyMethodName(),
                            convertResultToString(result))
                } else {
                    log.debug("⮤ Exit   {}.{}()",
                            joinPoint.signature.declaringTypeName.prettifyClassName(),
                            joinPoint.signature.name.prettifyMethodName())
                }
            }
            return result
        } catch (e: IllegalArgumentException) {
            val arguments = joinPoint.args.joinToString(", ") { objectMapper.writeValueAsString(it) }
            log.error("↯ Illegal argument {} in {}.{}()",
                    arguments,
                    joinPoint.signature.declaringTypeName.prettifyClassName(),
                    joinPoint.signature.name.prettifyMethodName())
            throw e
        }

    }

    private fun convertResultToString(result: Any?): String = when (result) {
        null -> AnsiConstants.GRAY + "NULL" + AnsiConstants.RESET
        is Mono<*> -> AnsiConstants.YELLOW + result.metrics().toString() + AnsiConstants.RESET
        is String -> AnsiConstants.GREEN + result + AnsiConstants.RESET
        else -> AnsiConstants.YELLOW + objectMapper.writeValueAsString(result) + AnsiConstants.RESET
    }

    private fun String.prettifyClassName(): String =
            this.removePrefix("$basePackageName.")
                    .let { fullClassName ->
                        val shortClassNameIndex = fullClassName.indexOfLast { it == '.' }
                        fullClassName.substring(0, shortClassNameIndex) +
                                AnsiConstants.PURPLE +
                                fullClassName.substring(shortClassNameIndex, fullClassName.length) +
                                AnsiConstants.RESET
                    }

    private fun String.prettifyMethodName(): String =
            this.let { AnsiConstants.BLUE + it + AnsiConstants.RESET }

    object AnsiConstants {
        val RESET = "\u001B[0m"
        val BLACK = "\u001B[30m"
        val RED = "\u001B[31m"
        val GREEN = "\u001B[32m"
        val YELLOW = "\u001B[33m"
        val BLUE = "\u001B[34m"
        val PURPLE = "\u001B[35m"
        val CYAN = "\u001B[36m"
        val GRAY = "\u001B[37m"
        val WHITE = "\u001B[37;1m"
    }
}
