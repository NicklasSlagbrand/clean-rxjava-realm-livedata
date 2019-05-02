package com.nicklasslagbrand.placeholder.data.repository

import com.nicklasslagbrand.placeholder.data.entity.RealmAttraction
import com.nicklasslagbrand.placeholder.data.entity.converter.RealmAttractionConverter
import com.nicklasslagbrand.placeholder.domain.error.NothingInCache
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where

interface LocalAttractionsRepository {
    fun getAllAttractions(): Observable<List<Attraction>>
    fun getAttractionById(id: Int): Observable<Attraction>
    fun getAttractionsByIds(ids: List<Int>): Observable<List<Attraction>>
    fun storeAllAttractions(attractions: List<Attraction>): Observable<List<Attraction>>

    class RealmAttractionsRepository : LocalAttractionsRepository {
        override fun getAttractionsByIds(ids: List<Int>): Observable<List<Attraction>> {
            return Observable.fromCallable {
                Realm.getDefaultInstance().use { realm ->
                    val realmResults = realm.where<RealmAttraction>()
                        .`in`("id", ids.toTypedArray())
                        .findAll()

                    return@fromCallable ids.mapNotNull {
                        realmResults.where()
                            .equalTo("id", it)
                            .findFirst()
                    }.map {
                        RealmAttractionConverter.toAttraction(it)
                    }
                }
            }
        }

        override fun getAttractionById(id: Int): Observable<Attraction> {
            return Observable.fromCallable {
                Realm.getDefaultInstance().use { realm ->
                    val foundAttraction = realm.where<RealmAttraction>()
                        .equalTo("id", id)
                        .findFirst()

                    if (foundAttraction == null) {
                        throw NothingInCache()
                    } else {
                        RealmAttractionConverter.toAttraction(foundAttraction)
                    }
                }
            }
        }

        override fun storeAllAttractions(attractions: List<Attraction>): Observable<List<Attraction>> {
            return Observable.fromCallable {
                Realm.getDefaultInstance().use { realm ->
                    realm.executeTransaction {
                        attractions.forEach { attraction ->
                            val realmAttraction = RealmAttractionConverter.fromAttraction(attraction)
                            realm.insertOrUpdate(realmAttraction)
                        }
                    }
                }

                return@fromCallable attractions
            }
        }

        override fun getAllAttractions(): Observable<List<Attraction>> {
            return Observable.fromCallable {
                Realm.getDefaultInstance().use { realm ->
                    val realmResults = realm.where<RealmAttraction>().findAll()
                    return@fromCallable handleQueryResults(realmResults, realm)
                }
            }
        }

        private fun handleQueryResults(realmResults: RealmResults<RealmAttraction>, realm: Realm): List<Attraction> {
            if (realmResults.isEmpty()) {
                throw NothingInCache()
            } else {
                val realmAttractions = realm.copyFromRealm(realmResults)
                return realmAttractions.map {
                    RealmAttractionConverter.toAttraction(it)
                }
            }
        }
    }
}
