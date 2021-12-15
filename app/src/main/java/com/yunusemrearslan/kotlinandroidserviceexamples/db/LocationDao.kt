package com.yunusemrearslan.kotlinandroidserviceexamples.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.yunusemrearslan.kotlinandroidserviceexamples.model.LocationModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
@Dao
interface LocationDao {
    @Query("SELECT * FROM LocationModel")
    fun getAll() : Flowable<List<LocationModel>>

    @Insert
    fun insert(locationModel: LocationModel): Completable

    @Delete
    fun  delete(locationModel: LocationModel) : Completable
}