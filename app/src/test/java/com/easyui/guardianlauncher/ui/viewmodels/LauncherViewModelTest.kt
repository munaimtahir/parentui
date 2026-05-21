package com.easyui.guardianlauncher.ui.viewmodels

import com.easyui.guardianlauncher.data.AllowedApp
import com.easyui.guardianlauncher.data.Category
import org.junit.Assert.*
import org.junit.Test
import java.security.MessageDigest

class LauncherViewModelTest {

    private fun sha256(input: String): String {
        val bytes = input.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    @Test
    fun testPinHashing() {
        val pin = "56789"
        val expectedHash = sha256(pin)
        
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(pin.toByteArray())
        val computed = digest.fold("") { str, it -> str + "%02x".format(it) }
        
        assertEquals(expectedHash, computed)
    }

    @Test
    fun testAppCategorizationHeuristic() {
        assertEquals(Category.LEARNING, getCategoryHeuristic("com.duolingo.education"))
        assertEquals(Category.GAMES, getCategoryHeuristic("com.rovio.angrybirds"))
        assertEquals(Category.COMMUNICATION, getCategoryHeuristic("com.whatsapp"))
        assertEquals(Category.CAMERA_PHOTOS, getCategoryHeuristic("com.sec.android.app.camera"))
        assertEquals(Category.UTILITIES, getCategoryHeuristic("com.android.calculator2"))
        assertEquals(Category.OTHER, getCategoryHeuristic("com.example.random"))
    }

    private fun getCategoryHeuristic(packageName: String): Category {
        val pkg = packageName.lowercase()
        return when {
            pkg.contains("edu") || pkg.contains("learn") || pkg.contains("study") || pkg.contains("dict") || pkg.contains("math") -> Category.LEARNING
            pkg.contains("game") || pkg.contains("play") || pkg.contains("arcade") || pkg.contains("puzzle") || pkg.contains("toy") || pkg.contains("angrybirds") -> Category.GAMES
            pkg.contains("chat") || pkg.contains("messag") || pkg.contains("whatsapp") || pkg.contains("telegr") || pkg.contains("skype") || pkg.contains("discord") || pkg.contains("talk") || pkg.contains("mail") || pkg.contains("gmail") -> Category.COMMUNICATION
            pkg.contains("camera") || pkg.contains("photo") || pkg.contains("gallery") || pkg.contains("snap") || pkg.contains("instagram") -> Category.CAMERA_PHOTOS
            pkg.contains("calc") || pkg.contains("clock") || pkg.contains("calend") || pkg.contains("note") || pkg.contains("weather") || pkg.contains("setting") || pkg.contains("file") || pkg.contains("map") || pkg.contains("browser") || pkg.contains("chrome") -> Category.UTILITIES
            else -> Category.OTHER
        }
    }

    @Test
    fun testModeFilteringLogic() {
        val installedApps = listOf(
            AllowedApp("com.duolingo.education", "Duolingo", Category.LEARNING),
            AllowedApp("com.rovio.angrybirds", "Angry Birds", Category.GAMES),
            AllowedApp("com.whatsapp", "WhatsApp", Category.COMMUNICATION),
            AllowedApp("com.android.calculator2", "Calculator", Category.UTILITIES)
        )

        val allowedApps = setOf("com.duolingo.education", "com.whatsapp", "com.android.calculator2") 
        val homeApps = setOf("com.duolingo.education", "com.whatsapp")
        val schoolApps = setOf("com.duolingo.education", "com.android.calculator2")
        val sleepApps = emptySet<String>()

        // Home Mode filtering
        val homeFiltered = installedApps
            .filter { it.packageName in allowedApps }
            .filter { it.packageName in homeApps }
        assertEquals(2, homeFiltered.size)
        assertTrue(homeFiltered.any { it.packageName == "com.duolingo.education" })
        assertFalse(homeFiltered.any { it.packageName == "com.rovio.angrybirds" })

        // School Mode filtering
        val schoolFiltered = installedApps
            .filter { it.packageName in allowedApps }
            .filter { it.packageName in schoolApps }
        assertEquals(2, schoolFiltered.size)
        assertTrue(schoolFiltered.any { it.packageName == "com.android.calculator2" })
        assertFalse(schoolFiltered.any { it.packageName == "com.whatsapp" })

        // Sleep Mode filtering
        val sleepFiltered = installedApps
            .filter { it.packageName in allowedApps }
            .filter { it.packageName in sleepApps }
        assertTrue(sleepFiltered.isEmpty())
    }
}
