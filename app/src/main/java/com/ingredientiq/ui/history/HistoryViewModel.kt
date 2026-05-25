package com.ingredientiq.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ingredientiq.data.db.entity.ScanHistoryEntity
import com.ingredientiq.data.repository.IngredientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: IngredientRepository,
) : ViewModel() {

    val scans: StateFlow<List<ScanHistoryEntity>> = repository
        .getAllScans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun delete(scan: ScanHistoryEntity) {
        viewModelScope.launch { repository.deleteScan(scan) }
    }
}
