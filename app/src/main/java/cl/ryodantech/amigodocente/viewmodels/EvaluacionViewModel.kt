package cl.ryodantech.amigodocente.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.ryodantech.amigodocente.database.EvaluacionEntity
import cl.ryodantech.amigodocente.repositories.EvaluacionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EvaluacionViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = EvaluacionRepository(application)

    private val _evaluaciones = MutableStateFlow<List<EvaluacionEntity>>(emptyList())
    val evaluaciones: StateFlow<List<EvaluacionEntity>> = _evaluaciones

    //nuevo estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun cargarEvaluacionesPorNivel(nivelId: Long) {
        _isLoading.value = true // Empezamos a cargar
        viewModelScope.launch {
            val lista = withContext(Dispatchers.IO) {
                repo.obtenerTodas().filter { it.nivelAcademicoId == nivelId }
            }
            _evaluaciones.value = lista
            _isLoading.value = false // Terminamos de cargar
        }
    }

    fun guardarEvaluacion(evaluacion: EvaluacionEntity, nivelId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { repo.guardarEvaluacion(evaluacion) }
            cargarEvaluacionesPorNivel(nivelId)
        }
    }

    fun editarEvaluacion(evaluacion: EvaluacionEntity, nivelId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { repo.actualizarEvaluacion(evaluacion) }
            cargarEvaluacionesPorNivel(nivelId) // Recargamos para reflejar el cambio
        }
    }

    fun eliminarEvaluacion(evaluacion: EvaluacionEntity, nivelId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { repo.eliminarEvaluacion(evaluacion) }
            cargarEvaluacionesPorNivel(nivelId)
        }
    }
}
