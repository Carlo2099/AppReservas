package com.example.reservasimei

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CamposActivity : AppCompatActivity() {
    private var correo: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campos)

        correo = intent.getStringExtra("correo") ?: ""
        val nombreCampo = intent.getStringExtra("NombreCampo")

        val campoNombreTextView = findViewById<TextView>(R.id.nombreCampoDeport)
        campoNombreTextView.text = nombreCampo

        // Consulta Firebase Firestore para obtener los campos asociados a la empresa
        val db = Firebase.firestore
        db.collection("Campos")
            .whereEqualTo("id_empresa", nombreCampo)
            .get()
            .addOnSuccessListener { documents ->
                // Crea dinámicamente los CardView basados en los resultados de la consulta
                val layoutCampos = findViewById<LinearLayout>(R.id.layoutCampos)
                for (document in documents) {
                    val cardView = createCardView(document)
                    layoutCampos.addView(cardView)
                }
            }
            .addOnFailureListener { exception ->
                // Maneja el error
                // Aquí puedes mostrar un mensaje de error o realizar alguna acción adecuada
            }
    }
    private fun createCardView(document: DocumentSnapshot): CardView {
        val cardView = CardView(this)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.card_margin_top)
        layoutParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.card_margin_bottom)
        cardView.layoutParams = layoutParams

        // Configura la elevación, esquinas redondeadas y relleno del CardView
        cardView.cardElevation = resources.getDimension(R.dimen.card_elevation)
        cardView.radius = resources.getDimensionPixelSize(R.dimen.card_cornerRadius).toFloat()
        cardView.setContentPadding(
            resources.getDimensionPixelSize(R.dimen.card_padding),
            resources.getDimensionPixelSize(R.dimen.card_padding),
            resources.getDimensionPixelSize(R.dimen.card_padding),
            resources.getDimensionPixelSize(R.dimen.card_padding)
        )

        // Configura la imagen del CardView
        val imageView = ImageView(this)
        imageView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            resources.getDimensionPixelSize(R.dimen.card_image_height)
        )
        imageView.setImageResource(R.drawable.futbasquet)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        cardView.addView(imageView)

        // Configura el fondo semitransparente blanco
        val overlay = LinearLayout(this)
        overlay.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        overlay.setBackgroundColor(
            ContextCompat.getColor(this, R.color.white_overlay)
        )
        overlay.alpha = 0.9f
        cardView.addView(overlay)

        // Configura el contenido del CardView
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

        // Añade el nombre del campo
        val textViewCampo = TextView(this)
        textViewCampo.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textViewCampo.text = document.getString("detalle")
        textViewCampo.setTextColor(ContextCompat.getColor(this, R.color.color_Boton))
        textViewCampo.setTextSize(
            resources.getDimension(R.dimen.card_text_size) / resources.displayMetrics.density
        )
        textViewCampo.setTypeface(null, Typeface.BOLD)
        linearLayout.addView(textViewCampo)

        // Añade la descripción del campo
        val textViewDescripcion = TextView(this)
        textViewDescripcion.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textViewDescripcion.text = "Cancha de ${document.getString("detalle")}"
        textViewDescripcion.setTextSize(
            resources.getDimension(R.dimen.card_text_size) / resources.displayMetrics.density
        )
        textViewDescripcion.setTypeface(null, Typeface.BOLD)
        linearLayout.addView(textViewDescripcion)

        // Añade el precio del campo
        val textViewPrecio = TextView(this)
        textViewPrecio.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textViewPrecio.text = "Precio: S/ ${document.getString("precioDia")} la hora"
        textViewPrecio.setTextSize(
            resources.getDimension(R.dimen.card_text_size) / resources.displayMetrics.density
        )
        textViewPrecio.setTypeface(null, Typeface.BOLD)
        linearLayout.addView(textViewPrecio)

        // Agrega el LinearLayout al CardView
        cardView.addView(linearLayout)

        // Configura el listener para reservar el campo al hacer clic en el CardView
        cardView.setOnClickListener {
            val detalleCampo = document.getString("detalle") // FUTBOL EJEMPLO
            val idDocumento = document.id  // Obtiene el ID del documento id_campo

            val NombreEmpresa = findViewById<TextView>(R.id.nombreCampoDeport)
            reservar(NombreEmpresa.text.toString(), detalleCampo!!, idDocumento)
        }

        return cardView
    }

    private fun reservar(nombreEmpresa: String, detalleCampo: String, idDocumento: String) {
        val i = Intent(this, ReservaActivity::class.java)
        i.putExtra("correo", correo)
        i.putExtra("NombreEmpresa", nombreEmpresa)
        i.putExtra("Campo", detalleCampo) //futrbol
        i.putExtra("IdDocumento", idDocumento)  // Pasa el ID del documento
        startActivity(i)
    }
}