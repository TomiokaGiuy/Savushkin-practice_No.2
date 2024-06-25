package com.example.savushkin_practice_no2.Domain.Repository

import androidx.lifecycle.LiveData
import com.example.savushkin_practice_no2.Domain.Models.NS_SEMK

interface StorageRepository {
    suspend fun insertData(ns_semk:  List<NS_SEMK>)
    fun getAllData(): LiveData<List<NS_SEMK>>
    suspend fun deleteAllData()
    suspend fun getFirstItem(): NS_SEMK?
}