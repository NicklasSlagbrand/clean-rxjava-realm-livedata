package com.nicklasslagbrand.placeholder.data.repository

import com.nicklasslagbrand.placeholder.data.entity.RealmFavourite
import com.nicklasslagbrand.placeholder.data.entity.converter.RealmFavouriteConverter
import com.nicklasslagbrand.placeholder.domain.error.NothingInCache
import com.nicklasslagbrand.placeholder.domain.model.Favourite
import io.reactivex.Observable
import io.realm.Realm
import io.realm.kotlin.where

interface LocalFavouritesRepository {
    fun getAllFavourites(): Observable<List<Favourite>>
    fun getAllFavouriteIds(): Observable<List<Int>>
    fun saveFavouriteItem(favourite: Favourite): Observable<Any>
    fun getFavourite(attractionId: Int): Observable<Favourite?>
    fun removeFavourite(attractionId: Int): Observable<Any>

    class RealmLocalFavouritesRepository : LocalFavouritesRepository {
        override fun removeFavourite(attractionId: Int): Observable<Any> {
            return Observable.fromCallable {
                Realm.getDefaultInstance().use {
                    it.executeTransaction { realm ->
                        realm.where<RealmFavourite>()
                            .equalTo("attractionId", attractionId)
                            .findAll()
                            .deleteAllFromRealm()
                    }

                    return@fromCallable Any()
                }
            }
        }

        override fun getAllFavourites(): Observable<List<Favourite>> {
            return Observable.fromCallable {
                Realm.getDefaultInstance().use {
                    val realmResults = it.where<RealmFavourite>().findAll()
                    val realmFavourites = it.copyFromRealm(realmResults)
                    return@fromCallable realmFavourites.map { realFavourite ->
                        RealmFavouriteConverter.toFavourite(realFavourite)
                    }
                }
            }
        }

        override fun getAllFavouriteIds(): Observable<List<Int>> {
            return Observable.fromCallable {
                Realm.getDefaultInstance().use {
                    val realmResults = it.where<RealmFavourite>().findAll()
                    val realmFavourites = it.copyFromRealm(realmResults)
                    return@fromCallable realmFavourites.map { realFavourite ->
                        RealmFavouriteConverter.toFavouriteId(realFavourite)
                    }
                }
            }
        }

        override fun saveFavouriteItem(favourite: Favourite): Observable<Any> {
            return Observable.fromCallable {
                Realm.getDefaultInstance().use { realm ->
                    realm.executeTransaction {
                        val realmAttraction = RealmFavouriteConverter.fromFavourite(favourite)
                        realm.insertOrUpdate(realmAttraction)
                    }
                }

                return@fromCallable Any()
            }
        }

        override fun getFavourite(attractionId: Int): Observable<Favourite?> {
            return Observable.fromCallable {
                Realm.getDefaultInstance().use { realm ->
                    val foundFavourite = realm.where<RealmFavourite>()
                        .equalTo("attractionId", attractionId)
                        .findFirst()

                    if (foundFavourite == null) {
                        throw NothingInCache()
                    } else {
                        RealmFavouriteConverter.toFavourite(foundFavourite)
                    }
                }
            }
        }
    }
}
