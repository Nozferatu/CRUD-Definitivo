package com.cmj.crud_definitivo.vistas

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cmj.crud_definitivo.composables.MensajeItem
import com.cmj.crud_definitivo.crud.ChatCRUD
import com.cmj.crud_definitivo.entity.Mensaje
import com.cmj.crud_definitivo.entity.Usuario
import com.cmj.crud_definitivo.ui.theme.CRUDDefinitivoTheme
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import java.util.Calendar

class ChatActivity : ComponentActivity() {
    private lateinit var sesion: Usuario
    private lateinit var chatCRUD: ChatCRUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val databaseRef = Firebase.database.reference
        chatCRUD = ChatCRUD(applicationContext, databaseRef)

        sesion = intent.getSerializableExtra("sesion") as Usuario

        setContent {
            CRUDDefinitivoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Chat(
                        modifier = Modifier.padding(innerPadding),
                        sesion,
                        chatCRUD
                    )
                }
            }
        }
    }
}

@Composable
fun Chat(modifier: Modifier = Modifier, sesion: Usuario, chatCRUD: ChatCRUD) {
    val coroutineScope = rememberCoroutineScope()
    val listaMensajes = remember { mutableStateListOf<Mensaje>() }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            chatCRUD.recuperarMensajes{ mensajes ->
                listaMensajes.clear()
                listaMensajes.addAll(mensajes)
            }
        }
    }

    var contenidoMensaje by rememberSaveable { mutableStateOf("") }

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
    ) {
        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
        ) {
            items(listaMensajes) { mensaje ->
                val esEmisor = sesion.key == mensaje.idEmisor
                val paddingLimite = when(esEmisor){
                    true -> PaddingValues(start = 20.dp)
                    false -> PaddingValues(end = 20.dp)
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingLimite),
                    horizontalArrangement = when(esEmisor){
                        true -> Arrangement.End
                        false -> Arrangement.Start
                    }
                ) {
                    MensajeItem(mensaje)
                }
            }
        }

        Row(modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(20.dp)
                    .wrapContentHeight()
                    .weight(1f),
                value = contenidoMensaje,
                onValueChange = { contenidoMensaje = it }
            )

            Button(modifier = Modifier
                .padding(10.dp)
                .weight(.3f),
                onClick = {
                    val calendar = Calendar.getInstance()

                    val fechaActual = StringBuilder()
                    with(fechaActual){
                        append(calendar.get(Calendar.YEAR))
                        append("-")
                        append(calendar.get(Calendar.MONTH) + 1)
                        append("-")
                        append(calendar.get(Calendar.DATE))
                    }

                    val horaActual = StringBuilder()
                    with(horaActual){
                        append(calendar.get(Calendar.HOUR_OF_DAY))
                        append(":")

                        val minutos = calendar.get(Calendar.MINUTE)
                        if(minutos > 9) append(minutos)
                        else append("0$minutos")
                        append(":")

                        val segundos = calendar.get(Calendar.SECOND)
                        if(segundos > 9) append(segundos)
                        else append("0$segundos")
                    }

                    val fechaHora = "$fechaActual $horaActual"
                    Log.d("Chat", fechaHora)

                    val mensaje = Mensaje("", contenidoMensaje, fechaHora, sesion.key, sesion.nombre)

                    coroutineScope.launch {
                        chatCRUD.mandarMensaje(mensaje)
                    }
                }
            ) {
                Icon(imageVector = Icons.AutoMirrored.Default.Send, "Enviar")
            }
        }
    }
}