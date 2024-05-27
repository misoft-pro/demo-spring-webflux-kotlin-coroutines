package pro.misoft.poc.springreactive.kotlin.business.model.account

enum class AccountType(val label: String) {
    FIAT("Money"),
    CRYPTO("Crypto");

    companion object {
        fun of(type: String): AccountType {
            return entries.find { it.label.equals(type, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown account type: $type")
        }
    }
}