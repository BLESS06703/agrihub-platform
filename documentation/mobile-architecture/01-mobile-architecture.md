# AgriHub Platform - Mobile Architecture Guide

## Architecture Pattern

MVVM + Clean Architecture with offline-first design.

## Layer Structure

app/
  presentation/
    screens/       - Jetpack Compose screens
    components/    - Reusable UI components
    theme/         - Colors, typography, shapes
  domain/
    model/         - Domain entities
    repository/    - Repository interfaces
    usecase/       - Business logic operations
  data/
    local/         - Room database, DAOs, entities
    remote/        - Ktor HTTP client, API DTOs
    sync/          - Offline sync engine
  di/              - Koin dependency injection modules

## Key Libraries

UI: Jetpack Compose (Material 3)
Navigation: Compose Navigation with type-safe routes
Local Database: Room with coroutines support
Network: Ktor Client with kotlinx.serialization
DI: Koin for Compose
Image Loading: Coil (lightweight, coroutine-based)
Security: Android Keystore, EncryptedSharedPreferences
Background Sync: WorkManager with constraints
Testing: JUnit 5, MockK, Compose Testing

## Offline-First Design

Write path:
1. User performs action in UI
2. ViewModel calls UseCase
3. UseCase saves to Room database immediately
4. Record added to SyncQueue table
5. UI updates from local data (instant feedback)
6. WorkManager syncs when connectivity available

Read path:
1. UI observes Room database via Flow
2. On first load, fetch from network if online
3. Cache network response in Room
4. Subsequent loads use cached data
5. Pull-to-refresh forces network fetch

## Color Tokens

Primary colors:
- Primary: 0xFF2E7D32 (Forest Green)
- PrimaryLight: 0xFF43A047 (Emerald Green)
- PrimaryDark: 0xFF1B5E20 (Dark Green)

Accent:
- AccentGold: 0xFFF9A825 (Harvest Gold)
- AccentGoldLight: 0xFFFDD835

Background:
- Background: 0xFFF8FAF5 (Off White)
- Surface: 0xFFFFFFFF (White)

Text:
- TextPrimary: 0xFF263238 (Charcoal)
- TextSecondary: 0xFF546E7A (Grey Blue)
- TextOnPrimary: 0xFFFFFFFF (White on green)

Status:
- Info: 0xFF1E88E5
- Success: 0xFF4CAF50
- Warning: 0xFFFB8C00
- Error: 0xFFD32F2F

Module Colors:
- Farm: 0xFF388E3C
- Livestock: 0xFF6D4C41
- Marketplace: 0xFFF57C00
- Finance: 0xFFF9A825
- Inventory: 0xFF1976D2
- Warehouse: 0xFF546E7A
- Reports: 0xFF7B1FA2
- AI: 0xFF00838F

Dark Theme:
- DarkBackground: 0xFF121212
- DarkSurface: 0xFF1E1E1E
- DarkCard: 0xFF2A2A2A
- DarkPrimary: 0xFF66BB6A
- DarkAccent: 0xFFFFD54F
- DarkText: 0xFFFFFFFF

## Navigation Structure

Bottom Navigation Tabs:
1. Dashboard (Home) - AI insights, alerts, metrics
2. Farm - Fields, crops, planting, harvest
3. Marketplace - Listings, offers, orders
4. Finance - Income, expenses, profit/loss
5. More - Livestock, Inventory, Reports, Settings

Deep linking:
- Notification tap opens relevant screen
- URL scheme: agrihub://farms/{id}
- Push notification payload includes route

## State Management

ViewModel pattern:
- StateFlow for UI state
- sealed class for UI states: Loading, Success, Error
- One-way data flow: UI observes state, sends events

class FarmListViewModel(
    private val getFarmsUseCase: GetFarmsUseCase
) : ViewModel() {
    
    sealed class UiState {
        object Loading : UiState()
        data class Success(val farms: List<Farm>) : UiState()
        data class Error(val message: String) : UiState()
    }
    
    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state: StateFlow<UiState> = _state.asStateFlow()
    
    fun loadFarms() {
        viewModelScope.launch {
            getFarmsUseCase()
                .collect { result ->
                    _state.value = when (result) {
                        is Result.Success -> UiState.Success(result.data)
                        is Result.Error -> UiState.Error(result.message)
                    }
                }
        }
    }
}

## Sync Engine Design

Components:
- SyncManager: Orchestrates push and pull operations
- SyncQueue: Room table tracking pending changes
- NetworkMonitor: Observes connectivity state
- ConflictResolver: Handles sync conflicts

Sync priority levels:
1. CRITICAL: Financial transactions, contracts
2. HIGH: Farm records, harvest data
3. MEDIUM: Inventory, marketplace listings
4. LOW: Analytics events, logs

Conflict resolution rules:
- Financial records: Server always wins
- Farm data: Timestamp-based merge (newest wins)
- Inventory: Last-write-wins with user notification
- Marketplace: Lock during active transactions

## Security

Token storage:
- Access token: Android Keystore (hardware-backed)
- Refresh token: EncryptedSharedPreferences
- Never store tokens in plain SharedPreferences

Certificate pinning:
- Pin server certificate to prevent MITM attacks
- Use OkHttp CertificatePinner

ProGuard/R8:
- Obfuscate release builds
- Keep serialization classes
- Remove logging in release builds

## Performance Targets

- Cold start: under 3 seconds
- Warm start: under 1 second
- Screen rendering: under 2 seconds (2GB RAM devices)
- Image loading: thumbnail under 500ms
- Offline sync: 60 seconds for 24-hour backlog
- App size: under 30MB APK
