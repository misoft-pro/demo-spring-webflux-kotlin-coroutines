package pro.misoft.poc.springreactive.kotlin.infra.finstar

import io.micrometer.core.annotation.Timed
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component
import pro.misoft.poc.springreactive.kotlin.business.model.account.Account
import pro.misoft.poc.springreactive.kotlin.business.model.account.AccountService
import pro.misoft.poc.springreactive.kotlin.business.model.account.AccountType
import java.math.BigDecimal

@Component
class FsAccountService(
    private val accountApiClient: FinstarApiClient
) : AccountService {

    @Timed("custom.api.calls.downstream")
    override suspend fun getAllAccounts(userId: String): List<Account> = coroutineScope {
        val accounts = accountApiClient.getAllAccounts(userId)
        accounts.map { a ->
            async {
                val balanceString = accountApiClient.getBalanceByAccount(a.id)
                Account(a.id, a.currency, BigDecimal(balanceString), AccountType.of(a.domain))
            }
        }.awaitAll()
    }
}