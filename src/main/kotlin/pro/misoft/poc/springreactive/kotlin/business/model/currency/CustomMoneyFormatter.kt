package pro.misoft.poc.springreactive.kotlin.business.model.currency

import java.io.Serializable
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import javax.money.MonetaryAmount
import javax.money.format.AmountFormatContext
import javax.money.format.AmountFormatContextBuilder
import javax.money.format.MonetaryAmountFormat


class CustomMoneyFormatter(private val localeParam: Locale, private val currencyCodeParam: String) :
    MonetaryAmountFormat,
    Serializable {

    private val decimalFormat: DecimalFormat = createDecimalFormat()

    private fun createDecimalFormat(): DecimalFormat {
        val symbols = DecimalFormatSymbols(localeParam).apply {
            currencySymbol = CurrencySymbols.getCurrencySymbol(currencyCodeParam, localeParam)
        }
        val pattern = if (CryptoCurrencies.codeSymbols.containsKey(currencyCodeParam)) {
            "###,###.00 ¤"
        } else {
            "¤ ###,###.00"
        }
        return DecimalFormat(pattern, symbols).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
            isParseBigDecimal = true
            roundingMode = RoundingMode.HALF_UP
        }
    }

    override fun queryFrom(amount: MonetaryAmount): String {
        return format(amount)
    }

    override fun getContext(): AmountFormatContext {
        return AmountFormatContextBuilder.of("MonetaryAmountFormatSymbols").build()
    }

    override fun format(amount: MonetaryAmount): String {
        // Using the BigDecimal representation to ensure precision is respected
        val number = amount.number.numberValue(BigDecimal::class.java)
        return decimalFormat.format(number)
    }

    override fun print(appendable: java.lang.Appendable, amount: MonetaryAmount) {
        format(amount, appendable)
    }

    private fun format(amount: MonetaryAmount, appendable: Appendable) {
        appendable.append(format(amount))
    }

    override fun parse(text: CharSequence): MonetaryAmount {
        throw UnsupportedOperationException("Parsing is not supported.")
    }
}
