package com.mercadolivro.enums

enum class Errors(val code: String, val message: String) {
    ML0001("ML-0001", "Fields errors"),
    ML1001("ML-1001", "Book [%s] doesn't exists"),
    ML1002("ML-1002", "Cannot update book with the status [%s]"),
    ML1102("ML-1102", "Customer with the id [%s] doesn't exist")
}