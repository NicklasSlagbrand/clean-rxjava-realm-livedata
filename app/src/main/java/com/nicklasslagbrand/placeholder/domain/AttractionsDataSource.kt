package com.nicklasslagbrand.placeholder.domain

import com.nicklasslagbrand.placeholder.data.repository.LocalAttractionsRepository
import com.nicklasslagbrand.placeholder.data.repository.RemoteAttractionsRepository
import com.nicklasslagbrand.placeholder.domain.error.NothingInCache
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.extension.toResult
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable

interface AttractionsDataSource {
    fun getAllAttractions(): Observable<Result<List<Attraction>, Error>>
    fun getAttractionById(id: Int): Observable<Result<Attraction, Error>>

    class DefaultAttractionsDataSource(
        private val remoteAttractionsRepository: RemoteAttractionsRepository,
        private val localAttractionsRepository: LocalAttractionsRepository
    ) : AttractionsDataSource {
        override fun getAttractionById(id: Int): Observable<Result<Attraction, Error>> {
            return localAttractionsRepository.getAttractionById(id)
                .toResult()
        }

        override fun getAllAttractions(): Observable<Result<List<Attraction>, Error>> {
            return localAttractionsRepository.getAllAttractions()
                .onErrorResumeNext { error: Throwable ->
                    if (error is NothingInCache) {
                        remoteAttractionsRepository.getAllAttractions()
                            .flatMap {
                                localAttractionsRepository.storeAllAttractions(it)
                            }
                    } else {
                        // If we face some unsupported error we just pass it further in the flow
                        Observable.error(error)
                    }
                }
                .toResult()
        }
    }
}
