package com.yunusemrearslan.kotlinandroidserviceexamples.db

import android.provider.BaseColumns

class LocationNoteContract {

    companion object{
        val DB_NAME = ""
        var DB_VERSION =1

    }
    class LocationEntry:BaseColumns{
        companion object {
            val TABLE ="location"
            val COL_LONGITUDE ="longitude"
            val COL_LATITUDE ="latitude"
            val COL_TITLE = "title"
            val _ID =BaseColumns._ID

        }
    }

}