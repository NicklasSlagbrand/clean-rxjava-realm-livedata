package com.nicklasslagbrand.placeholder.data.repository

import com.nicklasslagbrand.placeholder.data.network.PlaceholderApiWrapper
import com.nicklasslagbrand.placeholder.domain.model.Message
import io.reactivex.Observable

interface RemoteMessagesRepository {
    fun getAllMessages(): Observable<List<Message>>

    class NetworkMessageRepository(private val placeholderApiWrapper: PlaceholderApiWrapper)
        : RemoteMessagesRepository {
        override fun getAllMessages(): Observable<List<Message>> = placeholderApiWrapper.fetchAllMessages()
    }
}
