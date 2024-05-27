package pro.misoft.poc.springreactive.kotlin.business.model.currency

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.javamoney.moneta.Money
import org.junit.jupiter.api.Test
import java.util.*

class CustomMoneyFormatterTest {

    @Test
    fun `should format money according to user locale and currency code`() {
        val currencyCode = "CHF"
        val formatter = CustomMoneyFormatter(Locale.forLanguageTag("de-CH"), currencyCode)

        val result = formatter.format(Money.of(1_000_000_000.136, currencyCode))

        assertThat(result).isEqualTo("Fr. 1’000’000’000.14")
    }
}