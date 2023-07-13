package com.example.aplicacionmovil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import com.google.android.material.textfield.TextInputEditText

class RestablecerClave : AppCompatActivity() {

    private lateinit var im_back2: ImageButton
    private lateinit var et_Correo2: TextInputEditText
    private lateinit var b_Enviar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restablecer_clave)

        im_back2 = findViewById(R.id.im_back2)
        et_Correo2 = findViewById(R.id.et_Correo2)
        b_Enviar = findViewById(R.id.b_Enviar)

        et_Correo2.addTextChangedListener(textWatcher)

        val botonBack: ImageButton = findViewById(R.id.im_back2)
        botonBack.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })

        b_Enviar.isEnabled = false

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
        val correo = et_Correo2.text.toString().trim()

        val isCorreoValid = correo.isNotEmpty()
        b_Enviar.isEnabled = isCorreoValid
    }

}