package com.easyui.guardianlauncher.guardian.routine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.easyui.guardianlauncher.data.Mode
import com.easyui.guardianlauncher.data.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RoutineReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SCHEDULE_EVENT = "com.easyui.guardianlauncher.ACTION_SCHEDULE_EVENT"
        const val EXTRA_MODE = "extra_mode"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_SCHEDULE_EVENT) {
            val modeName = intent.getStringExtra(EXTRA_MODE) ?: return
            val mode = try { Mode.valueOf(modeName) } catch (e: Exception) { return }

            val repository = SettingsRepository(context)
            val routineManager = RoutineManager(context)

            CoroutineScope(Dispatchers.IO).launch {
                repository.saveActiveMode(mode)
                val schedules = repository.routineSchedules.first()
                routineManager.scheduleNextEvent(schedules)
            }
        }
    }
}
