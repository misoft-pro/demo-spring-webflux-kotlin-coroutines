package pro.misoft.poc.springreactive.kotlin.infra.spring.errorhandling

data class ApiSubError(
    val objectName: String,
    val fieldName: String,
    val rejectedValue: Any?,
    val message: String
)

