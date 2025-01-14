package com.cmj.crud_definitivo.entity

import java.io.Serializable

data class Guitarra(
    var key: String = "",
    var fechaCreacion: String = "2000-01-01",
    var idImagen: String = "",
    var urlImagen: String = "",
    var nombre: String = "",
    var descripcion: String = "",
    var marca: String = "",
    var modelo: String = "",
    var rating: Float = 5f,
    var precio: Float = 100f
): Serializable
