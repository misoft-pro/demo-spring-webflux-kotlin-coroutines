package pro.misoft.poc.springreactive.kotlin.business.model.account

import io.mockk.coEvery
import io.mockk.mockk
import pro.misoft.poc.springreactive.kotlin.business.model.market.PriceService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.javamoney.moneta.Money
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class AccountTest {

    private val priceService: PriceService = mockk()

    @Test
    fun `should calculate properly account market value`() = runTest {
        val refCurrency = "USD"
        val currency = "EUR"
        val balance = BigDecimal(100)
        val account = AccountMother.newAccount(currency, balance)
        val rate = BigDecimal.ONE
        val delay: Long = 1000
        coEvery { priceService.quote(currency, refCurrency) } coAnswers {
            delay(delay)
            rate
        }
        val virtualStartTime = currentTime
        val times = 4

        repeat(times) {
            val marketValue = account.marketValue(refCurrency, priceService)
            assertThat(marketValue).isEqualTo(Money.of(balance.multiply(rate), refCurrency))
        }

        advanceUntilIdle()
        assertThat(currentTime - virtualStartTime).isGreaterThanOrEqualTo(times * delay)
    }
}
