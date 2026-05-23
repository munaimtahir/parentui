package com.easyui.guardianlauncher.guardian.providers

interface NetworkStatusProvider {
    /**
     * @return `true` if connected, `false` if disconnected, or `null` if unknown.
     */
    suspend fun isConnected(): Boolean?
}

