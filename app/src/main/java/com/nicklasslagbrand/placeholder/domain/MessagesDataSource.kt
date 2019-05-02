package com.nicklasslagbrand.placeholder.domain

import com.nicklasslagbrand.placeholder.data.repository.RemoteMessagesRepository
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.domain.model.Message
import com.nicklasslagbrand.placeholder.extension.toResult
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable

interface MessagesDataSource {
    fun getAllMessages(): Observable<Result<List<Message>, Error>>

    class DefaultMessagesDataSource(
        private val remoteProductsRepository: RemoteMessagesRepository
    ) : MessagesDataSource {
        override fun getAllMessages(): Observable<Result<List<Message>, Error>> {
            return remoteProductsRepository.getAllMessages()
                .toResult()
        }
    }
}
