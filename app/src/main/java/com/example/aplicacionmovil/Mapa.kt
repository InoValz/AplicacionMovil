package com.example.aplicacionmovil

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class Mapa : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnMapReadyCallback {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)
        createFragment()

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_mapa2)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_Layout2)

        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open2, R.string.navigation_drawer_close2)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view2)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_inicio2 -> {
                Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MenuPrincipal::class.java)
                startActivity(intent)
            }
            R.id.nav_mapa2 -> {
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
            R.id.nav_cerrar_sesion2 -> {
                signOut() // Llama a la función para cerrar sesión
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Sesión Cerrada", Toast.LENGTH_SHORT).show()
                finish() // Cierra la actividad actual para evitar que el usuario regrese presionando "Atrás"
            }
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapa) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker()

    }

    private fun createMarker() {
        val coordinates = LatLng(-37.46973, -72.35366)
        val marker = MarkerOptions().position(coordinates).title("Los Angeles Chile")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
            4000,
            null
        )
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

    }
