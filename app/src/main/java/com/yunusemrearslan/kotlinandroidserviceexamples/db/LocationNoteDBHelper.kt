package com.yunusemrearslan.kotlinandroidserviceexamples.db

import androidx.room.Database
import androidx.room.Entity
import androidx.room.RoomDatabase
import com.yunusemrearslan.kotlinandroidserviceexamples.model.LocationModel

@Database(entities = [LocationModel::class],version = 1)
abstract class LocationNoteDBHelper : RoomDatabase(){
    abstract fun locationDao():LocationDao
}