package pro.misoft.poc.springreactive.kotlin.infra.spring.filter

import io.micrometer.context.ThreadLocalAccessor
import java.util.*

object LocaleThreadLocalAccessor : ThreadLocalAccessor<Locale> {

    private val LOCALE_THREAD_LOCAL = ThreadLocal<Locale>()

    override fun key(): String {
        return LOCALE_KEY
    }

    override fun getValue(): Locale {
        val value = LOCALE_THREAD_LOCAL.get()
        return value ?: Locale.US
    }

    override fun setValue(value: Locale) {
        LOCALE_THREAD_LOCAL.set(value)
    }

    override fun restore(originalValue: Locale) {
        LOCALE_THREAD_LOCAL.set(originalValue)
    }

    @Deprecated("Deprecated in Java")
    override fun reset() {
        LOCALE_THREAD_LOCAL.remove()
    }
}
