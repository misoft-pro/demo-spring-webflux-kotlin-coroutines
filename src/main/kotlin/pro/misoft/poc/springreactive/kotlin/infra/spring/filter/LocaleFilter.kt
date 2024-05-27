package pro.misoft.poc.springreactive.kotlin.infra.spring.filter

import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.*

const val LOCALE_KEY = "locale"
val DEFAULT_LOCALE: Locale = Locale.UK

@Component
class LocaleFilter : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val langParam = exchange.request.headers.getFirst(HttpHeaders.ACCEPT_LANGUAGE)
        val locale = langParam?.let { Locale.forLanguageTag(it) } ?: DEFAULT_LOCALE
        LocaleThreadLocalAccessor.setValue(locale)
        return chain.filter(exchange)
            .contextWrite { context -> context.put(LOCALE_KEY, locale) }
            .doFinally { LocaleThreadLocalAccessor.reset() }
    }
}
