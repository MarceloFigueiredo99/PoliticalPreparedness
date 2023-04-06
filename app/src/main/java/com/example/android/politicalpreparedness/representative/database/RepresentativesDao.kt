package com.example.android.politicalpreparedness.representative.database

import androidx.room.*
import com.example.android.politicalpreparedness.representative.model.RepresentativesList

@Dao
interface RepresentativesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg representatives: RepresentativesList)

    @Query("select * from representative_table ORDER BY id DESC LIMIT 1")
    suspend fun getStoredList(): RepresentativesList
}
