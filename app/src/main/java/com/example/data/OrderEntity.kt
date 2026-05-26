package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderNumber: String, // "000001", "000002" etc.
    val userEmail: String, // Belongs to which tailor
    val customerName: String,
    val mobileNumber: String,
    val address: String?,
    val givenDate: String,
    val deliveryDate: String,
    val totalAmount: Double,
    val advancePaid: Double,
    val balanceAmount: Double, // totalAmount - advancePaid
    
    // Shirt Measurements (numbers only, nullable to support empty states)
    val shirtChest: Double?,
    val shirtShoulder: Double?,
    val shirtSleeve: Double?,
    val shirtLength: Double?,
    val shirtNeck: Double?,
    
    // Pant Measurements
    val pantWaist: Double?,
    val pantHip: Double?,
    val pantThigh: Double?,
    val pantKnee: Double?,
    val pantBottom: Double?,
    val pantLength: Double?,
    
    // Image paths/URIs
    val images: List<String>
)
