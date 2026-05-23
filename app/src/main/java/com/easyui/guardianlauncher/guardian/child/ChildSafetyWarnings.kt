package com.easyui.guardianlauncher.guardian.child

import com.easyui.guardianlauncher.guardian.GuardianCheckStatus
import com.easyui.guardianlauncher.guardian.CheckState

object ChildSafetyWarnings {
    /**
     * Produces at most [maxWarnings] calm, child-friendly warnings.
     *
     * Priority:
     * 1) Low battery
     * 2) Emergency contact missing
     * 3) Setup incomplete (PIN/parent contact/default launcher)
     */
    fun fromGuardianStatus(
        status: GuardianCheckStatus,
        maxWarnings: Int = 2,
    ): List<ChildSafetyWarning> {
        val warnings = ArrayList<ChildSafetyWarning>(maxWarnings)

        if (status.batteryLow) {
            warnings.add(
                ChildSafetyWarning(
                    type = ChildSafetyWarningType.LOW_BATTERY,
                    message = "Battery is low. Please charge the phone.",
                )
            )
        }

        if (!status.emergencyContactConfigured && warnings.size < maxWarnings) {
            warnings.add(
                ChildSafetyWarning(
                    type = ChildSafetyWarningType.EMERGENCY_CONTACT_MISSING,
                    message = "Emergency contact is not set yet. Ask your parent to add it.",
                )
            )
        }

        val setupIncomplete =
            !status.parentContactConfigured || !status.pinConfigured || status.defaultLauncherActive == CheckState.ACTION_REQUIRED
        if (setupIncomplete && warnings.size < maxWarnings) {
            warnings.add(
                ChildSafetyWarning(
                    type = ChildSafetyWarningType.SETUP_INCOMPLETE,
                    message = "Ask your parent to finish EasyUI setup.",
                )
            )
        }

        return warnings
    }
}
