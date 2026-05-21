# API and Interfaces

## MVP has no backend API

The MVP is offline-first and should not require internet permission.

This document defines internal app interfaces.

## Installed app provider

```kotlin
interface InstalledAppProvider {
    suspend fun getLaunchableApps(): List<InstalledAppInfo>
    fun getLaunchIntent(packageName: String): Intent?
}
```

## Settings repository

```kotlin
interface SettingsRepository {
    suspend fun getSettings(): AppSettings
    suspend fun setActiveMode(mode: ChildMode)
    suspend fun setOnboardingComplete(complete: Boolean)
}
```

## PIN repository

```kotlin
interface PinRepository {
    suspend fun setPin(rawPin: String)
    suspend fun verifyPin(rawPin: String): Boolean
    suspend fun hasPin(): Boolean
}
```

## Allowed apps repository

```kotlin
interface AllowedAppsRepository {
    suspend fun getAllowedApps(): List<AllowedApp>
    suspend fun setAppAllowed(packageName: String, allowed: Boolean)
    suspend fun assignAppToMode(packageName: String, mode: ChildMode, enabled: Boolean)
    suspend fun getAppsForMode(mode: ChildMode): List<AllowedApp>
}
```

## Contacts repository

```kotlin
interface ContactsRepository {
    suspend fun getParentContact(): GuardianContact?
    suspend fun saveParentContact(contact: GuardianContact)
    suspend fun getEmergencyContact(): EmergencyContact?
    suspend fun saveEmergencyContact(contact: EmergencyContact)
}
```

## Tile engine

```kotlin
interface TileEngine {
    suspend fun buildTilesForMode(mode: ChildMode): List<LauncherTile>
}
```

## Child mode service

```kotlin
interface ChildModeService {
    suspend fun getActiveMode(): ChildMode
    suspend fun switchMode(mode: ChildMode)
    suspend fun getVisibleApps(mode: ChildMode): List<AllowedApp>
}
```

## Future backend boundary

If cloud sync is added later, create a separate sync module. Do not mix cloud logic into domain rules.
