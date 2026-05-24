package com.easyui.guardianlauncher.data

import kotlinx.serialization.Serializable

@Serializable
enum class Mode {
    HOME,
    SCHOOL,
    SLEEP,
    BEDTIME,
    TRAVEL,
    EXAM
}

@Serializable
enum class Category {
    LEARNING,
    GAMES,
    COMMUNICATION,
    CAMERA_PHOTOS,
    UTILITIES,
    OTHER
}

@Serializable
data class GuardianContact(
    val label: String,
    val phoneNumber: String
)

@Serializable
data class EmergencyContact(
    val label: String,
    val phoneNumber: String,
    val enabled: Boolean
)

@Serializable
data class AllowedApp(
    val packageName: String,
    val displayLabel: String,
    val category: Category
)
