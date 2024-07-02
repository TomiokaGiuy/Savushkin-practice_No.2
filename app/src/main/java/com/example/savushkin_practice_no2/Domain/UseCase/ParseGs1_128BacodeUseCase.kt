package com.example.savushkin_practice_no2.Domain.UseCase

import com.example.savushkin_practice_no2.Domain.Repository.ParserRepository

class ParseGs1_128BacodeUseCase(private val repository: ParserRepository) {
    fun invoke(barcode: String): Map<String, String>{
        return repository.parseGS1_128(barcode)
    }
}