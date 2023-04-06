package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.database.RepresentativesDatabase
import com.example.android.politicalpreparedness.representative.database.RepresentativesRepositoryImpl
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_representative.*
import kotlinx.android.synthetic.main.fragment_representative.view.*
import java.util.*

private const val TAG = "#PLP RepresentativeFragment"
private const val LOCATION_REQUEST = 1
private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
private const val BUNDLE_KEY_LIST = "REPRESENTATIVE_LIST_KEY"

class RepresentativeFragment : Fragment() {

    private val representativesRepository by lazy {
        RepresentativesRepositoryImpl(RepresentativesDatabase.getInstance(requireContext()))
    }

    private val viewModel: RepresentativeViewModel by viewModels {
        RepresentativeViewModelFactory(representativesRepository)
    }

    private lateinit var binding: FragmentRepresentativeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.fragment_representative, container, false
        )

        binding.lifecycleOwner = this

        fillSpinner()

        val representativeListAdapter = RepresentativeListAdapter()

        binding.representativeRecyclerView.adapter = representativeListAdapter

        viewModel.representatives.observe(viewLifecycleOwner, Observer {
            it?.let {
                representativeListAdapter.submitList(it)
            }
        })

        binding.buttonLocation.setOnClickListener {
            checkLocationPermissions()
        }

        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            if (addressCorrectlyFilled(binding)) {
                Log.i(TAG, "Parameters correct filled")
                viewModel.loadParametersAddress(
                    binding.addressLine1.text.toString(),
                    binding.addressLine2.text.toString(),
                    binding.city.text.toString(),
                    binding.state.selectedItem.toString(),
                    binding.zip.text.toString()
                )
                viewModel.populateRepresentatives()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.fragment_representative_fill_address),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        return binding.root
    }

    private fun addressCorrectlyFilled(binding: FragmentRepresentativeBinding): Boolean {
        with(binding) {
            return addressLine1.text.isNotEmpty() &&
                    addressLine2.text.isNotEmpty() &&
                    city.text.isNotEmpty() &&
                    zip.text.isNotEmpty() &&
                    state.selectedItem.toString().isNotEmpty()
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getMotionLayoutState()?.let { state ->
            Log.i(TAG, "Saved motion layout state: $state")
            binding.motionLayout.transitionToState(state)
        }

        if (addressCorrectlyFilled(binding)) {
            Log.i(TAG, "Parameters are still filled")
            viewModel.loadParametersAddress(
                binding.addressLine1.text.toString(),
                binding.addressLine2.text.toString(),
                binding.city.text.toString(),
                binding.state.selectedItem.toString(),
                binding.zip.text.toString()
            )
        }
        viewModel.populateRepresentatives()
    }

    override fun onStop() {
        super.onStop()

        viewModel.saveMotionLayoutState(binding.motionLayout.currentState)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                checkDeviceLocationSettings()
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.fragment_representative_grant_locations),
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction("Settings") {
                        startActivity(
                            Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                        )
                    }.show()
            }
        }
    }

    private fun checkDeviceLocationSettings(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            Log.i(TAG, "OnFailureListener")
            if (exception is ResolvableApiException && resolve) {
                try {
                    startIntentSenderForResult(
                        exception.resolution.intentSender,
                        REQUEST_TURN_DEVICE_LOCATION_ON,
                        null,
                        0,
                        0,
                        0,
                        null
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error getting location settings resolution: ${sendEx.message}")
                }
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.fragment_representative_enable_location),
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettings()
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener { response ->
            if (response.isSuccessful) {
                Log.i(TAG, "Enabled location")
                getLocation()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(TAG, "onActivityResult")
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            checkDeviceLocationSettings(false)
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            checkDeviceLocationSettings()
            true
        } else {
            val permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(
                permissionsArray,
                LOCATION_REQUEST
            )
            false
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        Log.i(TAG, "getLocation")
        val locationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        locationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentAddress = geoCodeLocation(location)
                viewModel.loadGeoLocationAddress(currentAddress)
                viewModel.populateRepresentatives()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.fragment_representative_error_current_location),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun fillSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.states,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.state.adapter = adapter
        }
    }
}
