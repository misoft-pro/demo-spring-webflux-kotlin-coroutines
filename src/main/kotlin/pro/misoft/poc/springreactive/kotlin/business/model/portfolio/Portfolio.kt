package pro.misoft.poc.springreactive.kotlin.business.model.portfolio

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.javamoney.moneta.Money
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pro.misoft.poc.springreactive.kotlin.business.model.account.Account
import pro.misoft.poc.springreactive.kotlin.business.model.account.AccountType
import pro.misoft.poc.springreactive.kotlin.business.model.market.PriceService
import javax.money.Monetary
import javax.money.MonetaryAmount

class Portfolio(private val accounts: List<Account>) {

    private val log: Logger = LoggerFactory.getLogger(Portfolio::class.java)

    suspend fun allValuation(refCurrency: String, priceService: PriceService): Valuation = coroutineScope {

        Valuation(
            refCurrency,
            accounts.map { a ->
                async {
                    log.info("Portfolio item is about to be calculated async")
                    newPortfolioItem(a, refCurrency, priceService)
                }
            }.awaitAll()
        )
    }


    private suspend fun newPortfolioItem(
        account: Account,
        refCurrency: String,
        priceService: PriceService
    ) = PortfolioItem(
        account.id,
        account.type,
        Money.of(account.balance, account.currency),
        account.marketValue(refCurrency, priceService)
    )

    class Valuation(private val refCurrency: String, val items: List<PortfolioItem>) {
        fun total(): MonetaryAmount {
            return items.map { it.marketValue }
                .fold(Money.zero(Monetary.getCurrency(refCurrency))) { acc, value -> acc.add(value) }
        }
    }
}

data class PortfolioItem(
    val id: String,
    val type: AccountType,
    val balance: MonetaryAmount,
    val marketValue: MonetaryAmount
)
