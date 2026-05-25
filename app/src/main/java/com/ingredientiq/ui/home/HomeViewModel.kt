package com.ingredientiq.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ingredientiq.data.db.entity.ScanHistoryEntity
import com.ingredientiq.data.repository.IngredientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: IngredientRepository,
) : ViewModel() {

    val recentScans: StateFlow<List<ScanHistoryEntity>> = repository
        .getAllScans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
