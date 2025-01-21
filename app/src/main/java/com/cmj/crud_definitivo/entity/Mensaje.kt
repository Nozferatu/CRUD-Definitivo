package com.cmj.crud_definitivo.entity

import java.io.Serializable

data class Mensaje(
    var key: String = "",
    var contenido: String = "",
    var fechaHora: String = "",
    var idEmisor: String = "",
    //var urlAvatar: String,
    var nombreEmisor: String = ""
): Serializable
