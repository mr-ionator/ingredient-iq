package com.ingredientiq.ui.shared

import com.ingredientiq.domain.model.ScanResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentScanHolder @Inject constructor() {
    private val _result = MutableStateFlow<ScanResult?>(null)
    val result: StateFlow<ScanResult?> = _result

    fun set(result: ScanResult) { _result.value = result }
    fun clear() { _result.value = null }
}
