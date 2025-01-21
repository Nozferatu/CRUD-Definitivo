package com.cmj.crud_definitivo.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cmj.crud_definitivo.entity.Mensaje

@Composable
fun MensajeItem(mensaje: Mensaje){
    Card(modifier = Modifier
        .padding(vertical =  10.dp, horizontal = 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF367939)
        ),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Text(
            modifier = Modifier
            .padding(10.dp),
            text = mensaje.contenido,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}