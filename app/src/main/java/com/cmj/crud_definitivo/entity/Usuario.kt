package com.cmj.crud_definitivo.entity

import java.io.Serializable

data class Usuario(
    var key: String,
    var nombre: String,
    var pass: String
):Serializable {
    constructor() : this("", "", "")
}
