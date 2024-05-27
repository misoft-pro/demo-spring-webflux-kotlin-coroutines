package pro.misoft.poc.springreactive.kotlin.business.model.account

interface AccountService {
    suspend fun getAllAccounts(userId: String) : List<Account>
}
