package com.example.reservasimei

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

enum class ProviderType{
    BASIC
}
class MenuActivity : AppCompatActivity() {
    private var correo: String = ""
    private lateinit var db:DataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        correo = intent.getStringExtra("email") ?: ""

        val nombresYApellidosTextView = findViewById<TextView>(R.id.nombresYApellidos)
        val db = DataBase(this)
        if (correo != null) {
            nombresYApellidosTextView.text = "$correo"
        }

        val btnReservarCampo = findViewById<Button>(R.id.btnReservarCampo)
        btnReservarCampo.setOnClickListener {
            reservarCampo()
        }

        val btnListaReserva = findViewById<Button>(R.id.listaReservas)
        btnListaReserva.setOnClickListener {
            listaReserva()
        }

        val btnCerrar = findViewById<Button>(R.id.btnCerrarSesion)
        btnCerrar.setOnClickListener {
            cerrarSesion()
        }

        val btnMapa = findViewById<Button>(R.id.Mapa)
        btnMapa.setOnClickListener {
            mapa()
        }

        //Guardado de datos:
        val prefs:SharedPreferences.Editor=getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
        prefs.putString("email",correo)
        prefs.putString("tipo","usuario")
        prefs.apply()
    }

    private fun reservarCampo() {
        val i = Intent(this, ReservaCampoActivity::class.java)

        i.putExtra("email", correo)

        startActivity(i)
    }

    private fun listaReserva() {
        val i = Intent(this, ListaReservasActivity::class.java)

        i.putExtra("email", correo)

        startActivity(i)
    }

    private fun cerrarSesion() {
        val prefs:SharedPreferences.Editor=getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()
        regresarLogin()
    }

    private fun regresarLogin() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        finish()
    }
    private fun mapa() {
        val i = Intent(this, LocateEmpresasActivity::class.java)
        i.putExtra("email", correo)
        startActivity(i)
        finish()
    }
}
