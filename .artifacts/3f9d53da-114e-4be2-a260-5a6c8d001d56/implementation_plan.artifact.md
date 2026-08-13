# Restore and Update Material 3 Theme

Restore the fully supported Material 3 theme and update all screens to use the theme's color scheme instead of hardcoded colors, ensuring a uniform look across the application.

## User Review Required

> [!IMPORTANT]
> I will be replacing direct references to the `Fin...` palette colors (like `FinTextDark` and `FinSurfaceDark`) with their corresponding Material 3 `ColorScheme` slots. This ensures that the app correctly adapts to Light and Dark modes.

## Proposed Changes

### Theme and Colors

#### [MODIFY] [Color.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/theme/Color.kt)
- Ensure all palette colors are correctly defined.
- Map palette colors to Material 3 light and dark schemes consistently.

#### [MODIFY] [TokenatorTheme.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/theme/TokenatorTheme.kt)
- Verify `TokenatorTheme` correctly applies the schemes.
- (Optional) Enable dynamic color support if desired, though the user focused on the custom theme.

### UI Screens (Uniformity Update)

#### [MODIFY] [MainScreen.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/main/MainScreen.kt)
- Replace `FinTextDark` with `MaterialTheme.colorScheme.onSurface` or `onBackground`.
- Replace `FinSurfaceDark` with `MaterialTheme.colorScheme.surfaceVariant` (or a specific slot like `inverseSurface` if it represents the dark card).
- Ensure consistent use of `MaterialTheme.shapes`.

#### [MODIFY] [AddPasswordScreen.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/generate/AddPasswordScreen.kt)
- Replace hardcoded colors with `MaterialTheme.colorScheme`.
- Use theme typography for headers and labels.

#### [MODIFY] [SavedTokenScreen.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/savedpassword/SavedTokenScreen.kt)
- Update cards and text to use the theme scheme.

#### [MODIFY] [SettingsScreen.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/settings/SettingsScreen.kt)
- Update section headers and cards.

#### [MODIFY] [TokenDetailScreen.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/savedpassword/passworddetails/TokenDetailScreen.kt)
- Minor cleanup to ensure full theme compliance.

## Verification Plan

### Automated Tests
- I'll run `gradle_sync` to ensure no dependency issues.
- I'll use `render_compose_preview` on `MainScreen` and `AddPasswordScreen` (if previews exist) to verify the visual changes.

### Manual Verification
- Deploy the app to the device and toggle between Light and Dark modes to ensure colors adapt correctly.
