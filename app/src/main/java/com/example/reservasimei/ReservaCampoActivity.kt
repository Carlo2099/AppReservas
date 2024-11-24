package com.example.reservasimei

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

fun CardView.applyEmpresaStyle() {
    val layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.card_margin_top)

    this.layoutParams = layoutParams
    cardElevation = 0f
    radius = resources.getDimensionPixelSize(R.dimen.card_cornerRadius).toFloat()
    setContentPadding(
        resources.getDimensionPixelSize(R.dimen.card_padding),
        resources.getDimensionPixelSize(R.dimen.card_padding),
        resources.getDimensionPixelSize(R.dimen.card_padding),
        resources.getDimensionPixelSize(R.dimen.card_padding)
    )
}

class ReservaCampoActivity : AppCompatActivity() {
    private var correo: String = ""
    private var dbF = Firebase.firestore
    private lateinit var linearLayoutEmpresas: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserva_campo)

        correo = intent.getStringExtra("email") ?: ""

        linearLayoutEmpresas = findViewById(R.id.totalEmpresas)

        // Consulta todas las empresas en Firebase Firestore
        dbF.collection("Empresas")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val cardView = createCardView(document)
                    linearLayoutEmpresas.addView(cardView)
                }
            }
            .addOnFailureListener { exception ->
                // Maneja el error
                Log.e("ReservaCampoActivity", "Error al consultar empresas: $exception")
            }
    }

    private fun createCardView(document: DocumentSnapshot): CardView {
        val cardView = CardView(this)

        cardView.applyEmpresaStyle()

        val linearLayout = LinearLayout(this)
        linearLayout.apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(
                resources.getDimensionPixelSize(R.dimen.card_padding),
                resources.getDimensionPixelSize(R.dimen.card_padding),
                resources.getDimensionPixelSize(R.dimen.card_padding),
                resources.getDimensionPixelSize(R.dimen.card_padding)
            )
        }

        val nombreEmpresa = document.getString("nombres")
        val textViewNombre = TextView(this)
        textViewNombre.apply {
            text = "Campo deportivo $nombreEmpresa"
            textSize = 20f
            setTextColor(Color.parseColor("#4e73df"))
            setTypeface(null, Typeface.BOLD)
        }

        val textViewDisponibles = TextView(this)
        textViewDisponibles.apply {
            val idEmpresa = document.getString("email")
            getNumeroCampos(idEmpresa) { numeroCampos ->
                text = "Número de campos: $numeroCampos"
            }
            textSize = 13f
            setTypeface(null, Typeface.BOLD)
        }
        linearLayout.addView(textViewNombre)
        linearLayout.addView(textViewDisponibles)

        cardView.addView(linearLayout)

        cardView.setOnClickListener {
            val nombreCampo = document.getString("email")
            Campo("" + nombreCampo)
        }

        return cardView
    }

    private fun Campo(nombreCampo: String) {
        val i = Intent(this, CamposActivity::class.java)
        i.putExtra("correo", correo)
        i.putExtra("NombreCampo", nombreCampo)
        startActivity(i)
    }

    private fun getNumeroCampos(idEmpresa: String?, callback: (Int) -> Unit) {
        if (idEmpresa == null) {
            callback(0)
            return
        }
        dbF.collection("Campos")
            .whereEqualTo("id_empresa", idEmpresa)
            .get()
            .addOnSuccessListener { documents ->
                callback(documents.size())
            }
            .addOnFailureListener { exception ->
                Log.e("ReservaCampoActivity", "Error al consultar campos: $exception")
                callback(0)
            }
    }
}
