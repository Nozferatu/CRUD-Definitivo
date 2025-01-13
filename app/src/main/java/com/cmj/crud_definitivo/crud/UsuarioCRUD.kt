package com.cmj.crud_definitivo.crud

import android.content.Context
import com.cmj.crud_definitivo.entity.Usuario
import com.cmj.crud_definitivo.hacerTostada
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener


class UsuarioCRUD(
    var contexto: Context,
    var databaseRef: DatabaseReference
) {
    fun buscarUsuario(usuario: Usuario, verbose: Boolean = false, callback: (Usuario?) -> Unit) {
        var usuarioEncontrado: Usuario? = null
        var usuarioExiste = false
        var passwordValida = false

        databaseRef.child("usuarios").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (pojo in snapshot.children) {
                    val usuarioPojo = pojo.getValue(Usuario::class.java)

                    if (usuarioPojo != null) {
                        if (usuario.nombre == usuarioPojo.nombre) {
                            usuarioExiste = true
                            usuarioEncontrado = usuarioPojo

                            if (usuario.pass == usuarioPojo.pass) {
                                passwordValida = true
                            }

                            break
                        }
                    }
                }

                if(!usuarioExiste && verbose) hacerTostada(contexto, "El usuario no existe")
                if(passwordValida) callback(usuarioEncontrado)
                else if(verbose) hacerTostada(contexto, "La contraseña no es correcta")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    fun usuarioExiste(usuario: Usuario, callback: (Boolean?) -> Unit){
        databaseRef.child("usuarios").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (pojo in snapshot.children) {
                    val usuarioPojo = pojo.getValue(Usuario::class.java)

                    if (usuarioPojo != null) {
                        if (usuario.nombre == usuarioPojo.nombre) {
                            callback(true)

                            break
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    fun registrarUsuario(usuario: Usuario): String{
        val idRef = databaseRef.child("usuarios").push().key!!
        usuario.key = idRef

        databaseRef.child("usuarios").child(idRef).setValue(usuario)

        return idRef
    }
}