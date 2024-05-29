package pro.misoft.poc.springreactive.kotlin.infra.spring.errorhandling


import org.slf4j.MDC
import org.springframework.context.support.AbstractResourceBasedMessageSource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import pro.misoft.poc.springreactive.kotlin.business.model.common.BusinessException
import pro.misoft.poc.springreactive.kotlin.infra.spring.filter.LocaleThreadLocalAccessor

@Component
class ApiErrorFactory(private val msgSource: AbstractResourceBasedMessageSource) {

    fun error(ex: Exception): ApiError {
        val errorKeys: ApiErrorKeys = RestErrorMapping.getKeys(ex)
        val args: Array<String> = if (ex is BusinessException) ex.getArgs() else emptyArray()

        val userLocale = LocaleThreadLocalAccessor.getValue()
        val message = msgSource.getMessage(errorKeys.i18nKey, args, ex.message, userLocale)
        return ApiError(errorKeys.httpStatus, errorKeys.internalCode, message ?: error("Error message is not specified"))
    }

    fun error(ex: MethodArgumentNotValidException): ApiError {
        return error(ex.bindingResult.fieldErrors)
    }

    private fun error(fieldErrors: List<FieldError>): ApiError {
        return ApiError(
            HttpStatus.BAD_REQUEST.value(),
            "" + 4002,
            "Input fields contain errors",
            MDC.get("traceId"),
            fieldErrors.map { fe: FieldError ->
                ApiSubError(
                    fe.objectName,
                    fe.field,
                    fe.rejectedValue,
                    fe.defaultMessage ?: ""
                )
            }
        )
    }
}