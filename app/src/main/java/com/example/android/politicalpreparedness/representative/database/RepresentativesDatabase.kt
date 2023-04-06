package com.example.android.politicalpreparedness.representative.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.android.politicalpreparedness.representative.model.RepresentativesList

@Database(entities = [RepresentativesList::class], version = 2, exportSchema = false)
@TypeConverters(RepresentativesConverter::class)
abstract class RepresentativesDatabase : RoomDatabase() {

    abstract val representativesDao: RepresentativesDao

    companion object {

        @Volatile
        private var INSTANCE: RepresentativesDatabase? = null

        fun getInstance(context: Context): RepresentativesDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RepresentativesDatabase::class.java,
                        "representatives_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}
