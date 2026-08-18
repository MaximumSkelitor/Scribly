# Implementation Plan - Refactor Scribly to Blackjack Architecture

This plan refactors the Scribly app to follow the robust, production-ready architecture used in the Blackjack app. This includes **Hilt for Dependency Injection**, **MVI (Model-View-Intent) pattern** with a BaseViewModel, and **Modular Type-Safe Navigation**.

## User Review Required

> [!IMPORTANT]
> - **Architecture Shift**: We are moving from a simple MVVM to a more structured MVI pattern. Each screen will have a `Contract` (State, Event, Effect).
> - **Dependency Injection**: Hilt will now manage all your dependencies (Database, Repository, ViewModels).
> - **Navigation**: We'll use Jetpack Navigation's Type-Safe DSL, which makes passing data between screens much safer.
> - **Folder Reorganization**: Files will be moved into `core`, `common`, and feature-specific folders (e.g., `home`, `subscriptions`).

## Proposed Changes

### 1. Dependencies & Hilt Setup
#### [MODIFY] [libs.versions.toml](file:///C:/Users/askma/AndroidStudioProjects/Scribly/gradle/libs.versions.toml)
- Add Hilt versions and libraries.
- Add Serialization (required for type-safe navigation).

#### [MODIFY] [build.gradle.kts (App)](file:///C:/Users/askma/AndroidStudioProjects/Scribly/app/build.gradle.kts)
- Apply Hilt and Kotlin Serialization plugins.
- Add Hilt implementation and compiler.

#### [NEW] [ScriblyApp.kt](file:///C:/Users/askma/AndroidStudioProjects/Scribly/app/src/main/java/com/weberpackage/scribly/ScriblyApp.kt)
- Define the `Application` class with `@HiltAndroidApp`.

### 2. Core & Common Base
#### [NEW] Base Components in `common.presentation.base`
- `BaseViewModel.kt`: Port the generic MVI ViewModel from Blackjack.
- `MviInterfaces.kt`: Define `ViewEvent`, `ViewState`, `ViewSideEffect`.

#### [NEW] Utils in `common.presentation.utils`
- `ObserveAsEvents.kt`: Handle one-time side effects (like navigation or toasts).
- `UiText.kt`: Better string resource handling.

### 3. DI Modules
#### [NEW] [DatabaseModule.kt](file:///C:/Users/askma/AndroidStudioProjects/Scribly/app/src/main/java/com/weberpackage/scribly/core/di/DatabaseModule.kt)
- Provide `ScriblyDatabase` and `SubscriptionDao`.

#### [NEW] [RepositoryModule.kt](file:///C:/Users/askma/AndroidStudioProjects/Scribly/app/src/main/java/com/weberpackage/scribly/core/di/RepositoryModule.kt)
- Provide `SubscriptionRepository`.

### 4. Navigation Refactor
#### [NEW] [NavRoutes.kt](file:///C:/Users/askma/AndroidStudioProjects/Scribly/app/src/main/java/com/weberpackage/scribly/common/presentation/navigation/NavRoutes.kt)
- Define `@Serializable` objects for all app routes.

#### [NEW] [RootNavGraph.kt](file:///C:/Users/askma/AndroidStudioProjects/Scribly/app/src/main/java/com/weberpackage/scribly/common/presentation/navigation/RootNavGraph.kt)
- Centralized navigation host with sub-graphs for features.

### 5. Feature Refactor (Home, Subscriptions, etc.)
Each feature will be reorganized into:
- `feature.presentation.contract`: `HomeContract.kt` (Event, State, Effect).
- `feature.presentation.screen`: `HomeScreen.kt` (Composable).
- `feature.presentation.viewmodel`: `HomeViewModel.kt` (extending `BaseViewModel`).

### 6. MainActivity Refactor
#### [MODIFY] [MainActivity.kt](file:///C:/Users/askma/AndroidStudioProjects/Scribly/app/src/main/java/com/weberpackage/scribly/MainActivity.kt)
- Add `@AndroidEntryPoint`.
- Use a `MainViewModel` to handle global state like theme.
- Clean up initialization logic (moved to Hilt).

## Verification Plan

### Automated Verification
- **Build**: Ensure successful compilation after Hilt and Serialization integration.
- **Dependency Graph**: Verify that Hilt is correctly injecting the repository into ViewModels.

### Manual Verification
1. **Launch**: Confirm sample data still appears.
2. **MVI Flow**: Verify that adding a subscription sends an `Event` and updates the `State`.
3. **Navigation**: Test all bottom tabs and verify type-safe route passing (e.g., passing ID to Edit screen).
4. **Theme**: Verify dark/light mode still works with the new structure.
