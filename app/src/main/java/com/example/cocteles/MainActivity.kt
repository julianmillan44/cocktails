package com.example.cocteles

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

        private lateinit var txtcocktail: EditText
        private lateinit var txtIngredientOne: EditText
        private lateinit var txtIngredientTwo: EditText
        private lateinit var txtIngredientThree: EditText
        private lateinit var txtIngredientFour: EditText
        private lateinit var txtIngredientFive: EditText
        private lateinit var txtPreparation: EditText
        private lateinit var btnGetInfo : Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtcocktail = findViewById(R.id.txtcocktail)
        txtIngredientOne = findViewById(R.id.txtIngredientUno)
        txtIngredientTwo = findViewById(R.id.txtIngredientDos)
        txtIngredientThree = findViewById(R.id.txtIngredientTres)
        txtIngredientFour = findViewById(R.id.txtIngredientFour)
        txtIngredientFive = findViewById(R.id.txtIngredientFive)
        btnGetInfo = findViewById(R.id.btnGetInfo)
        txtPreparation =findViewById(R.id.txtPreparation)



        btnGetInfo.setOnClickListener {
            fetchCocktailInfo()
        }

    }

    private fun fetchCocktailInfo() {

        val cocktail = txtcocktail.text.toString().trim()

        if (cocktail.isEmpty()){
            Toast.makeText(this, "Por favor ingresar el nombre del cocktail", Toast.LENGTH_LONG).show()
        }else{
            var url = "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=$cocktail"

            //Crear solicitudes con Volley
            val requestQueue : RequestQueue = Volley.newRequestQueue(this)

            //Crear la solicitud al API
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                { response ->
                    this.handleCocktailResponse(response)
                },
                { error ->
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                }

            )
            //Agregar la solicitud
            requestQueue.add(jsonObjectRequest)
        }
    }

    private fun handleCocktailResponse(response: JSONObject) {
        // Obtener los datos del array "drinks" desde la respuesta JSON
        val drinks = response.getJSONArray("drinks")

        if (drinks.length() > 0) {
            // Obtener el primer objeto dentro del array
            val drink = drinks.getJSONObject(0)

            // Extraer los ingredientes e instrucciones
            val ingredienteUno = drink.optString("strIngredient1", "No disponible")
            val ingredienteDos = drink.optString("strIngredient2", "No disponible")
            val ingredienteTres = drink.optString("strIngredient3", "No disponible")
            val ingredienteCuatro = drink.optString("strIngredient4", "No disponible")
            val ingredienteCinco = drink.optString("strIngredient5", "No disponible")
            val preparacionCocktail = drink.optString("strInstructionsES", "No disponible")

            // Mostrar los datos en los EditText correspondientes
            txtIngredientOne.apply {
                visibility = View.VISIBLE
                setText(ingredienteUno)
            }

            txtIngredientTwo.apply {
                visibility = View.VISIBLE
                setText(ingredienteDos)
            }

            txtIngredientThree.apply {
                visibility = View.VISIBLE
                setText(ingredienteTres)
            }

            txtIngredientFour.apply {
                visibility = View.VISIBLE
                setText(ingredienteCuatro)
            }

            txtIngredientFive.apply {
                visibility = View.VISIBLE
                setText(ingredienteCinco)
            }

            txtPreparation.apply {
                visibility = View.VISIBLE
                setText(preparacionCocktail)
            }
        } else {
            // Si no hay resultados, mostrar un mensaje al usuario
            Toast.makeText(this, "No se encontró información para el cocktail especificado.", Toast.LENGTH_LONG).show()
        }
    }


}