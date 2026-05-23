package com.easyui.guardianlauncher.guardian.child

enum class ChildSafetyWarningType {
    LOW_BATTERY,
    EMERGENCY_CONTACT_MISSING,
    SETUP_INCOMPLETE,
}

data class ChildSafetyWarning(
    val type: ChildSafetyWarningType,
    val message: String,
)

