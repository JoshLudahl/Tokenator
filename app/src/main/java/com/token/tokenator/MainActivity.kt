package com.token.tokenator

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.token.tokenator.ui.security.SecurityManager
import com.token.tokenator.ui.settings.SettingsScreen
import com.token.tokenator.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val onboardingViewModel: OnboardingViewModel by viewModels()

    @Inject
    lateinit var securityManager: SecurityManager

    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var aut: Task<AppUpdateInfo>
    private val updateType = AppUpdateType.FLEXIBLE

    private var isAuthenticated by mutableStateOf(false)

    private val listener =
        InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                Log.i("MainActivity", "Update has been downloaded.")
                Toast.makeText(this, "Update Completed. Restarting application.", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    appUpdateManager.completeUpdate()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            onboardingViewModel.isOnboardingCompleted.value == null
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        aut = appUpdateManager.appUpdateInfo
        checkIsUpdateAvailable()

        lifecycleScope.launch {
            securityManager.isSecurityEnabled.collect { enabled ->
                Log.d("MainActivity", "Security enabled: $enabled, Authenticated: ${securityManager.isAuthenticated.value}")
                if (enabled && !securityManager.isAuthenticated.value) {
                    val biometricManager = BiometricManager.from(this@MainActivity)
                    val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL

                    val canAuthenticate = biometricManager.canAuthenticate(authenticators)
                    Log.d("MainActivity", "canAuthenticate status: $canAuthenticate")

                    when (canAuthenticate) {
                        BiometricManager.BIOMETRIC_SUCCESS -> {
                            Log.d("MainActivity", "Can authenticate, showing prompt")
                            showBiometricPrompt()
                        }
                        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                            Log.e("MainActivity", "No biometric features available on this device.")
                            securityManager.setAuthenticated(true)
                        }
                        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                            Log.e("MainActivity", "Biometric features are currently unavailable.")
                            securityManager.setAuthenticated(true)
                        }
                        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                            Log.e("MainActivity", "The user has not enrolled any biometrics or device credentials.")
                            // Optionally, prompt the user to enroll or just allow access
                            securityManager.setAuthenticated(true)
                        }
                        else -> {
                            Log.e("MainActivity", "Biometric status unknown: $canAuthenticate")
                            securityManager.setAuthenticated(true)
                        }
                    }
                } else if (!enabled) {
                    securityManager.setAuthenticated(true)
                }
            }
        }

        setContent {
            AppTheme {
                val isAuthenticated by securityManager.isAuthenticated.collectAsState()
                if (isAuthenticated) {
                    TokenatorApp()
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background,
                    ) {}
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (securityManager.isSecurityEnabled.value && !securityManager.isAuthenticated.value) {
            showBiometricPrompt()
        }
    }

    private var isPromptShowing = false

    private fun showBiometricPrompt() {
        if (isPromptShowing) return
        isPromptShowing = true
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt =
            BiometricPrompt(
                this,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence,
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                        isPromptShowing = false
                        Log.e("MainActivity", "Auth error: $errorCode - $errString")
                        if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                            errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                            errorCode == BiometricPrompt.ERROR_LOCKOUT ||
                            errorCode == BiometricPrompt.ERROR_LOCKOUT_PERMANENT
                        ) {
                            finish()
                        } else {
                            Toast
                                .makeText(
                                    applicationContext,
                                    "Authentication error: $errString",
                                    Toast.LENGTH_SHORT,
                                ).show()
                        }
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        isPromptShowing = false
                        Log.d("MainActivity", "Auth succeeded")
                        securityManager.setAuthenticated(true)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        isPromptShowing = false
                        Log.d("MainActivity", "Auth failed")
                        Toast
                            .makeText(
                                applicationContext,
                                "Authentication failed",
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
                },
            )

        val promptInfo =
            BiometricPrompt.PromptInfo
                .Builder()
                .setTitle("Tokenator Security")
                .setSubtitle("Unlock your vault")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build()

        biometricPrompt.authenticate(promptInfo)
    }

    override fun onStop() {
        super.onStop()
        // Reset authentication when app goes to background if security is enabled
        if (securityManager.isSecurityEnabled.value) {
            Log.d("MainActivity", "App backgrounded, resetting authentication")
            securityManager.resetAuthentication()
        }
    }

    @Composable
    fun TokenatorApp() {
        val isOnboardingCompleted by onboardingViewModel.isOnboardingCompleted.collectAsState()

        if (isOnboardingCompleted == null) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {}
            return
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
                entry<Route.Main> { MainScreen(navigator) }
                entry<Route.AddPassword> { AddPasswordScreen(navigator) }
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
