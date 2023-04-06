package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.database.ElectionsRepositoryImpl
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

private const val TAG = "#PLP ElectionsFragment"

class ElectionsFragment : Fragment() {

    private val electionsRepository by lazy {
        ElectionsRepositoryImpl(ElectionDatabase.getInstance(requireContext()))
    }

    private val viewModel: ElectionsViewModel by viewModels {
        ElectionsViewModelFactory(electionsRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentElectionBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_election, container, false
        )

        binding.lifecycleOwner = this

        viewModel.refreshAdapters()

        val upcomingElectionsAdapter = ElectionListAdapter(ElectionListener { election ->
            Log.i(TAG, "upcomingElectionsAdapter Listener")
            viewModel.onElectionClicked(election)
        })

        val savedElectionsAdapter = ElectionListAdapter(ElectionListener { election ->
            Log.i(TAG, "savedElectionsAdapter Listener")
            viewModel.onElectionClicked(election)
        })

        Log.i(TAG, "Setting adapters")
        binding.upcomingRecyclerView.adapter = upcomingElectionsAdapter
        binding.savedRecyclerView.adapter = savedElectionsAdapter

        viewModel.upcomingElections.observe(viewLifecycleOwner, Observer { upcomingElections ->
            Log.i(TAG, "Elections retrieved from viewmodel: $upcomingElections")
            upcomingElectionsAdapter.submitList(upcomingElections)
        })

        viewModel.savedElections.observe(viewLifecycleOwner, Observer { savedElections ->
            savedElectionsAdapter.submitList(savedElections)
        })

        viewModel.navigateToVoterInfo.observe(viewLifecycleOwner, Observer { election ->
            election?.let {
                findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        election
                    )
                )
                viewModel.onVoterIntoNavigated()
            }
        })

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume refresing adapters")
        viewModel.refreshAdapters()
    }
}
