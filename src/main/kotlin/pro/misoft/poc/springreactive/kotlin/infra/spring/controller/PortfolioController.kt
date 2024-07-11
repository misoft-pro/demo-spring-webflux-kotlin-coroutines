package pro.misoft.poc.springreactive.kotlin.infra.spring.controller

import jakarta.validation.constraints.Size
import org.javamoney.moneta.Money
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pro.misoft.poc.springreactive.kotlin.business.model.account.AccountType
import pro.misoft.poc.springreactive.kotlin.business.model.common.IdGenerator
import pro.misoft.poc.springreactive.kotlin.business.model.currency.CryptoCurrencies.AVAX
import pro.misoft.poc.springreactive.kotlin.business.model.currency.CryptoCurrencies.BTC
import pro.misoft.poc.springreactive.kotlin.business.model.currency.CryptoCurrencies.DOT
import pro.misoft.poc.springreactive.kotlin.business.model.currency.CryptoCurrencies.ETH
import pro.misoft.poc.springreactive.kotlin.business.model.currency.CryptoCurrencies.RATES
import pro.misoft.poc.springreactive.kotlin.business.model.portfolio.PortfolioItem
import pro.misoft.poc.springreactive.kotlin.business.usecases.PortfolioUseCases
import pro.misoft.poc.springreactive.kotlin.infra.spring.controller.contract.PortfolioOverview
import java.math.BigDecimal
import javax.money.CurrencyUnit
import javax.money.Monetary

@RestController
@RequestMapping("/v1/portfolio")
class PortfolioController(private val portfolioUseCases: PortfolioUseCases) {

    private val log: Logger = LoggerFactory.getLogger(PortfolioController::class.java)

    @GetMapping
    suspend fun getPortfolioOverview(
        @RequestParam @Size(min = 3, max = 3) currency: String
    ): PortfolioOverview {
        return portfolioUseCases.getPortfolioOverview("testUserIdToBeResolvedFromJWT", currency)
    }

    @GetMapping("/stub")
    fun getPortfolioOverviewStub(
        @RequestParam @Size(
            min = 3,
            max = 3
        ) currency: String
    ): PortfolioOverview {
        log.info("Getting portfolioOverview stub as a temporal solution for frontend integration")
        val items = listOf(
            newPortfolioItem(10.10, BTC, currency),
            newPortfolioItem(100.12, ETH, currency),
            newPortfolioItem(1000.14, DOT, currency),
            newPortfolioItem(100.16, AVAX, currency),
        )
        return PortfolioOverview(
            items,
            items.map { it.marketValue }
                .fold(Money.zero(Monetary.getCurrency(currency))) { acc, value -> acc.add(value) }
        )
    }

    private fun newPortfolioItem(balance: Double, itemCurrency: CurrencyUnit, refCurrency: String): PortfolioItem {
        val rate = RATES[itemCurrency.currencyCode + refCurrency]
            ?: error("Exchange rate is not stubbed for $itemCurrency to $refCurrency")
        return PortfolioItem(
            IdGenerator.uniqueNumber(), AccountType.CRYPTO, Money.of(BigDecimal.valueOf(balance), itemCurrency),
            Money.of(
                BigDecimal(balance * rate), refCurrency
            )
        )
    }
}