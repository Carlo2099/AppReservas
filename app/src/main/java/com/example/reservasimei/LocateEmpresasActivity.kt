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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LocateEmpresasActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private lateinit var map: GoogleMap
    private var correo: String = ""
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locate_empresas)

        correo = intent.getStringExtra("email") ?: ""
        createFragment()
        loadEmpresasMarkers()

        val btnGuardar = findViewById<TextView>(R.id.btnGuardar)
        btnGuardar.setOnClickListener {
            regresar()
        }
    }

    private fun createFragment() {
        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMapClickListener(this)

        // Coordenadas de la Plaza de Armas de Cajamarca
        val plazaDeArmas = LatLng(-7.1575653, -78.5178524)

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(plazaDeArmas, 13f))
    }

    private fun loadEmpresasMarkers() {
        val db = Firebase.firestore
        db.collection("Empresas")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val ubicacion = document.getGeoPoint("ubicacion")
                    if (ubicacion != null) {
                        val empresaLatLng = LatLng(ubicacion.latitude, ubicacion.longitude)
                        addEmpresaMarker(empresaLatLng, document.getString("nombres") ?: "Nombre Desconocido")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LocateEmpresasActivity", "Error al obtener las empresas: $exception")
            }
    }

    private fun addEmpresaMarker(coordenada: LatLng, nombreEmpresa: String) {
        map.addMarker(MarkerOptions().position(coordenada).title(nombreEmpresa))
    }

    override fun onMapClick(coordenada: LatLng) {

    }

    private fun regresar() {
        val i = Intent(this, MenuActivity::class.java)
        i.putExtra("email", correo)
        startActivity(i)
    }
}
