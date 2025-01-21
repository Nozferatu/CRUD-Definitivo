package com.cmj.crud_definitivo.composables

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.SubcomposeAsyncImage
import com.cmj.crud_definitivo.R
import com.cmj.crud_definitivo.crud.GuitarraCRUD
import com.cmj.crud_definitivo.entity.AccionGuitarra
import com.cmj.crud_definitivo.entity.Guitarra
import com.cmj.crud_definitivo.hacerTostada
import com.cmj.crud_definitivo.vistas.PersistirGuitarraActivity

@Composable
fun GuitarraItem(
    guitarra: Guitarra,
    accion: AccionGuitarra?,
    guitarraCRUD: GuitarraCRUD
) {
    val contexto = LocalContext.current

    val rating = remember { mutableFloatStateOf (guitarra.rating) }
    val url:String?=when(guitarra.urlImagen){
        "" -> null
        else -> guitarra.urlImagen
    }

    Card(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth()
            .clickable {
                if (accion != null) {
                    when (accion) {
                        AccionGuitarra.MODIFICAR -> {
                            val intent = Intent(contexto, PersistirGuitarraActivity::class.java)
                            intent.putExtra("guitarraIntent", guitarra)

                            contexto.startActivity(intent)
                        }

                        AccionGuitarra.BORRAR -> {
                            val builder: AlertDialog.Builder = AlertDialog.Builder(contexto)
                            builder
                                .setTitle("Confirmación")
                                .setMessage("¿Está seguro de que quiere borrar esta guitarra?")
                                .setPositiveButton("Sí") { _, _ ->
                                    guitarraCRUD.borrarGuitarra(guitarra)
                                }
                                .setNegativeButton("No") { _, _ ->
                                    hacerTostada(contexto, "Cancelado")
                                }

                            val dialog: AlertDialog = builder.create()
                            dialog.show()
                        }
                    }
                }
            }
    ) {
        ConstraintLayout(
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 15.dp)
                .fillMaxWidth()
        ) {
            val (
                imagen, nombre, descripcion,
                marca, modelo, precio,
                ratingBar
            ) = createRefs()

            Log.d("Debug", url!!)

            SubcomposeAsyncImage(
                modifier = Modifier
                    .size(75.dp)
                    .constrainAs(imagen) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    },
                model = url,
                contentDescription = "Imagen",
                contentScale = ContentScale.Crop,
                loading = { CircularProgressIndicator() },
                error = {
                    Image(
                        painter = painterResource(R.drawable.imagen_guitarra_default),
                        contentDescription = "Error"
                    )
                }
            )

            Text(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .constrainAs(nombre) {
                        top.linkTo(imagen.top)
                        start.linkTo(imagen.end)
                    },
                text = guitarra.nombre,
                fontSize = 14.sp
            )

            Text(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .constrainAs(descripcion) {
                        top.linkTo(nombre.bottom)
                        start.linkTo(imagen.end)
                    },
                text = guitarra.descripcion,
                fontSize = 14.sp
            )

            Text(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .constrainAs(marca) {
                        top.linkTo(parent.top)
                        end.linkTo(modelo.start)
                    },
                text = guitarra.marca,
                fontSize = 14.sp
            )

            Text(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .constrainAs(modelo) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    },
                text = guitarra.modelo,
                fontSize = 14.sp
            )

            Text(
                modifier = Modifier
                    .constrainAs(precio){
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    },
                text = guitarra.precio.toString(),
                fontSize = 14.sp
            )

            RatingBar(modifier = Modifier
                .constrainAs(ratingBar){
                    bottom.linkTo(parent.bottom)
                    start.linkTo(imagen.end)
                    end.linkTo(precio.start)
                },
                stars = 5,
                rating = rating.value,
                onRatingChanged = {
                    rating.floatValue = it
                }
            )
        }
    }
}