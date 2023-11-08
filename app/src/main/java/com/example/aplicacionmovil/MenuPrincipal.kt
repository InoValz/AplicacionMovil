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
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MenuItem
import android.view.View
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class MenuPrincipal : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var btnToolbar: ImageButton
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private lateinit var btnFecha: Button
    private lateinit var btnHora: Button
    private val publicacionesList = mutableListOf<Publicacion>()
    private lateinit var publicacionAdapter: PublicacionAdapter

    private var totalPublicacionesDisponibles = 0
    private val CANTIDAD_PUBLICACIONES_POR_CARGA = 5 // Aquí define la cantidad deseada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = FirebaseDatabase.getInstance()
        val reference: DatabaseReference = database.getReference("publicaciones")

        FirebaseFirestore.getInstance()
        setContentView(R.layout.activity_menu_principal)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val btnLoadMore = findViewById<Button>(R.id.btnLoadMore)
        btnLoadMore.setOnClickListener {
            onCargarMasClick()
        }

        publicacionAdapter = PublicacionAdapter(publicacionesList)
        recyclerView.adapter = publicacionAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        cargarPublicaciones()

        val adapter = PublicacionAdapter(publicacionesList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnToolbar = findViewById(R.id.btn_toolbar)
        btnToolbar.setOnClickListener {
            mostrarDialogoPublicacion()
        }

        val edtBuscar = findViewById<EditText>(R.id.edtBuscar)

        edtBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Filtrar publicaciones según el texto de búsqueda
                val textoBusqueda = s.toString().toLowerCase()
                val publicacionesFiltradas = publicacionesList.filter { publicacion ->
                    publicacion.titulo.toLowerCase().contains(textoBusqueda) ||
                            publicacion.ubicacion.toLowerCase().contains(textoBusqueda) ||
                            publicacion.categoria.toLowerCase().contains(textoBusqueda) ||
                            publicacion.fecha.toLowerCase().contains(textoBusqueda)
                }

                mostrarPublicacionesEnUI(publicacionesFiltradas)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_menuprincipal)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_Layout)

        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Agrega un oyente de valores para la referencia
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Se llama cuando los datos cambian en la base de datos
                val nuevasPublicaciones = mutableListOf<Publicacion>()

                for (snapshot in dataSnapshot.children) {
                    val publicacion = snapshot.getValue(Publicacion::class.java)
                    if (publicacion != null) {
                        nuevasPublicaciones.add(publicacion)
                    }
                }

                publicacionesList.clear()
                publicacionesList.addAll(nuevasPublicaciones)

                // Actualiza la IU con las nuevas publicaciones
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MenuPrincipal, "Error al cargar las publicaciones", Toast.LENGTH_SHORT).show()
            }
        })

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
                signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
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
        val sharedPreferences = getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("UserId").apply()
    }

    private fun mostrarDialogoPublicacion() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_publicacion)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER

        dialog.window?.attributes = layoutParams

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
                selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
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
                selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                btnHora.text = selectedTime
            }, hourOfDay, minute, true)
            timePickerDialog.show()
        }

        val btnCategoria = dialog.findViewById<Button>(R.id.btnCategoria)
        var categoriaSeleccionada: String = ""
        btnCategoria.setOnClickListener{
            mostrarMenuCategorias()
            val categorias = arrayOf("Categoría 1", "Categoría 2", "Categoría 3", "Categoría 4")

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Selecciona una categoría")
                .setItems(categorias) { _, which ->
                    categoriaSeleccionada = categorias[which]
                    btnCategoria.text = categoriaSeleccionada
                }
            builder.create().show()
        }

        val btnPublicar = dialog.findViewById<Button>(R.id.btnPublicar)

        btnPublicar.setOnClickListener {
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
                reference.child(userId).push().setValue(publicacion).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        dialog.dismiss()
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
        // Implementa el DatePickerDialog aquí
    }

    private fun mostrarTimePickerDialog() {
        // Implementa el TimePickerDialog aquí
    }

    private fun mostrarMenuCategorias() {
        // Implementa la lógica para mostrar el menú de categorías aquí
    }

    private fun cargarPublicaciones() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val reference = FirebaseDatabase.getInstance().getReference("publicaciones").child(userId)

            // Obtener el número total de publicaciones disponibles
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    totalPublicacionesDisponibles = dataSnapshot.childrenCount.toInt()
                    // Limitar la cantidad de publicaciones a cargar inicialmente
                    val cantidadInicial = minOf(totalPublicacionesDisponibles, 5)

                    // Consultar las últimas "cantidadInicial" publicaciones
                    reference.limitToLast(cantidadInicial).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val nuevasPublicaciones = mutableListOf<Publicacion>()

                            for (snapshot in dataSnapshot.children) {
                                val publicacion = snapshot.getValue(Publicacion::class.java)
                                if (publicacion != null) {
                                    nuevasPublicaciones.add(publicacion)
                                }
                            }

                            publicacionesList.clear()
                            publicacionesList.addAll(nuevasPublicaciones)

                            mostrarPublicacionesEnUI(publicacionesList)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(this@MenuPrincipal, "Error al cargar las publicaciones", Toast.LENGTH_SHORT).show()
                        }
                    })
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@MenuPrincipal, "Error al obtener el número total de publicaciones", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun onCargarMasClick() {
        if (publicacionesList.size < totalPublicacionesDisponibles) {
            val cantidadPorCargar = minOf(CANTIDAD_PUBLICACIONES_POR_CARGA, totalPublicacionesDisponibles - publicacionesList.size)
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val reference = FirebaseDatabase.getInstance().getReference("publicaciones").child(userId)
                reference.limitToLast(publicacionesList.size + cantidadPorCargar).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val nuevasPublicaciones = mutableListOf<Publicacion>()

                        for (snapshot in dataSnapshot.children) {
                            val publicacion = snapshot.getValue(Publicacion::class.java)
                            if (publicacion != null) {
                                nuevasPublicaciones.add(publicacion)
                            }
                        }

                        publicacionesList.clear()
                        publicacionesList.addAll(nuevasPublicaciones)

                        mostrarPublicacionesEnUI(publicacionesList)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(this@MenuPrincipal, "Error al cargar más publicaciones", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        } else {
            Toast.makeText(this, "No hay más publicaciones para cargar", Toast.LENGTH_SHORT).show()
        }
    }


    private fun mostrarPublicacionesEnUI(publicaciones: List<Publicacion>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = PublicacionAdapter(publicaciones)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}
