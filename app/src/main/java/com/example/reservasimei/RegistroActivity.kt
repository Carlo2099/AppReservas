package com.example.reservasimei

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class RegistroActivity : AppCompatActivity() {
    private lateinit var uname: EditText
    private lateinit var email: EditText
    private lateinit var dnit: EditText
    private lateinit var celT: EditText
    private lateinit var pass: EditText
    private lateinit var cpword: EditText

    private lateinit var btnRegistrar: Button
    private lateinit var db: DataBase

    private var dbF = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        uname=findViewById(R.id.etNombres)
        email=findViewById(R.id.etCorreo)
        dnit=findViewById(R.id.etDNI)
        celT=findViewById(R.id.etCel)
        pass=findViewById(R.id.etPass1)
        cpword=findViewById(R.id.etPass2)
        btnRegistrar=findViewById(R.id.btnRegistrar)
        db= DataBase(this)
        btnRegistrar.setOnClickListener {
            val nombresApellidosText=uname.text.toString()
            val correoText=email.text.toString()
            val DniText=dnit.text.toString()
            val CelText=celT.text.toString()
            val passText=pass.text.toString()
            val cpassText=cpword.text.toString()

            val guardar=db.insertData(nombresApellidosText,correoText,passText)
            if (TextUtils.isEmpty(nombresApellidosText) || TextUtils.isEmpty(correoText) || TextUtils.isEmpty(passText) || TextUtils.isEmpty(cpassText)){
                Toast.makeText(this,"Se requiere que todos los campos esten llenos",Toast.LENGTH_SHORT).show()
            }else{
                if (passText.equals(cpassText)){
                    if (guardar==true){
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(correoText.toString(),passText.toString()).addOnCompleteListener(){
                            if (it.isSuccessful){
                                Toast.makeText(this,"Registro exitoso",Toast.LENGTH_SHORT).show()

                                dbF.collection("Usuarios").document(correoText).set(hashMapOf(
                                    "nombres" to nombresApellidosText,
                                    "email" to correoText,
                                    "DNI" to DniText,
                                    "Cel" to CelText,
                                    "pass" to passText,
                                    "proveedor" to ProviderType.BASIC))
                                val i=Intent(applicationContext,MainActivity::class.java)
                                startActivity(i)
                            }else{
                                Toast.makeText(this,"No estas registrado",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        alerta()
                    }
                }else{
                    Toast.makeText(this,"Las contraseñas no coinciden",Toast.LENGTH_SHORT).show()
                }
            }
        }

        val btnRegresar=findViewById<TextView>(R.id.btnVolverLogin)
        btnRegresar.setOnClickListener{
            regresarLogin()
        }
    }
    private fun regresarLogin(){
        FirebaseAuth.getInstance().signOut()
        val i=Intent(this,MainActivity::class.java)
        startActivity(i)
    }
    private fun alerta(){
        val builder= AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Este correo ya ah sido registrado")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }
}