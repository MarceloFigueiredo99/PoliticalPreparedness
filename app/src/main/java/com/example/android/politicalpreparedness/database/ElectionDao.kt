package com.example.android.politicalpreparedness.database

import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg elections: Election)

    @Query("select * from election_table")
    suspend fun getAllElections(): List<Election>

    @Query("select * from election_table where id = :id")
    suspend fun getElectionById(id: Int): Election?

    @Delete
    suspend fun delete(election: Election)

    @Query("delete from election_table")
    suspend fun clearElections()
}
