package pro.misoft.poc.springreactive.kotlin.infra.spring.errorhandling

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.server.ServerWebInputException
import java.nio.file.attribute.UserPrincipalNotFoundException

object RestErrorMapping {
    private val log: Logger = LoggerFactory.getLogger(RestErrorMapping::class.java)

    private const val COMMON_PREFIX = "common-"
    private const val USER_PREFIX = "user-"
    private val UNSUPPORTED = ApiErrorKeys(HttpStatus.SERVICE_UNAVAILABLE, COMMON_PREFIX + 5001, "errors.common")
    private val BAD_REQUEST =
        ApiErrorKeys(HttpStatus.BAD_REQUEST, COMMON_PREFIX + 4002, "errors.common.illegalargument")
    private val UNPROCESSABLE_ENTITY =
        ApiErrorKeys(HttpStatus.UNPROCESSABLE_ENTITY, COMMON_PREFIX + 4003, "errors.common.illegalstate")
    private val exceptionsMap: MutableMap<Class<out Exception>, ApiErrorKeys> = HashMap()

    init {
        exceptionsMap[UserPrincipalNotFoundException::class.java] =
            ApiErrorKeys(HttpStatus.NOT_FOUND, USER_PREFIX + 6001, "errors.user.user-not-found")
    }

    fun getKeys(exception: Exception): ApiErrorKeys {
        var keys = exceptionsMap[exception.javaClass]
        if (keys == null) {
            log.debug("No translation is explicitly configured for exception={}", exception.javaClass.name)
            keys = when (exception) {
                is IllegalArgumentException, is HandlerMethodValidationException, is ServerWebInputException -> {
                    BAD_REQUEST
                }

                is IllegalStateException -> {
                    UNPROCESSABLE_ENTITY
                }

                else -> {
                    UNSUPPORTED
                }
            }
        }
        return keys
    }
}

data class ApiErrorKeys(val httpStatus: HttpStatus, val internalCode: String, val i18nKey: String)