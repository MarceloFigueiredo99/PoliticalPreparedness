package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.representative.database.RepresentativesRepository

class RepresentativeViewModelFactory(
    private val repository: RepresentativesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RepresentativeViewModel::class.java)) {
            return RepresentativeViewModel(repository, SavedStateHandle()) as T
        }
        throw IllegalArgumentException("Unable to create viewmodel")
    }
}
