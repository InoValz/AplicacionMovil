package com.example.aplicacionmovil

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Publicacion(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val ubicacion: String = "",
    val fecha: String = "",
    val hora: String = "",
    val categoria: String = "",
    val userId: String = "",
    val comentarios: List<Comentario> = mutableListOf(),
    val tipoBoton: PublicacionAdapter.TipoBoton = PublicacionAdapter.TipoBoton.ESCRIBIR,
) {
    // Constructor sin argumentos necesario para Firebase
    constructor() : this("", "", "", "", "", "", "", "", mutableListOf(), PublicacionAdapter.TipoBoton.ESCRIBIR)
}
