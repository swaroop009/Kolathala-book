package com.example.data

import kotlinx.coroutines.flow.Flow

class TailorRepository(private val tailorDao: TailorDao) {

    suspend fun getUserByEmail(email: String): UserEntity? {
        return tailorDao.getUserByEmail(email)
    }

    suspend fun registerUser(user: UserEntity) {
        tailorDao.registerUser(user)
    }

    fun getAllOrdersForUser(userEmail: String): Flow<List<OrderEntity>> {
        return tailorDao.getAllOrdersForUser(userEmail)
    }

    fun getOrderByNumber(orderNumber: String): Flow<OrderEntity?> {
        return tailorDao.getOrderByNumber(orderNumber)
    }

    suspend fun insertOrder(order: OrderEntity) {
        tailorDao.insertOrder(order)
    }

    suspend fun generateNextOrderNumber(): String {
        val maxNum = tailorDao.getMaxOrderNumber()
        val nextInt = if (maxNum != null) {
            maxNum.toIntOrNull()?.plus(1) ?: 1
        } else {
            1
        }
        return String.format("%06d", nextInt)
    }
}
