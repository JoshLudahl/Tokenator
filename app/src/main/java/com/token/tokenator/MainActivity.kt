package com.token.tokenator

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.token.tokenator.navigation.*
import com.token.tokenator.ui.generate.AddPasswordScreen
import com.token.tokenator.ui.main.MainScreen
import com.token.tokenator.ui.savedpassword.SavedTokenScreen
import com.token.tokenator.ui.savedpassword.passworddetails.TokenDetailScreen
import com.token.tokenator.ui.security.SecurityScreen
import com.token.tokenator.ui.settings.SettingsScreen
import com.token.tokenator.ui.theme.TokenatorTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var aut: Task<AppUpdateInfo>
    private val updateType = AppUpdateType.FLEXIBLE

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
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        aut = appUpdateManager.appUpdateInfo
        checkIsUpdateAvailable()

        setContent {
            TokenatorTheme {
                TokenatorApp()
            }
        }
    }

    @Composable
    fun TokenatorApp() {
        val startRoute = Route.Main
        val topLevelRoutes = remember { setOf(Route.Main, Route.SavedToken, Route.Settings) }

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
                entry<Route.Security> { SecurityScreen(navigator) }
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
