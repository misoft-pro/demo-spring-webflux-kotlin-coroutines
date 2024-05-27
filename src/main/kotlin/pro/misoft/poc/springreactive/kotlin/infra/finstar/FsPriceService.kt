package pro.misoft.poc.springreactive.kotlin.infra.finstar

import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import pro.misoft.poc.springreactive.kotlin.business.model.market.PriceService
import java.math.BigDecimal

@Component
class FsPriceService(webClient: WebClient.Builder, @Value("\${forex.baseurl}") forexBaseUrl: String) : PriceService {

    private val log: Logger = LoggerFactory.getLogger(FsPriceService::class.java)
    private val client = webClient.baseUrl(forexBaseUrl).build()

    //TODO cache quote in Redis
    override suspend fun quote(fromCurrency: String, toCurrency: String): BigDecimal {
        val rate = client.get()
            .uri("/rates/${fromCurrency}-${toCurrency}")
            .retrieve()
            .bodyToMono(FsQuote::class.java)
            .map { quote -> quote.mid }
            .awaitSingle()
        log.info("Price Service successfully retrieved rate for $fromCurrency to $toCurrency")
        return rate
    }
}