package com.cmj.crud_definitivo.vistas

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.cmj.crud_definitivo.hacerTostada
import com.cmj.crud_definitivo.ui.theme.CRUDDefinitivoTheme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.database

class ListadoGuitarrasActivity : ComponentActivity() {
    private var accion: AccionGuitarra? = null
    private lateinit var guitarraCRUD: GuitarraCRUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val listaGuitarras = mutableStateListOf<Guitarra>()

        //Tema AppWrite
        val id_proyecto = "6762fbc00010d599c17c"
        val id_bucket = "6762fbed003a60f5f03f"

        //Tema Firebase
        FirebaseApp.initializeApp(applicationContext)
        val dbRef = Firebase.database.reference
        guitarraCRUD = GuitarraCRUD(applicationContext, dbRef, id_proyecto, id_bucket)

        guitarraCRUD.recuperarGuitarras { guitarras ->
            for(guitarra in guitarras){
                Log.d("Debug", guitarra.toString())
            }
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
                        guitarraCRUD,
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
    guitarraCRUD: GuitarraCRUD,
    accion: AccionGuitarra?
) {
    val modifierInput = Modifier
        .padding(vertical = 10.dp)
        .wrapContentHeight()
        .fillMaxWidth()

    var inputFiltro: String by rememberSaveable { mutableStateOf("") }
    val guitarrasFiltradasState = remember { mutableStateOf(guitarras.toList()) }
    val guitarrasFiltradasDerived = remember { derivedStateOf {
            guitarras.filter {
                it.nombre
                    .lowercase()
                    .contains(inputFiltro.lowercase())
            }
        }
    }

    LaunchedEffect(key1 = inputFiltro) {
        guitarrasFiltradasState.value = guitarras.filter {
            it.nombre
                .lowercase()
                .contains(inputFiltro.lowercase())
        }
    }

    LazyColumn(modifier = modifier.padding(horizontal = 20.dp)) {
        item {
            OutlinedTextField(
                modifier = modifierInput,
                value = inputFiltro,
                onValueChange = { value ->
                    inputFiltro = value

                                },
                label = { Text("Buscar guitarra por nombre") },
                singleLine = true
            )
        }

        items(guitarrasFiltradasDerived.value) { guitarra ->
            Guitarra(guitarra, accion, guitarraCRUD)
        }
    }
}

@Composable
fun Guitarra(guitarra: Guitarra, accion: AccionGuitarra?, guitarraCRUD: GuitarraCRUD) {
    val contexto = LocalContext.current

    val rating = remember { mutableFloatStateOf(guitarra.rating) }
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
                    .width(starSize)
                    .height(starSize)
            )

            if (i < maxStars) {
                Spacer(modifier = Modifier.width(starSpacing))
            }
        }
    }
}
