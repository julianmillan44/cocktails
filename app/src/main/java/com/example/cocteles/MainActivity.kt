package com.example.cocteles

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var txtcocktail: TextInputEditText
    private lateinit var txtIngredientOne: TextView
    private lateinit var txtIngredientTwo: TextView
    private lateinit var txtIngredientThree: TextView
    private lateinit var txtIngredientFour: TextView
    private lateinit var txtIngredientFive: TextView
    private lateinit var txtPreparation: TextView
    private lateinit var btnGetInfo: MaterialButton
    private lateinit var resultsCard: androidx.cardview.widget.CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar vistas
        txtcocktail = findViewById(R.id.txtcocktail)
        txtIngredientOne = findViewById(R.id.txtIngredientUno)
        txtIngredientTwo = findViewById(R.id.txtIngredientDos)
        txtIngredientThree = findViewById(R.id.txtIngredientTres)
        txtIngredientFour = findViewById(R.id.txtIngredientFour)
        txtIngredientFive = findViewById(R.id.txtIngredientFive)
        txtPreparation = findViewById(R.id.txtPreparation)
        btnGetInfo = findViewById(R.id.btnGetInfo)
        resultsCard = findViewById(R.id.resultsCard)
        
        // Inicialmente ocultar la tarjeta de resultados
        resultsCard.visibility = View.GONE

        btnGetInfo.setOnClickListener {
            fetchCocktailInfo()
        }
    }

    private fun fetchCocktailInfo() {
        val cocktail = txtcocktail.text.toString().trim()

        if (cocktail.isEmpty()) {
            Toast.makeText(this, "Por favor ingresar el nombre del cocktail", Toast.LENGTH_LONG).show()
        } else {
            val url = "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=$cocktail"

            // Crear solicitudes con Volley
            val requestQueue: RequestQueue = Volley.newRequestQueue(this)

            // Crear la solicitud al API
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                { response ->
                    this.handleCocktailResponse(response)
                },
                { error ->
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                    resultsCard.visibility = View.GONE
                }
            )
            // Agregar la solicitud
            requestQueue.add(jsonObjectRequest)
        }
    }

    private fun handleCocktailResponse(response: JSONObject) {
        // Verificar si la respuesta tiene el array "drinks"
        if (!response.has("drinks") || response.isNull("drinks")) {
            Toast.makeText(this, "No se encontró información para el cocktail especificado.", Toast.LENGTH_LONG).show()
            resultsCard.visibility = View.GONE
            return
        }

        // Obtener los datos del array "drinks" desde la respuesta JSON
        val drinks = response.getJSONArray("drinks")

        if (drinks.length() > 0) {
            // Mostrar la tarjeta de resultados
            resultsCard.visibility = View.VISIBLE
            
            // Obtener el primer objeto dentro del array
            val drink = drinks.getJSONObject(0)

            // Extraer los ingredientes e instrucciones
            txtIngredientOne.text = formatNullable(drink.optString("strIngredient1"))
            txtIngredientTwo.text = formatNullable(drink.optString("strIngredient2"))
            txtIngredientThree.text = formatNullable(drink.optString("strIngredient3"))
            txtIngredientFour.text = formatNullable(drink.optString("strIngredient4"))
            txtIngredientFive.text = formatNullable(drink.optString("strIngredient5"))
            
            // Intentar obtener instrucciones en español primero, si no hay, usar las instrucciones en inglés
            var instructions = drink.optString("strInstructionsES")
            if (instructions.isNullOrEmpty() || instructions == "null" || instructions == "No disponible") {
                instructions = drink.optString("strInstructions", "No disponible")
            }
            
            txtPreparation.text = instructions
        } else {
            // Si no hay resultados, mostrar un mensaje al usuario
            Toast.makeText(this, "No se encontró información para el cocktail especificado.", Toast.LENGTH_LONG).show()
            resultsCard.visibility = View.GONE
        }
    }
    
    // Función auxiliar para formatear valores potencialmente nulos o vacíos
    private fun formatNullable(value: String?): String {
        return when {
            value.isNullOrEmpty() -> "No especificado"
            value == "null" -> "No especificado"
            else -> value
        }
    }
}
