package pro.misoft.poc.springreactive.kotlin.infra.finstar

import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class FinstarApiClient(webClientBuilder: WebClient.Builder, @Value("\${finstar.baseurl}") finstarBaseUrl: String) {

    private val log: Logger = LoggerFactory.getLogger(FinstarApiClient::class.java)

    private val client = webClientBuilder
        .baseUrl(finstarBaseUrl)
        .build()

    suspend fun getAllAccounts(userId: String): List<FsAccount> {
        log.info("Fetching accounts for userId: $userId")
        val accounts = client.get()
            .uri("/users/{userId}/accounts", userId)
            .retrieve()
            .bodyToFlux(FsAccount::class.java)
            .collectList()
            .awaitSingle()
        log.info("Successfully fetched ${accounts.size} accounts for userId: $userId")
        return accounts
    }

    suspend fun getBalanceByAccount(accountId: String): String {
        return client.get()
            .uri("/accounts/$accountId/balance")
            .retrieve()
            .bodyToMono(String::class.java)
            .awaitSingle()
    }
}