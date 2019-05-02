package com.nicklasslagbrand.placeholder.data.network

import com.nicklasslagbrand.placeholder.data.network.body.ActivateCardBody
import com.nicklasslagbrand.placeholder.data.network.body.CreateOrderBody
import com.nicklasslagbrand.placeholder.data.network.body.ProductOrderBody
import com.nicklasslagbrand.placeholder.data.network.body.TransferCardBody
import com.nicklasslagbrand.placeholder.domain.model.Order
import com.nicklasslagbrand.placeholder.domain.usecase.params.CreateOrderParam
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Path

class PlaceholderApiWrapper(
    private val commonRequestParams: CommonRequestParams,
    private val placeholderApi: PlaceholderApi
) {
    private val jsonParamValue: String = "json"

    fun fetchAllProducts() = placeholderApi.fetchAllProducts()

    fun fetchAllMessages() = placeholderApi.fetchAllMessages()

    fun fetchAllAttractions(content: String) = placeholderApi.fetchAllAttractions(content)

    fun fetchAllUserOrders(): Observable<List<Order>> {
        val query = "${commonRequestParams.language},${commonRequestParams.deviceId},$jsonParamValue"

        return placeholderApi.fetchAllUserOrders(query)
    }

    fun activateCard(@Path("card_id") cardId: Int, activationDate: String):
        Observable<Response<Void>> {
        val body = ActivateCardBody(commonRequestParams.deviceId, activationDate)

        return fetchToken()
            .flatMap { token ->
                placeholderApi.activateCard(token, cardId, body)
            }
    }

    private fun fetchToken(): Observable<String> {
        return placeholderApi.fetchToken()
    }

    fun createOrder(orderParam: CreateOrderParam, deliveryType: String): Observable<String> {
        val productOrdersBody = orderParam.orderProducts.map {
            ProductOrderBody(it.productsAmount, it.productId)
        }
        val body = CreateOrderBody(
            commonRequestParams.deviceId,
            orderParam.orderName,
            orderParam.orderEmail,
            deliveryType,
            productOrdersBody
        )

        return placeholderApi.createOrder(body).map {
            it.orderReference
        }
    }

    fun transferCard(cardId: Int, receiverDeviceId: String): Observable<Int> {
        val params = TransferCardBody(receiverDeviceId, commonRequestParams.deviceId)
        return fetchToken().flatMap {
            placeholderApi.transferCard(it, cardId, params)
        }.map {
            it.cardId
        }
    }
}
