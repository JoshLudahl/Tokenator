# Fix Edge-to-Edge Deprecation Warnings

The app is currently flagged for using deprecated APIs and parameters for edge-to-edge support in Android 15. The main culprits are the explicit settings in the XML themes, which are no longer recommended (and some are disabled) when targeting Android 15+.

## Proposed Changes

### [Resources]

#### [MODIFY] [themes.xml](file:///Users/joshy/Development/Android/Tokenator/app/src/main/res/values/themes.xml)
- Remove `android:statusBarColor` and `android:navigationBarColor` (deprecated in Android 15).
- Remove `android:windowLayoutInDisplayCutoutMode` with value `shortEdges` (deprecated in Android 15, replaced by `always` or handled by `enableEdgeToEdge()`).

#### [MODIFY] [themes.xml](file:///Users/joshy/Development/Android/Tokenator/app/src/main/res/values-night/themes.xml)
- Mirror the changes from the light theme to ensure consistency.

## Verification Plan

### Automated Tests
- Build the project to ensure no XML errors: `./gradlew assembleDebug`

### Manual Verification
- Deploy the app to an Android 15 emulator or device.
- Verify that the app still renders edge-to-edge (content draws behind the status and navigation bars).
- Confirm that the splash screen still looks correct.
