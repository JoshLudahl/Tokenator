# Rename Settings and Add Appearance Screen

This plan covers renaming the existing Settings screen to "Token Settings", creating a new main Settings hub, and adding an Appearance screen for theme management (Light/Dark/System and Dynamic Color).

## User Review Required

> [!IMPORTANT]
> The appearance settings (Theme Mode and Dynamic Color) will be stored in DataStore. I'll be adding two new keys to the existing `settings` DataStore.
> The "About" section in the new Settings screen will be prepopulated with placeholder text as requested.

## Proposed Changes

### Core & Navigation

#### [MODIFY] [Qualifiers.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/di/Qualifiers.kt)
- Add `@DataStoreThemeMode` and `@DataStoreDynamicColor` qualifiers.

#### [MODIFY] [Utilities.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/di/Utilities.kt)
- Provide string keys for `theme_mode` and `dynamic_color`.

#### [MODIFY] [Route.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/navigation/Route.kt)
- Add `TokenSettings` and `Appearance` routes.
- Keep `Settings` as the new main hub.

#### [MODIFY] [MainActivity.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/MainActivity.kt)
- Update `entryProvider` to include new routes.
- Observe theme preferences and pass them to `AppTheme`.

#### [MODIFY] [MainViewModel.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/MainViewModel.kt)
- Add StateFlows for `themeMode` and `dynamicColor` to allow the entire app to react to theme changes.

#### [MODIFY] [Theme.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/theme/Theme.kt)
- Update `AppTheme` to support the new theme modes (Light, Dark, System) and properly handle Dynamic Color on Android 12+.

---

### Settings Components

#### [MODIFY] [strings.xml](file:///Users/joshy/Development/Android/Tokenator/app/src/main/res/values/strings.xml)
- Add strings for "Token Settings", "Appearance", "Theme Mode", "Dynamic Color", and "About" section content.

#### [NEW] [ThemeMode.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/model/ThemeMode.kt)
- Define `ThemeMode` enum (SYSTEM, LIGHT, DARK).

#### [NEW] [SettingsScreen.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/settings/SettingsScreen.kt)
- Create the new main hub with links to Appearance, Token Settings, Legal (Dialog), and an About section.

#### [RENAME] `SettingsScreen.kt` -> [TokenSettingsScreen.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/settings/TokenSettingsScreen.kt)
- Update class name and title to "Token Settings".

#### [RENAME] `SettingsViewModel.kt` -> [TokenSettingsViewModel.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/settings/TokenSettingsViewModel.kt)
- Update class name.

#### [NEW] [AppearanceScreen.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/settings/AppearanceScreen.kt)
- Implement theme selection UI based on the reference provided.

#### [NEW] [AppearanceViewModel.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/settings/AppearanceViewModel.kt)
- Handle logic for updating theme preferences.

## Verification Plan

### Automated Tests
- N/A (UI focused changes)

### Manual Verification
1.  Launch the app and navigate to Settings.
2.  Verify the new hub shows Appearance, Token Settings, Legal, and About.
3.  Click "Token Settings" and verify it opens the original settings (now titled "Token Settings").
4.  Click "Appearance" and change the theme to Dark/Light/System. Verify the app theme updates immediately.
5.  Toggle "Dynamic Color" (on supported devices) and verify it applies.
6.  Verify "Legal" still opens the Privacy Policy dialog.
7.  Check the "About" section for correct prepopulated text.
