# Replace Icons.Rounded with Drawables

The goal is to replace all usages of `Icons.Rounded.*` (and `Icons.AutoMirrored.Rounded.*`) with the project's local XML drawables. For icons without a direct match, `R.drawable.settings_24px` will be used as a placeholder. Finally, the `material-icons-extended` dependency will be removed.

## User Review Required

> [!IMPORTANT]
> The following icons did not have a direct match in the drawable folder and will be replaced with `R.drawable.settings_24px` as requested:
> - `Icons.Rounded.Close`
> - `Icons.Rounded.Save`
> - `Icons.Rounded.Warning`
> - `Icons.Rounded.GeneratingTokens` (Assuming `token_24px` might be a better match, but will use `settings` if uncertain)
>
> I have mapped `Icons.AutoMirrored.Rounded.ArrowBack` to `R.drawable.arrow_back_24px`.

## Proposed Changes

### UI Components

#### [MODIFY] [PrivacyPolicyDialog.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/components/PrivacyPolicyDialog.kt)
- Replace `Icons.Rounded.Close` with `R.drawable.settings_24px`.
- Update imports and `Icon` calls to use `painterResource`.

#### [MODIFY] [AddPasswordScreen.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/generate/AddPasswordScreen.kt)
- Replace `Icons.Rounded.ContentCopy`, `Icons.Rounded.GeneratingTokens`, `Icons.Rounded.Refresh`.
- Update imports and `Icon` calls.

#### [MODIFY] [MainScreen.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/main/MainScreen.kt)
- Replace multiple `Icons.Rounded.*` usages.
- Update imports and `Icon` calls.

#### [MODIFY] [PasswordDetailScreen.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/passworddetail/PasswordDetailScreen.kt)
- Replace multiple `Icons.Rounded.*` usages.
- Update imports and `Icon` calls.

#### [MODIFY] [AppearanceScreen.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/settings/AppearanceScreen.kt)
- Replace `Icons.Rounded.Done`.
- Update imports and `Icon` calls.

#### [MODIFY] [SettingsScreen.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/settings/SettingsScreen.kt)
- Replace `Icons.AutoMirrored.Rounded.ArrowBack`, `Icons.Rounded.ChevronRight`, `Icons.Rounded.Info`, `Icons.Rounded.Palette`, `Icons.Rounded.Policy`, `Icons.Rounded.Settings`.
- Update imports and `Icon` calls.

#### [MODIFY] [TokenSettingsScreen.kt](file:///Users/joshy/Development/Android/Tokenator/app/src/main/java/com/token/tokenator/ui/settings/TokenSettingsScreen.kt)
- Replace `Icons.AutoMirrored.Rounded.ArrowBack`, `Icons.Rounded.Save`.
- Update imports and `Icon` calls.

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///Users/joshy/Development/Android/Tokenator/gradle/libs.versions.toml)
- Remove `androidx-compose-material-icons-extended`.

#### [MODIFY] [build.gradle.kts](file:///Users/joshy/Development/Android/Tokenator/app/build.gradle.kts)
- Remove `libs.androidx.compose.material.icons.extended` dependency.

## Verification Plan

### Automated Tests
- Run `./gradlew assembleDebug` to ensure the project compiles with the new drawable references and without the removed dependency.

### Manual Verification
- The user will verify the icon placements and replace placeholders as needed.
