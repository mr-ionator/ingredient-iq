package com.ingredientiq.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ingredientiq.data.repository.IngredientRepository
import com.ingredientiq.domain.model.Ingredient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IngredientDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: IngredientRepository,
) : ViewModel() {

    private val _ingredient = MutableStateFlow<Ingredient?>(null)
    val ingredient: StateFlow<Ingredient?> = _ingredient

    init {
        savedStateHandle.get<Long>("ingredientId")?.let { id ->
            viewModelScope.launch {
                _ingredient.value = repository.getById(id)
            }
        }
    }
}
