package com.easyui.guardianlauncher.data

import kotlinx.serialization.Serializable

@Serializable
data class RoutineSchedule(
    val id: String,
    val mode: Mode,
    val startTime: String, // HH:mm format
    val endTime: String,   // HH:mm format
    val daysEnabled: List<Int>, // 1=Sun, 2=Mon...
    val enabled: Boolean = true
)

@Serializable
data class AppBackup(
    val parentContact: GuardianContact,
    val emergencyContact: EmergencyContact,
    val allowedApps: Set<String>,
    val modeAppsHome: Set<String>,
    val modeAppsSchool: Set<String>,
    val modeAppsSleep: Set<String>,
    val modeAppsBedtime: Set<String>,
    val modeAppsTravel: Set<String>,
    val modeAppsExam: Set<String>,
    val schedules: List<RoutineSchedule>,
    val layoutLockEnabled: Boolean,
    val parentPinHash: String,
    val timestamp: Long = System.currentTimeMillis()
)
