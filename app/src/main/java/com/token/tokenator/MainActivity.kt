package com.token.tokenator

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.token.tokenator.navigation.Navigator
import com.token.tokenator.navigation.Route
import com.token.tokenator.navigation.rememberNavigationState
import com.token.tokenator.navigation.toEntries
import com.token.tokenator.ui.generate.AddPasswordScreen
import com.token.tokenator.ui.main.MainScreen
import com.token.tokenator.ui.onboarding.OnboardingScreen
import com.token.tokenator.ui.onboarding.OnboardingViewModel
import com.token.tokenator.ui.savedpassword.SavedTokenScreen
import com.token.tokenator.ui.savedpassword.passworddetails.TokenDetailScreen
import com.token.tokenator.ui.settings.SettingsScreen
import com.token.tokenator.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val onboardingViewModel: OnboardingViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private val mainScreenViewModel: com.token.tokenator.ui.main.MainViewModel by viewModels()

    private var isAuthenticated by mutableStateOf(false)

    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var aut: Task<AppUpdateInfo>
    private val updateType = AppUpdateType.FLEXIBLE

    private val listener =
        InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                Log.i("MainActivity", "Update has been downloaded.")
                Toast.makeText(this, R.string.update_completed_restart, Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    appUpdateManager.completeUpdate()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            onboardingViewModel.isOnboardingCompleted.value == null ||
                mainViewModel.isBiometricEnabled.value == null
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        aut = appUpdateManager.appUpdateInfo
        checkIsUpdateAvailable()

        setContent {
            AppTheme {
                TokenatorApp()
            }
        }
    }

    @Composable
    fun TokenatorApp() {
        val isOnboardingCompleted by onboardingViewModel.isOnboardingCompleted.collectAsState()
        val isBiometricEnabled by mainViewModel.isBiometricEnabled.collectAsState()

        if (isOnboardingCompleted == null || isBiometricEnabled == null) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {}
            return
        }

        if (isBiometricEnabled == true && !isAuthenticated) {
            val context = LocalContext.current
            val executor = remember { ContextCompat.getMainExecutor(context) }
            val biometricPrompt =
                remember {
                    BiometricPrompt(
                        this,
                        executor,
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                super.onAuthenticationSucceeded(result)
                                isAuthenticated = true
                            }

                            override fun onAuthenticationError(
                                errorCode: Int,
                                errString: CharSequence,
                            ) {
                                super.onAuthenticationError(errorCode, errString)
                                if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                                    errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON
                                ) {
                                    finish()
                                }
                            }
                        },
                    )
                }

            val promptInfo =
                remember {
                    BiometricPrompt.PromptInfo
                        .Builder()
                        .setTitle(getString(R.string.tokenator))
                        .setSubtitle(getString(R.string.authenticate_title))
                        .setAllowedAuthenticators(
                            androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or
                                androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL,
                        ).build()
                }

            LaunchedEffect(Unit) {
                biometricPrompt.authenticate(promptInfo)
            }
        }

        val startRoute = if (isOnboardingCompleted == true) Route.Main else Route.Onboarding
        val topLevelRoutes =
            remember(isOnboardingCompleted) {
                if (isOnboardingCompleted == true) {
                    setOf(Route.Main, Route.SavedToken, Route.Settings)
                } else {
                    setOf(Route.Onboarding)
                }
            }

        val navigationState =
            rememberNavigationState(
                startRoute = startRoute,
                topLevelRoutes = topLevelRoutes,
            )

        val navigator = remember { Navigator(navigationState) }

        val entryProvider =
            entryProvider<NavKey> {
                entry<Route.Main> { MainScreen(navigator, mainScreenViewModel) }
                entry<Route.AddPassword> { AddPasswordScreen(navigator, mainScreenViewModel) }
                entry<Route.SavedToken> { SavedTokenScreen(navigator) }
                entry<Route.PasswordDetail> { key -> TokenDetailScreen(key.id, navigator) }
                entry<Route.Settings> { SettingsScreen(navigator) }
                entry<Route.Onboarding> { OnboardingScreen(navigator) }
            }

        NavDisplay(
            entries = navigationState.toEntries(entryProvider),
            onBack = { navigator.goBack() },
        )
    }

    private fun checkIsUpdateAvailable() {
        val activityResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult(),
            ) { result ->
                if (result.resultCode != RESULT_OK) {
                    Log.i("MainActivity", "The Update has failed.")
                }
            }

        aut.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(updateType)
            ) {
                Log.i("MainActivity", "Update is available.")
                appUpdateManager.registerListener(listener)
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(updateType).build(),
                )
            } else {
                Log.i("MainActivity", "No Update Available.")
            }
        }
    }
}
