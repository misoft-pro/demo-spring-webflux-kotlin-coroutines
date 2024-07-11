package pro.misoft.poc.springreactive.kotlin.business.usecases

import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pro.misoft.poc.springreactive.kotlin.business.model.account.AccountService
import pro.misoft.poc.springreactive.kotlin.business.model.market.PriceService
import pro.misoft.poc.springreactive.kotlin.business.model.portfolio.Portfolio
import pro.misoft.poc.springreactive.kotlin.infra.spring.controller.contract.PortfolioOverview

@Component
class PortfolioUseCases(
    private val accountService: AccountService,
    private val priceService: PriceService,
) {
    private val log: Logger = LoggerFactory.getLogger(PortfolioUseCases::class.java)

    suspend fun getPortfolioOverview(userId: String, referenceCurrency: String): PortfolioOverview {
        log.info("Get Portfolio Overview is started by user=$userId")
        val accounts = withContext(MDCContext()) { accountService.getAllAccounts(userId) }

        val portfolioOverview = withContext(MDCContext()) {
            Portfolio(accounts).allValuation(referenceCurrency, priceService).let {
                PortfolioOverview(it.items, it.total())
            }
        }
        log.info("Get Portfolio Overview is completed by user=$userId")
        return portfolioOverview
    }
}