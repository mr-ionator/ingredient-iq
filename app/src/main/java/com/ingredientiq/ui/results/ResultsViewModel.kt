package com.ingredientiq.ui.results

import androidx.lifecycle.ViewModel
import com.ingredientiq.domain.model.ScanResult
import com.ingredientiq.ui.shared.CurrentScanHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(
    scanHolder: CurrentScanHolder,
) : ViewModel() {
    val result: StateFlow<ScanResult?> = scanHolder.result
}
