package com.cmj.crud_definitivo.vistas.usuario

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmj.crud_definitivo.composables.GuitarraItem
import com.cmj.crud_definitivo.crud.GuitarraCRUD
import com.cmj.crud_definitivo.entity.AccionGuitarra
import com.cmj.crud_definitivo.entity.Guitarra
import com.cmj.crud_definitivo.entity.Usuario
import com.cmj.crud_definitivo.ui.theme.CRUDDefinitivoTheme
import com.cmj.crud_definitivo.ui.theme.Purple40
import com.cmj.crud_definitivo.ui.theme.Purple80
import com.cmj.crud_definitivo.vistas.ListadoGuitarrasActivity
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.database

class GuitarrasFavoritasActivity : ComponentActivity() {
    private lateinit var guitarraCRUD: GuitarraCRUD
    private lateinit var sesion: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val guitarrasFavoritas = mutableStateListOf<Guitarra>()

        //Tema AppWrite
        val id_proyecto = "6762fbc00010d599c17c"
        val id_bucket = "6762fbed003a60f5f03f"

        //Tema Firebase
        FirebaseApp.initializeApp(applicationContext)
        val dbRef = Firebase.database.reference
        guitarraCRUD = GuitarraCRUD(applicationContext, dbRef, id_proyecto, id_bucket)

        sesion = intent.getSerializableExtra("sesion") as Usuario

        guitarraCRUD.recuperarGuitarrasFavoritas(sesion) { guitarras ->
            for(guitarra in guitarras){
                Log.d("Debug", guitarra.toString())
            }
            guitarrasFavoritas.clear()
            guitarrasFavoritas.addAll(guitarras)
        }

        setContent {
            CRUDDefinitivoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GuitarrasFavoritas(
                        modifier = Modifier.padding(innerPadding),
                        guitarrasFavoritas,
                        guitarraCRUD,
                        sesion
                    )
                }
            }
        }
    }
}

@Composable
fun GuitarrasFavoritas(
    modifier: Modifier = Modifier,
    guitarrasFavoritas: SnapshotStateList<Guitarra>,
    guitarraCRUD: GuitarraCRUD,
    sesion: Usuario
) {
    val contexto = LocalContext.current
    val modifierBoton = Modifier
        .width(200.dp)
        .padding(vertical = 10.dp)
    val colorBoton = ButtonDefaults.buttonColors(
        containerColor = Purple80
    )

    Column(modifier = modifier.padding(horizontal = 20.dp, vertical = 10.dp)){
        LazyColumn(modifier = Modifier
            .fillMaxWidth()
        ){
            item{
                Text(
                    modifier = Modifier.padding(10.dp),
                    text =  "Guitarras favoritas de ${sesion.nombre}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(guitarrasFavoritas){ guitarra ->
                GuitarraItem(guitarra, guitarraCRUD, AccionGuitarra.BORRAR_FAVORITA, sesion)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ){
            Button(
                modifier = modifierBoton,
                colors = colorBoton,
                onClick = {
                    val intent = Intent(contexto, ListadoGuitarrasActivity::class.java)
                    intent.putExtra("sesion", sesion)
                    intent.putExtra("accion", AccionGuitarra.AGREGAR_FAVORITA)
                    contexto.startActivity(intent)
                }
            ) { Text("Añadir guitarra", color = Purple40) }
        }
    }
}
