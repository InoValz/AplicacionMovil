package com.example.aplicacionmovil

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MenuPrincipal : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var btnToolbar: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        btnToolbar = findViewById(R.id.btn_toolbar)
        btnToolbar.setOnClickListener {
            mostrarDialogoPublicacion()
        }


        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_menuprincipal)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_Layout)

        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_inicio -> {
                Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MenuPrincipal::class.java)
                startActivity(intent)
            }
            R.id.nav_mapa -> {
                Toast.makeText(this, "Mapa", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Mapa::class.java)
                startActivity(intent)
            }
            R.id.nav_notificaciones -> {
                Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_MisSectores -> {
                Toast.makeText(this, "Mis Sectores", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_cerrar_sesion -> {
                signOut() // Llama a la función para cerrar sesión
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Cierra la actividad actual para evitar que el usuario regrese presionando "Atrás"
            }
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        // Borra el ID del usuario de las preferencias compartidas
        val sharedPreferences = getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("UserId").apply()
    }

    private fun mostrarDialogoPublicacion() {
        // Crear un diálogo personalizado
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_pubicacion)

        // Configuraciones adicionales para centrar el diálogo
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER

        dialog.window?.attributes = layoutParams

        // Obtener referencias a elementos en el diálogo
        val editTextTitulo = dialog.findViewById<EditText>(R.id.editTextTitulo)
        val editTextDescripcion = dialog.findViewById<EditText>(R.id.editTextDescripcion)
        val editTextUbicacion = dialog.findViewById<EditText>(R.id.editTextUbicacion)
        val btnFecha = dialog.findViewById<Button>(R.id.btnFecha)
        val btnHora = dialog.findViewById<Button>(R.id.btnHora)
        val btnPublicar = dialog.findViewById<Button>(R.id.btnPublicar)

        // Configurar la acción del botón de publicación
        btnPublicar.setOnClickListener {
            // Aquí puedes obtener los valores de los campos
            val titulo = editTextTitulo.text.toString()
            val descripcion = editTextDescripcion.text.toString()
            val ubicacion = editTextUbicacion.text.toString()
            val fecha = btnFecha.text.toString()
            val hora = btnHora.text.toString()

            // Realiza la lógica de publicación con los datos ingresados
            // Por ejemplo, puedes guardar estos datos en Firebase Firestore
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val db = FirebaseFirestore.getInstance()
                val publicacion = hashMapOf(
                    "titulo" to titulo,
                    "descripcion" to descripcion,
                    "ubicacion" to ubicacion,
                    "fecha" to fecha,
                    "hora" to hora,
                    "userId" to userId
                )

                db.collection("publicaciones")
                    .add(publicacion)
                    .addOnSuccessListener {
                        // Publicación exitosa, cierra el diálogo
                        dialog.dismiss()
                        // Puedes mostrar un mensaje de éxito o realizar otras acciones
                    }
                    .addOnFailureListener {
                        // Manejar el error, si es necesario
                    }
            }
        }

        // Mostrar el diálogo
        dialog.show()
    }

}