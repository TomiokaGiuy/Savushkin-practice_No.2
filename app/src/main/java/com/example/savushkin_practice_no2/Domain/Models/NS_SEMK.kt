package com.example.savushkin_practice_no2.Domain.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ns_semk")
data class NS_SEMK(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val KMC: String?,
    val KRK: String?,
    val KT: String?,
    val EMK: String,
    val PR: String?,
    val KTARA: String,
    val GTIN: String? = null,
    val EMKPOD: String?
)
