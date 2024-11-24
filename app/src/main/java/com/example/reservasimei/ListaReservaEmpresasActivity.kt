package com.example.reservasimei

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListaReservaEmpresasActivity : AppCompatActivity() {
    private var correo: String = ""
    private val dbF = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_reserva_empresas)
        correo = intent.getStringExtra("email") ?: ""

        // Realizar la consulta a Firestore para obtener las reservas del usuario
        val db = Firebase.firestore
        db.collection("Reservas")
            .whereEqualTo("id_empresa", correo)
            .get()
            .addOnSuccessListener { documents ->
                // Recorrer los documentos y crear dinámicamente CardViews para cada reserva
                val layoutReservas = findViewById<LinearLayout>(R.id.layoutReservas)
                for (document in documents) {
                    val cardView = createReservaCardView(document)
                    layoutReservas.addView(cardView)
                }
            }
            .addOnFailureListener { exception ->
                // Manejar el error
                Log.e("ListaReservasActivity", "Error al obtener reservas: $exception")
            }
    }
    private fun createReservaCardView(document: DocumentSnapshot): CardView {
        val cardView = CardView(this)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.card_margin_top)
        layoutParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.card_margin_bottom)
        cardView.layoutParams = layoutParams

        // Configurar la elevación, esquinas redondeadas y relleno del CardView
        cardView.cardElevation = resources.getDimension(R.dimen.card_elevation)
        cardView.radius = resources.getDimensionPixelSize(R.dimen.card_cornerRadius).toFloat()
        cardView.setContentPadding(
            resources.getDimensionPixelSize(R.dimen.card_padding),
            resources.getDimensionPixelSize(R.dimen.card_padding),
            resources.getDimensionPixelSize(R.dimen.card_padding),
            resources.getDimensionPixelSize(R.dimen.card_padding)
        )

        // Configurar el contenido del CardView con los datos de la reserva
        val linearLayout = LinearLayout(this)
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(
            resources.getDimensionPixelSize(R.dimen.card_content_padding),
            0,
            resources.getDimensionPixelSize(R.dimen.card_content_padding),
            0
        )

        // Añadir los detalles de la reserva al LinearLayout
        val idCancha = document.getString("id_cancha")
        val idUsuario = document.getString("id_usuario")
        val nmrHoras = document.getString("nmrHoras")
        val fechaReserva = document.getString("fechaReserva")
        val horaReserva = document.getString("horaReserva")
        val medioDePago = document.getString("medioDePago")

        val textViewIdCancha = TextView(this)
        textViewIdCancha.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        queryCanchaDetails(""+idCancha) { resultado ->
            // Aquí puedes usar el resultado, por ejemplo, establecer el texto en tu TextView
            if (resultado != null) {
                textViewIdCancha.text = resultado
            } else {
                // Manejar el caso donde no se pudo obtener la información
                textViewIdCancha.text = "Error al obtener detalles de la cancha"
            }
        }
        textViewIdCancha.setTextColor(ContextCompat.getColor(this, R.color.color_BotonEmpresa))
        textViewIdCancha.setTextSize(
            resources.getDimension(R.dimen.card_text_size) / resources.displayMetrics.density
        )
        textViewIdCancha.setTypeface(null, Typeface.BOLD)
        linearLayout.addView(textViewIdCancha)

        val textViewReserva = TextView(this)
        textViewReserva.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textViewReserva.text = "FECHA: $fechaReserva            HORARIO: Alquilado desde $horaReserva por $nmrHoras horas       /     PAGO:$medioDePago"
        textViewReserva.setTextSize(
            resources.getDimension(R.dimen.card_text_size) / resources.displayMetrics.density
        )
        textViewReserva.setTypeface(null, Typeface.BOLD)
        linearLayout.addView(textViewReserva)

        // Agregar el LinearLayout al CardView
        cardView.addView(linearLayout)

        return cardView
    }
    private fun queryCanchaDetails(idCancha: String, callback: (String?) -> Unit) {
        val camposRef = dbF.collection("Campos").document(idCancha)
        camposRef.get()
            .addOnSuccessListener { camposDocument ->
                if (camposDocument.exists()) {
                    val detalleCancha = camposDocument.getString("detalle")
                    val idEmpresa = camposDocument.getString("id_empresa")
                    val resultado = if (detalleCancha != null && idEmpresa != null) {
                        "CANCHA: $detalleCancha - $idEmpresa"
                    } else {
                        null
                    }
                    callback(resultado)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("ListaReservasActivity", "Error al obtener detalles de la cancha: $e")
                callback(null)
            }
    }
}