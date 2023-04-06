package com.example.android.politicalpreparedness.database

import com.example.android.politicalpreparedness.network.models.Election

interface ElectionsRepository {
    suspend fun insert(election: Election)
    suspend fun getAllElections(): List<Election>
    suspend fun getElectionById(id: Int): Election?
    suspend fun delete(election: Election)
    suspend fun clearElections()
}
