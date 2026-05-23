package com.easyui.guardianlauncher.guardian.providers

interface LauncherStatusProvider {
    /**
     * @return `true` if EasyUI is default HOME handler, `false` if not, or `null` if unknown.
     */
    suspend fun isDefaultLauncher(): Boolean?
}

