package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TailorDao {
    // User accounts
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerUser(user: UserEntity)

    // Orders
    @Query("SELECT * FROM orders WHERE userEmail = :userEmail ORDER BY orderNumber DESC")
    fun getAllOrdersForUser(userEmail: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderNumber = :orderNumber LIMIT 1")
    fun getOrderByNumber(orderNumber: String): Flow<OrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("SELECT MAX(orderNumber) FROM orders")
    suspend fun getMaxOrderNumber(): String?
}
