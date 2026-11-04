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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.ryodantech.amigodocente.database.EvaluacionEntity
import cl.ryodantech.amigodocente.database.NivelEntity
import cl.ryodantech.amigodocente.repositories.ConfiguracionRepository
import cl.ryodantech.amigodocente.repositories.EvaluacionRepository
import cl.ryodantech.amigodocente.ui.theme.AmigoDocenteTheme


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
fun PantallaPrincipal(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var listaNiveles by remember { mutableStateOf<List<NivelEntity>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) } // Para evitar parpadeos al inicio
    var nivelAEliminar by remember { mutableStateOf<NivelEntity?>(null) }

    var nivelActual by remember { mutableStateOf<NivelEntity?>(null) }
    var mostrarConfiguracion by remember { mutableStateOf(false) }
    var mostrarDialogoNivel by remember { mutableStateOf(false) } // <-- Nuevo

    fun cargarNiveles() {
        val configRepo = ConfiguracionRepository(context)
        listaNiveles = configRepo.obtenerNiveles()
        cargando = false
    }

    LaunchedEffect(Unit) {
        cargarNiveles()
    }

    if (cargando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Si la lista está vacía, mostramos la Bienvenida
    if (listaNiveles.isEmpty()) {
        PantallaBienvenida { cursosElegidos ->
            val configRepo = ConfiguracionRepository(context)
            // Guardamos cada curso seleccionado en la base de datos
            for (curso in cursosElegidos) {
                configRepo.agregarNivel(curso)
            }
            cargarNiveles() // Recargamos para que desaparezca la bienvenida
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
                val configRepo = ConfiguracionRepository(context)
                configRepo.agregarNivel(nombre)
                mostrarDialogoNivel = false
                cargarNiveles()
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
                    val configRepo = ConfiguracionRepository(context)
                    configRepo.eliminarNivel(nivelAEliminar!!)
                    nivelAEliminar = null
                    cargarNiveles()
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
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var listaEvaluaciones by remember { mutableStateOf<List<EvaluacionEntity>>(emptyList()) }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var evaluacionSeleccionada by remember { mutableStateOf<EvaluacionEntity?>(null) }

    // Filtramos para mostrar solo las evaluaciones de ESTE nivel
    fun cargarEvaluaciones() {
        val repo = EvaluacionRepository(context)
        listaEvaluaciones = repo.obtenerTodas().filter { it.nivelAcademicoId == nivel.id }
    }

    LaunchedEffect(Unit) {
        cargarEvaluaciones()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(nivel.nombre, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
        if (listaEvaluaciones.isEmpty()) {
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
                                val repo = EvaluacionRepository(context)
                                repo.eliminarEvaluacion(eval)
                                cargarEvaluaciones()
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar Evaluación", tint = MaterialTheme.colorScheme.error)
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
                val repo = EvaluacionRepository(context)
                repo.guardarEvaluacion(nuevaEval)
                mostrarDialogo = false
                cargarEvaluaciones()
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