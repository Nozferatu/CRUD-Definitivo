package com.cmj.crud_definitivo.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.cmj.crud_definitivo.entity.Mensaje

@Composable
fun MensajeItem(mensaje: Mensaje){
    val fechaHoraItems = mensaje.fechaHora.split(" ")
    val fechaStr = fechaHoraItems[0]
    val horaStr = fechaHoraItems[1]

    Card(modifier = Modifier
        .wrapContentSize()
        .padding(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF367939)
        ),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        ConstraintLayout(modifier = Modifier
            .wrapContentSize()
            .padding(10.dp)
        ) {
            val (nombre, fecha, hora, contenido) = createRefs()

            Text(
                modifier = Modifier
                    .constrainAs(nombre){
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(fecha.start)
                    },
                text = mensaje.nombreEmisor,
                fontSize = 14.sp,
                color = Color.White
            )

            Text(
                modifier = Modifier
                    .constrainAs(contenido){
                        start.linkTo(nombre.start)
                        top.linkTo(nombre.bottom)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    },
                text = mensaje.contenido,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Text(
                modifier = Modifier
                    .constrainAs(fecha){
                        top.linkTo(parent.top)
                        start.linkTo(nombre.end)
                        end.linkTo(parent.end)
                    },
                text = fechaStr,
                fontSize = 14.sp,
                color = Color.White
            )

            Text(
                modifier = Modifier
                    .constrainAs(hora){
                        bottom.linkTo(parent.bottom)
                        top.linkTo(contenido.bottom)
                        end.linkTo(parent.end)
                    },
                text = horaStr,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}