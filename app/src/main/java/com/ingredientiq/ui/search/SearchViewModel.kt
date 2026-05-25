package com.ingredientiq.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ingredientiq.data.repository.IngredientRepository
import com.ingredientiq.domain.model.Ingredient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: IngredientRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _results = MutableStateFlow<List<Ingredient>>(emptyList())
    val results: StateFlow<List<Ingredient>> = _results

    init {
        @OptIn(FlowPreview::class)
        viewModelScope.launch {
            _query
                .debounce(300)
                .collect { q ->
                    _results.value = if (q.isBlank()) emptyList()
                    else repository.searchIngredients(q.trim())
                }
        }
    }

    fun onQueryChange(q: String) { _query.value = q }
}
