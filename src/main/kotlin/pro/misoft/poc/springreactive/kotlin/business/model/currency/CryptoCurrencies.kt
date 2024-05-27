package pro.misoft.poc.springreactive.kotlin.business.model.currency

import org.javamoney.moneta.CurrencyUnitBuilder
import org.javamoney.moneta.spi.ConfigurableCurrencyUnitProvider
import javax.money.CurrencyUnit


private const val providerName = "Crypto"

private const val BTC_CODE = "BTC"

private const val ETH_CODE = "ETH"

private const val AVAX_CODE = "AVAX"

private const val DOT_CODE = "DOT"

object CryptoCurrencies {

    val codeSymbols: Map<String, String> = mapOf(
        BTC_CODE to BTC_CODE,
        ETH_CODE to ETH_CODE,
        AVAX_CODE to AVAX_CODE,
        DOT_CODE to DOT_CODE,
    )

    val RATES: Map<String, Double> = mapOf(
        BTC_CODE + "EUR" to 62098.53,
        ETH_CODE + "EUR" to 3410.62,
        AVAX_CODE + "EUR" to 35.60,
        DOT_CODE + "EUR" to 6.59,

        BTC_CODE + "CHF" to 61585.91,
        ETH_CODE + "CHF" to 3382.47,
        AVAX_CODE + "CHF" to 37.01,
        DOT_CODE + "CHF" to 6.82,
    )

    val BTC: CurrencyUnit = CurrencyUnitBuilder.of(BTC_CODE, providerName)
        .setDefaultFractionDigits(8)
        .build(true)

    val ETH: CurrencyUnit = CurrencyUnitBuilder.of(ETH_CODE, providerName)
        .setDefaultFractionDigits(18)
        .build(true)

    val AVAX: CurrencyUnit = CurrencyUnitBuilder.of(AVAX_CODE, providerName)
        .setDefaultFractionDigits(9)
        .build(true)

    val DOT: CurrencyUnit = CurrencyUnitBuilder.of(DOT_CODE, providerName)
        .setDefaultFractionDigits(10)
        .build(true)

    fun initialize() {
        ConfigurableCurrencyUnitProvider.registerCurrencyUnit(BTC)
        ConfigurableCurrencyUnitProvider.registerCurrencyUnit(ETH)
        ConfigurableCurrencyUnitProvider.registerCurrencyUnit(AVAX)
        ConfigurableCurrencyUnitProvider.registerCurrencyUnit(DOT)
    }
}