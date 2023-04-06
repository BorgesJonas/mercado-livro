package com.mercadolivro.controller

import com.mercadolivro.controller.request.PostPurchaseRequest
import com.mercadolivro.service.PurchaseService.PurchaseService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController("/purchase")
class PurchaseController(
    purchaseService: PurchaseService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun purchase(@RequestBody request: PostPurchaseRequest) {
    }
}