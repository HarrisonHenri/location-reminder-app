package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*
import com.udacity.project4.utils.Constants.MAP_ZOOM
import com.udacity.project4.utils.EspressoIdlingResource.wrapEspressoIdlingResource
import com.udacity.project4.utils.hasAllLocationPermissions
import com.udacity.project4.utils.showPermissionSnackBar

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val TAG = SelectLocationFragment::class.java.simpleName

    private lateinit var marker: Marker
    private lateinit var selectedPointOfInterest: PointOfInterest

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)


        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            onLocationSelected()
        }
        setMapFragment()

        return binding.root
    }

    private fun setMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }


    private fun onLocationSelected() {
        if (this::selectedPointOfInterest.isInitialized){
            _viewModel.selectedPOI.value = selectedPointOfInterest
            _viewModel.reminderSelectedLocationStr.value = selectedPointOfInterest.name
            _viewModel.latitude.value = selectedPointOfInterest.latLng.latitude
            _viewModel.longitude.value = selectedPointOfInterest.latLng.longitude
        }
        _viewModel.navigateBack()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setMapStyle(map)
        setMapLongClick(map)
        setPoiClick(map)
        setMyLocation()
    }

    private fun setMapStyle(map: GoogleMap){
        try {
            val success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
            )
            if(!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find the style. Error: ", e)
        }
    }

    private fun setMapLongClick(map: GoogleMap){
        map.setOnMapLongClickListener {latLng ->
            if (::marker.isInitialized){
                marker.remove()
            }

            val snippet = String.format(
                    Locale.getDefault(),
                    getString(R.string.lat_long_snippet),
                    latLng.latitude,
                    latLng.longitude
            )

            selectedPointOfInterest = PointOfInterest(latLng, snippet, snippet)

            marker = map.addMarker(
                    MarkerOptions()
                            .position(latLng)
                            .title(getString(R.string.reminder_location))
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
            marker.showInfoWindow()
            wrapEspressoIdlingResource{
                _viewModel.isLocationSelected.postValue(true)
            }
        }
    }

    private fun setPoiClick(map: GoogleMap){
        map.setOnPoiClickListener { poi ->
            if (::marker.isInitialized){
                marker.remove()
            }

            marker = map.addMarker(
                    MarkerOptions()
                            .position(poi.latLng)
                            .title(poi.name)
            )

            selectedPointOfInterest = poi

            marker.showInfoWindow()
            wrapEspressoIdlingResource{
                _viewModel.isLocationSelected.postValue(true)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setMyLocation(){
        if(requireActivity().hasAllLocationPermissions()){
            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation?.addOnSuccessListener {
                it?.let {
                    val snippet = String.format(
                            Locale.getDefault(),
                            getString(R.string.lat_long_snippet),
                            it.latitude,
                            it.longitude
                    )
                    val latlng = LatLng(it.latitude, it.longitude)

                    selectedPointOfInterest = PointOfInterest(latlng, snippet, "Current Location")

                    marker = map.addMarker(
                            MarkerOptions()
                                    .position(latlng)
                                    .title(getString(R.string.reminder_location))
                                    .snippet(snippet)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    )

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, MAP_ZOOM))

                    marker.showInfoWindow()
                }
            }
        } else {
            requireActivity().showPermissionSnackBar(binding.root)
        }
    }

}