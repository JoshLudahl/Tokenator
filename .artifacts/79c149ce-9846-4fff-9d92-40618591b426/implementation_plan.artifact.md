# Update to Native Splash Screen

This plan updates the application to fully utilize the `androidx.core:core-splashscreen` library. This includes updating the splash screen icon to use the new `ic_tokenator` and ensuring the splash screen remains visible until the initial app state (onboarding status) is loaded, preventing a brief blank screen or flicker.

## Proposed Changes

### Resources

#### [MODIFY] [themes.xml](file:///Users/joshy/Development/Android/Tokenator/app/src/main/res/values/themes.xml)
Update `splashScreenTheme` to use `@drawable/ic_tokenator` as the animated icon and ensure background colors are consistent.

#### [MODIFY] [themes.xml](file:///Users/joshy/Development/Android/Tokenator/app/src/main/res/values-night/themes.xml)
Update `splashScreenTheme` for dark mode to use `@drawable/ic_tokenator` and consistent background colors.

#### [DELETE] [splash_screen.xml](file:///Users/joshy/Development/Android/Tokenator/app/src/main/res/drawable/splash_screen.xml)
Remove the legacy `layer-list` splash screen drawable.

### UI / Activity

#### [MODIFY] [MainActivity.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/MainActivity.kt)
- Inject `OnboardingViewModel` to observe the onboarding state.
- Use `SplashScreen.setKeepOnScreenCondition` to keep the splash screen visible until `isOnboardingCompleted` is no longer `null`.

## Verification Plan

### Automated Tests
- Build the project to ensure no resource or compilation errors.

### Manual Verification
- Deploy the app to an emulator or device (Android 12+ and older).
- Verify the splash screen displays the `ic_tokenator` icon.
- Verify there is no blank screen flicker between the splash screen and the first app screen (Onboarding or Main).
