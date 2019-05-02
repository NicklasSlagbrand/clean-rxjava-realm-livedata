package com.nicklasslagbrand.placeholder.domain.usecase.params

data class CreateOrderParam(
    val orderName: String,
    val orderEmail: String,
    val orderProducts: List<ProductOrder>
) {
    @SuppressWarnings("ThrowsCount")
    fun validate() {
        orderName.ifBlank {
            throw IllegalArgumentException("'orderName' should not be empty")
        }
        orderEmail.ifBlank {
            throw IllegalArgumentException("'orderEmail' should not be empty")
        }
        orderProducts.ifEmpty {
            throw IllegalArgumentException("'orderProducts' should not be empty")
        }
        orderProducts.onEach {
            it.validate()
        }
    }
}

data class ProductOrder(val productsAmount: Int, val productId: Int) {
    fun validate() {
        if (productsAmount <= 0) {
            throw IllegalArgumentException("'productsAmount' should be greater then zero")
        }
        if (productId == 0) {
            throw IllegalArgumentException("'productId' should not be zero")
        }
    }
}
