package com.example.aplicacionmovil

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var et_Correo: TextInputEditText
    private lateinit var et_Contraseña: TextInputEditText
    private lateinit var b_iniciarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

        val auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        val tv_CrearCuenta = findViewById<TextView>(R.id.tv_CrearCuenta)
        val tv_ReestablecerClave = findViewById<TextView>(R.id.tv_ReestablecerClave)

        et_Correo = findViewById(R.id.et_Correo)
        et_Contraseña = findViewById(R.id.et_Contraseña)
        b_iniciarSesion = findViewById(R.id.b_iniciarSesion)

        tv_CrearCuenta.setOnClickListener {startActivity(Intent(this, CrearCuenta::class.java)) }

        tv_ReestablecerClave.setOnClickListener {startActivity(Intent(this, RestablecerClave::class.java))}

        et_Correo.addTextChangedListener(textWatcher)
        et_Contraseña.addTextChangedListener(textWatcher)

        et_Contraseña.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                val userInput = s.toString()
                val minLength = 8
                if (userInput.length < minLength) {
                    et_Contraseña.error = "Se requieren al menos $minLength caracteres."
                } else {
                    et_Contraseña.error = null
                }

            }
        })

        b_iniciarSesion.setOnClickListener {
            val correo = et_Correo.text.toString().trim()
            val contraseña = et_Contraseña.text.toString().trim()

            if (correo.isNotEmpty() && contraseña.isNotEmpty()) {
                // Iniciar sesión con Firebase Authentication
                auth.signInWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            if (user != null) {
                                val userId = user.uid
                                saveUserId(userId)
                            }
                            val intent = Intent(this, MenuPrincipal::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val error = task.exception?.message
                            Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor, ingresa tu correo y contraseña.", Toast.LENGTH_SHORT).show()
            }
        }

        if (currentUser != null) {
            startActivity(Intent(this, MenuPrincipal::class.java))
            finish()
        }


    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            val correo = et_Correo.text.toString().trim()
            val contraseña = et_Contraseña.text.toString().trim()

            b_iniciarSesion.isEnabled = correo.isNotEmpty() && contraseña.isNotEmpty()

        }
    }

    private fun saveUserId(userId: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("UserId", userId)
        editor.apply()
    }
}