package com.example.reservasimei
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var btnLogin:Button
    private lateinit var etCorreo:EditText
    private lateinit var etPass:EditText
    private lateinit var db:DataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Sesion()
        btnLogin=findViewById(R.id.btnLogin)
        etCorreo=findViewById(R.id.etUser)
        etPass=findViewById(R.id.etPass)
        db= DataBase(this)

        btnLogin.setOnClickListener {
            val userText=etCorreo.text.toString()
            val passText=etPass.text.toString()
            if (TextUtils.isEmpty(userText) || TextUtils.isEmpty(passText)){
                Toast.makeText(this,"Agrega correo y contraseña",Toast.LENGTH_SHORT).show()
            }else{
                val usuariosCollection = Firebase.firestore.collection("Usuarios")
                val usuarioQuery = usuariosCollection.whereEqualTo("email", userText)

                usuarioQuery.get()
                    .addOnSuccessListener { usuariosSnapshot ->
                        if (!usuariosSnapshot.isEmpty) {
                            // El correo pertenece a una empresa
                            logueo(userText,ProviderType.BASIC)
                            Toast.makeText(this, "Logueo como usuario exitoso", Toast.LENGTH_SHORT).show()
                        } else {
                            // Verificar si el correo pertenece a una empresa
                            val empresasCollection = Firebase.firestore.collection("Empresas")
                            val empresaQuery = empresasCollection.whereEqualTo("email", userText)

                            empresaQuery.get()
                                .addOnSuccessListener { empresasSnapshot ->
                                    if (!empresasSnapshot.isEmpty) {
                                        // El correo pertenece a una empresa
                                        logueoEmpresa(userText)
                                        Toast.makeText(this, "Logueo como empresa exitoso", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // El correo no pertenece ni a un usuario ni a una empresa
                                        Toast.makeText(this, "No estás registrado", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        val btnRegistrar=findViewById<TextView>(R.id.btnRegistrar)
        btnRegistrar.setOnClickListener{
            entrarRegistro()
        }

        val btnRegistrarEmpresa=findViewById<TextView>(R.id.btnRegistrarEmpresa)
        btnRegistrarEmpresa.setOnClickListener{
            entrarRegistroEmpresa()
        }

        val analisis:FirebaseAnalytics=FirebaseAnalytics.getInstance(this)
        val bundle=Bundle()
        bundle.putString("message","Firebase activada")
        analisis.logEvent("InitScreen",bundle)
    }
    private fun entrarRegistro(){
        val i=Intent(this,RegistroActivity::class.java)
        startActivity(i)
    }
    private fun entrarRegistroEmpresa(){
        val i=Intent(this,RegistroEmpresaActivity::class.java)
        startActivity(i)
    }
    private fun logueo(userId: String, provider: ProviderType) {
        val i = Intent(this, MenuActivity::class.java)

        i.putExtra("email", userId)
        i.putExtra("proveedor",provider.name)
        startActivity(i)
        finish()
    }
    private fun logueoEmpresa(userId: String) {
        val i = Intent(this, MenuEmpresaActivity::class.java)

        i.putExtra("email", userId)
        startActivity(i)
        finish()
    }

    private fun Sesion(){
        val prefs: SharedPreferences=getSharedPreferences(getString(R.string.prefs_file),
            Context.MODE_PRIVATE)
        val email:String?=prefs.getString("email",null)
        val tipo:String?=prefs.getString("tipo",null)
        if (email!=null && tipo=="usuario"){
            logueo(email.toString(),ProviderType.BASIC)
        }else if (email!=null && tipo=="empresa"){
            logueoEmpresa(email.toString())
        }
    }
}