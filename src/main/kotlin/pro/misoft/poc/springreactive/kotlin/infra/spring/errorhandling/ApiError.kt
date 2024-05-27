package pro.misoft.poc.springreactive.kotlin.infra.spring.errorhandling

import org.slf4j.MDC
import org.springframework.http.HttpStatus

/**
 * Example:
 * `{
 *    "httpStatus": 400,
 *    "internalCode":"portfolio-4001",
 *    "errorMessage":"Input fields contain errors",
 *    "traceId":"7f006775-04b5-4f81-8250-a85ffb976722",
 *    "subErrors":[
 *    {
 *        "objectName":"orderDto",
 *        "fieldName":"userId",
 *        "rejectedValue":"1",
 *        "message":"size must be between 2 and 8"
 *    }
 *    ]
 * }`
 */
data class ApiError(
    val httpStatus: Int,
    /**
     * Internal code to classify error
     *
     * pattern="${serviceNamePrefix}-${httpErrorCategory}${sequenceNumberUniqueForServiceNameAndHttpErrorCode}".
     *
     * examples=["portfolio-4001", "portfolio-4002","order-5001", "user-4001", "user-4002", "user-5001"]
     */
    val internalCode: String,
    /**
     * Human-readable localized message to display on client side
     */
    val errorMessage: String,
    /**
     * Unique identifier of user request.
     * In case of distributed architecture this identifier is passed to all downstream requests to other services.
     */
    val traceId: String,
    /**
     * Collect information about sub errors,
     * for example specific fields of forms providing human-readable error messages for each field to guide user trough out a flow
     */
    val subErrors: List<ApiSubError> = listOf()
) {
    constructor (status: HttpStatus, errorCode: String, errorMessage: String, traceId: String) : this(
        status.value(),
        errorCode,
        errorMessage,
        traceId,
        emptyList<ApiSubError>()
    )

    constructor (status: HttpStatus, errorCode: String, errorMessage: String) : this(
        status.value(),
        errorCode,
        errorMessage,
        MDC.get("traceId"),
        emptyList<ApiSubError>()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ApiError
        if (internalCode != other.internalCode) return false
        if (errorMessage != other.errorMessage) return false
        if (subErrors != other.subErrors) return false
        return true
    }

    override fun hashCode(): Int {
        var result = internalCode.hashCode()
        result = 31 * result + errorMessage.hashCode()
        result = 31 * result + subErrors.hashCode()
        return result
    }
}