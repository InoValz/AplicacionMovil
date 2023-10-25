package com.example.aplicacionmovil

import PublicacionAdapter
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar



class MenuPrincipal : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var btnToolbar: ImageButton
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private lateinit var btnFecha: Button
    private lateinit var btnHora: Button
    private val publicacionesList = mutableListOf<Publicacion>()
    private lateinit var publicacionAdapter: PublicacionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseFirestore.getInstance()
        setContentView(R.layout.activity_menu_principal)

        val database = FirebaseDatabase.getInstance()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        publicacionAdapter = PublicacionAdapter(publicacionesList)
        recyclerView.adapter = publicacionAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        cargarPublicaciones()


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
        dialog.setContentView(R.layout.dialog_publicacion)

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
            btnFecha.setOnClickListener {
                mostrarDatePickerDialog()
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                    // Formatea la fecha seleccionada como desees, aquí se usa el formato dd/MM/yyyy
                    selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    // Muestra la fecha en el botón o en otro lugar si lo prefieres
                    btnFecha.text = selectedDate
                }, year, month, dayOfMonth)
                datePickerDialog.show()
            }
        val btnHora = dialog.findViewById<Button>(R.id.btnHora)
            btnHora.setOnClickListener {
                mostrarTimePickerDialog()
                val calendar = Calendar.getInstance()
                val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                val timePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    // Formatea la hora seleccionada como desees, aquí se usa el formato HH:mm
                    selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                    // Muestra la hora en el botón o en otro lugar si lo prefieres
                    btnHora.text = selectedTime
                }, hourOfDay, minute, true)
                timePickerDialog.show()
            }
        val btnCategoria = dialog.findViewById<Button>(R.id.btnCategoria)
        var categoriaSeleccionada: String = ""
            btnCategoria.setOnClickListener{
                mostrarMenuCategorias()
                val categorias = arrayOf("Categoría 1", "Categoría 2", "Categoría 3", "Categoría 4") // Define tus categorías aquí

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Selecciona una categoría")
                    .setItems(categorias) { _, which ->
                        categoriaSeleccionada = categorias[which]
                        btnCategoria.text = categoriaSeleccionada
                    }
                builder.create().show()
            }

        val btnPublicar = dialog.findViewById<Button>(R.id.btnPublicar)

        // Configurar la acción del botón de publicación
        btnPublicar.setOnClickListener {
            // Aquí puedes obtener los valores de los campos
            val titulo = editTextTitulo.text.toString()
            val descripcion = editTextDescripcion.text.toString()
            val ubicacion = editTextUbicacion.text.toString()
            val fecha = selectedDate
            val hora = selectedTime
            val categoria = categoriaSeleccionada
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val database = FirebaseDatabase.getInstance()
                val reference = database.getReference("publicaciones")

                val publicacion = Publicacion(
                    titulo,
                    descripcion,
                    ubicacion,
                    fecha,
                    hora,
                    categoria,
                    userId
                )
                // Guarda la publicación en la base de datos con la ID del usuario como clave
                reference.child(userId).push().setValue(publicacion).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        dialog.dismiss()
                        // Puedes mostrar un mensaje de éxito o realizar otras acciones
                        Toast.makeText(this, "Publicación exitosa", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al publicar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun mostrarDatePickerDialog() {
    }
    private fun mostrarTimePickerDialog() {
    }
    private fun mostrarMenuCategorias() {
    }

    private fun cargarPublicaciones() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val reference = FirebaseDatabase.getInstance().getReference("publicaciones").child(userId)
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    publicacionesList.clear()
                    for (snapshot in dataSnapshot.children) {
                        val publicacion = snapshot.getValue(Publicacion::class.java)
                        if (publicacion != null) {
                            publicacionesList.add(publicacion)
                        }
                    }
                    // Luego de obtener las publicaciones, puedes mostrarlas en la interfaz de usuario
                    mostrarPublicacionesEnUI(publicacionesList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Manejar el error, si es necesario
                    Toast.makeText(this@MenuPrincipal, "Error al cargar las publicaciones", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun mostrarPublicacionesEnUI(publicaciones: List<Publicacion>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = PublicacionAdapter(publicaciones)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }




}