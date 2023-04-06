package com.example.android.politicalpreparedness.representative.database

import com.example.android.politicalpreparedness.representative.model.Representative

interface RepresentativesRepository {
    suspend fun insert(representatives: List<Representative>)
    suspend fun getStoredList(): List<Representative>
}
