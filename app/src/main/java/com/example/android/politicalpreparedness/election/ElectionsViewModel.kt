package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionsRepository
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

private const val TAG = "#PLP ElectionsViewModel"

class ElectionsViewModel(
    private val repository: ElectionsRepository
) : ViewModel() {

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    private fun populateUpcomingElections() {
        viewModelScope.launch {
            val elections = CivicsApi.retrofitService.getElections().elections
            Log.i(TAG, "Elections retriveved from api: $elections")
            _upcomingElections.value = elections
        }
    }

    private fun populateSavedElections() {
        viewModelScope.launch {
            _savedElections.value = repository.getAllElections()
        }
    }

    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo: LiveData<Election>
        get() = _navigateToVoterInfo

    fun onElectionClicked(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun onVoterIntoNavigated() {
        _navigateToVoterInfo.value = null
    }

    fun refreshAdapters() {
        Log.i(TAG, "Adapters going to be refreshed")
        populateUpcomingElections()
        populateSavedElections()
    }
}
