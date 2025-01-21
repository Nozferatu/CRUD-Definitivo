package com.cmj.crud_definitivo.vistas

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cmj.crud_definitivo.composables.GuitarraItem
import com.cmj.crud_definitivo.crud.GuitarraCRUD
import com.cmj.crud_definitivo.entity.AccionGuitarra
import com.cmj.crud_definitivo.entity.Guitarra
import com.cmj.crud_definitivo.ui.theme.CRUDDefinitivoTheme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.database

class ListadoGuitarrasActivity : ComponentActivity() {
    private var accion: AccionGuitarra? = null
    private lateinit var guitarraCRUD: GuitarraCRUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val listaGuitarras = mutableStateListOf<Guitarra>()

        //Tema AppWrite
        val id_proyecto = "6762fbc00010d599c17c"
        val id_bucket = "6762fbed003a60f5f03f"

        //Tema Firebase
        FirebaseApp.initializeApp(applicationContext)
        val dbRef = Firebase.database.reference
        guitarraCRUD = GuitarraCRUD(applicationContext, dbRef, id_proyecto, id_bucket)

        guitarraCRUD.recuperarGuitarras { guitarras ->
            for(guitarra in guitarras){
                Log.d("Debug", guitarra.toString())
            }
            listaGuitarras.clear()
            listaGuitarras.addAll(guitarras)
        }

        accion = intent.getSerializableExtra("accion") as AccionGuitarra?

        setContent {
            CRUDDefinitivoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ListadoGuitarras(
                        modifier = Modifier.padding(innerPadding),
                        listaGuitarras,
                        guitarraCRUD,
                        accion,
                        ordenar = { opcion ->
                            when(opcion){
                                "Ascendente" -> listaGuitarras.sortBy { it.rating }
                                "Descendente" -> listaGuitarras.sortByDescending { it.rating }
                            }

                            for(guitarra in listaGuitarras){
                                Log.d("Debug", guitarra.toString())
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ListadoGuitarras(
    modifier: Modifier = Modifier,
    guitarras: SnapshotStateList<Guitarra>,
    guitarraCRUD: GuitarraCRUD,
    accion: AccionGuitarra?,
    ordenar: (opcion: String) -> Unit
) {
    val modifierInput = Modifier
        .padding(vertical = 10.dp)
        .fillMaxWidth()
        .wrapContentHeight()

    var inputFiltro: String by rememberSaveable { mutableStateOf("") }
    val guitarrasFiltradas = remember { derivedStateOf {
            guitarras.filter {
                it.nombre
                    .lowercase()
                    .contains(inputFiltro.lowercase())
            }
        }
    }

    val opcionesOrdenar = listOf("Ascendente", "Descendente")
    var opcionElegida by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    LazyColumn(modifier = modifier.padding(horizontal = 20.dp)) {
        item {
            OutlinedTextField(
                modifier = modifierInput,
                value = inputFiltro,
                onValueChange = { inputFiltro = it},
                label = { Text("Buscar guitarra por nombre") },
                singleLine = true
            )

            Box {
                OutlinedTextField(
                    value = opcionElegida,
                    onValueChange = {},
                    label = {
                        Text(
                            text = "Ordenar de forma...",
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Outlined.ArrowDropDown, null) },
                    readOnly = true,
                    shape = RoundedCornerShape(6.dp),
                )

                DropdownMenu(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    opcionesOrdenar.forEach { opcion ->
                        DropdownMenuItem(
                            onClick = {
                                expanded = false
                                opcionElegida = opcion
                                ordenar(opcion)
                            },
                            text = { Text(opcion) }
                        )
                    }
                }
                Spacer(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Transparent)
                        .padding(10.dp)
                        .clickable(
                            onClick = { expanded = !expanded }
                        )
                )
            }
        }

        items(guitarrasFiltradas.value, key = { it.key }) { guitarra ->
            GuitarraItem(guitarra, accion, guitarraCRUD)
        }
    }
}
