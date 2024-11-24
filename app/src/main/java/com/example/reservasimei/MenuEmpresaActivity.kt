package com.example.reservasimei

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MenuEmpresaActivity : AppCompatActivity() {
    private var correo: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_empresa)

        correo = intent.getStringExtra("email") ?: ""

        val nombresYApellidosTextView = findViewById<TextView>(R.id.nombresYApellidos)
        val db = DataBase(this)
        if (correo != null) {
            nombresYApellidosTextView.text = "$correo"
        }

        val btnCrearCampo = findViewById<Button>(R.id.btnCrearCampo)
        btnCrearCampo.setOnClickListener {
            CrearCampo(""+correo)
        }

        val btnListaReserva = findViewById<Button>(R.id.listaReservas)
        btnListaReserva.setOnClickListener {
            listaReserva()
        }

        val btnCerrar = findViewById<Button>(R.id.btnCerrarSesion)
        btnCerrar.setOnClickListener {
            regresarLogin()
        }

        val btnMapa = findViewById<Button>(R.id.Mapa)
        btnMapa.setOnClickListener {
            mapa()
        }
        //Guardado de datos:
        val prefs: SharedPreferences.Editor=getSharedPreferences(getString(R.string.prefs_file),
            Context.MODE_PRIVATE).edit()
        prefs.putString("email",correo)
        prefs.putString("tipo","empresa")
        prefs.apply()
    }

    private fun CrearCampo(userName:String) {
        val i = Intent(this, CrearCampoActivity::class.java)

        i.putExtra("email", userName)

        startActivity(i)
    }

    private fun listaReserva() {
        val i = Intent(this, ListaReservaEmpresasActivity::class.java)

        i.putExtra("email", correo)

        startActivity(i)
    }

    private fun regresarLogin() {
        val prefs:SharedPreferences.Editor=getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()
        val i = Intent(this, MainActivity::class.java)
        finishAffinity()  // Cierra todas las actividades en la pila
        startActivity(i)
        finish()
    }
    private fun mapa() {
        val i = Intent(this, LocateActivity::class.java)
        i.putExtra("email", correo)
        startActivity(i)
        finish()
    }
}