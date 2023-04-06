package com.example.android.politicalpreparedness.representative.database

import android.util.Log
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.representative.model.RepresentativesList

private const val TAG = "#PLP RepresentativesRepository"

class RepresentativesRepositoryImpl(
    private val representativesDatabase: RepresentativesDatabase
) : RepresentativesRepository {
    override suspend fun insert(representatives: List<Representative>) {
        Log.i(TAG, "Insert representatives list ${RepresentativesList(list = representatives)}")
        representativesDatabase.representativesDao.insert(RepresentativesList(list = representatives))
    }

    override suspend fun getStoredList(): List<Representative> {
        var storedList = listOf<Representative>()
        representativesDatabase.representativesDao.getStoredList()?.let {
            storedList = it.list
        }
        Log.i(TAG, "Saved representatives list $storedList")
        return storedList
    }
}
