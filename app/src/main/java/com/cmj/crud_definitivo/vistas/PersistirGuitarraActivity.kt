package com.cmj.crud_definitivo.vistas

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cmj.crud_definitivo.crud.GuitarraCRUD
import com.cmj.crud_definitivo.ui.theme.CRUDDefinitivoTheme
import com.google.firebase.database.DatabaseReference

class PersistirGuitarraActivity : ComponentActivity() {
    private lateinit var databaseRef: DatabaseReference
    private lateinit var id_proyecto: String
    private lateinit var id_bucket: String
    private lateinit var guitarraCRUD: GuitarraCRUD

    private var url_imagen: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CRUDDefinitivoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}