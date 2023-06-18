package com.example.tellso.ui.location

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.example.tellso.R
import com.example.tellso.databinding.FragmentLocationBinding
import com.example.tellso.ui.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@ExperimentalPagingApi
@AndroidEntryPoint
class LocationFragment : Fragment() {

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var token: String = ""
    private val viewModel: LocationVM by viewModels()

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        mMap = googleMap
        mMap.uiSettings.apply {
            isCompassEnabled = true
            isMapToolbarEnabled = true
            isIndoorLevelPickerEnabled = true
        }
        getDeviceLocation()
        setMapStyle()
        storyLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        token = requireActivity().intent.getStringExtra(MainActivity.EXTRA_TOKEN) ?: ""

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private fun getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8f))
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.activate_location_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    private fun storyLocation() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            launch {
                viewModel.getAllStories(token).collect { result ->
                    result.onSuccess { response ->
                        response.stories.forEach { story ->

                            // Verify that latitude and longitude field not null
                            // Create marker on the map
                            if (story.lat != null && story.lon != null) {
                                val latLng = LatLng(story.lat, story.lon)

                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(latLng)
                                        .title(story.name)
                                        .snippet("Lat: ${story.lat}, Lon: ${story.lon}")
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    private fun setMapStyle() {
        try {
            mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                getDeviceLocation()
            }
        }
}