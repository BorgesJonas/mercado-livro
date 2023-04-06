package com.mercadolivro.controller.response

data class FieldErrorResponse(
    var message: Comparable<*>,
    var field: String,
)
