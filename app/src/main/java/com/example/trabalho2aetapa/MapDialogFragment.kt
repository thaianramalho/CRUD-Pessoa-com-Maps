package com.example.trabalho2aetapa

import android.app.Dialog
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MapDialogFragment(private val selectedAddress: String) : DialogFragment(), OnMapReadyCallback {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.map_dialog, container, false)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapContainer) as SupportMapFragment?
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction().replace(R.id.mapContainer, it).commit()
            }
        mapFragment.getMapAsync { googleMap ->
            onMapReady(googleMap)
            progressBar.visibility = View.GONE
        }

        view.findViewById<Button>(R.id.closeButton).setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocationName(selectedAddress, 1)

        if (!addresses.isNullOrEmpty()) {
            val location = addresses[0]
            val latLng = LatLng(location.latitude, location.longitude)
            googleMap.addMarker(MarkerOptions().position(latLng).title(selectedAddress))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        } else {
            Toast.makeText(context, "Não foi possível encontrar a localização", Toast.LENGTH_SHORT).show()
        }
    }
}
