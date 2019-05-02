package com.nicklasslagbrand.placeholder.data.network

import com.nicklasslagbrand.placeholder.data.network.body.ActivateCardBody
import com.nicklasslagbrand.placeholder.data.network.body.CreateOrderBody
import com.nicklasslagbrand.placeholder.data.network.body.TransferCardBody
import com.nicklasslagbrand.placeholder.data.network.response.CreateOrderResponse
import com.nicklasslagbrand.placeholder.data.network.response.TransferCardResponse
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.domain.model.Message
import com.nicklasslagbrand.placeholder.domain.model.Order
import com.nicklasslagbrand.placeholder.domain.model.Product
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaceholderApi {
    @POST("/api/order/create")
    fun createOrder(@Body body: CreateOrderBody): Observable<CreateOrderResponse>

    @GET("/rest/session/token")
    fun fetchToken(): Observable<String>

    @GET("/api/products")
    fun fetchAllProducts(): Observable<List<Product>>

    @GET("/api/notifications")
    fun fetchAllMessages(): Observable<List<Message>>

    @GET("/api/attractions")
    fun fetchAllAttractions(
        @Query("content") content: String
    ): Observable<List<Attraction>>

    @GET("/api/cards")
    fun fetchAllUserOrders(@Query("") query: String): Observable<List<Order>>

    @POST("/api/cards/{card_id}/activate")
    fun activateCard(
        @Header("X-CSRF-Token") csrfToken: String,
        @Path("card_id") cardId: Int,
        @Body activateCardBody: ActivateCardBody
    ): Observable<Response<Void>>

    @POST("/api/cards/{card_id}/transfer?_format=json")
    fun transferCard(
        @Header("X-CSRF-Token") csrfToken: String,
        @Path("card_id") cardId: Int,
        @Body transferCardBody: TransferCardBody
    ): Observable<TransferCardResponse>
}
