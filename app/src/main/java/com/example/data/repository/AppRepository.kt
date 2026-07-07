package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class AppRepository(private val db: AppDatabase) {
    private val userDao = db.userDao()
    private val productDao = db.productDao()
    private val customerDao = db.customerDao()
    private val orderDao = db.orderDao()
    private val paymentDao = db.paymentDao()
    private val activityLogDao = db.activityLogDao()

    // --- Users ---
    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email)
    suspend fun getUserByPhone(phone: String): UserEntity? = userDao.getUserByPhone(phone)
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()
    suspend fun insertUser(user: UserEntity): Long = userDao.insertUser(user)
    suspend fun deleteUser(user: UserEntity) = userDao.deleteUser(user)

    // --- Products ---
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()
    suspend fun getProductById(id: Int): ProductEntity? = productDao.getProductById(id)
    suspend fun insertProduct(product: ProductEntity): Long = productDao.insertProduct(product)
    suspend fun deleteProduct(product: ProductEntity) = productDao.deleteProduct(product)

    // --- Customers ---
    fun getAllCustomers(): Flow<List<CustomerEntity>> = customerDao.getAllCustomers()
    suspend fun getCustomerById(id: Int): CustomerEntity? = customerDao.getCustomerById(id)
    suspend fun insertCustomer(customer: CustomerEntity): Long = customerDao.insertCustomer(customer)
    suspend fun deleteCustomer(customer: CustomerEntity) = customerDao.deleteCustomer(customer)

    // --- Orders ---
    fun getAllOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrders()
    suspend fun getOrderById(id: Int): OrderEntity? = orderDao.getOrderById(id)
    suspend fun insertOrder(order: OrderEntity): Long = orderDao.insertOrder(order)
    suspend fun updateOrderStatus(orderId: Int, status: String) = orderDao.updateOrderStatus(orderId, status)
    suspend fun insertOrderItem(orderItem: OrderItemEntity) = orderDao.insertOrderItem(orderItem)
    fun getOrderItemsForOrder(orderId: Int): Flow<List<OrderItemEntity>> = orderDao.getOrderItemsForOrder(orderId)
    suspend fun getOrderItemsForOrderList(orderId: Int): List<OrderItemEntity> = orderDao.getOrderItemsForOrderList(orderId)
    suspend fun deleteOrder(order: OrderEntity) = orderDao.deleteOrder(order)

    // --- Payments ---
    fun getAllPayments(): Flow<List<PaymentEntity>> = paymentDao.getAllPayments()
    fun getPaymentsForCustomer(customerId: Int): Flow<List<PaymentEntity>> = paymentDao.getPaymentsForCustomer(customerId)
    suspend fun insertPayment(payment: PaymentEntity): Long = paymentDao.insertPayment(payment)

    // --- Activity Logs ---
    fun getAllLogs(): Flow<List<ActivityLogEntity>> = activityLogDao.getAllLogs()
    suspend fun insertLog(log: ActivityLogEntity): Long = activityLogDao.insertLog(log)
}
