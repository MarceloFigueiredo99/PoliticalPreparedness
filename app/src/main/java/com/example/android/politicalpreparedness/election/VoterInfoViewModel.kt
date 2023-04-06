package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionsRepository
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch

private const val TAG = "#PLP VoterInfoViewModel"

class VoterInfoViewModel(private val repository: ElectionsRepository) : ViewModel() {

    private val _voterInfoResponse = MutableLiveData<VoterInfoResponse>()
    val voterInfoResponse: LiveData<VoterInfoResponse>
        get() = _voterInfoResponse

    fun populateVoterInfo(election: Election) {
        viewModelScope.launch {
            val electionAddress = election.division.state + "," + election.division.country
            try {
                val voterInfoResponse =
                    CivicsApi.retrofitService.getVoterInfo(electionAddress, election.id)
                Log.i(TAG, "VoterInfo retrieved from api: $voterInfoResponse")
                _voterInfoResponse.value = voterInfoResponse
            } catch (e: Exception) {
                Log.i(TAG, "Error retrieving voterInfoResponse")
            }
        }
    }

    private val _voterLocationsUrl = MutableLiveData<String>()
    val voterLocationsUrl: LiveData<String>
        get() = _voterLocationsUrl

    private val _ballotInformationUrl = MutableLiveData<String>()
    val ballotInformationUrl: LiveData<String>
        get() = _ballotInformationUrl

    fun fetchLocationsUrl() {
        _voterLocationsUrl.value =
            _voterInfoResponse.value?.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
    }

    fun fetchBallotUrl() {
        _voterLocationsUrl.value =
            _voterInfoResponse.value?.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
    }

    private val _follow = MutableLiveData<Boolean>()
    val follow: LiveData<Boolean>
        get() = _follow

    fun populateFollow(election: Election) {
        viewModelScope.launch {
            _follow.value = repository.getElectionById(election.id) != null
        }
    }

    fun followElection(election: Election) {
        viewModelScope.launch {
            if (repository.getElectionById(election.id) != null) {
                // Election is followed -> Remove it from database
                repository.delete(election)
                _follow.value = false
            }
            else {
                // Election is not followed -> Add it to database
                repository.insert(election)
                _follow.value = true
            }
        }
    }
}
