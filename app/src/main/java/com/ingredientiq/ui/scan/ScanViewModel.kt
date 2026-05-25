package com.ingredientiq.ui.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ingredientiq.domain.usecase.LookupIngredientsUseCase
import com.ingredientiq.ocr.IngredientTextExtractor
import com.ingredientiq.ocr.OcrManager
import com.ingredientiq.ocr.OcrState
import com.ingredientiq.ui.shared.CurrentScanHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ScanUiState {
    object Idle : ScanUiState()
    object Scanning : ScanUiState()
    object IngredientsDetected : ScanUiState()   // "INGREDIENTS:" anchor found
    object Analyzing : ScanUiState()
    object Done : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}

@HiltViewModel
class ScanViewModel @Inject constructor(
    val ocrManager: OcrManager,
    private val extractor: IngredientTextExtractor,
    private val lookup: LookupIngredientsUseCase,
    private val scanHolder: CurrentScanHolder,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState

    private val _currentText = MutableStateFlow("")
    val currentText: StateFlow<String> = _currentText

    init {
        viewModelScope.launch {
            ocrManager.state.collectLatest { ocrState ->
                when (ocrState) {
                    is OcrState.Scanning -> if (_uiState.value == ScanUiState.Idle)
                        _uiState.value = ScanUiState.Scanning
                    is OcrState.TextDetected -> {
                        _currentText.value = ocrState.rawText
                        if (extractor.hasIngredientsSection(ocrState.rawText) &&
                            _uiState.value == ScanUiState.Scanning
                        ) {
                            _uiState.value = ScanUiState.IngredientsDetected
                        }
                    }
                    is OcrState.Error -> _uiState.value = ScanUiState.Error(ocrState.message)
                    else -> {}
                }
            }
        }
    }

    fun analyze(rawText: String = _currentText.value) {
        if (rawText.isBlank()) return
        viewModelScope.launch {
            _uiState.value = ScanUiState.Analyzing
            try {
                val result = lookup(rawText)
                scanHolder.set(result)
                _uiState.value = ScanUiState.Done
            } catch (e: Exception) {
                _uiState.value = ScanUiState.Error(e.message ?: "Analysis failed")
            }
        }
    }

    fun reset() {
        _uiState.value = ScanUiState.Scanning
        _currentText.value = ""
        ocrManager.reset()
    }

    override fun onCleared() {
        super.onCleared()
        ocrManager.shutdown()
    }
}
