package com.yunusemrearslan.kotlinandroidserviceexamples.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class LocationModel(
    @ColumnInfo(name = "title")
    var title:String,

    @ColumnInfo(name="longitude")
    var longitude:Double,

    @ColumnInfo(name="latitude")
    var latitude:Double,

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
)