package com.mercadolivro.controller.request

import java.math.BigDecimal
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class PutBookRequest(
    @field:NotEmpty(message = "Nome deve ser informado")
    var name: String?,

    @field:NotNull(message = "Preço deve ser informado")
    var price: BigDecimal?
)
