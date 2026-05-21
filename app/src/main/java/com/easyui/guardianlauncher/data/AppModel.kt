package com.easyui.guardianlauncher.data

enum class Mode {
    HOME,
    SCHOOL,
    SLEEP
}

enum class Category {
    LEARNING,
    GAMES,
    COMMUNICATION,
    CAMERA_PHOTOS,
    UTILITIES,
    OTHER
}

data class GuardianContact(
    val label: String,
    val phoneNumber: String
)

data class EmergencyContact(
    val label: String,
    val phoneNumber: String,
    val enabled: Boolean
)

data class AllowedApp(
    val packageName: String,
    val displayLabel: String,
    val category: Category
)
