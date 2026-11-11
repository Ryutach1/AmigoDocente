package cl.ryodantech.amigodocente

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cl.ryodantech.amigodocente.database.ConfiguracionEntity
import cl.ryodantech.amigodocente.repositories.ConfiguracionRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAjustes(
    onDismiss: () -> Unit,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    fontScale: Float,
    onFontScaleChange: (Float) -> Unit
) {
    // ESTADO INTERNO: Controla si estamos en el menú principal o en la vista UTP
    var mostrarUTP by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = if (mostrarUTP) "Parámetros UTP" else "Ajustes",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                if (mostrarUTP) mostrarUTP = false // Vuelve al menú principal
                                else onDismiss() // Cierra la pantalla de ajustes
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                }
            ) { padding ->
                if (mostrarUTP) {
                    // --- VISTA 1: CONFIGURACIÓN UTP ---
                    ContenidoUTP(
                        modifier = Modifier.padding(padding),
                        onGuardado = { mostrarUTP = false } // Al guardar, vuelve al menú
                    )
                } else {
                    // --- VISTA 2: MENÚ PRINCIPAL DE AJUSTES ---
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // SECCIÓN: APARIENCIA
                        Text("Apariencia", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Modo Oscuro", fontSize = 16.sp)
                            ThemeToggleButton(isDarkMode = isDarkMode, onToggle = onToggleTheme)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                        Text("Accesibilidad", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Tamaño de letra", fontSize = 16.sp, modifier = Modifier.weight(1f))

                            // Selector de 3 opciones (Pequeño, Normal, Grande)
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Opciones: 0.85f (S), 1.0f (M), 1.25f (L)
                                val opciones = listOf(0.85f to "S", 1.0f to "M", 1.25f to "L")

                                opciones.forEach { (escala, letra) ->
                                    val isSelected = fontScale == escala

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clickable { onFontScaleChange(escala) }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = letra,
                                                fontSize = if (letra == "S") 12.sp else if (letra == "M") 14.sp else 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                        // SECCIÓN: DATOS ACADÉMICOS
                        Text("Datos Académicos", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))

                        ListItem(
                            headlineContent = { Text("Configuración UTP") },
                            supportingContent = { Text("Exigencia, nota mínima y escala") },
                            modifier = Modifier.clickable { mostrarUTP = true },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}

// --- COMPONENTE SEPARADO PARA LA VISTA UTP ---
@Composable
fun ContenidoUTP(
    modifier: Modifier = Modifier,
    onGuardado: () -> Unit
) {
    val context = LocalContext.current
    val configRepo = ConfiguracionRepository(context)
    val configActual = configRepo.obtenerConfiguracion()

    var exigenciaStr by remember { mutableStateOf((configActual.exigencia * 100).toInt().toString()) }
    var decimalesStr by remember { mutableStateOf(configActual.decimales.toString()) }
    var notaMinimaStr by remember { mutableStateOf(configActual.notaMinima.toString()) }

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Estos parámetros son definidos por la dirección del colegio y afectan directamente el cálculo de todas las notas.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = exigenciaStr,
            onValueChange = { exigenciaStr = it },
            label = { Text("Exigencia (%)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = notaMinimaStr,
            onValueChange = { notaMinimaStr = it },
            label = { Text("Nota Mínima (ej. 1.0 o 2.0)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = decimalesStr,
            onValueChange = { decimalesStr = it },
            label = { Text("Decimales de nota (1 o 2)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )

        Button(
            onClick = {
                val nuevaExigencia = (exigenciaStr.toDoubleOrNull() ?: 60.0) / 100.0
                val nuevosDecimales = decimalesStr.toIntOrNull() ?: 1
                val nuevaNotaMinima = notaMinimaStr.toDoubleOrNull() ?: 1.0

                val configModificada = ConfiguracionEntity().apply {
                    this.id = configActual.id
                    this.notaMinima = nuevaNotaMinima
                    this.notaMaxima = configActual.notaMaxima
                    this.notaAprobacion = configActual.notaAprobacion
                    this.exigencia = nuevaExigencia
                    this.decimales = nuevosDecimales
                    this.modoRedondeo = configActual.modoRedondeo
                }

                configRepo.guardarConfiguracion(configModificada)
                onGuardado()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Text("Guardar Cambios", fontWeight = FontWeight.Bold)
        }
    }
}

// (Asegúrate de mantener aquí abajo tu función ThemeToggleButton)
@Composable
fun ThemeToggleButton(
    isDarkMode: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animamos el desplazamiento (offset) en lugar del padding
    val offsetAnimado by animateDpAsState(
        targetValue = if (isDarkMode) 40.dp else 0.dp,
        animationSpec = tween(durationMillis = 100),
        label = "deslizamiento_switch"
    )

    Surface(
        modifier = modifier
            .width(80.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onToggle() },
        // Fondo oscuro para modo oscuro, gris claro para modo claro
        color = if (isDarkMode) Color(0xFF475569) else Color(0xFFE2E8F0)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(4.dp) // Padding fijo para que el círculo no toque los bordes
        ) {
            // El círculo interior
            Surface(
                shape = CircleShape,
                // Fondo del círculo: un gris muy oscuro en modo oscuro, blanco en modo claro
                color = if (isDarkMode) Color(0xFF1E293B) else Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .size(32.dp)
                    .offset(x = offsetAnimado) // Desplazamos en el eje X
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(
                            id = if (isDarkMode) R.drawable.luna_by_magnific_in_flaticon_com
                            else R.drawable.sol_by_magnific_in_flaticon_com
                        ),
                        contentDescription = "Cambiar Tema",
                        modifier = Modifier.size(20.dp),
                        // Ajuste de color: Blanco en modo oscuro, naranja en modo claro
                        tint = if (isDarkMode) Color.White else Color(0xFFF59E0B)
                    )
                }
            }
        }
    }
}