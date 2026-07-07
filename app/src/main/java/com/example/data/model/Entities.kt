package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String, // "Admin", "Sales Executive", "Distributor/Retailer"
    val shopName: String? = null,
    val ownerName: String? = null,
    val avatar: String = ""
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val imageUrl: String,
    val price: Double,
    val stock: Int,
    val description: String,
    val discountPercent: Double = 0.0,
    val gstPercent: Double = 18.0 // default GST rate in India is commonly 18%
)

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val shopName: String,
    val ownerName: String,
    val mobileNumber: String,
    val address: String,
    val gpsLocation: String, // format "latitude,longitude"
    val photoUri: String? = null
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerId: Int,
    val customerName: String,
    val orderDate: Long = System.currentTimeMillis(),
    val status: String, // "Pending", "Delivered", "Cancelled"
    val totalAmount: Double,
    val discountAmount: Double,
    val taxAmount: Double,
    val netAmount: Double,
    val paymentStatus: String, // "Paid", "Unpaid", "Partial"
    val paymentMethod: String, // "Cash", "UPI", "Card", "Credit"
    val salesExecutiveName: String
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: Int,
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val discountPercent: Double,
    val gstPercent: Double,
    val subtotal: Double
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: Int? = null,
    val customerId: Int,
    val customerName: String,
    val paymentDate: Long = System.currentTimeMillis(),
    val amount: Double,
    val method: String, // "Cash", "UPI", "Card", "Credit"
    val referenceNumber: String? = null
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val userName: String,
    val userRole: String,
    val action: String,
    val timestamp: Long = System.currentTimeMillis()
)
