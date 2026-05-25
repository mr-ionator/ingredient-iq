package com.ingredientiq.ui.splash

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.ingredientiq.data.seeder.DatabaseSeeder
import com.ingredientiq.data.seeder.SeedState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    val seeder: DatabaseSeeder,
    private val prefs: SharedPreferences,
) : ViewModel() {

    val seedState: StateFlow<SeedState> = seeder.state

    fun isOnboardingDone(): Boolean = prefs.getBoolean("onboarding_done", false)
}
