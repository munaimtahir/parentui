package com.easyui.guardianlauncher.guardian.providers.android

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.easyui.guardianlauncher.guardian.providers.LauncherStatusProvider

class AndroidLauncherStatusProvider(
    private val context: Context,
) : LauncherStatusProvider {
    override suspend fun isDefaultLauncher(): Boolean? {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            ?: return null
        val defaultPackage = resolveInfo.activityInfo?.packageName ?: return null
        return defaultPackage == context.packageName
    }
}

