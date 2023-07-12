package com.example.aplicacionmovil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

class CrearCuenta : AppCompatActivity() {

    private lateinit var et_Nombre: TextInputEditText
    private lateinit var et_Rut: TextInputEditText
    private lateinit var et_Correo: TextInputEditText
    private lateinit var et_Contraseña: TextInputEditText
    private lateinit var b_CrearCuenta: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)

        val minLength = 8

        val tv_IniciarSesion = findViewById<TextView>(R.id.tv_IniciarSesion)

        et_Nombre = findViewById(R.id.et_Nombre)
        et_Rut = findViewById(R.id.et_Rut)
        et_Correo = findViewById(R.id.et_Correo)
        et_Contraseña = findViewById(R.id.et_Contraseña)
        b_CrearCuenta = findViewById(R.id.b_CrearCuenta)

        tv_IniciarSesion.setOnClickListener {startActivity(Intent(this, MainActivity::class.java)) }

        et_Nombre.addTextChangedListener(textWatcher)
        et_Rut.addTextChangedListener(textWatcher)
        et_Correo.addTextChangedListener(textWatcher)
        et_Contraseña.addTextChangedListener(textWatcher)

        b_CrearCuenta.isEnabled = false
        b_CrearCuenta.setOnClickListener {startActivity(Intent(this, MainActivity::class.java))
            startActivity(Intent(this, MainActivity::class.java))
            Toast.makeText(this, "Cuenta creada con éxito, inicie sesión.", Toast.LENGTH_SHORT).show()
        }

        val rutFilter = InputFilter { source, start, end, _, _, _ ->
            val input = source.subSequence(start, end).toString()
            val allowedChars = "0123456789kK-"
            val validInput = input.filter { allowedChars.contains(it) }
            if (validInput.length != input.length) {
                val invalidChars = input.filterNot { allowedChars.contains(it) }
                et_Rut.error = "Caracteres no permitidos: $invalidChars"
            } else {
                et_Rut.error = null
            }
            validInput
        }
        et_Rut.filters = arrayOf(rutFilter)

        et_Contraseña.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                val userInput = s.toString()
                if (userInput.length < minLength) {
                    et_Contraseña.error = "Se requieren al menos $minLength caracteres."
                } else if (!userInput[0].isUpperCase()) {
                    et_Contraseña.error = "La primera letra debe ser mayúscula."
                } else if (!userInput.matches(".*[0-9].*".toRegex())) {
                    et_Contraseña.error = "Debe contener al menos un número."
                } else if (!userInput.matches(".*[^A-Za-z0-9].*".toRegex())) {
                    et_Contraseña.error = "Debe contener al menos un carácter especial."
                } else {
                    et_Contraseña.error = null
                }
                updateButtonEnabledState()
            }
        })

        et_Rut.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val userInput = s.toString()
                val rutPattern = Regex("^[0-9]{7,8}-[0-9kK]$")
                if (userInput.length !in 9..10) {
                    et_Rut.error = "RUT no valido."
                } else if (!userInput.matches(rutPattern)) {
                    et_Rut.error = "El RUT no tiene el formato correcto."
                } else {
                    et_Rut.error = null
                }
                updateButtonEnabledState()
            }
        })
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
        override fun afterTextChanged(s: Editable?) {
            updateButtonEnabledState()
        }
    }

    private fun updateButtonEnabledState() {
        val nombre = et_Nombre.text.toString().trim()
        val rut = et_Rut.text.toString().trim()
        val correo = et_Correo.text.toString().trim()
        val contraseña = et_Contraseña.text.toString().trim()

        val isNombreValid = nombre.isNotEmpty()
        val isRutValid = rut.length in 9..10 && rut.matches("^[0-9]{7,8}-[0-9kK]$".toRegex())
        val isCorreoValid = correo.isNotEmpty()
        val minLength = 8
        val isContraseñaValid = contraseña.length >= minLength && contraseña[0].isUpperCase() &&
                contraseña.matches(".*[0-9].*".toRegex()) && contraseña.matches(".*[^A-Za-z0-9].*".toRegex())


        b_CrearCuenta.isEnabled = isNombreValid && isRutValid && isCorreoValid && isContraseñaValid
    }

}