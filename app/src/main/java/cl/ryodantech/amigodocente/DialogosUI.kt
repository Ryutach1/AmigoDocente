package cl.ryodantech.amigodocente

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cl.ryodantech.amigodocente.database.ConfiguracionEntity
import cl.ryodantech.amigodocente.database.EvaluacionEntity
import cl.ryodantech.amigodocente.repositories.ConfiguracionRepository

@Composable
fun PantallaBienvenida(
    onCursosSeleccionados: (List<String>) -> Unit
) {
    // Lista de los cursos típicos en Chile
    val cursosDisponibles = listOf(
        "1° Básico",
        "2° Básico",
        "3° Básico",
        "4° Básico",
        "5° Básico",
        "6° Básico",
        "7° Básico",
        "8° Básico",
        "1° Medio",
        "2° Medio",
        "3° Medio",
        "4° Medio"
    )

    // Estado para guardar los que el profesor vaya marcando
    val cursosSeleccionados = remember { mutableStateListOf<String>() }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¡Bienvenido a Amigo Docente!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 32.dp)
            )
            Text(
                text = "¿Cuáles son los cursos que tienes asignados este año? Selecciona tus niveles para comenzar.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Lista con scroll para los Checkboxes
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(cursosDisponibles) { curso ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (cursosSeleccionados.contains(curso)) {
                                    cursosSeleccionados.remove(curso)
                                } else {
                                    cursosSeleccionados.add(curso)
                                }
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = cursosSeleccionados.contains(curso),
                            onCheckedChange = { isChecked ->
                                if (isChecked) cursosSeleccionados.add(curso)
                                else cursosSeleccionados.remove(curso)
                            }
                        )
                        Text(
                            text = curso,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // Botón para guardar
            Button(
                onClick = { onCursosSeleccionados(cursosSeleccionados.toList()) },
                enabled = cursosSeleccionados.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Comenzar a crear tablas", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun DialogoNuevaEvaluacion(
    nombreNivel: String, // Recibimos el nombre del nivel para mostrarlo
    onDismiss: () -> Unit,
    onGuardar: (String, Double) -> Unit // Ya no devolvemos el nivelId
) {
    var nombre by remember { mutableStateOf("") }
    var puntaje by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Evaluación") },
        text = {
            Column {
                Text(
                    text = "Nivel: $nombreNivel",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre (ej. Prueba 1)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = puntaje,
                    onValueChange = { puntaje = it },
                    label = { Text("Puntaje Máximo") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val p = puntaje.toDoubleOrNull() ?: 0.0
                    if (nombre.isNotBlank() && p > 0) {
                        onGuardar(nombre, p)
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
    configRepo.inicializarDatosBase()
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
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Puntaje", modifier = Modifier.weight(1f).padding(start = 8.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                    Text("Nota", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                    Text("Puntaje", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                    Text("Nota", modifier = Modifier.weight(1f).padding(end = 8.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant)

                // CUADRÍCULA
                // Lógica para dividir la tabla en dos grandes bloques
                val totalItems = tabla.items.size
                val mitad = (totalItems + 1) / 2

                val itemsIzquierda = tabla.items.take(mitad)
                val itemsDerecha = tabla.items.drop(mitad)

                // Un solo LazyColumn para que el scroll sea unificado
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    items(mitad) { index ->
                        val itemIzq = itemsIzquierda[index]
                        val itemDer = if (index < itemsDerecha.size) itemsDerecha[index] else null

                        val colorFondo = if (index % 2 == 0) Color(0xFFF5F5F5) else Color.Transparent

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colorFondo)
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // LADO IZQUIERDO
                            Text(
                                text = "${itemIzq.puntaje.toInt()}",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "${itemIzq.nota}",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (itemIzq.nota < 4.0) Color.Red else Color.Unspecified
                            )
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(24.dp) // Ajusta la altura según el tamaño de tu fila
                                    .background(Color.LightGray)
                            )
                            // LADO DERECHO (Si existe)
                            if (itemDer != null) {
                                Text(
                                    text = "${itemDer.puntaje.toInt()}",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "${itemDer.nota}",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (itemDer.nota < 4.0) Color.Red else Color.Unspecified
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
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
fun FilaTabla(item: cl.ryodantech.amigodocente.models.ItemTabla, isCebra: Boolean) {
    val colorFondo = if (isCebra) Color(0xFFF5F5F5) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorFondo)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${item.puntaje.toInt()}",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 15.sp
        )
        Text(
            text = "${item.nota}",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (item.nota < 4.0) Color.Red else Color.Unspecified
        )
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

@Composable
fun DialogoNuevoNivel(
    onDismiss: () -> Unit,
    onGuardar: (String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Nivel") },
        text = {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Ej: 3° Medio C, 8° Básico") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (nombre.isNotBlank()) {
                    onGuardar(nombre)
                }
            }) { Text("Agregar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}