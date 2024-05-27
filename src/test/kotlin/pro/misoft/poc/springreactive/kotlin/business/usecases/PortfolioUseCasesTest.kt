package pro.misoft.poc.springreactive.kotlin.business.usecases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.javamoney.moneta.Money
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pro.misoft.poc.springreactive.kotlin.business.model.market.PriceService
import pro.misoft.poc.springreactive.kotlin.infra.finstar.FinstarApiClient
import pro.misoft.poc.springreactive.kotlin.infra.finstar.FsAccountMother
import java.math.BigDecimal

@SpringBootTest
class PortfolioUseCasesTest {

    @MockkBean
    private lateinit var priceService: PriceService

    @MockkBean
    private lateinit var finstarApiClient: FinstarApiClient

    @Autowired
    private lateinit var portfolioUseCases: PortfolioUseCases

    @Test
    fun `should retrieve portfolio overview with total balance`() = runTest {
        val currency = "EUR"
        val refCurrency = "USD"
        val balance = BigDecimal(100)
        val fsAccount = FsAccountMother.newAccount(currency)
        val rate = BigDecimal.TEN
        val userId = "testUserIdToBeResolvedFromJWT"
        coEvery { priceService.quote(currency, refCurrency) } coAnswers { rate }
        coEvery { finstarApiClient.getAllAccounts(userId) } coAnswers { listOf(fsAccount) }
        coEvery { finstarApiClient.getBalanceByAccount(fsAccount.id) } coAnswers { balance.toString() }

        val (items, totalMarketValue) = portfolioUseCases.getPortfolioOverview(userId, refCurrency)

        assertThat(items).hasSize(1)
        assertThat(totalMarketValue).isEqualTo(Money.of(balance.multiply(rate), refCurrency))
    }
}