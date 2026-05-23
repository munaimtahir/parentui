package com.easyui.guardianlauncher.guardian.clock

class SystemClock : Clock {
    override fun nowMillis(): Long = System.currentTimeMillis()
}

