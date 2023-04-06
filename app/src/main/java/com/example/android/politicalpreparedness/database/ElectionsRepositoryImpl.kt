package com.example.android.politicalpreparedness.database

import android.util.Log
import com.example.android.politicalpreparedness.network.models.Election

private const val TAG = "#PLP ElectionsRepository"

class ElectionsRepositoryImpl(
    private val electionsDatabase: ElectionDatabase
) : ElectionsRepository {
    override suspend fun insert(elections: Election) {
        Log.i(TAG, "Insert election $elections")
        electionsDatabase.electionDao.insert(elections)
    }

    override suspend fun getAllElections(): List<Election> {
        Log.i(TAG, "getAllElections")
        return electionsDatabase.electionDao.getAllElections()
    }

    override suspend fun getElectionById(id: Int): Election? {
        Log.i(TAG, "Get election with id $id")
        return electionsDatabase.electionDao.getElectionById(id)
    }

    override suspend fun delete(election: Election) {
        Log.i(TAG, "Delete election $election")
        electionsDatabase.electionDao.delete(election)
    }

    override suspend fun clearElections() {
        Log.i(TAG, "clearElections")
        electionsDatabase.electionDao.clearElections()
    }
}
