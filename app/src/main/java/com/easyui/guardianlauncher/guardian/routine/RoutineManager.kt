package com.easyui.guardianlauncher.guardian.routine

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.easyui.guardianlauncher.data.RoutineSchedule
import java.util.Calendar

class RoutineManager(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleNextEvent(schedules: List<RoutineSchedule>) {
        val enabledSchedules = schedules.filter { it.enabled }
        if (enabledSchedules.isEmpty()) {
            cancelAllAlarms()
            return
        }

        // For each enabled schedule, we find the next start and end time
        // The earliest of all these times is our next alarm
        val nextEvent = findNextEvent(enabledSchedules) ?: return

        val intent = Intent(context, RoutineReceiver::class.java).apply {
            action = RoutineReceiver.ACTION_SCHEDULE_EVENT
            putExtra(RoutineReceiver.EXTRA_MODE, nextEvent.mode.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            nextEvent.timeMillis,
            pendingIntent
        )
    }

    private fun findNextEvent(schedules: List<RoutineSchedule>): ScheduledEvent? {
        val now = Calendar.getInstance()
        val events = mutableListOf<ScheduledEvent>()

        schedules.forEach { schedule ->
            // Start Event
            val startCal = parseTime(schedule.startTime)
            findNextOccurrence(startCal, schedule.daysEnabled)?.let {
                events.add(ScheduledEvent(schedule.mode, it))
            }

            // End Event (switch back to HOME)
            val endCal = parseTime(schedule.endTime)
            findNextOccurrence(endCal, schedule.daysEnabled)?.let {
                events.add(ScheduledEvent(com.easyui.guardianlauncher.data.Mode.HOME, it))
            }
        }

        return events.minByOrNull { it.timeMillis }
    }

    private fun findNextOccurrence(cal: Calendar, days: List<Int>): Long? {
        val now = Calendar.getInstance()
        val testCal = cal.clone() as Calendar
        
        // Try next 7 days
        for (i in 0..7) {
            val dayOfWeek = testCal.get(Calendar.DAY_OF_WEEK)
            if (dayOfWeek in days && testCal.after(now)) {
                return testCal.timeInMillis
            }
            testCal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return null
    }

    private fun parseTime(time: String): Calendar {
        val parts = time.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    private fun cancelAllAlarms() {
        val intent = Intent(context, RoutineReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    data class ScheduledEvent(
        val mode: com.easyui.guardianlauncher.data.Mode,
        val timeMillis: Long
    )
}
