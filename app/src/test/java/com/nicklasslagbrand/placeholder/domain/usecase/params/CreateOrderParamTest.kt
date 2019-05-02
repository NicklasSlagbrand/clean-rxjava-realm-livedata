package com.nicklasslagbrand.placeholder.domain.usecase.params

import io.mockk.mockk
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.junit.Test

class CreateOrderParamTest {

    @Test
    fun `test CreateOrderParam without name fails`() {
        val func = {
            CreateOrderParam(" ", "email", listOf(mockk())).validate()
        }

        func.shouldThrow(IllegalArgumentException::class)
            .withMessage("'orderName' should not be empty")
    }

    @Test
    fun `test CreateOrderParam without email fails`() {
        val func = {
            CreateOrderParam("name", " ", listOf(mockk())).validate()
        }

        func.shouldThrow(IllegalArgumentException::class)
            .withMessage("'orderEmail' should not be empty")
    }

    @Test
    fun `test CreateOrderParam without products fails`() {
        val func = {
            CreateOrderParam("name", "email", emptyList()).validate()
        }

        func.shouldThrow(IllegalArgumentException::class)
            .withMessage("'orderProducts' should not be empty")
    }

    @Test
    fun `test CreateOrderParam with one of the products that has 0 as id fails`() {
        val func = {
            CreateOrderParam(
                "name",
                "email",
                listOf(ProductOrder(1, 0), ProductOrder(1, 1))
            ).validate()
        }

        func.shouldThrow(IllegalArgumentException::class)
            .withMessage("'productId' should not be zero")
    }

    @Test
    fun `test product order validation fails if id is zero`() {
        val func = {
            ProductOrder(1, 0).validate()
        }

        func.shouldThrow(IllegalArgumentException::class)
            .withMessage("'productId' should not be zero")
    }

    @Test
    fun `test product order validation fails if amount is less or equal zero`() {
        val func = {
            ProductOrder(0, 1).validate()
        }

        func.shouldThrow(IllegalArgumentException::class)
            .withMessage("'productsAmount' should be greater then zero")
    }
}
