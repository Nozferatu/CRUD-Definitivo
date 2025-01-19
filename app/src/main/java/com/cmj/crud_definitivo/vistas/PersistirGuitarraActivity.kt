package com.cmj.crud_definitivo.vistas

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.SubcomposeAsyncImage
import com.cmj.crud_definitivo.R
import com.cmj.crud_definitivo.crud.GuitarraCRUD
import com.cmj.crud_definitivo.entity.Guitarra
import com.cmj.crud_definitivo.hacerTostada
import com.cmj.crud_definitivo.ui.theme.CRUDDefinitivoTheme
import com.cmj.crud_definitivo.ui.theme.Purple40
import com.cmj.crud_definitivo.ui.theme.Purple80
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import java.util.Calendar

class PersistirGuitarraActivity : ComponentActivity() {
    private var guitarraIntent: Guitarra? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(applicationContext)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        guitarraIntent = intent.getSerializableExtra("guitarraIntent") as Guitarra?

        setContent {
            CRUDDefinitivoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PersistirGuitarra(
                        guitarra = guitarraIntent,
                        modifier = Modifier.padding(innerPadding),
                        this
                    )
                }
            }
        }
    }
}

@Composable
fun PersistirGuitarra(guitarra: Guitarra?, modifier: Modifier = Modifier, activity: ComponentActivity) {
    val contexto = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val colorBoton = ButtonDefaults.buttonColors(
        containerColor = Purple80
    )

    //Tema AppWrite
    val id_proyecto = "6762fbc00010d599c17c"
    val id_bucket = "6762fbed003a60f5f03f"

    //Tema DB
    val databaseRef = Firebase.database.reference
    val guitarraCRUD = GuitarraCRUD(contexto, databaseRef, id_proyecto, id_bucket)

    //Tema inputs
    val modifierInput = Modifier
        .padding(vertical = 10.dp)
        .wrapContentHeight()
        .fillMaxWidth()

    //Tema galería
    var url_imagen by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        url_imagen = uri
    }

    var nombre: String by rememberSaveable { mutableStateOf(guitarra?.nombre ?: "") }
    var descripcion: String by rememberSaveable { mutableStateOf(guitarra?.descripcion ?: "") }
    var marca: String by rememberSaveable { mutableStateOf(guitarra?.marca ?: "") }
    var modelo: String by rememberSaveable { mutableStateOf(guitarra?.modelo ?: "") }
    var precio: String by rememberSaveable { mutableStateOf(guitarra?.precio?.toString() ?: "") }
    val rating = remember { mutableFloatStateOf(guitarra?.rating ?: 5f) }

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
    ){
        LaunchedEffect(Unit) {
            if(guitarra != null){
                url_imagen = guitarra.urlImagen.toUri()
            }
        }

        OutlinedTextField(
            modifier = modifierInput,
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            singleLine = true
        )

        OutlinedTextField(
            modifier = modifierInput,
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            singleLine = true
        )

        OutlinedTextField(
            modifier = modifierInput,
            value = marca,
            onValueChange = { marca = it },
            label = { Text("Marca") },
            singleLine = true
        )

        OutlinedTextField(
            modifier = modifierInput,
            value = modelo,
            onValueChange = { modelo = it },
            label = { Text("Modelo") },
            singleLine = true
        )

        OutlinedTextField(
            modifier = modifierInput,
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        RatingBar(modifier = Modifier,
            stars = 5,
            rating = rating.floatValue,
            canChangeRating = true,
            onRatingChanged = {
                rating.floatValue = it
            }
        )

        Button(modifier = Modifier.padding(vertical = 20.dp),
            onClick = {
                launcher.launch("image/*")
            }
        ) { Text("Escoger imagen") }

        SubcomposeAsyncImage(
            modifier = Modifier
                .size(100.dp),
            model = url_imagen,
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

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                modifier = Modifier
                    .width(200.dp),
                colors = colorBoton,

                onClick = {
                    if(nombre.isNotEmpty()){
                        if(url_imagen != null){
                            val calendar = Calendar.getInstance()
                            val fechaActual = StringBuilder()
                                .append(calendar.get(Calendar.YEAR)).append("-")
                                .append(calendar.get(Calendar.MONTH) + 1).append("-")
                                .append(calendar.get(Calendar.DATE)).toString()

                            coroutineScope.launch{
                                val idFile: String

                                if(guitarra != null){
                                    if(guitarra.urlImagen != url_imagen.toString()){
                                        idFile = guitarraCRUD.guardarImagenGuitarra(url_imagen)
                                    }else{
                                        idFile = guitarra.idImagen
                                    }
                                }else{
                                    idFile = guitarraCRUD.guardarImagenGuitarra(url_imagen)
                                }

                                val urlImagen =
                                    "https://cloud.appwrite.io/v1/storage/buckets/$id_bucket/files/$idFile/preview?project=$id_proyecto"

                                val guitarraAPersistir = Guitarra(guitarra?.key ?: "",
                                    fechaActual,
                                    idFile,
                                    urlImagen,
                                    nombre,
                                    descripcion,
                                    marca,
                                    modelo,
                                    rating.floatValue,
                                    precio.toFloatOrNull() ?: 0f
                                )

                                guitarraCRUD.persistirGuitarra(guitarraAPersistir)
                                activity.finish()
                            }
                        }else hacerTostada(contexto, "Hay que elegir una imagen")
                    }else hacerTostada(contexto, "El nombre no puede estar vacío")
                }
            ) { Text(
                text = when(guitarra) {
                    null -> { "Agregar guitarra" }
                    else -> { "Modificar guitarra" }
                },
                color = Purple40
            )
            }
        }
    }
}