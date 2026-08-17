# Biometric/PIN Check Optimization

Optimize the app launch biometric/PIN check to only occur when both the app-level setting is enabled and the device has biometrics or credentials enrolled.

## Proposed Changes

### MainActivity

#### [MODIFY] [MainActivity.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/MainActivity.kt)

- Add `import androidx.biometric.BiometricManager` to handle device-level authentication checks.
- Introduce a `canAuthenticate` check within the `TokenatorApp` composable using `BiometricManager.canAuthenticate()`.
- Update the condition for showing the `BiometricPrompt` to include the `canAuthenticate` check.
- Clean up the `setAllowedAuthenticators` call to use imported constants for better readability.

## Verification Plan

### Manual Verification
- **Scenario 1: App Toggle ON, Device Biometrics/PIN ON**
    - Verify that the biometric prompt appears on app launch.
- **Scenario 2: App Toggle ON, Device Biometrics/PIN OFF**
    - Verify that the biometric prompt is skipped and the app opens directly.
- **Scenario 3: App Toggle OFF, Device Biometrics/PIN ON**
    - Verify that no biometric prompt appears.
- **Scenario 4: App Toggle OFF, Device Biometrics/PIN OFF**
    - Verify that no biometric prompt appears.
