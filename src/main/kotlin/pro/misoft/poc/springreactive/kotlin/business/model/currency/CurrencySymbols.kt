package pro.misoft.poc.springreactive.kotlin.business.model.currency

import java.util.*

object CurrencySymbols {

    private val customSymbols = mapOf(
        "CHF" to "Fr.",
    )

    fun getCurrencySymbol(currencyCode: String, locale: Locale): String {
        if (CryptoCurrencies.codeSymbols.containsKey(currencyCode)) {
            return CryptoCurrencies.codeSymbols[currencyCode]!!
        }
        if (customSymbols.containsKey(currencyCode)) {
            return customSymbols[currencyCode]!!
        }
        val currency = Currency.getInstance(currencyCode)
        return currency.getSymbol(locale)
    }
}