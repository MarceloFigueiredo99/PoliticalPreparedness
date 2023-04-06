package com.example.android.politicalpreparedness.launch

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding

private const val TAG = "#PLP LaunchFragment"

class LaunchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLaunchBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.buttonFindRepresentatives.setOnClickListener { navToRepresentatives() }
        binding.buttonUpcoming.setOnClickListener { navToElections() }

        return binding.root
    }

    private fun navToElections() {
        Log.i(TAG, "Navigating to elections fragment")
        this.findNavController()
            .navigate(LaunchFragmentDirections.actionLaunchFragmentToElectionsFragment())
    }

    private fun navToRepresentatives() {
        Log.i(TAG, "Navigating to representatives fragment")
        this.findNavController()
            .navigate(LaunchFragmentDirections.actionLaunchFragmentToRepresentativeFragment())
    }
}
