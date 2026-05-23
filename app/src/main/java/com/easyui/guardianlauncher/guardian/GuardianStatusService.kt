package com.easyui.guardianlauncher.guardian

interface GuardianStatusService {
    suspend fun getGuardianStatus(): GuardianCheckStatus
}

