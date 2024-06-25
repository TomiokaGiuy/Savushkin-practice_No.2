package com.example.savushkin_practice_no2.Presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.savushkin_practice_no2.Domain.Repository.DatabaseRepository
import com.example.savushkin_practice_no2.Domain.Repository.ParserRepository

class MainViewModelFactory(
    private val fileRepository: ParserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(fileRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}