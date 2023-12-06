package com.example.aplicacionmovil

import ComentariosAdapter
import PublicacionAdapter
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.widget.ImageButton
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.Calendar

class MenuPrincipal : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    PublicacionAdapter.OnComentariosButtonClickListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var btnToolbar: ImageButton

    private var selectedDate: String = ""
    private var selectedTime: String = ""

    private var publicacionesList = mutableListOf<Publicacion>()
    private lateinit var publicacionAdapter: PublicacionAdapter

    private lateinit var btnLoadMore: Button

    private var totalPublicacionesDisponibles = 0
    private var lastLoadedPublicationsCount = 0
    private val CANTIDAD_PUBLICACIONES_POR_CARGA = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)
        val database = FirebaseDatabase.getInstance()
        val reference: DatabaseReference = database.getReference("publicaciones")

        val edtBuscar = findViewById<EditText>(R.id.edtBuscar)

        edtBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textoBusqueda = s.toString().toLowerCase()
                val publicacionesFiltradas = publicacionesList.filter { publicacion ->
                    publicacion.titulo.toLowerCase().contains(textoBusqueda) ||
                            publicacion.ubicacion.toLowerCase().contains(textoBusqueda) ||
                            publicacion.categoria.toLowerCase().contains(textoBusqueda) ||
                            publicacion.fecha.toLowerCase().contains(textoBusqueda)
                }

                mostrarPublicacionesEnUI(publicacionesFiltradas)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        initializeViews()
        setupDrawer()
        setupListeners()
        cargarPublicaciones()

    }

    private fun initializeViews() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        publicacionAdapter = PublicacionAdapter()
        publicacionAdapter.setOnComentariosButtonClickListener(this)
        recyclerView.adapter = publicacionAdapter

        btnLoadMore = findViewById(R.id.btnLoadMore)
        btnToolbar = findViewById(R.id.btn_toolbar)
    }
    private fun setupDrawer() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_menuprincipal)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_Layout)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    private fun setupListeners() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        btnToolbar.setOnClickListener {
            mostrarDialogoPublicacion()
        }

        btnLoadMore.setOnClickListener {
            onCargarMasClick()
        }
    }

    override fun onComentariosButtonClick(position: Int, tipoBoton: PublicacionAdapter.TipoBoton) {
        val publicacion = publicacionesList[position]

        when (tipoBoton) {
            PublicacionAdapter.TipoBoton.ESCRIBIR -> mostrarDialogoEscribirComentarios()
            PublicacionAdapter.TipoBoton.LEER -> {
                val comentarios = publicacion.comentarios.map { it.texto }
                mostrarDialogoLeerComentarios(comentarios)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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

        val tipoBoton = PublicacionAdapter.TipoBoton.ESCRIBIR

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
        btnCategoria.setOnClickListener {
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

                val postId = reference.push().key

                if (postId != null) {
                    val publicacion = Publicacion(
                        postId,
                        titulo,
                        descripcion,
                        ubicacion,
                        fecha,
                        hora,
                        categoria,
                        userId,
                        tipoBoton = tipoBoton
                    )

                    reference.child(postId).setValue(publicacion).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            publicacionesList.clear()
                            cargarPublicaciones()
                            mostrarPublicacionesEnUI(publicacionesList)
                            dialog.dismiss()
                            Toast.makeText(this, "Publicación exitosa", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error al publicar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
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
            val reference = FirebaseDatabase.getInstance().getReference("publicaciones")
            reference.orderByKey().limitToLast(CANTIDAD_PUBLICACIONES_POR_CARGA).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    totalPublicacionesDisponibles = dataSnapshot.childrenCount.toInt()
                    var nuevasPublicaciones = mutableListOf<Publicacion>()

                    for (snapshot in dataSnapshot.children) {
                        val publicacion = snapshot.getValue(Publicacion::class.java)
                        if (publicacion != null) {
                            nuevasPublicaciones.add(publicacion)
                        }
                    }

                    if (nuevasPublicaciones.size > CANTIDAD_PUBLICACIONES_POR_CARGA) {
                        nuevasPublicaciones = nuevasPublicaciones.take(CANTIDAD_PUBLICACIONES_POR_CARGA).toMutableList()
                    }

                    lastLoadedPublicationsCount = nuevasPublicaciones.size
                    publicacionesList.clear()
                    if (nuevasPublicaciones.isNotEmpty()) {
                        publicacionesList.addAll(nuevasPublicaciones.reversed())
                    }

                    mostrarPublicacionesEnUI(publicacionesList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@MenuPrincipal, "Error al cargar las publicaciones", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun onCargarMasClick() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val reference = FirebaseDatabase.getInstance().getReference("publicaciones")

            if (publicacionesList.isNotEmpty()) {
                val lastLoadedKey = publicacionesList.lastOrNull()?.id ?: ""
                reference.orderByKey().endBefore(lastLoadedKey).limitToLast(CANTIDAD_PUBLICACIONES_POR_CARGA)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val nuevasPublicaciones = mutableListOf<Publicacion>()

                            for (snapshot in dataSnapshot.children) {
                                val publicacion = snapshot.getValue(Publicacion::class.java)
                                if (publicacion != null) {
                                    nuevasPublicaciones.add(publicacion)
                                }
                            }

                            if (nuevasPublicaciones.size > CANTIDAD_PUBLICACIONES_POR_CARGA) {
                                nuevasPublicaciones.removeAt(0)
                            }

                            val nuevasPublicacionesOrdenadas = nuevasPublicaciones.reversed()
                            publicacionesList.addAll(nuevasPublicacionesOrdenadas)

                            mostrarPublicacionesEnUI(publicacionesList)

                            if (publicacionesList.size >= totalPublicacionesDisponibles) {
                                Toast.makeText(this@MenuPrincipal, "Todas las publicaciones han sido cargadas", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(this@MenuPrincipal, "Error al cargar más publicaciones", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Toast.makeText(this@MenuPrincipal, "No hay más publicaciones para cargar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarPublicacionesEnUI(publicaciones: List<Publicacion>) {
        publicacionAdapter.submitList(publicaciones)
        publicacionAdapter.notifyDataSetChanged()

    }

    private fun mostrarDialogoEscribirComentarios() {
        val dialog_a = Dialog(this)
        dialog_a.setContentView(R.layout.dialog_escribircomentarios)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog_a.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER

        dialog_a.window?.attributes = layoutParams

        val ediTextComentario = dialog_a.findViewById<EditText>(R.id.ediTextComentario)
        val btnComentar = dialog_a.findViewById<Button>(R.id.btnComentar)

        btnComentar.setOnClickListener {
            val comentarioText = ediTextComentario.text.toString()
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId != null) {
                if (comentarioText.isNotEmpty()) {
                    val database = FirebaseDatabase.getInstance()
                    val reference = database.getReference("comentarios")

                    val comentarioId = reference.push().key

                    if (comentarioId != null) {
                        val comentario = Comentario(
                            id = comentarioId,
                            texto = comentarioText,
                            userId = userId
                        )

                        reference.child(comentarioId).setValue(comentario).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Realiza las acciones necesarias después de publicar el comentario
                                dialog_a.dismiss()
                                Toast.makeText(this, "Comentario exitoso", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Error al comentar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Por favor, ingresa un comentario", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog_a.show()
    }

    private fun mostrarDialogoLeerComentarios(comentarios: List<String>) {

        val dialog_b = Dialog(this)
        dialog_b.setContentView(R.layout.dialog_comentarios)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog_b.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER

        dialog_b.window?.attributes = layoutParams

        val recyclerViewComentarios = dialog_b.findViewById<RecyclerView>(R.id.recyclerViewComentarios)
        val comentariosAdapter = ComentariosAdapter()

        recyclerViewComentarios.adapter = comentariosAdapter
        recyclerViewComentarios.layoutManager = LinearLayoutManager(this)

        // Configura el adaptador con la lista de comentarios
        comentariosAdapter.setComentarios(comentarios)

        // Botón para cerrar el diálogo de leer comentarios
        val btnCerrar = dialog_b.findViewById<ImageButton>(R.id.btnCerrar)
        btnCerrar.setOnClickListener {
            dialog_b.dismiss()
        }

        dialog_b.show()
    }

}
