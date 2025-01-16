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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.cmj.crud_definitivo.R
import com.cmj.crud_definitivo.crud.GuitarraCRUD
import com.cmj.crud_definitivo.entity.Guitarra
import com.cmj.crud_definitivo.ui.theme.CRUDDefinitivoTheme
import com.cmj.crud_definitivo.ui.theme.Purple40
import com.cmj.crud_definitivo.ui.theme.Purple80
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.database
import io.appwrite.Client
import io.appwrite.services.Storage

class PersistirGuitarraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(applicationContext)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CRUDDefinitivoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PersistirGuitarra(
                        guitarra = Guitarra(),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PersistirGuitarra(guitarra: Guitarra, modifier: Modifier = Modifier) {
    val contexto = LocalContext.current
    val colorBoton = ButtonDefaults.buttonColors(
        containerColor = Purple80
    )

    //Tema DB
    val databaseRef = Firebase.database.reference
    var guitarraCRUD = GuitarraCRUD(contexto, databaseRef)

    //Tema AppWrite
    val id_proyecto = "6762fbc00010d599c17c"
    var id_bucket = "6762fbed003a60f5f03f"
    val client = Client()
        .setEndpoint("https://cloud.appwrite.io/v1")
        .setProject(id_proyecto)
    val bucket = Storage(client)

    //Tema inputs
    val modifierInput = Modifier
        .padding(vertical = 10.dp)
        .wrapContentHeight()
        .fillMaxWidth()

    //Tema galería
    var url_imagen by remember { mutableStateOf<Uri?>(null) }
    //val bitmap =  remember { mutableStateOf<Bitmap?>(null) }
    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        url_imagen = uri
    }

    var nombre: String by rememberSaveable { mutableStateOf("") }
    var descripcion: String by rememberSaveable { mutableStateOf("") }
    var marca: String by rememberSaveable { mutableStateOf("") }
    var modelo: String by rememberSaveable { mutableStateOf("") }
    var precio: String by rememberSaveable { mutableStateOf("") }

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
    ){
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

                }
            ) { Text("Agregar guitarra", color = Purple40) }
        }
    }
}