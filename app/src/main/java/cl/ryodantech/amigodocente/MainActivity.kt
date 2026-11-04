package cl.ryodantech.amigodocente

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.ryodantech.amigodocente.database.EvaluacionEntity
import cl.ryodantech.amigodocente.database.NivelEntity
import cl.ryodantech.amigodocente.ui.theme.AmigoDocenteTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.ryodantech.amigodocente.viewmodels.ConfiguracionViewModel
import cl.ryodantech.amigodocente.viewmodels.EvaluacionViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AmigoDocenteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaPrincipal(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(modifier: Modifier = Modifier, configViewModel: ConfiguracionViewModel = viewModel()) {
    val listaNiveles by configViewModel.niveles.collectAsState()
    var nivelAEliminar by remember { mutableStateOf<NivelEntity?>(null) }

    var nivelActual by remember { mutableStateOf<NivelEntity?>(null) }
    var mostrarConfiguracion by remember { mutableStateOf(false) }
    var mostrarDialogoNivel by remember { mutableStateOf(false) } // <-- Nuevo

    val cargando by configViewModel.isLoading.collectAsState() // <-- NUEVO

    // Si está cargando, mostramos el circulito
    if (cargando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Si la lista está vacía, mostramos la Bienvenida
    if (listaNiveles.isEmpty()) {
        PantallaBienvenida { cursosElegidos ->
            // Guardamos cada curso seleccionado en la base de datos
            for (curso in cursosElegidos) {
                configViewModel.agregarNivel(curso)
            }
        }
        return // Detenemos la ejecución aquí para que no dibuje el resto
    }

    if (nivelActual == null) {
        // --- PANTALLA 1: LISTA DE NIVELES DEL PROFESOR ---
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mis Cursos", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { mostrarConfiguracion = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Configuración")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            floatingActionButton = {
                // BOTÓN FLOTANTE PARA AGREGAR NIVELES
                ExtendedFloatingActionButton(
                    onClick = { mostrarDialogoNivel = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Agregar Nivel") },
                    text = { Text("Agregar Curso") }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Hola, Profe 👋", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("Elige los cursos para crear y gestionar sus pruebas.", fontSize = 14.sp)
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp) // Espacio para el botón flotante
                ) {
                    items(listaNiveles) { nivel ->
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .fillMaxWidth()
                                .clickable { nivelActual = nivel },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.libro_de_lectura_by_magnific_in_flaticon_com),
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = nivel.nombre, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))

                                // BOTÓN ELIMINAR NIVEL
                                IconButton(onClick = { nivelAEliminar = nivel }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Eliminar Nivel", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        PantallaEvaluacionesPorNivel(
            nivel = nivelActual!!,
            onVolver = { nivelActual = null }
        )
    }

    // --- DIÁLOGOS GLOBALES DE LA PANTALLA PRINCIPAL ---
    if (mostrarConfiguracion) {
        DialogoConfiguracion(onDismiss = { mostrarConfiguracion = false })
    }

    if (mostrarDialogoNivel) {
        DialogoNuevoNivel(
            onDismiss = { mostrarDialogoNivel = false },
            onGuardar = { nombre ->
                configViewModel.agregarNivel(nombre) // <-- Usamos el ViewModel
                mostrarDialogoNivel = false
            }
        )
    }

    // DIÁLOGO DE CONFIRMACIÓN PARA ELIMINAR NIVEL
    if (nivelAEliminar != null) {
        AlertDialog(
            onDismissRequest = { nivelAEliminar = null },
            title = { Text("¿Desea eliminar el curso vigente?") },
            text = { Text("Si eliminas ${nivelAEliminar!!.nombre}, las tablas de notas asociadas ya no estarán disponibles. ¿Estás seguro?") },
            confirmButton = {
                TextButton(onClick = {
                    configViewModel.eliminarNivel(nivelAEliminar!!)
                    nivelAEliminar = null
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { nivelAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEvaluacionesPorNivel(
    nivel: NivelEntity,
    onVolver: () -> Unit,
    evalViewModel: EvaluacionViewModel = viewModel() //inyeccion de viewmodel
) {

    var evaluacionAEditar by remember { mutableStateOf<EvaluacionEntity?>(null) }
    val listaEvaluaciones by evalViewModel.evaluaciones.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var evaluacionSeleccionada by remember { mutableStateOf<EvaluacionEntity?>(null) }

    val cargando by evalViewModel.isLoading.collectAsState()

    LaunchedEffect(nivel.id) {
        evalViewModel.cargarEvaluacionesPorNivel(nivel.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(nivel.nombre, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { mostrarDialogo = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Nueva") },
                text = { Text("Nueva Evaluación") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        if (cargando) {
            Box(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }else if (listaEvaluaciones.isEmpty()) {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay evaluaciones para ${nivel.nombre}.\n¡Crea una nueva!", textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(listaEvaluaciones) { eval ->
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .fillMaxWidth()
                            .clickable { evaluacionSeleccionada = eval },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = eval.nombre, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            IconButton(onClick = {
                                evaluacionAEditar = eval
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = {
                                evalViewModel.eliminarEvaluacion(eval, nivel.id)
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Eliminar Evaluación", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        DialogoNuevaEvaluacion(
            nombreNivel = nivel.nombre, // Le pasamos el nombre del nivel
            onDismiss = { mostrarDialogo = false },
            onGuardar = { nombre, puntaje -> // Ya no recibimos nivelId
                val nuevaEval = EvaluacionEntity().apply {
                    this.nombre = nombre
                    this.puntajeMaximo = puntaje
                    this.nivelAcademicoId = nivel.id // Lo asignamos directamente aquí
                    this.configuracionId = 1
                    this.fecha = "2026-10-30"
                }
                evalViewModel.guardarEvaluacion(nuevaEval, nivel.id)
                mostrarDialogo = false
            }
        )
    }

    // Diálogo para editar
    if (evaluacionAEditar != null) {
        DialogoNuevaEvaluacion(
            nombreNivel = nivel.nombre,
            evaluacionAEditar = evaluacionAEditar,
            onDismiss = { evaluacionAEditar = null },
            onGuardar = { nombre, puntaje ->
                val evalModificada = EvaluacionEntity().apply {
                    this.id = evaluacionAEditar!!.id // Mantenemos el ID original
                    this.nombre = nombre
                    this.puntajeMaximo = puntaje
                    this.nivelAcademicoId = nivel.id
                    this.configuracionId = evaluacionAEditar!!.configuracionId
                    this.fecha = evaluacionAEditar!!.fecha
                }
                evalViewModel.editarEvaluacion(evalModificada, nivel.id)
                evaluacionAEditar = null
            }
        )
    }

    if (evaluacionSeleccionada != null) {
        DialogoTablaNotas(
            evaluacion = evaluacionSeleccionada!!,
            onDismiss = { evaluacionSeleccionada = null }
        )
    }

}

// Función auxiliar para determinar el color de la nota
fun obtenerColorNota(nota: Double): Color {
    return if (nota < 4.0) Color.Red else Color.Unspecified
}