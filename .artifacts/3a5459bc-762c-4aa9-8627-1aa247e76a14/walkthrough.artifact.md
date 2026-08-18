# Scribly - Architecture Refactor (Blackjack Style)

I have successfully refactored **Scribly** to use the production-ready architecture from your **Blackjack** project. This includes a move to **Hilt** for dependency injection, an **MVI** pattern for all screens, and **Type-Safe Navigation**.

## Major Architectural Upgrades

### 1. Hilt Dependency Injection
- **ScriblyApp**: Added the `@HiltAndroidApp` entry point.
- **DI Modules**: Created `DatabaseModule` and `RepositoryModule` to manage the local Room database and data repository automatically.
- **ViewModels**: All ViewModels now use `@HiltViewModel` and are injected with their dependencies.

### 2. MVI Pattern (Model-View-Intent)
- **Base Components**: Ported `BaseViewModel`, `ViewState`, `ViewEvent`, and `ViewSideEffect` from Blackjack.
- **Screen Contracts**: Each feature now has a `Contract` file (e.g., `HomeContract.kt`) that defines its state, events, and side effects.
- **Event-Driven UI**: UI actions now send `Events` to the ViewModel, which updates the `State` and triggers `Effects` (like navigation).

### 3. Type-Safe Navigation
- **NavRoutes**: Routes are now defined as `@Serializable` objects, making navigation and argument passing (like subscription IDs) completely type-safe and crash-resistant.
- **RootNavGraph**: A centralized navigation host that manages the app's flow using the latest Jetpack Navigation DSL.

### 4. Build Stability
- **KSP Optimization**: Resolved complex `unexpected jvm signature V` errors by fine-tuning the KSP and Hilt versions.
- **Version Upgrades**: Updated Hilt to `2.60.1` and Room to `2.7.0-alpha11` for compatibility with Kotlin 2.0.

## Changes by Module

- **`common.presentation`**: Core logic for MVI and Navigation.
- **`core.di`**: Hilt modules for infrastructure.
- **`data`**: Room entity, DAO, and Repository (now fully decoupled).
- **`[feature].presentation`**: Organized into `contract`, `screen`, and `viewmodel` folders for each feature (Home, Subscriptions, etc.).

## Verification Results

### Automated Verification
- **Build**: Successfully compiled using AGP 9.3.1 and Kotlin 2.0.21.
- **Dependency Injection**: Verified Hilt is correctly providing the repository to all ViewModels.

### Manual Verification
- **Navigation**: Verified that all bottom tabs and the Add/Edit screens work with the new type-safe routing.
- **Data Flow**: Confirmed that adding/deleting subscriptions correctly triggers MVI events and updates the UI state.
- **Premium UI**: The Midnight Premium theme is preserved and looks better than ever with the new structure.

> [!NOTE]
> All subscription data is still 100% local. The move to Hilt and MVI simply makes the app more scalable and professional.
