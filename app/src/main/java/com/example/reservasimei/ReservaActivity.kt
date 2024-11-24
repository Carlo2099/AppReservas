package com.example.reservasimei

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.widget.Toast
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.example.reservasimei.Clases.Reservaciones
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReservaActivity : AppCompatActivity()  {
    private var correo: String = ""
    private var IdDocumento: String = ""
    private var detalleCampo: String = ""
    private var detalleEmpresa: String = ""
    private var dbF = Firebase.firestore
    private lateinit var fecha: EditText
    private val calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserva)

        correo = intent.getStringExtra("correo") ?: ""
        IdDocumento = intent.getStringExtra("IdDocumento") ?: ""
        detalleCampo = intent.getStringExtra("Campo") ?: "" // Futbol
        detalleEmpresa = intent.getStringExtra("NombreEmpresa") ?: ""

        val campoDetalle = findViewById<TextView>(R.id.campoDetalle)
        campoDetalle.setText("Campo deportivo "+detalleEmpresa.toString()+" - cancha de "+detalleCampo)

        val nmrHora= findViewById<EditText>(R.id.etNmrHoras)
        fecha = findViewById(R.id.etfecha)
        val hora= findViewById<EditText>(R.id.ethora)
        val spinner = findViewById<Spinner>(R.id.spiner1)

        val mediosDePago = resources.getStringArray(R.array.mediosDePago)
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, mediosDePago)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adaptador

        fecha.setOnClickListener {
            showDatePickerDialog()
        }
        hora.setOnClickListener {
            showTimePickerDialog()
        }

        val btnVistaReserva=findViewById<Button>(R.id.btnReservar)
        btnVistaReserva.setOnClickListener {
            if (nmrHora.text.toString().isNotEmpty() && fecha.text.toString().isNotEmpty() && hora.text.toString().isNotEmpty()) {

                val reserva = hashMapOf(
                    "id_empresa" to detalleEmpresa,
                    "id_cancha" to IdDocumento,
                    "id_usuario" to correo,
                    "nmrHoras" to nmrHora.text.toString(),
                    "fechaReserva" to fecha.text.toString(),
                    "horaReserva" to hora.text.toString(),
                    "medioDePago" to spinner.selectedItem.toString()
                )

                dbF.collection("Reservas")
                    .add(reserva)
                    .addOnSuccessListener { documentReference ->
                        Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error adding document", e)
                    }
                Toast.makeText(this, "Reserva exitosa", Toast.LENGTH_SHORT).show()
                val i=Intent(applicationContext,MainActivity::class.java)
                startActivity(i)

            }else{
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Actualizar el calendario con la fecha seleccionada
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Formatear la fecha y establecerla en el EditText
                val dateFormat = "dd/MM/yyyy"
                val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
                fecha.setText(simpleDateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Mostrar el DatePickerDialog
        datePickerDialog.show()
    }
    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                // Actualizar el calendario con la hora seleccionada
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                // Formatear la hora y establecerla en el EditText
                val timeFormat = "HH:mm"
                val simpleTimeFormat = SimpleDateFormat(timeFormat, Locale.getDefault())
                findViewById<EditText>(R.id.ethora).setText(simpleTimeFormat.format(calendar.time))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24 horas
        )

        // Mostrar el TimePickerDialog
        timePickerDialog.show()
    }

    private fun reservar(){
        val i= Intent(this,ListaReservasActivity::class.java)
        i.putExtra("userId", correo)
        startActivity(i)
    }
}