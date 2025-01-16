package com.cmj.crud_definitivo.vistas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmj.crud_definitivo.HomeActivity
import com.cmj.crud_definitivo.crud.UsuarioCRUD
import com.cmj.crud_definitivo.entity.Usuario
import com.cmj.crud_definitivo.hacerTostada
import com.cmj.crud_definitivo.toSHA256
import com.cmj.crud_definitivo.ui.theme.CRUDDefinitivoTheme
import com.cmj.crud_definitivo.ui.theme.Purple40
import com.cmj.crud_definitivo.ui.theme.Purple80
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.database
import kotlinx.coroutines.launch

class IniciarSesionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseApp.initializeApp(applicationContext)

        val dbRef = Firebase.database.reference
        val usuarioCRUD = UsuarioCRUD(applicationContext, dbRef)

        setContent {
            CRUDDefinitivoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    InicioSesion(
                        modifier = Modifier.padding(innerPadding),
                        usuarioCRUD
                    )
                }
            }
        }
    }
}

@Composable
fun InicioSesion(modifier: Modifier = Modifier, usuarioCRUD: UsuarioCRUD) {
    val contexto = LocalContext.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val modifierInput = Modifier
        .padding(vertical = 10.dp)
        .wrapContentHeight()
        .fillMaxWidth()

    val colorBoton = ButtonDefaults.buttonColors(
        containerColor = Purple80
    )

    var nombre: String by rememberSaveable { mutableStateOf("") }
    var password: String by rememberSaveable { mutableStateOf("") }

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 40.dp)
        .verticalScroll(scrollState)
    ) {
        Text(modifier = Modifier
            .padding(vertical = 20.dp),
            text = "Inicio de sesión",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            modifier = modifierInput,
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            singleLine = true
        )

        OutlinedTextField(
            modifier = modifierInput,
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                modifier = Modifier
                    .width(150.dp)
                    .padding(vertical = 30.dp),
                colors = colorBoton,

                onClick = {
                    val sesion = Usuario("", nombre.trim(), password.toSHA256())

                    coroutineScope.launch {
                        usuarioCRUD.buscarUsuario(sesion, true) { usuario ->
                            if (usuario != null) {
                                hacerTostada(contexto, "Inicio de sesión correcto")

                                val intent = Intent(contexto, HomeActivity::class.java)
                                intent.putExtra("sesion", usuario)

                                contexto.startActivity(intent)
                            }
                        }
                    }
                }
            ) { Text("Confirmar", color = Purple40) }
        }
    }
}