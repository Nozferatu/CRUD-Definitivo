package com.cmj.crud_definitivo.crud

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.cmj.crud_definitivo.entity.Guitarra
import com.cmj.crud_definitivo.entity.Usuario
import com.cmj.crud_definitivo.hacerTostada
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.HashMap

class GuitarraCRUD(
    private var contexto: Context,
    private var databaseRef: DatabaseReference,
    private var idProyecto: String? = "",
    private var idBucket: String? = ""
) {
    private var contentResolver: ContentResolver
    private var client: Client
    private var bucket: Storage

    init {
        contentResolver = contexto.contentResolver
        client = Client()
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject(idProyecto!!)
        bucket = Storage(client)
    }

    suspend fun guardarImagenGuitarra(uriImagen: Uri?): String{
        var nombreArchivo = ""
        val inputStream = contentResolver.openInputStream(uriImagen!!)
        val aux = contentResolver.query(uriImagen, null, null, null, null)
        aux.use {
            if (it!!.moveToFirst()) {
                // Obtener el nombre del archivo
                val nombreIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nombreIndex != -1) {
                    nombreArchivo = it.getString(nombreIndex)
                }
            }
        }
        val mimeType: String = contentResolver.getType(uriImagen).toString()

        val fileInput = InputFile.fromBytes(
            bytes = inputStream?.readBytes() ?: byteArrayOf(),
            filename = nombreArchivo,
            mimeType = mimeType
        )

        val idFile = ID.unique()
        bucket.createFile(
            bucketId = idBucket!!,
            fileId = idFile,
            file = fileInput
        )

        return idFile
    }

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

    fun agregarGuitarraFavorita(guitarra: Guitarra, usuario: Usuario){
        val data = HashMap<String, Guitarra>()
        data.put(guitarra.key, guitarra)
        databaseRef.child("guitarras_favoritas").child(usuario.key).setValue(data)

        hacerTostada(contexto, "Guitarra favorita añadida")
    }

    fun borrarGuitarra(guitarra: Guitarra){
        //Se borra la imagen de AppWrite
        GlobalScope.launch {
            bucket.deleteFile(
                bucketId = idBucket!!,
                fileId = guitarra.idImagen
            )

            Log.d("AppWrite", "Imagen borrada: ${guitarra.urlImagen}")
        }

        //Se borra del Firebase
        databaseRef.child("guitarras").child(guitarra.key).removeValue()
        hacerTostada(contexto, "Guitarra borrada")
    }

    fun borrarGuitarraFavorita(guitarra: Guitarra, sesion: Usuario){
        databaseRef.child("guitarras_favoritas").child(sesion.key).child(guitarra.key).removeValue()

        hacerTostada(contexto, "Guitarra favorita borrada")
    }

    fun guitarraExistePorNombre(nombre: String, existe: (Boolean) -> Unit){
        var nombreCoincide = false

        databaseRef.child("guitarras")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(child: DataSnapshot? in snapshot.children) {
                        val pojoGuitarra = child?.getValue(Guitarra::class.java)

                        if(pojoGuitarra?.nombre.equals(nombre)){
                            nombreCoincide = true
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                    existe(false)
                }
            })

        existe(nombreCoincide)
    }

    fun recuperarGuitarras(onDataReady: (List<Guitarra>) -> Unit) {
        val listaGuitarras = mutableListOf<Guitarra>()

        databaseRef.child("guitarras")
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

    fun recuperarGuitarrasFavoritas(usuario: Usuario, onDataReady: (List<Guitarra>) -> Unit) {
        val listaGuitarras = mutableListOf<Guitarra>()

        databaseRef.child("guitarras_favoritas")
            .child(usuario.key)
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