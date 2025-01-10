package com.cmj.crud_definitivo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmj.crud_definitivo.ui.theme.CRUDDefinitivoTheme
import com.cmj.crud_definitivo.ui.theme.Purple40
import com.cmj.crud_definitivo.ui.theme.Purple80

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CRUDDefinitivoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Inicio(
                        nombre = "humano",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Inicio(nombre: String, modifier: Modifier = Modifier) {
    val colorBoton = ButtonDefaults.buttonColors(
        containerColor = Purple80
    )

    Column(modifier = modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¡Bienvenido, $nombre!",
            fontSize = 24.sp
        )

        Spacer(Modifier.height(40.dp))

        Button(
           modifier = Modifier
               .width(200.dp),
           colors = colorBoton,

           onClick = {}
        ) { Text("Iniciar sesión", color = Purple40) }

        Button(
            modifier = Modifier
                .width(200.dp),
            colors = colorBoton,

            onClick = {}
        ) { Text("Registrarse", color = Purple40) }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 900)
@Composable
fun InicioPreview() {
    CRUDDefinitivoTheme {
        Inicio("humano")
    }
}