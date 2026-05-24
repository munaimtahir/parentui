package com.easyui.guardianlauncher.data

data class SetupChecklistItem(
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val actionLabel: String? = null,
    val route: String? = null,
    val subTab: Int? = null
)

data class SetupChecklist(
    val items: List<SetupChecklistItem>,
    val isFullySetup: Boolean
)
