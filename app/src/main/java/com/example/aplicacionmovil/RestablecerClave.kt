package com.example.aplicacionmovil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

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

        im_back2.setOnClickListener {
            finish()
        }

        b_Enviar.setOnClickListener {
            val correo = et_Correo2.text.toString().trim()

            if (correo.isNotEmpty()) {
                val auth = FirebaseAuth.getInstance()

                auth.sendPasswordResetEmail(correo)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Correo de restablecimiento enviado con éxito
                            Toast.makeText(this, "Se ha enviado un correo de restablecimiento a $correo", Toast.LENGTH_SHORT).show()
                        } else {
                            // Hubo un error al enviar el correo de restablecimiento
                            Toast.makeText(this, "Error al enviar el correo de restablecimiento. Por favor, intenta nuevamente.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor, ingresa tu correo electrónico.", Toast.LENGTH_SHORT).show()
            }
        }

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