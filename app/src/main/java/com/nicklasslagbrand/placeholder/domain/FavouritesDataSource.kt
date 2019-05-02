package com.nicklasslagbrand.placeholder.domain

import com.nicklasslagbrand.placeholder.data.repository.LocalAttractionsRepository
import com.nicklasslagbrand.placeholder.data.repository.LocalFavouritesRepository
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.domain.model.Favourite
import com.nicklasslagbrand.placeholder.extension.toResult
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

interface FavouritesDataSource {
    fun saveAttractionIdToFavourites(attractionId: Int): Observable<Result<Any, Error>>
    fun getAllFavouritesItems(): Observable<Result<List<Favourite>, Error>>
    fun getAllFavouritesIds(): Observable<Result<List<Int>, Error>>

    fun removeFavouriteByAttractionId(attractionId: Int): Observable<Result<Any, Error>>
    fun isAttractionInFavourites(attractionId: Int): Observable<Result<Boolean, Error>>

    fun getAttractionsInFavourites(): Observable<Result<List<Attraction>, Error>>

    class DefaultFavouritesDataSource(
        private val localFavouritesRepository: LocalFavouritesRepository,
        private val localAttractionsRepository: LocalAttractionsRepository
    ) : FavouritesDataSource {
        override fun saveAttractionIdToFavourites(attractionId: Int): Observable<Result<Any, Error>> {
            val timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
            val favourite = Favourite(attractionId, timeStamp)

            return localFavouritesRepository.saveFavouriteItem(favourite)
                .toResult()
        }

        override fun getAllFavouritesItems(): Observable<Result<List<Favourite>, Error>> {
            return localFavouritesRepository.getAllFavourites().map { items ->
                items.sortedBy { it.timestamp }
            }.toResult()
        }

        override fun getAllFavouritesIds(): Observable<Result<List<Int>, Error>> {
            return localFavouritesRepository.getAllFavouriteIds().toResult()
        }

        override fun removeFavouriteByAttractionId(attractionId: Int): Observable<Result<Any, Error>> {
            return localFavouritesRepository.removeFavourite(attractionId)
                .toResult()
        }

        override fun isAttractionInFavourites(attractionId: Int): Observable<Result<Boolean, Error>> {
            return localFavouritesRepository.getFavourite(attractionId)
                .map { true }
                .onErrorReturn { false }
                .toResult()
        }

        override fun getAttractionsInFavourites(): Observable<Result<List<Attraction>, Error>> {
            return getAllFavouritesItems().flatMap { result ->
                return@flatMap if (result is Result.Success) {
                    val ids = result.value.map { it.attractionId }
                    localAttractionsRepository.getAttractionsByIds(ids)
                        .toResult()
                } else {
                    Observable.just(Result.success(emptyList()))
                }
            }
        }
    }
}
