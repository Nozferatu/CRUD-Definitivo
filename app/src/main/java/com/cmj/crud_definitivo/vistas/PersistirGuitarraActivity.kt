package com.cmj.crud_definitivo.vistas

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cmj.crud_definitivo.crud.GuitarraCRUD
import com.cmj.crud_definitivo.entity.Guitarra
import com.cmj.crud_definitivo.ui.theme.CRUDDefinitivoTheme
import com.google.firebase.Firebase
import com.google.firebase.database.database
import io.appwrite.Client
import io.appwrite.services.Storage

class PersistirGuitarraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CRUDDefinitivoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PersistirGuitarra(
                        guitarra = Guitarra(),
                        this,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PersistirGuitarra(guitarra: Guitarra, activity: ComponentActivity, modifier: Modifier = Modifier) {
    val contexto = LocalContext.current

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

    var url_imagen: Uri? = null
    val accesoGaleria = activity.registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        if (uri != null) {
            url_imagen = uri
        }
    }

    //Tema inputs
    val modifierInput = Modifier
        .padding(vertical = 10.dp)
        .wrapContentHeight()
        .fillMaxWidth()

    var nombre: String by rememberSaveable { mutableStateOf("") }
    var descripcion: String by rememberSaveable { mutableStateOf("") }
    var marca: String by rememberSaveable { mutableStateOf("") }
    var modelo: String by rememberSaveable { mutableStateOf("") }
    var precio: String by rememberSaveable { mutableStateOf("") }

    Column(modifier = modifier
        .fillMaxWidth()
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
            label = { Text("Descripcióm") },
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
            singleLine = true
        )
    }
}