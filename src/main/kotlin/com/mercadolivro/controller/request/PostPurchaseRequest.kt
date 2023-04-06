package com.mercadolivro.controller.request

import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

data class PostPurchaseRequest(
    @field:NotNull
    @field:Positive
    val customerId: Int,

    @field:NotNull
    val bookIds: Set<Int> // Set no List cause it ignores duplicated values and only cares about the first one
)