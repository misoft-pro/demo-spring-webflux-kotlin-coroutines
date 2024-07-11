//package pro.misoft.poc.springreactive.kotlin.business.model.portfolio
//
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.test.advanceUntilIdle
//import kotlinx.coroutines.test.currentTime
//import kotlinx.coroutines.test.runTest
//import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
//import org.javamoney.moneta.Money
//import org.junit.jupiter.api.Test
//import pro.misoft.poc.springreactive.kotlin.business.model.account.AccountMother.newAccount
//import pro.misoft.poc.springreactive.kotlin.business.model.market.PriceService
//import java.math.BigDecimal
//
//private const val CHF = "CHF"
//private const val EUR = "EUR"
//
//private val RATES = mapOf(
//    CHF to BigDecimal("1.1"),
//    EUR to BigDecimal("1.08"),
//)
//private const val DELAY_MS = 1000L
//
//@OptIn(ExperimentalCoroutinesApi::class)
//class PortfolioTest {
//
//    @Test
//    fun `should return correct valuation of Portfolio with many accounts using async approach`() = runTest {
//        val balanceCHF = BigDecimal(100)
//        val balanceEUR = BigDecimal(100)
//        val refCurrency = "USD"
//        val accounts = listOf(newAccount("CHF", balanceCHF), newAccount("EUR", balanceEUR))
//        val sut = Portfolio(accounts, any())
//        val virtualStartTime = currentTime
//
//        val valuation = sut.allValuation(refCurrency, TestPriceService)
//        advanceUntilIdle()
//
//        assertThat(valuation.total()).isEqualTo(
//            Money.of(
//                RATES[CHF]?.multiply(balanceCHF)?.add(RATES[EUR]?.multiply(balanceEUR)), refCurrency
//            )
//        )
//        assertThat(currentTime - virtualStartTime).isLessThan(DELAY_MS * accounts.size)
//    }
//}
//
//object TestPriceService : PriceService {
//    override suspend fun quote(fromCurrency: String, toCurrency: String): BigDecimal {
//        delay(DELAY_MS)
//        return RATES[fromCurrency] ?: error("Rate is not configured in test for currency: $fromCurrency")
//    }
//}
