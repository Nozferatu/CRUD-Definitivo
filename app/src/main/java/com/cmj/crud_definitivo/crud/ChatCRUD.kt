package com.cmj.crud_definitivo.crud

import android.content.Context
import com.cmj.crud_definitivo.entity.Mensaje
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ChatCRUD(
    private var contexto: Context,
    private var databaseRef: DatabaseReference
) {
    fun mandarMensaje(mensaje: Mensaje){
        val idRef = databaseRef.child("chat_publico").push().key!!
        mensaje.key = idRef

        databaseRef.child("chat_publico").child(idRef).setValue(mensaje)
    }

    fun recuperarMensajes(onDataReady: (List<Mensaje>) -> Unit){
        val listaMensajes = mutableListOf<Mensaje>()

        databaseRef.child("chat_publico")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaMensajes.clear()

                    snapshot.children.forEach { child: DataSnapshot? ->
                        val pojoMensaje = child?.getValue(Mensaje::class.java)
                        pojoMensaje?.let { listaMensajes.add(it) }
                    }

                    onDataReady(listaMensajes)
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })
    }
}