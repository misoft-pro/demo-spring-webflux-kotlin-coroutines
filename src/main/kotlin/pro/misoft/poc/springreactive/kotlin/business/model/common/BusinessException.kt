package pro.misoft.poc.springreactive.kotlin.business.model.common

import java.util.*

class BusinessException(errorMsgTemplate: String, vararg args: Any) :
    RuntimeException(String.format(errorMsgTemplate, *Arrays.stream(args).map { obj: Any -> obj.toString() }
        .toList().toTypedArray<String>())) {

    private val args: Array<Any> = arrayOf(args)

    fun getArgs(): Array<String> {
        return Arrays.stream(args).map { obj: Any -> obj.toString() }.toList().toTypedArray<String>()
    }
}