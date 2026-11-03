package cl.ryodantech.amigodocente

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.window.Dialog
import cl.ryodantech.amigodocente.database.EvaluacionEntity
import cl.ryodantech.amigodocente.database.NivelEntity
import cl.ryodantech.amigodocente.database.ConfiguracionEntity
import cl.ryodantech.amigodocente.repositories.ConfiguracionRepository
import cl.ryodantech.amigodocente.repositories.EvaluacionRepository
import cl.ryodantech.amigodocente.ui.theme.AmigoDocenteTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    var listaEvaluaciones by remember { mutableStateOf<List<EvaluacionEntity>>(emptyList()) }
    var listaNiveles by remember { mutableStateOf<List<NivelEntity>>(emptyList()) }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var evaluacionSeleccionada by remember { mutableStateOf<EvaluacionEntity?>(null) }
    var mostrarConfiguracion by remember { mutableStateOf(false) }


    fun cargarEvaluaciones() {
        val repo = EvaluacionRepository(context)
        listaEvaluaciones = repo.obtenerTodas()
    }

    LaunchedEffect(Unit) {
        val configRepo = ConfiguracionRepository(context)
        listaNiveles = configRepo.obtenerNiveles()
        cargarEvaluaciones()
    }

    // Scaffold es el lienzo principal. Aquí definimos la barra superior y el botón flotante.
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Amigo Docente", fontWeight = FontWeight.Bold)
                },
                actions = {
                    // BOTÓN DE AJUSTES (Engranaje)
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
            ExtendedFloatingActionButton(
                onClick = { mostrarDialogo = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Nueva") },
                text = { Text("Nueva Evaluación") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // 1. SECCIÓN DE BIENVENIDA (Didáctica)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Bienvenido, Profe 👋",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "¿Qué evaluaciones estan pendientes hoy?",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // 2. TÍTULO DE LA LISTA
            Text(
                text = "Tus evaluaciones disponibles",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // 3. LISTA DE EVALUACIONES
            if (listaEvaluaciones.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Aún no tienes evaluaciones.\n¡Toca el botón + para crear la primera!",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp) // Para que el botón flotante no tape la última tarjeta
                ) {
                    items(listaEvaluaciones) { eval ->
                        val nombreNivel = listaNiveles.find { it.id == eval.nivelAcademicoId }?.nombre ?: "Nivel desconocido"

                        // TARJETA DE EVALUACIÓN ESTILIZADA
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
                                // Ícono decorativo
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.examen_by_magnific_in_flaticon_com),
                                            contentDescription = null
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // Textos
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = eval.nombre, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Text(text = nombreNivel, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "${eval.puntajeMaximo} pts", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                }

                                // Botón eliminar
                                IconButton(onClick = {
                                    val repo = EvaluacionRepository(context)
                                    repo.eliminarEvaluacion(eval)
                                    cargarEvaluaciones()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Lógica de los diálogos (se mantiene igual)
    if (mostrarDialogo) {
        DialogoNuevaEvaluacion(
            niveles = listaNiveles,
            onDismiss = { mostrarDialogo = false },
            onGuardar = { nombre, puntaje, nivelId ->
                val nuevaEval = EvaluacionEntity().apply {
                    this.nombre = nombre
                    this.puntajeMaximo = puntaje
                    this.nivelAcademicoId = nivelId
                    this.configuracionId = 1
                    this.fecha = "2026-10-30"
                    this.cursoId = 0
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

    if (mostrarConfiguracion) {
        DialogoConfiguracion(
            onDismiss = { mostrarConfiguracion = false }
        )
    }
}

@Composable
fun DialogoNuevaEvaluacion(
    niveles: List<NivelEntity>,
    onDismiss: () -> Unit,
    onGuardar: (String, Double, Long) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var puntaje by remember { mutableStateOf("") }
    var nivelSeleccionado by remember { mutableStateOf<NivelEntity?>(null) }
    var expandirDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Evaluación") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre (ej. Prueba 1)") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = puntaje,
                    onValueChange = { puntaje = it },
                    label = { Text("Puntaje Máximo") }
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Selector de Nivel (Dropdown)
                Box {
                    OutlinedButton(onClick = { expandirDropdown = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(nivelSeleccionado?.nombre ?: "Seleccionar Nivel")
                    }
                    DropdownMenu(expanded = expandirDropdown, onDismissRequest = { expandirDropdown = false }) {
                        niveles.forEach { nivel ->
                            DropdownMenuItem(
                                text = { Text(nivel.nombre) },
                                onClick = {
                                    nivelSeleccionado = nivel
                                    expandirDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val p = puntaje.toDoubleOrNull() ?: 0.0
                    if (nombre.isNotBlank() && p > 0 && nivelSeleccionado != null) {
                        onGuardar(nombre, p, nivelSeleccionado!!.id)
                    }
                }
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun DialogoTablaNotas(
    evaluacion: EvaluacionEntity,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val configRepo = ConfiguracionRepository(context)
    val configEntity = configRepo.obtenerConfiguracion()

    val configModel = cl.ryodantech.amigodocente.models.ConfiguracionUTP().apply {
        this.exigencia = configEntity.exigencia
        this.decimales = configEntity.decimales
        this.notaMinima = configEntity.notaMinima
        this.notaMaxima = configEntity.notaMaxima
        this.notaAprobacion = configEntity.notaAprobacion
        this.modoRedondeo = if (configEntity.modoRedondeo == "HACIA_ARRIBA") {
            cl.ryodantech.amigodocente.models.ConfiguracionUTP.ModoRedondeo.HACIA_ARRIBA
        } else {
            cl.ryodantech.amigodocente.models.ConfiguracionUTP.ModoRedondeo.NORMAL
        }
    }

    val evalModel = cl.ryodantech.amigodocente.models.Evaluacion().apply {
        this.id = evaluacion.id
        this.nombre = evaluacion.nombre // <--- AGREGA ESTA LÍNEA
        this.puntajeMaximo = evaluacion.puntajeMaximo
    }

    val tabla = cl.ryodantech.amigodocente.services.GeneradorTablaService.generar(evalModel, configModel)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Tabla: ${evaluacion.nombre}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // ENCABEZADOS DE LA TABLA
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant) // Fondo gris suave
                        .padding(vertical = 8.dp)
                ) {
                    Text("Puntaje", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                    Text("Nota", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                    Text("Puntaje", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                    Text("Nota", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant)

                // CUADRÍCULA
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f)
                ) {
                    val itemsAgrupados = tabla.items.chunked(2)

                    items(itemsAgrupados.size) { index ->
                        val par = itemsAgrupados[index]

                        // Fila de datos
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            // Columna 1
                            Text(
                                text = "${par[0].puntaje.toInt()}",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "${par[0].nota}",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                // LÓGICA DE COLOR ROJO
                                color = if (par[0].nota < 4.0) Color.Red else MaterialTheme.colorScheme.onSurface
                            )

                            // Columna 2 (si existe)
                            if (par.size > 1) {
                                Text(
                                    text = "${par[1].puntaje.toInt()}",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${par[1].nota}",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    // LÓGICA DE COLOR ROJO
                                    color = if (par[1].nota < 4.0) Color.Red else MaterialTheme.colorScheme.onSurface
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    }
                }
                // Botón para exportar a PDF
                Button(
                    onClick = {
                        try {
                            cl.ryodantech.amigodocente.services.PdfGeneratorService
                                .exportarYCompartir(context, evalModel, tabla, configModel) // <-- Agregamos configModel
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text("Convertir a PDF")
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                ) {
                    Text("Cerrar Tabla")
                }
            }
        }
    }
}

@Composable
fun DialogoConfiguracion(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val configRepo = ConfiguracionRepository(context)
    val configActual = configRepo.obtenerConfiguracion()

    // Estados para los campos
    var exigenciaStr by remember { mutableStateOf((configActual.exigencia * 100).toInt().toString()) }
    var decimalesStr by remember { mutableStateOf(configActual.decimales.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configuración UTP") },
        text = {
            Column {
                Text("Parámetros institucionales de evaluación", fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))

                OutlinedTextField(
                    value = exigenciaStr,
                    onValueChange = { exigenciaStr = it },
                    label = { Text("Exigencia (%)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = decimalesStr,
                    onValueChange = { decimalesStr = it },
                    label = { Text("Decimales de nota (0, 1 o 2)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val nuevaExigencia = (exigenciaStr.toDoubleOrNull() ?: 60.0) / 100.0
                val nuevosDecimales = decimalesStr.toIntOrNull() ?: 1

                // Actualizamos el objeto
                val configModificada = ConfiguracionEntity().apply{
                    this.id = configActual.id
                    this.notaMinima = configActual.notaMinima
                    this.notaMaxima = configActual.notaMaxima
                    this.notaAprobacion = configActual.notaAprobacion
                    this.exigencia = nuevaExigencia
                    this.decimales = nuevosDecimales
                    this.modoRedondeo = configActual.modoRedondeo
                }

                configRepo.guardarConfiguracion(configModificada)
                onDismiss()
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}