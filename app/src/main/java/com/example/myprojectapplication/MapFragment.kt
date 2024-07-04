package com.example.myprojectapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.VectorDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_TOKEN = "token"

class MapFragment : Fragment() {
    private var token: String? = null
    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            token = it.getString(ARG_TOKEN)
        }
        UtilNetwork.checkConnection(requireContext())
        Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(android.content.Context.MODE_PRIVATE))
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.map)
        mapView.setMultiTouchControls(true)
        checkLocationPermission()
        return view
    }

    fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        } else {
            getLastKnownLocation()
            fetchAllUploads(token.toString())
        }
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val userLocation = GeoPoint(it.latitude, it.longitude)
                    mapView.controller.setZoom(18.0)
                    mapView.controller.setCenter(userLocation)

                    val startMarker = Marker(mapView)
                    startMarker.position = userLocation
                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                    val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location) as VectorDrawable
                    startMarker.icon = icon

                    mapView.overlays.add(startMarker)
                }
            }
        }
    }

    private fun fetchAllUploads(token: String) {
        val apiService = ApiClient.instance.create(ApiService::class.java)
        val call = apiService.seeAllUploads("Bearer $token")

        call.enqueue(object : Callback<List<ResponseAllUploads>> {
            override fun onResponse(call: Call<List<ResponseAllUploads>>, response: Response<List<ResponseAllUploads>>) {
                if (response.isSuccessful) {
                    val uploads = response.body()
                    uploads?.let {
                        displayAllUploads(it, token)
                    }
                } else {
                    val errorMessage: String = when (response.code()) {
                        401 -> {
                            utilLogin.forceLogin(requireContext())
                            "User is not authenticated"
                        }
                        else -> "Error: ${response.errorBody()?.string()}"
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<ResponseAllUploads>>, t: Throwable) {
                Toast.makeText(context, "Fetch Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayAllUploads(uploads: List<ResponseAllUploads>, token: String) {
        uploads.forEach { upload ->
            val uploadLocation = GeoPoint(upload.latitude.toDouble(), upload.longitude.toDouble())
            val marker = Marker(mapView)
            marker.position = uploadLocation
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = "ID: ${upload.id}"

            val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location_file) as VectorDrawable
            marker.icon = icon

            marker.setOnMarkerClickListener { marker, mapView ->
                val intent = Intent(context, MoreInfoActivity::class.java).apply {
                    putExtra("uploadId", upload.id)
                    putExtra("token", token)
                }
                startActivity(intent)
                true
            }

            mapView.overlays.add(marker)
            Log.d("MapFragment", "Marker added at: ${upload.latitude}, ${upload.longitude}")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(token: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TOKEN, token)
                }
            }
    }
}