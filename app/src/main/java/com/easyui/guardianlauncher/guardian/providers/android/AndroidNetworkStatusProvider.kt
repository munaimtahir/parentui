package com.easyui.guardianlauncher.guardian.providers.android

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.easyui.guardianlauncher.guardian.providers.NetworkStatusProvider

class AndroidNetworkStatusProvider(
    private val context: Context,
) : NetworkStatusProvider {
    override suspend fun isConnected(): Boolean? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return null
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        val hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        return hasInternet
    }
}

