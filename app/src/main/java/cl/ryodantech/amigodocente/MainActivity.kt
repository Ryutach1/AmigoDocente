package cl.ryodantech.amigodocente

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import kotlinx.coroutines.delay
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
                Scaffold(modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFFF4F6F9)) { innerPadding ->
                    PantallaPrincipal(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun PantallaPrincipal(modifier: Modifier = Modifier, configViewModel: ConfiguracionViewModel = viewModel()) {
    val listaNiveles by configViewModel.niveles.collectAsState()

    var nivelAEditar by remember { mutableStateOf<NivelEntity?>(null) }
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
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(R.drawable.instalaciones_by_magnific_in_flaticon_com),
                                contentDescription = "IconoInicial")
                            Text("Amigo Docente", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                        }
                    },
                    //title = { Text("Mis Cursos", fontWeight = FontWeight.Bold) },
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
                    text = { Text("Añadir Curso") },
                    shape = MaterialTheme.shapes.large, // <--- Botón más redondeado
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hola, Profe 👋", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                        Text("Elige un curso para crear y gestionar las notas.", fontSize = 14.sp, color = Color.Gray)
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp) // Separación entre tarjetas
                ) {
                    items(listaNiveles) { nivel ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {
                                        nivelActual = nivel // Acción principal
                                    },
                                    onLongClick = {
                                        nivelAEditar = nivel // Acción secundaria (Editar)
                                    }
                                ),
                            shape = RoundedCornerShape(20.dp), // Bordes muy redondeados
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Ícono con fondo de color
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.libro_de_lectura_by_magnific_in_flaticon_com),
                                            contentDescription = null,
                                            modifier = Modifier.size(28.dp),
                                            tint = Color.White
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = nivel.nombre, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                    TextoAnimadoAyuda()
                                }
                                IconButton(onClick = { nivelAEliminar = nivel }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
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

    //dialogo de edición de nivel
    if (nivelAEditar != null) {
        DialogoEditarNivel(
            nivelActual = nivelAEditar!!,
            onDismiss = { nivelAEditar = null },
            onGuardar = { nuevoNombre ->
                configViewModel.editarNivel(nivelAEditar!!, nuevoNombre)
                nivelAEditar = null
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
                shape = MaterialTheme.shapes.large, // <--- Botón más redondeado
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
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
                        shape = RoundedCornerShape(20.dp), // Bordes muy redondeados
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // <--- Sombra
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = eval.nombre, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text(text = "Puntaje: " + eval.puntajeMaximo, fontSize = 12.sp, color = Color.Gray)
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

@Composable
fun TextoAnimadoAyuda() {
    val textos = listOf("Presiona para gestionar las evaluaciones", "Mantén presionado para editar el curso")
    var index by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000) // Cambia cada 5 segundos
            index = (index + 1) % textos.size
        }
    }

    Crossfade(targetState = index, label = "texto_ayuda") { i ->
        Text(text = textos[i], fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
// Función auxiliar para determinar el color de la nota
fun obtenerColorNota(nota: Double): Color {
    return if (nota < 4.0) Color.Red else MaterialTheme.colorScheme.primary
}