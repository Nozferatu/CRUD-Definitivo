package com.cmj.crud_definitivo

import android.content.Context
import android.widget.Toast
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import java.security.MessageDigest

fun animacion_carga(contexto: Context): CircularProgressDrawable {
    val animacion = CircularProgressDrawable(contexto)
    animacion.strokeWidth = 5f
    animacion.centerRadius = 30f
    animacion.start()

    return animacion
}

val transicion = DrawableTransitionOptions.withCrossFade(500)

fun opcionesGlide(context: Context): RequestOptions {
    val options = RequestOptions()
        .placeholder(animacion_carga(context))
        //.fallback(R.drawable.imagen_guitarra_default)
        .error(R.drawable.ic_launcher_foreground)
    return options
}

fun hacerTostada(contexto: Context, mensaje: String, tiempo: Int = Toast.LENGTH_SHORT){
    Toast.makeText(
        contexto,
        mensaje,
        tiempo
    ).show()
}

fun String.toSHA256(): String{
    val HEX_CHARS = "0123456789ABCDEF"
    val digest = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())

    return digest.joinToString(
        separator = "",
        transform = { a ->
            String(
                charArrayOf(
                    HEX_CHARS[a.toInt() shr 4 and 0X0f],
                    HEX_CHARS[a.toInt() and 0x0f]
                )
            )
        }
    )
}