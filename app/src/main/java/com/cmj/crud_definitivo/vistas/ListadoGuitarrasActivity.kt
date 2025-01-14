package com.cmj.crud_definitivo.vistas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import androidx.constraintlayout.compose.ConstraintLayout
import com.cmj.crud_definitivo.R
import com.cmj.crud_definitivo.crud.GuitarraCRUD
import com.cmj.crud_definitivo.entity.Guitarra
import com.cmj.crud_definitivo.ui.theme.CRUDDefinitivoTheme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.database

class ListadoGuitarrasActivity : ComponentActivity() {
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

        setContent {
            CRUDDefinitivoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ListadoGuitarras(
                        modifier = Modifier.padding(innerPadding),
                        listaGuitarras
                    )
                }
            }
        }
    }
}

@Composable
fun ListadoGuitarras(
    modifier: Modifier = Modifier,
    guitarras: SnapshotStateList<Guitarra>
) {
    LazyColumn(modifier = modifier) {
        items(guitarras) { guitarra ->
            Guitarra(guitarra)
        }
    }
}

@Composable
fun Guitarra(guitarra: Guitarra) {
    val URL:String?=when(guitarra.urlImagen){
        "" -> null
        else -> guitarra.urlImagen
    }

    Card(
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        ConstraintLayout(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
        ) {
            val (
                imagen, nombre, descripcion,
                marca, modelo, precio,
                rating
            ) = createRefs()

            SubcomposeAsyncImage(
                modifier = Modifier
                    .size(100.dp)
                    .constrainAs(imagen){
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    },
                model = URL,
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

            /*Text(
                modifier = Modifier
                    .padding(start = 10.dp),
                text = guitarra.nombre
            )

            Text(
                modifier = Modifier
                    .padding(start = 10.dp),
                text = guitarra.descripcion
            )*/
        }
    }
}
