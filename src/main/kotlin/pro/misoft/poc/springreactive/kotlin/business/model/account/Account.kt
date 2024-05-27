package pro.misoft.poc.springreactive.kotlin.business.model.account

import pro.misoft.poc.springreactive.kotlin.business.model.market.PriceService
import org.javamoney.moneta.Money
import java.math.BigDecimal
import javax.money.MonetaryAmount

class Account(val id: String, val currency: String, val balance: BigDecimal, val type: AccountType) {

    suspend fun marketValue(refCurrency: String, priceService: PriceService): MonetaryAmount =
        if (currency != refCurrency) {
            val rate = priceService.quote(currency, refCurrency)
            Money.of(balance.multiply(rate), refCurrency)
        } else {
            Money.of(balance, refCurrency)
        }
}

