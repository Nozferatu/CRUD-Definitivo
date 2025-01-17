package com.cmj.crud_definitivo.vistas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import androidx.constraintlayout.compose.ConstraintLayout
import com.cmj.crud_definitivo.R
import com.cmj.crud_definitivo.crud.GuitarraCRUD
import com.cmj.crud_definitivo.entity.AccionGuitarra
import com.cmj.crud_definitivo.entity.Guitarra
import com.cmj.crud_definitivo.ui.theme.CRUDDefinitivoTheme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.database

class ListadoGuitarrasActivity : ComponentActivity() {
    private var accion: AccionGuitarra? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val listaGuitarras = mutableStateListOf<Guitarra>()

        FirebaseApp.initializeApp(applicationContext)
        val dbRef = Firebase.database.reference
        val guitarraCRUD = GuitarraCRUD(applicationContext, dbRef)

        guitarraCRUD.recuperarGuitarras { guitarras ->
            listaGuitarras.clear()
            listaGuitarras.addAll(guitarras)
        }

        accion = intent.getSerializableExtra("accion") as AccionGuitarra?

        setContent {
            CRUDDefinitivoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ListadoGuitarras(
                        modifier = Modifier.padding(innerPadding),
                        listaGuitarras,
                        accion
                    )
                }
            }
        }
    }
}

@Composable
fun ListadoGuitarras(
    modifier: Modifier = Modifier,
    guitarras: SnapshotStateList<Guitarra>,
    accion: AccionGuitarra?
) {
    LazyColumn(modifier = modifier) {
        items(guitarras) { guitarra ->
            Guitarra(guitarra, accion)
        }
    }
}

@Composable
fun Guitarra(guitarra: Guitarra, accion: AccionGuitarra?) {
    val contexto = LocalContext.current
    val rating = remember { mutableFloatStateOf(guitarra.rating) }
    val url:String?=when(guitarra.urlImagen){
        "" -> null
        else -> guitarra.urlImagen
    }

    Card(
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 20.dp)
            .fillMaxWidth()
            .clickable {
                if(accion != null){
                    val intent = Intent(contexto, PersistirGuitarraActivity::class.java)
                    intent.putExtra("guitarraIntent", guitarra)

                    contexto.startActivity(intent)
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

            SubcomposeAsyncImage(
                modifier = Modifier
                    .size(75.dp)
                    .constrainAs(imagen){
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
                    .constrainAs(nombre){
                        top.linkTo(imagen.top)
                        start.linkTo(imagen.end)
                    },
                text = guitarra.nombre,
                fontSize = 14.sp
            )

            Text(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .constrainAs(descripcion){
                        top.linkTo(nombre.bottom)
                        start.linkTo(imagen.end)
                    },
                text = guitarra.descripcion,
                fontSize = 14.sp
            )

            Text(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .constrainAs(marca){
                        top.linkTo(parent.top)
                        end.linkTo(modelo.start)
                    },
                text = guitarra.marca,
                fontSize = 14.sp
            )

            Text(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .constrainAs(modelo){
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

            StarRatingBar(modifier = Modifier
                .constrainAs(ratingBar){
                    bottom.linkTo(parent.bottom)
                    start.linkTo(imagen.end)
                    end.linkTo(precio.start)
                },
                maxStars = 5,
                rating = rating.floatValue,
                onRatingChanged = {
                    rating.floatValue = it
                }
            )
        }
    }
}

@Composable
fun StarRatingBar(
    modifier: Modifier,
    maxStars: Int = 5,
    rating: Float,
    onRatingChanged: (Float) -> Unit
) {
    val density = LocalDensity.current.density
    val starSize = (12f * density).dp
    val starSpacing = (0.5f * density).dp

    Row(
        modifier = modifier.selectableGroup(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isSelected = i <= rating
            val icon = if (isSelected) Icons.Filled.Star else Icons.Default.Star
            val iconTintColor = if (isSelected) Color(0xFFFFC700) else Color(0xFF222222)
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier
                    /*.selectable(
                        selected = isSelected,
                        onClick = {
                            onRatingChanged(i.toFloat())
                        }
                    )*/
                    .width(starSize).height(starSize)
            )

            if (i < maxStars) {
                Spacer(modifier = Modifier.width(starSpacing))
            }
        }
    }
}
