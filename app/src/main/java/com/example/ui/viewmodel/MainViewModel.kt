package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository

    // Current logged in user
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Auth flows
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // Database state flows
    val allUsers: StateFlow<List<UserEntity>>
    val allProducts: StateFlow<List<ProductEntity>>
    val allCustomers: StateFlow<List<CustomerEntity>>
    val allOrders: StateFlow<List<OrderEntity>>
    val allPayments: StateFlow<List<PaymentEntity>>
    val allLogs: StateFlow<List<ActivityLogEntity>>

    // Cart management state
    val cartItems = mutableStateMapOf<ProductEntity, Int>()
    var cartCustomDiscountPercent by mutableDoubleStateOf(0.0)

    // Dark Mode Toggle
    var isDarkMode by mutableStateOf(true)

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database)

        allUsers = repository.getAllUsers().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allProducts = repository.getAllProducts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allCustomers = repository.getAllCustomers().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allOrders = repository.getAllOrders().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allPayments = repository.getAllPayments().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allLogs = repository.getAllLogs().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Prepopulate with gorgeous high-fidelity data if the database is brand new and empty
        viewModelScope.launch {
            allUsers.first { true } // wait for initial load
            val currentProductList = repository.getAllProducts().first()
            if (currentProductList.isEmpty()) {
                prepopulateDatabase()
            }
        }
    }

    private suspend fun prepopulateDatabase() {
        // 1. Prepopulate default users with distinct roles
        val admin = UserEntity(
            name = "Sarah Jenkins",
            email = "admin@order.com",
            phone = "9876543210",
            password = "admin123",
            role = "Admin",
            avatar = "Sarah"
        )
        val sales = UserEntity(
            name = "Alex Carter",
            email = "sales@order.com",
            phone = "8765432109",
            password = "sales123",
            role = "Sales Executive",
            avatar = "Alex"
        )
        val retailer = UserEntity(
            name = "Metro Distributors",
            email = "retailer@order.com",
            phone = "7654321098",
            password = "retailer123",
            role = "Distributor/Retailer",
            shopName = "Metro Mega Depot",
            ownerName = "John Doe",
            avatar = "Metro"
        )
        repository.insertUser(admin)
        repository.insertUser(sales)
        repository.insertUser(retailer)

        // 2. Prepopulate products (Categories: Electronics, Groceries, Clothing, Stationery)
        val p1 = ProductEntity(name = "Samsung OLED TV 55\"", category = "Electronics", imageUrl = "tv", price = 48000.0, stock = 15, description = "Vibrant 55-inch Ultra HD Smart TV with deep contrasts and AI processor.", discountPercent = 10.0, gstPercent = 18.0)
        val p2 = ProductEntity(name = "Beats Wireless Pro", category = "Electronics", imageUrl = "headphones", price = 4500.0, stock = 42, description = "Acoustically tuned wireless headphones with active noise cancellation.", discountPercent = 5.0, gstPercent = 18.0)
        val p3 = ProductEntity(name = "Premium Basmati Rice 5kg", category = "Groceries", imageUrl = "rice", price = 680.0, stock = 200, description = "Aged long-grain aromatic rice perfect for feasts.", discountPercent = 0.0, gstPercent = 5.0)
        val p4 = ProductEntity(name = "Organic Cold Pressed Sunflower Oil 1L", category = "Groceries", imageUrl = "oil", price = 180.0, stock = 150, description = "100% natural pure oil, packed with essential nutrients.", discountPercent = 2.0, gstPercent = 5.0)
        val p5 = ProductEntity(name = "Signature Leather Jacket", category = "Clothing", imageUrl = "jacket", price = 3200.0, stock = 28, description = "Genuine classic black leather jacket, tailored comfortable fit.", discountPercent = 15.0, gstPercent = 12.0)
        val p6 = ProductEntity(name = "Premium Hardcover Journal A5", category = "Stationery", imageUrl = "journal", price = 450.0, stock = 85, description = "Elegant ivory layout dotted paper with waterproof cover.", discountPercent = 0.0, gstPercent = 12.0)
        
        repository.insertProduct(p1)
        repository.insertProduct(p2)
        val riceId = repository.insertProduct(p3).toInt()
        val oilId = repository.insertProduct(p4).toInt()
        val jacketId = repository.insertProduct(p5).toInt()
        val journalId = repository.insertProduct(p6).toInt()

        // 3. Prepopulate customers
        val c1 = CustomerEntity(shopName = "SuperMart Plaza", ownerName = "Rajesh Kumar", mobileNumber = "9876543210", address = "123 MG Road, Bengaluru", gpsLocation = "12.9716,77.5946")
        val c2 = CustomerEntity(shopName = "Apex Digital Hub", ownerName = "Amit Shah", mobileNumber = "9823456781", address = "456 High Street, Mumbai", gpsLocation = "19.0760,72.8777")
        val c3 = CustomerEntity(shopName = "Metro Grocery Outlets", ownerName = "Sara Khan", mobileNumber = "9123456789", address = "789 Park Lane, Kolkata", gpsLocation = "22.5726,88.3639")
        val c4 = CustomerEntity(shopName = "Sagar General Stores", ownerName = "Sanjay Patil", mobileNumber = "9345678912", address = "321 Deccan Mall, Pune", gpsLocation = "18.5204,73.8567")

        val cust1Id = repository.insertCustomer(c1).toInt()
        val cust2Id = repository.insertCustomer(c2).toInt()
        val cust3Id = repository.insertCustomer(c3).toInt()
        val cust4Id = repository.insertCustomer(c4).toInt()

        // 4. Prepopulate Orders spanning the last month to make the charts beautiful!
        val now = System.currentTimeMillis()
        val dayInMs = 24 * 60 * 60 * 1000L

        // Order 1 (15 days ago, Delivered, Paid, UPI)
        val o1Id = repository.insertOrder(OrderEntity(
            customerId = cust1Id,
            customerName = "SuperMart Plaza",
            orderDate = now - (15 * dayInMs),
            status = "Delivered",
            totalAmount = 1450.0,
            discountAmount = 145.0,
            taxAmount = 156.6,
            netAmount = 1461.6,
            paymentStatus = "Paid",
            paymentMethod = "UPI",
            salesExecutiveName = "Alex Carter"
        )).toInt()
        repository.insertOrderItem(OrderItemEntity(orderId = o1Id, productId = riceId, productName = "Premium Basmati Rice 5kg", quantity = 2, unitPrice = 680.0, discountPercent = 10.0, gstPercent = 5.0, subtotal = 1224.0))

        // Order 2 (8 days ago, Delivered, Partial, Credit)
        val o2Id = repository.insertOrder(OrderEntity(
            customerId = cust2Id,
            customerName = "Apex Digital Hub",
            orderDate = now - (8 * dayInMs),
            status = "Delivered",
            totalAmount = 6400.0,
            discountAmount = 960.0,
            taxAmount = 652.8,
            netAmount = 6092.8,
            paymentStatus = "Partial",
            paymentMethod = "Credit",
            salesExecutiveName = "Alex Carter"
        )).toInt()
        repository.insertOrderItem(OrderItemEntity(orderId = o2Id, productId = jacketId, productName = "Signature Leather Jacket", quantity = 2, unitPrice = 3200.0, discountPercent = 15.0, gstPercent = 12.0, subtotal = 5440.0))

        // Order 3 (Today, Pending, Unpaid, Credit)
        val o3Id = repository.insertOrder(OrderEntity(
            customerId = cust3Id,
            customerName = "Metro Grocery Outlets",
            orderDate = now - (2 * 60 * 60 * 1000L), // 2 hours ago
            status = "Pending",
            totalAmount = 1800.0,
            discountAmount = 36.0,
            taxAmount = 88.2,
            netAmount = 1852.2,
            paymentStatus = "Unpaid",
            paymentMethod = "Credit",
            salesExecutiveName = "Alex Carter"
        )).toInt()
        repository.insertOrderItem(OrderItemEntity(orderId = o3Id, productId = oilId, productName = "Organic Cold Pressed Sunflower Oil 1L", quantity = 10, unitPrice = 180.0, discountPercent = 2.0, gstPercent = 5.0, subtotal = 1764.0))

        // Order 4 (3 days ago, Cancelled)
        val o4Id = repository.insertOrder(OrderEntity(
            customerId = cust4Id,
            customerName = "Sagar General Stores",
            orderDate = now - (3 * dayInMs),
            status = "Cancelled",
            totalAmount = 450.0,
            discountAmount = 0.0,
            taxAmount = 54.0,
            netAmount = 504.0,
            paymentStatus = "Unpaid",
            paymentMethod = "Cash",
            salesExecutiveName = "Alex Carter"
        )).toInt()
        repository.insertOrderItem(OrderItemEntity(orderId = o4Id, productId = journalId, productName = "Premium Hardcover Journal A5", quantity = 1, unitPrice = 450.0, discountPercent = 0.0, gstPercent = 12.0, subtotal = 450.0))

        // 5. Prepopulate payments
        repository.insertPayment(PaymentEntity(orderId = o1Id, customerId = cust1Id, customerName = "SuperMart Plaza", paymentDate = now - (15 * dayInMs), amount = 1461.6, method = "UPI", referenceNumber = "TXN873912803"))
        repository.insertPayment(PaymentEntity(orderId = o2Id, customerId = cust2Id, customerName = "Apex Digital Hub", paymentDate = now - (8 * dayInMs), amount = 4000.0, method = "Card", referenceNumber = "TXN992104812"))

        // 6. Prepopulate log
        repository.insertLog(ActivityLogEntity(userId = 1, userName = "Sarah Jenkins", userRole = "Admin", action = "System database initialized with pre-populated values.", timestamp = now))
    }

    // --- Authentication Actions ---
    fun loginWithEmail(email: String, pass: String): Boolean {
        _authError.value = null
        var success = false
        // Fetch matching user in scope
        val users = allUsers.value
        val match = users.find { it.email.trim().equals(email.trim(), ignoreCase = true) && it.password == pass }
        if (match != null) {
            _currentUser.value = match
            logActivity("User logged in with email: $email")
            success = true
        } else {
            _authError.value = "Invalid email or password."
        }
        return success
    }

    fun loginWithPhone(phone: String, otp: String): Boolean {
        _authError.value = null
        var success = false
        if (otp != "123456") {
            _authError.value = "Invalid OTP. Use '123456' for demo login."
            return false
        }
        val users = allUsers.value
        val match = users.find { it.phone.trim() == phone.trim() }
        if (match != null) {
            _currentUser.value = match
            logActivity("User logged in with Phone: $phone")
            success = true
        } else {
            // Create a temporary Sales Executive user for demo purpose if not exists
            viewModelScope.launch {
                val newUser = UserEntity(
                    name = "Sales Associate",
                    email = "temp_${Random.nextInt(1000)}@order.com",
                    phone = phone,
                    password = "123",
                    role = "Sales Executive"
                )
                repository.insertUser(newUser)
                _currentUser.value = newUser
                logActivity("Temporary Sales user created and logged in with phone: $phone")
            }
            success = true
        }
        return success
    }

    fun logout() {
        logActivity("User logged out: ${_currentUser.value?.email}")
        _currentUser.value = null
    }

    // --- Logs ---
    fun logActivity(action: String) {
        val user = _currentUser.value
        viewModelScope.launch {
            repository.insertLog(ActivityLogEntity(
                userId = user?.id ?: 0,
                userName = user?.name ?: "Guest",
                userRole = user?.role ?: "Guest",
                action = action
            ))
        }
    }

    // --- Customer CRUD ---
    fun addCustomer(shopName: String, ownerName: String, mobileNumber: String, address: String, gpsLocation: String, photoUri: String? = null) {
        viewModelScope.launch {
            repository.insertCustomer(CustomerEntity(
                shopName = shopName,
                ownerName = ownerName,
                mobileNumber = mobileNumber,
                address = address,
                gpsLocation = gpsLocation,
                photoUri = photoUri
            ))
            logActivity("Added customer: $shopName ($ownerName)")
        }
    }

    fun updateCustomer(customer: CustomerEntity) {
        viewModelScope.launch {
            repository.insertCustomer(customer)
            logActivity("Updated customer details: ${customer.shopName}")
        }
    }

    fun deleteCustomer(customer: CustomerEntity) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            logActivity("Deleted customer: ${customer.shopName}")
        }
    }

    // --- Product CRUD ---
    fun addProduct(name: String, category: String, price: Double, stock: Int, description: String, discountPercent: Double, gstPercent: Double, imageUrl: String = "tv") {
        viewModelScope.launch {
            repository.insertProduct(ProductEntity(
                name = name,
                category = category,
                price = price,
                stock = stock,
                description = description,
                discountPercent = discountPercent,
                gstPercent = gstPercent,
                imageUrl = imageUrl
            ))
            logActivity("Added product: $name under $category")
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.insertProduct(product)
            logActivity("Updated product: ${product.name}")
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            logActivity("Deleted product: ${product.name}")
        }
    }

    // --- User Administration CRUD ---
    fun addUser(name: String, email: String, phone: String, role: String) {
        viewModelScope.launch {
            repository.insertUser(UserEntity(
                name = name,
                email = email,
                phone = phone,
                password = "123", // default password
                role = role
            ))
            logActivity("Admin added system user: $name ($role)")
        }
    }

    fun deleteUser(user: UserEntity) {
        viewModelScope.launch {
            repository.deleteUser(user)
            logActivity("Admin deleted user: ${user.name}")
        }
    }

    // --- Cart Actions & Computed Totals ---
    fun addToCart(product: ProductEntity, quantity: Int = 1) {
        val currentQty = cartItems[product] ?: 0
        val targetQty = currentQty + quantity
        if (targetQty > product.stock) {
            cartItems[product] = product.stock
        } else if (targetQty <= 0) {
            cartItems.remove(product)
        } else {
            cartItems[product] = targetQty
        }
    }

    fun updateCartQuantity(product: ProductEntity, quantity: Int) {
        if (quantity <= 0) {
            cartItems.remove(product)
        } else if (quantity > product.stock) {
            cartItems[product] = product.stock
        } else {
            cartItems[product] = quantity
        }
    }

    fun clearCart() {
        cartItems.clear()
        cartCustomDiscountPercent = 0.0
    }

    // Cart calculations mapping
    fun getCartSummary(): CartSummary {
        var subtotal = 0.0
        var itemDiscounts = 0.0
        var gstAmount = 0.0

        for ((product, qty) in cartItems) {
            val originalTotal = product.price * qty
            val discVal = originalTotal * (product.discountPercent / 100.0)
            val taxableItemAmount = originalTotal - discVal
            val itemGst = taxableItemAmount * (product.gstPercent / 100.0)

            subtotal += originalTotal
            itemDiscounts += discVal
            gstAmount += itemGst
        }

        val priceAfterItemDiscounts = subtotal - itemDiscounts
        val customDiscountAmount = priceAfterItemDiscounts * (cartCustomDiscountPercent / 100.0)
        val finalTaxableAmount = priceAfterItemDiscounts - customDiscountAmount

        // Re-apportion GST based on final discounted value
        val ratio = if (priceAfterItemDiscounts > 0) finalTaxableAmount / priceAfterItemDiscounts else 1.0
        val finalGstAmount = gstAmount * ratio

        val netTotal = finalTaxableAmount + finalGstAmount

        return CartSummary(
            subtotal = subtotal,
            productDiscounts = itemDiscounts,
            customDiscount = customDiscountAmount,
            taxAmount = finalGstAmount,
            netTotal = netTotal
        )
    }

    // --- Check out & Order Placement ---
    fun checkoutCart(customerId: Int, customerName: String, paymentMethod: String): Int {
        if (cartItems.isEmpty()) return -1

        val summary = getCartSummary()
        val order = OrderEntity(
            customerId = customerId,
            customerName = customerName,
            orderDate = System.currentTimeMillis(),
            status = "Pending",
            totalAmount = summary.subtotal,
            discountAmount = summary.productDiscounts + summary.customDiscount,
            taxAmount = summary.taxAmount,
            netAmount = summary.netTotal,
            paymentStatus = if (paymentMethod == "Credit") "Unpaid" else "Paid",
            paymentMethod = paymentMethod,
            salesExecutiveName = currentUser.value?.name ?: "Sales Executive"
        )

        var insertedOrderId = -1
        viewModelScope.launch {
            val ordId = repository.insertOrder(order).toInt()
            insertedOrderId = ordId

            // Insert line items and reduce stock
            for ((prod, qty) in cartItems) {
                val subtotal = (prod.price * qty) * (1 - prod.discountPercent/100.0)
                repository.insertOrderItem(OrderItemEntity(
                    orderId = ordId,
                    productId = prod.id,
                    productName = prod.name,
                    quantity = qty,
                    unitPrice = prod.price,
                    discountPercent = prod.discountPercent,
                    gstPercent = prod.gstPercent,
                    subtotal = subtotal
                ))

                // Reduce stock
                val updatedProduct = prod.copy(stock = (prod.stock - qty).coerceAtLeast(0))
                repository.insertProduct(updatedProduct)
            }

            // If Paid directly, log payment transaction
            if (paymentMethod != "Credit") {
                repository.insertPayment(PaymentEntity(
                    orderId = ordId,
                    customerId = customerId,
                    customerName = customerName,
                    amount = summary.netTotal,
                    method = paymentMethod,
                    referenceNumber = "AUTO_${System.currentTimeMillis() % 1000000}"
                ))
            }

            logActivity("Placed order #$ordId for $customerName. Method: $paymentMethod")
            clearCart()
        }

        return insertedOrderId
    }

    // --- Payment Recording ---
    fun addPayment(customerId: Int, customerName: String, orderId: Int?, amount: Double, method: String, reference: String?) {
        viewModelScope.launch {
            repository.insertPayment(PaymentEntity(
                orderId = orderId,
                customerId = customerId,
                customerName = customerName,
                paymentDate = System.currentTimeMillis(),
                amount = amount,
                method = method,
                referenceNumber = reference
            ))

            // If this payment settles an outstanding order
            if (orderId != null) {
                val order = repository.getOrderById(orderId)
                if (order != null) {
                    val updatedOrder = order.copy(paymentStatus = "Paid")
                    repository.insertOrder(updatedOrder)
                }
            }
            logActivity("Recorded payment of ₹$amount from $customerName via $method")
        }
    }

    // --- Order Cancel/Status Edit ---
    fun cancelOrder(order: OrderEntity) {
        viewModelScope.launch {
            val updatedOrder = order.copy(status = "Cancelled")
            repository.insertOrder(updatedOrder)

            // Restock items
            val items = repository.getOrderItemsForOrderList(order.id)
            for (item in items) {
                val product = repository.getProductById(item.productId)
                if (product != null) {
                    repository.insertProduct(product.copy(stock = product.stock + item.quantity))
                }
            }

            logActivity("Cancelled Order #${order.id} for ${order.customerName}. Re-stocked items.")
        }
    }

    fun deliverOrder(order: OrderEntity) {
        viewModelScope.launch {
            val updatedOrder = order.copy(status = "Delivered")
            repository.insertOrder(updatedOrder)
            logActivity("Delivered Order #${order.id} for ${order.customerName}")
        }
    }
}

data class CartSummary(
    val subtotal: Double,
    val productDiscounts: Double,
    val customDiscount: Double,
    val taxAmount: Double,
    val netTotal: Double
)
