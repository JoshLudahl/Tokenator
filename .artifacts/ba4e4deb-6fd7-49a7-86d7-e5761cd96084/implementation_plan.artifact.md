# Migration of Tokenator to Jetpack Compose and Navigation 3

This plan outlines the steps to migrate the Tokenator app from the legacy Android View system and Fragments to Jetpack Compose, using Navigation 3 for navigation and adhering to the MVVM architecture.

## User Review Required

> [!IMPORTANT]
> - **Navigation 3 is a major paradigm shift**: It uses explicit back stack management and type-safe keys. This migration will replace the existing `nav_graph.xml` with Kotlin-based route definitions.
> - **Fragment Removal**: All Fragments will be replaced by Composable screens. `MainActivity` will be the sole entry point using `setContent`.
> - **Dependency Updates**: We will add Compose, Navigation 3, and KotlinX Serialization dependencies.

## Proposed Changes

### 1. Build Configuration & Dependencies
- Add Compose BOM and libraries (Material 3, Foundation, UI, Tooling).
- Add Navigation 3 dependencies.
- Add KotlinX Serialization plugin and library.
- Enable Compose in `app/build.gradle.kts`.

### 2. Core Navigation Setup
- [NEW] `NavigationState.kt`: Boilerplate for managing Navigation 3 back stacks.
- [NEW] `Navigator.kt`: Helper for performing navigation actions.
- [NEW] `Routes.kt`: Define `@Serializable` routes implementing `NavKey`.

### 3. Theme & UI Foundation
- [NEW] `TokenatorTheme.kt`: Migrate XML themes/colors to Compose Material 3.
- [MODIFY] `MainActivity.kt`: Remove Fragment-based setup; use `setContent` with a main App composable.

### 4. Screen Migrations (XML to Compose)
For each screen, we will:
1. Create a Composable screen.
2. Adapt the corresponding ViewModel to use `StateFlow` instead of `LiveData`.
3. Use `hiltViewModel()` for dependency injection.

#### Screens:
- **MainScreen**: Migration of `MainFragment`.
- **SavedTokensScreen**: Migration of `SavedTokenFragment`.
- **TokenDetailScreen**: Migration of `PasswordDetailFragment`.
- **SettingsScreen**: Migration of `SettingsFragment`.
- **SecurityScreen**: Migration of `SecurityFragment`.
- **PrivacyPolicyDialog**: Migration of `PrivacyPolicyDialogFragment`.

### 5. Cleanup
- [DELETE] All XML layouts in `res/layout/`.
- [DELETE] `res/navigation/nav_graph.xml`.
- [DELETE] All Fragment classes.
- [DELETE] Legacy navigation and databinding dependencies.

## Verification Plan

### Automated Tests
- I will add Compose UI tests for the main workflows.
- Verify that `MainViewModel` still functions correctly with Compose.

### Manual Verification
- Deploy to an emulator/device and verify:
  - Token generation works.
  - Saving tokens to the database.
  - Navigation between all screens.
  - Settings (switches) persist correctly via DataStore.
  - App update logic in `MainActivity` (visual check if possible, or ensure it's ported correctly).
