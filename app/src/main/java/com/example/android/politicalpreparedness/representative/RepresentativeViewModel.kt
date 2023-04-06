package com.example.android.politicalpreparedness.representative

import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.database.RepresentativesRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

private const val TAG = "#PLP RepresentativeViewModel"
private const val STATE_HANDLE_KEY_MOTION = "MOTION_LAYOUT_STATE"
private const val STATE_HANDLE_KEY_LIST = "REPRESENTATIVE_LIST_STATE"

class RepresentativeViewModel(
    private val representativesRepository: RepresentativesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    fun populateRepresentatives() {
        viewModelScope.launch {

            if (_address.value != null) {
                Log.i(TAG, "Restoring representatives list value")
                _representatives.value =
                    representativesRepository.getStoredList()
            }

            try {

                val representativeResponse =
                    _address.value?.let { CivicsApi.retrofitService.getRepresentatives(it.toFormattedString()) }

                val offices = representativeResponse?.offices
                val officials = representativeResponse?.officials

                officials?.let {
                    _representatives.value = offices?.flatMap { it.getRepresentatives(officials) }
                }
                Log.i(TAG, "RepresentativeResponse retrieved from api: $representativeResponse")
            } catch (e: Exception) {
                Log.w(TAG, "Error retrieving representativeResponse.\n Exception: ${e.cause}")
            }

            if (_representatives.value != null) {
                Log.i(TAG, "Saving representatives list value: ${_representatives.value!!}")
                representativesRepository.insert(_representatives.value!!)
            }
        }
    }

    fun loadGeoLocationAddress(address: Address) {
        _address.value = address
        Log.i(TAG, "loadGeoLocationAddress: ${_address.value}")
    }

    fun loadParametersAddress(
        addressLine1: String,
        addressLine2: String,
        city: String,
        state: String,
        zipCode: String
    ) {
        _address.value = Address(addressLine1, addressLine2, city, state, zipCode)
        Log.i(TAG, "loadParametersAddress: ${_address.value}")
    }

    fun saveMotionLayoutState(currentState: Int) {
        Log.i(TAG, "Saving motion layout state")
        savedStateHandle.set(STATE_HANDLE_KEY_MOTION, currentState)
    }

    fun getMotionLayoutState(): Int? {
        Log.i(TAG, "Retrieving motion layout state")
        return savedStateHandle.get<Int>(STATE_HANDLE_KEY_MOTION)
    }
}
