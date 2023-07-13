package com.example.aplicacionmovil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var et_Rut: TextInputEditText
    private lateinit var et_Contraseña: TextInputEditText
    private lateinit var b_iniciarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv_CrearCuenta = findViewById<TextView>(R.id.tv_CrearCuenta)
        val tv_ReestablecerClave = findViewById<TextView>(R.id.tv_ReestablecerClave)

        et_Rut = findViewById(R.id.et_Rut)
        et_Contraseña = findViewById(R.id.et_Contraseña)
        b_iniciarSesion = findViewById(R.id.b_iniciarSesion)

        tv_CrearCuenta.setOnClickListener {startActivity(Intent(this, CrearCuenta::class.java)) }

        tv_ReestablecerClave.setOnClickListener {startActivity(Intent(this, RestablecerClave::class.java))}

        et_Rut.addTextChangedListener(textWatcher)
        et_Contraseña.addTextChangedListener(textWatcher)

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
                val minLength = 8
                if (userInput.length < minLength) {
                    et_Contraseña.error = "Se requieren al menos $minLength caracteres."
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

        b_iniciarSesion.isEnabled = false
        b_iniciarSesion.setOnClickListener {startActivity(Intent(this, MenuPrincipal::class.java)) }

    }
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            val input1 = et_Rut.text.toString().trim()
            val input2 = et_Contraseña.text.toString().trim()

            b_iniciarSesion.isEnabled = input1.isNotEmpty() && input2.isNotEmpty()

            updateButtonEnabledState()
        }
    }
    private fun updateButtonEnabledState() {
        val rut = et_Rut.text.toString().trim()
        val contraseña = et_Contraseña.text.toString().trim()

        val isRutValid = rut.length in 9..10 && rut.matches("^[0-9]{7,8}-[0-9kK]$".toRegex())
        val minLength = 8
        val isContraseñaValid = contraseña.length >= minLength && contraseña[0].isUpperCase() &&
                contraseña.matches(".*[0-9].*".toRegex()) && contraseña.matches(".*[^A-Za-z0-9].*".toRegex())

        b_iniciarSesion.isEnabled = isRutValid && isContraseñaValid
    }
}