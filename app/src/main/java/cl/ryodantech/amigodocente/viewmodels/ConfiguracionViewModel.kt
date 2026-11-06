package cl.ryodantech.amigodocente.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.ryodantech.amigodocente.database.NivelEntity
import cl.ryodantech.amigodocente.repositories.ConfiguracionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfiguracionViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = ConfiguracionRepository(application)

    // StateFlow mantiene el estado de los niveles y notifica a la UI cuando cambian
    private val _niveles = MutableStateFlow<List<NivelEntity>>(emptyList())
    val niveles: StateFlow<List<NivelEntity>> = _niveles
    // NUEVO ESTADO DE CARGA
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        repo.inicializarDatosBase()
        cargarNiveles()
    }

    fun cargarNiveles() {
        _isLoading.value = true
        viewModelScope.launch {
            val lista = withContext(Dispatchers.IO) { repo.obtenerNiveles() }
            _niveles.value = lista
            _isLoading.value = false
        }
    }

    fun agregarNivel(nombre: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { repo.agregarNivel(nombre) }
            cargarNiveles()
        }
    }

    fun editarNivel(nivel: NivelEntity, nuevoNombre: String) {
        viewModelScope.launch {
            nivel.nombre = nuevoNombre // Como es Java, usa: nivel.setNombre(nuevoNombre)
            withContext(Dispatchers.IO) { repo.actualizarNivel(nivel) }
            cargarNiveles()
        }
    }

    fun eliminarNivel(nivel: NivelEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { repo.eliminarNivel(nivel) }
            cargarNiveles()
        }
    }
}