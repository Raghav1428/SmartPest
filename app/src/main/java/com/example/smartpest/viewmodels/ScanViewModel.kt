package com.example.smartpest.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.smartpest.models.ScanImageLoader
import com.example.smartpest.models.TensorFlowHelper
import com.example.smartpest.models.TomatoPrediction
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class ScanUiState(
    val imageUri: Uri? = null,
    val bitmap: Bitmap? = null,
    val isLoading: Boolean = false,
    val prediction: TomatoPrediction? = null,
    val error: String? = null
)

class ScanViewModel(application: Application, private val savedState: SavedStateHandle) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(ScanUiState())
    val state = _state.asStateFlow()
    private var scanJob: Job? = null
    private val inferenceLock = Mutex()

    init {
        savedState.get<String>("scanUri")?.let { selectImage(Uri.parse(it)) }
    }

    fun selectImage(uri: Uri) {
        savedState["scanUri"] = uri.toString()
        scanJob?.cancel()
        _state.value = ScanUiState(imageUri = uri, isLoading = true)
        scanJob = viewModelScope.launch {
            try {
                // Native inference is not interruptible. Serialize it and discard a cancelled job's result.
                val result = inferenceLock.withLock {
                    val bitmap = ScanImageLoader.load(getApplication(), uri)
                    val prediction = TensorFlowHelper.classifyTomato(getApplication(), bitmap)
                    ensureActive()
                    ScanUiState(imageUri = uri, bitmap = bitmap, prediction = prediction)
                }
                _state.value = result
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _state.value = ScanUiState(imageUri = uri,
                    error = "We couldn't read or analyze this photo. Try another clear, well-lit tomato leaf photo.")
            }
        }
    }

    fun retry() {
        _state.value.imageUri?.let(::selectImage)
    }

    fun acquisitionError(message: String) {
        scanJob?.cancel()
        _state.value = ScanUiState(error = message)
    }
}
