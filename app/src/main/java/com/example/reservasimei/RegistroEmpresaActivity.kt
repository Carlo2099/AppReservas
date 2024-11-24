package com.example.reservasimei

import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegistroEmpresaActivity : AppCompatActivity() {
    private lateinit var uname: EditText
    private lateinit var email: EditText
    private lateinit var cell: EditText
    private lateinit var hApertura: EditText
    private lateinit var hCierre: EditText
    private lateinit var pass: EditText
    private lateinit var cpword: EditText
    private val calendar = Calendar.getInstance()
    private lateinit var btnRegistrar: Button
    private lateinit var db: DataBase
    private var dbF = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_empresa)

        uname=findViewById(R.id.etNombre)
        email=findViewById(R.id.etCorreo)
        cell=findViewById(R.id.etCell)
        hApertura=findViewById(R.id.horaApertura)
        hCierre=findViewById(R.id.horaCierre)
        pass=findViewById(R.id.etPass1)
        cpword=findViewById(R.id.etPass2)

        btnRegistrar=findViewById(R.id.btnRegistrar)
        db= DataBase(this)

        hApertura.setOnClickListener {
            showTimePickerDialog(1)
        }
        hCierre.setOnClickListener {
            showTimePickerDialog(2)
        }
        btnRegistrar.setOnClickListener {
            try {
                val nombreEmpresa=uname.text.toString()
                val correoText=email.text.toString()
                val cellText=cell.text.toString()
                val hAperturaText=hApertura.text.toString()
                val horaCierreText=hCierre.text.toString()
                val passText=pass.text.toString()
                val cpassText=cpword.text.toString()

                val guardar=db.insertEmpresa(null,null,nombreEmpresa,correoText,cellText,hAperturaText,horaCierreText,passText)
                if (TextUtils.isEmpty(nombreEmpresa) || TextUtils.isEmpty(correoText) || TextUtils.isEmpty(passText) || TextUtils.isEmpty(cpassText) || TextUtils.isEmpty(cellText)|| TextUtils.isEmpty(hAperturaText)|| TextUtils.isEmpty(horaCierreText)){
                    Toast.makeText(this,"Se requiere que todos los campos esten llenos", Toast.LENGTH_SHORT).show()
                }else{
                    if (passText.equals(cpassText)){
                        if (guardar==true){
                            Toast.makeText(this,"Registro exitoso",Toast.LENGTH_SHORT).show()
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(correoText.toString(),passText.toString()).addOnCompleteListener(){
                                if (it.isSuccessful){
                                    Toast.makeText(this,"Registro exitoso",Toast.LENGTH_SHORT).show()

                                    dbF.collection("Empresas").document(correoText).set(hashMapOf(
                                        "nombres" to nombreEmpresa,
                                        "email" to correoText,
                                        "Cel" to cellText,
                                        "horaAper" to hAperturaText,
                                        "horaCierre" to horaCierreText,
                                        "pass" to passText,
                                        "id_mediosPago" to null,
                                        "proveedor" to ProviderType.BASIC))
                                    val i=Intent(applicationContext,MainActivity::class.java)
                                    startActivity(i)
                                }else{
                                    Toast.makeText(this,"No estas registrado",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }else{
                            Toast.makeText(this,"Correo existente", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this,"Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            catch (e: Exception) {
                Log.e("MiApp", "Error inesperado: " + e.message)
            }

        }

        val btnRegresar=findViewById<TextView>(R.id.btnVolverLogin)
        btnRegresar.setOnClickListener{
            regresarLogin()
        }
    }
    private fun showTimePickerDialog(horaAper: Int) {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                // Actualizar el calendario con la hora seleccionada
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                // Formatear la hora y establecerla en el EditText
                val timeFormat = "HH:mm"
                val simpleTimeFormat = SimpleDateFormat(timeFormat, Locale.getDefault())
                if (horaAper == 1){
                    findViewById<EditText>(R.id.horaApertura).setText(simpleTimeFormat.format(calendar.time))
                }else{
                    findViewById<EditText>(R.id.horaCierre).setText(simpleTimeFormat.format(calendar.time))
                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24 horas
        )

        // Mostrar el TimePickerDialog
        timePickerDialog.show()
    }
    private fun regresarLogin(){
        val i= Intent(this,MainActivity::class.java)
        startActivity(i)
    }
}