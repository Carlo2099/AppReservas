package com.example.reservasimei

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LocateActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private lateinit var map: GoogleMap
    private var correo: String = ""
    private var empresaUbicacion: GeoPoint? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locate)

        correo = intent.getStringExtra("email") ?: ""
        loadEmpresaUbicacion()

        val btnGuardar = findViewById<TextView>(R.id.btnGuardar)
        btnGuardar.setOnClickListener {
            regresar()
        }
    }

    private fun loadEmpresaUbicacion() {
        val db = Firebase.firestore
        val empresaRef = db.collection("Empresas").document(correo)

        empresaRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    empresaUbicacion = document.getGeoPoint("ubicacion")
                    initializeMap()
                } else {
                    initializeMap()
                }
            }
            .addOnFailureListener { e ->
                Log.e("LocateActivity", "Error al obtener la ubicación de la empresa: $e")
            }
    }

    private fun initializeMap() {
        val mapFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMapClickListener(this)

        if (empresaUbicacion != null) {
            val empresaLatLng = geoPointToLatLng(empresaUbicacion!!)
            updateMarker(empresaLatLng)
            moveCameraToLocation(empresaLatLng)
        } else {
            // Si no hay ubicación de empresa, centra el mapa en la Plaza de Armas sin un marcador
            val plazaDeArmas = LatLng(-7.1575653, -78.5178524)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(plazaDeArmas, 18f))
        }
    }

    private fun updateMarker(coordenada: LatLng) {
        if (marker == null) {
            marker = map.addMarker(MarkerOptions().position(coordenada).title(correo))
        } else {
            marker?.position = coordenada
        }
    }

    override fun onMapClick(coordenada: LatLng) {
        updateMarker(coordenada)
    }

    private fun moveCameraToLocation(location: LatLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18f))
    }

    private fun regresar() {
        // Obtén las coordenadas del marcador
        val coordenadas = marker?.position

        // Si hay coordenadas, guárdalas en la base de datos
        if (coordenadas != null) {
            val db = Firebase.firestore
            val empresaRef = db.collection("Empresas").document(correo)
            empresaRef
                .update("ubicacion", LatLngToGeoPoint(coordenadas))
                .addOnSuccessListener {
                    // Informa al usuario que la ubicación se ha actualizado
                    // También puedes manejar esto de otras maneras, como con un Toast
                    Log.d("LocateActivity", "Ubicación actualizada")
                }
                .addOnFailureListener { e ->
                    // Manejar errores
                    Log.e("LocateActivity", "Error al actualizar la ubicación: $e")
                }
        }

        // Regresa a la actividad de menú
        val i = Intent(this, MenuEmpresaActivity::class.java)
        i.putExtra("email", correo)
        startActivity(i)
    }

    private fun geoPointToLatLng(geoPoint: GeoPoint): LatLng {
        return LatLng(geoPoint.latitude, geoPoint.longitude)
    }

    private fun LatLngToGeoPoint(latLng: LatLng): GeoPoint {
        return GeoPoint(latLng.latitude, latLng.longitude)
    }
}
