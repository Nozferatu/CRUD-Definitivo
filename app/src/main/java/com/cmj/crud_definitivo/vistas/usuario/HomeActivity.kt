package com.cmj.crud_definitivo.vistas.usuario

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmj.crud_definitivo.entity.AccionGuitarra
import com.cmj.crud_definitivo.entity.Usuario
import com.cmj.crud_definitivo.ui.theme.CRUDDefinitivoTheme
import com.cmj.crud_definitivo.ui.theme.Purple40
import com.cmj.crud_definitivo.ui.theme.Purple80
import com.cmj.crud_definitivo.vistas.ChatActivity
import com.cmj.crud_definitivo.vistas.ListadoGuitarrasActivity
import com.cmj.crud_definitivo.vistas.PersistirGuitarraActivity

class HomeActivity : ComponentActivity() {
    private lateinit var sesion: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sesion = intent.getSerializableExtra("sesion") as Usuario

        setContent {
            CRUDDefinitivoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Home(
                        modifier = Modifier.padding(innerPadding),
                        sesion
                    )
                }
            }
        }
    }
}

@Composable
fun Home(modifier: Modifier = Modifier, sesion: Usuario) {
    val contexto = LocalContext.current
    val modifierBoton = Modifier
        .width(200.dp)
        .padding(vertical = 10.dp)
    val colorBoton = ButtonDefaults.buttonColors(
        containerColor = Purple80
    )

    Column(modifier = modifier
        .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(modifier = Modifier
            .padding(vertical = 40.dp),
            text = "¿Qué acción desde realizar?",
            fontSize = 20.sp
        )

        Button(
            modifier = modifierBoton,
            colors = colorBoton,

            onClick = {
                val intent = Intent(contexto, PersistirGuitarraActivity::class.java)
                contexto.startActivity(intent)
            }
        ) { Text("Agregar guitarra", color = Purple40) }

        Button(
            modifier = modifierBoton,
            colors = colorBoton,

            onClick = {
                val intent = Intent(contexto, ListadoGuitarrasActivity::class.java)
                intent.putExtra("accion", AccionGuitarra.MODIFICAR)
                contexto.startActivity(intent)
            }
        ) { Text("Modificar guitarra", color = Purple40) }

        Button(
            modifier = modifierBoton,
            colors = colorBoton,

            onClick = {
                val intent = Intent(contexto, ListadoGuitarrasActivity::class.java)
                intent.putExtra("accion", AccionGuitarra.BORRAR)
                contexto.startActivity(intent)
            }
        ) { Text("Borrar guitarra", color = Purple40) }

        Button(
            modifier = modifierBoton,
            colors = colorBoton,

            onClick = {
                val intent = Intent(contexto, ListadoGuitarrasActivity::class.java)
                contexto.startActivity(intent)
            }
        ) { Text("Listar guitarras", color = Purple40) }

        Button(
            modifier = modifierBoton,
            colors = colorBoton,

            onClick = {
                val intent = Intent(contexto, ChatActivity::class.java)
                intent.putExtra("sesion", sesion)
                contexto.startActivity(intent)
            }
        ) { Text("Foro público", color = Purple40) }
    }
}
