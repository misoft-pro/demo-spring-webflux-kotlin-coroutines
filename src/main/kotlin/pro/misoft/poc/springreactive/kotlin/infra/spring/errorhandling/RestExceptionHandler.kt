package pro.misoft.poc.springreactive.kotlin.infra.spring.errorhandling


import jakarta.validation.ConstraintViolationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.*
import org.springframework.http.server.reactive.observation.ServerRequestObservationContext
import org.springframework.lang.Nullable
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
class RestExceptionHandler(private val errorFactory: ApiErrorFactory) : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [ConstraintViolationException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolationException(ex: ConstraintViolationException, exchange: ServerWebExchange): Mono<ResponseEntity<ApiError>> {
        log.debug(MSG_TEMPLATE, ex)
        ServerRequestObservationContext.findCurrent(exchange.attributes)
            .ifPresent { context -> context.setError(ex) }
        return Mono.just(
            ResponseEntity.badRequest().header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(errorFactory.error(ex))
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleExceptions(ex: Exception, exchange: ServerWebExchange): Mono<ResponseEntity<ApiError>> {
        log.info(MSG_TEMPLATE, ex)
        val apiError: ApiError = errorFactory.error(ex)
        ServerRequestObservationContext.findCurrent(exchange.attributes)
            .ifPresent { context -> context.setError(ex) }

        return Mono.just(ResponseEntity<ApiError>(apiError, HttpStatus.valueOf(apiError.httpStatus)))
    }

    override fun handleExceptionInternal(
        ex: Exception, @Nullable body: Any?, @Nullable headers: HttpHeaders?, status: HttpStatusCode,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Any>> {
        log.info(MSG_TEMPLATE, ex)
        return Mono.just(
            ResponseEntity.badRequest().header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(errorFactory.error(ex))
        )
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(RestExceptionHandler::class.java)
        const val CONTENT_TYPE: String = "Content-type"
        const val MSG_TEMPLATE: String = "About to handle an exception"
    }
}