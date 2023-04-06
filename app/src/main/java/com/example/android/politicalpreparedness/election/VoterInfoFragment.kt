package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.database.ElectionsRepositoryImpl
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.models.Election

private const val TAG = "#PLP VoterInfoFragment"

class VoterInfoFragment : Fragment() {

    private val electionsRepository by lazy {
        ElectionsRepositoryImpl(ElectionDatabase.getInstance(requireContext()))
    }

    private val viewModel: VoterInfoViewModel by viewModels {
        VoterInfoViewModelFactory(electionsRepository)
    }

    private lateinit var election: Election

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentVoterInfoBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_voter_info, container, false
        )

        binding.lifecycleOwner = this

        election = VoterInfoFragmentArgs.fromBundle(requireArguments()).election

        binding.election = election
        binding.viewmodel = viewModel

        viewModel.voterInfoResponse.observe(viewLifecycleOwner, Observer {
            binding.address.text =
                it.state?.get(0)?.electionAdministrationBody?.correspondenceAddress.toString()
        })

        viewModel.voterLocationsUrl.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                loadUrls(it)
            } else {
                Toast.makeText(requireContext(), getString(R.string.fragment_voter_info_could_not_load), Toast.LENGTH_SHORT).show()
                Log.i(TAG, "Error displaying voterLocationsUrl")
            }
        })

        viewModel.ballotInformationUrl.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                loadUrls(it)
            } else {
                Toast.makeText(requireContext(), getString(R.string.fragment_voter_info_could_not_load), Toast.LENGTH_SHORT).show()
                Log.i(TAG, "Error displaying ballotInformationUrl")
            }
        })

        viewModel.follow.observe(viewLifecycleOwner, Observer { followed ->
            Log.i(TAG, "Follow state is $followed")
            followed.let {
                if (followed) {
                    binding.buttonFollow.text = getString(R.string.fragment_voter_info_unfollow_election)
                } else {
                    binding.buttonFollow.text = getString(R.string.fragment_voter_info_follow_election)
                }
            }
        })

        binding.buttonFollow.setOnClickListener {
            viewModel.followElection(election)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.populateVoterInfo(election)
        viewModel.populateFollow(election)
    }

    private fun loadUrls(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}
