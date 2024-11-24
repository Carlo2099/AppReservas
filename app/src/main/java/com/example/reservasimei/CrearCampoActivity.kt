package com.example.reservasimei

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CrearCampoActivity : AppCompatActivity() {
    private var email: String = ""
    private var dbF = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_campo)
        email = intent.getStringExtra("email") ?: ""

        val txNombre = findViewById<TextView>(R.id.txNombre)

        dbF.collection("Empresas").whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Accede al campo "nombres" del documento encontrado
                    val nombreEmpresa = document.getString("nombres")
                    if (nombreEmpresa != null) {
                        txNombre.text = "Campo deportivo $nombreEmpresa - Cancha"
                    } else {
                        println("No se encontró el nombre de la empresa para el correo $email")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al consultar la base de datos: $exception", Toast.LENGTH_SHORT).show()
            }

        val spinner = findViewById<Spinner>(R.id.spiner1)
        val campos = resources.getStringArray(R.array.campos)
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, campos)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adaptador

        val etCostohoraD= findViewById<EditText>(R.id.etCostohoraD)
        val etCostohoraN= findViewById<EditText>(R.id.etCostohoraN)

        val btnCrearCampo=findViewById<Button>(R.id.btnCrear)
        btnCrearCampo.setOnClickListener {
            if (etCostohoraD.text.toString().isNotEmpty() && etCostohoraN.text.toString().isNotEmpty()) {
                try {
                    val Campos = hashMapOf(
                        "id_empresa" to email,
                        "detalle" to spinner.selectedItem.toString(),
                        "precioDia" to etCostohoraD.text.toString(),
                        "precioNoche" to etCostohoraN.text.toString(),
                    )

                    dbF.collection("Campos")
                        .add(Campos)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }
                    Toast.makeText(this, "Reserva exitosa", Toast.LENGTH_SHORT).show()
                }catch (e: Exception){
                    Toast.makeText(this, "Ocurrio un error", Toast.LENGTH_SHORT).show()
                }

            }
            else{
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}