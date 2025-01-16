package com.cmj.crud_definitivo.crud

import android.content.Context
import android.os.Looper
import com.cmj.crud_definitivo.entity.Guitarra
import com.cmj.crud_definitivo.hacerTostada
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class GuitarraCRUD(
    private var contexto: Context,
    private var databaseRef: DatabaseReference
) {
    fun persistirGuitarra(guitarra: Guitarra){
        if(guitarra.key.isNotBlank()){
            databaseRef.child("guitarras").child(guitarra.key).setValue(guitarra)

            hacerTostada(contexto, "Guitarra modificada")
        }else{
            val idRef = databaseRef.child("guitarras").push().key!!
            guitarra.key = idRef

            databaseRef.child("guitarras").child(idRef).setValue(guitarra)

            hacerTostada(contexto, "Guitarra creada")
        }
    }

    fun recuperarGuitarras(onDataReady: (List<Guitarra>) -> Unit) {
        val listaGuitarras = mutableListOf<Guitarra>()
        val dbRef = Firebase.database.reference

        dbRef.child("guitarras")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaGuitarras.clear()
                    snapshot.children.forEach { child: DataSnapshot? ->
                        val pojoGuitarra = child?.getValue(Guitarra::class.java)
                        pojoGuitarra?.let { listaGuitarras.add(it) }
                    }
                    onDataReady(listaGuitarras)
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })
    }
}