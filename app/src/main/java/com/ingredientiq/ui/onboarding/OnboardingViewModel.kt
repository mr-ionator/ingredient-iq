package com.ingredientiq.ui.onboarding

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefs: SharedPreferences,
) : ViewModel() {

    fun markDone() = prefs.edit().putBoolean("onboarding_done", true).apply()
}
