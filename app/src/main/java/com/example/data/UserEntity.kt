package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String, // email or mobile number as unique ID
    val name: String,
    val phone: String,
    val passwordHash: String // simplified plaintext or hash for local authenticator
)
