import java.util.Date

data class TransactionDetail(
    val Id: String,              // Transaction document ID
    val amount: String,    // Transaction amount as String
    val category: String,        // Transaction category
    val paymentType: String,// Payment type
    val createdAt: Date = Date()
)
