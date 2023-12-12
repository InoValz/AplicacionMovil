package com.example.aplicacionmovil

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class Mapa : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)
        createFragment()

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_mapa2)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_Layout2)

        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open2,
            R.string.navigation_drawer_close2
        )
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view2)
        navigationView.setNavigationItemSelectedListener(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_inicio2 -> {
                Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MenuPrincipal::class.java)
                startActivity(intent)
            }
            R.id.nav_mapa2 -> {
                Toast.makeText(this, "Mapa", Toast.LENGTH_SHORT).show()
                // No es necesario crear una nueva instancia de Mapa, ya estás en Mapa
            }
            R.id.nav_notificaciones -> {
                Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_MisSectores -> {
                Toast.makeText(this, "Mis Sectores", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_cerrar_sesion2 -> {
                signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Sesión Cerrada", Toast.LENGTH_SHORT).show()
                finish()
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

        // Solicitar permisos de ubicación
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permiso ya concedido
            getDeviceLocation()
        } else {
            // Solicitar permiso si no se ha concedido
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }

    }

    private fun getDeviceLocation() {
        try {
            // Si los permisos de ubicación están habilitados
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        // Se obtiene la última ubicación conocida
                        location?.let {
                            val coordinates = LatLng(it.latitude, it.longitude)
                            val marker = MarkerOptions().position(coordinates).title("Ubicación Actual")
                            map.addMarker(marker)
                            map.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
                                4000,
                                null
                            )
                        }
                    }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
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

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getDeviceLocation()
                } else {
                    Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
}
