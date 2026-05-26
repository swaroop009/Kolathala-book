package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.OrderEntity
import com.example.data.TailorRepository
import com.example.data.UserEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TailorViewModel(private val repository: TailorRepository) : ViewModel() {

    // Authentication session state
    private val _loggedUser = MutableStateFlow<UserEntity?>(null)
    val loggedUser: StateFlow<UserEntity?> = _loggedUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _registrationSuccess = MutableStateFlow(false)
    val registrationSuccess: StateFlow<Boolean> = _registrationSuccess.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Orders for the logged in user
    val userOrders: StateFlow<List<OrderEntity>> = _loggedUser
        .flatMapLatest { user ->
            if (user != null) {
                repository.getAllOrdersForUser(user.email)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filtered orders list based on search bar contents
    val filteredOrders: StateFlow<List<OrderEntity>> = combine(userOrders, _searchQuery) { orders, query ->
        if (query.isBlank()) {
            orders
        } else {
            val q = query.trim().lowercase()
            orders.filter { order ->
                order.customerName.lowercase().contains(q) ||
                order.mobileNumber.contains(q) ||
                order.orderNumber.contains(q)
            }
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearAuthError() {
        _authError.value = null
    }

    fun resetRegistrationState() {
        _registrationSuccess.value = false
    }

    // Handles user login
    fun login(email: String, passwordRaw: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _authError.value = null
            if (email.isBlank() || passwordRaw.isBlank()) {
                _authError.value = "Please fill in all fields"
                return@launch
            }
            val user = repository.getUserByEmail(email.trim().lowercase())
            if (user != null && user.passwordHash == passwordRaw) {
                _loggedUser.value = user
                onSuccess()
            } else {
                _authError.value = "Invalid email or password"
            }
        }
    }

    // Handles user signup
    fun signUp(email: String, name: String, phone: String, passwordRaw: String) {
        viewModelScope.launch {
            _authError.value = null
            _registrationSuccess.value = false
            if (email.isBlank() || name.isBlank() || phone.isBlank() || passwordRaw.isBlank()) {
                _authError.value = "All fields are required"
                return@launch
            }
            val existing = repository.getUserByEmail(email.trim().lowercase())
            if (existing != null) {
                _authError.value = "A user with this email/username already exists"
                return@launch
            }
            val newUser = UserEntity(
                email = email.trim().lowercase(),
                name = name.trim(),
                phone = phone.trim(),
                passwordHash = passwordRaw
            )
            repository.registerUser(newUser)
            _registrationSuccess.value = true
        }
    }

    // Handles logout
    fun logout(onSuccess: () -> Unit) {
        _loggedUser.value = null
        _searchQuery.value = ""
        onSuccess()
    }

    fun getOrderDetails(orderNumber: String): Flow<OrderEntity?> {
        return repository.getOrderByNumber(orderNumber)
    }

    suspend fun getNextOrderNumber(): String {
        return repository.generateNextOrderNumber()
    }

    // Insert new order
    fun createOrder(
        customerName: String,
        mobileNumber: String,
        address: String?,
        givenDate: String,
        deliveryDate: String,
        totalAmount: Double,
        advancePaid: Double,
        shirtChest: Double?,
        shirtShoulder: Double?,
        shirtSleeve: Double?,
        shirtLength: Double?,
        shirtNeck: Double?,
        pantWaist: Double?,
        pantHip: Double?,
        pantThigh: Double?,
        pantKnee: Double?,
        pantBottom: Double?,
        pantLength: Double?,
        images: List<String>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val user = _loggedUser.value
            if (user == null) {
                onError("No active user session found.")
                return@launch
            }
            if (customerName.isBlank() || mobileNumber.isBlank()) {
                onError("Customer Name and Mobile Number are required.")
                return@launch
            }
            try {
                val nextNum = repository.generateNextOrderNumber()
                val balance = totalAmount - advancePaid
                val newOrder = OrderEntity(
                    orderNumber = nextNum,
                    userEmail = user.email,
                    customerName = customerName.trim(),
                    mobileNumber = mobileNumber.trim(),
                    address = address?.trim()?.ifEmpty { null },
                    givenDate = givenDate.trim(),
                    deliveryDate = deliveryDate.trim(),
                    totalAmount = totalAmount,
                    advancePaid = advancePaid,
                    balanceAmount = if (balance < 0.0) 0.0 else balance,
                    shirtChest = shirtChest,
                    shirtShoulder = shirtShoulder,
                    shirtSleeve = shirtSleeve,
                    shirtLength = shirtLength,
                    shirtNeck = shirtNeck,
                    pantWaist = pantWaist,
                    pantHip = pantHip,
                    pantThigh = pantThigh,
                    pantKnee = pantKnee,
                    pantBottom = pantBottom,
                    pantLength = pantLength,
                    images = images
                )
                repository.insertOrder(newOrder)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to save order")
            }
        }
    }
}

class TailorViewModelFactory(private val repository: TailorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TailorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TailorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
