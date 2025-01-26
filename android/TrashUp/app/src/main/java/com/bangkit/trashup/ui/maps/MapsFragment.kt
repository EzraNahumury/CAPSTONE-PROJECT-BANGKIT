package com.bangkit.trashup.ui.maps

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bangkit.trashup.R
import com.bangkit.trashup.databinding.FragmentMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Suppress("RedundantOverride")
class MapsFragment : Fragment(R.layout.fragment_maps), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentMapsBinding
    private lateinit var tpsPredictor: NearestTPSPredictor
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        tpsPredictor = NearestTPSPredictor(requireContext())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.searchTpsButton.setOnClickListener {
            searchNearestTPS()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    Log.d("MapsFragment", "Peta diperbarui ke lokasi pengguna: ${location.latitude}, ${location.longitude}")
                } else {
                    Log.e("MapsFragment", "Lokasi pengguna tidak ditemukan")
                }
            }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun searchNearestTPS() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLocation = doubleArrayOf(location.latitude, location.longitude)
                    Log.d("MapsFragment", "Lokasi pengguna: ${location.latitude}, ${location.longitude}")

                    val nearestTPS = tpsPredictor.findNearestTPS(currentLocation)

                    nearestTPS?.let {
                        mMap.clear()
                        mMap.addMarker(MarkerOptions().position(it).title("TPS Terdekat"))
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
                        Log.d("MapsFragment", "TPS Terdekat: ${it.latitude}, ${it.longitude}")
                    } ?: run {
                        Log.e("MapsFragment", "Gagal menemukan TPS terdekat")
                    }
                } else {
                    Log.e("MapsFragment", "Lokasi pengguna tidak ditemukan")
                }
            }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    @Suppress("EmptyMethod")
    override fun onDestroyView() {
        super.onDestroyView()
    }
}
