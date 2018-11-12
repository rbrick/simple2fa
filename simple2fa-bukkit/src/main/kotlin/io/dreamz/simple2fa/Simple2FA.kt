package io.dreamz.simple2fa

import io.dreamz.simple2fa.listeners.JoinListener
import io.dreamz.simple2fa.listeners.LeaveListener
import io.dreamz.simple2fa.listeners.PreventionListeners
import io.dreamz.simple2fa.session.Session
import io.dreamz.simple2fa.storage.StorageEngineBuilder
import io.dreamz.simple2fa.utils.HOTP
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*


const val PLUGIN_PREFIX = "&6[&aSimple2FA&6]"
const val PLUGIN_PERMISSION = "simple2fa.require_auth"

class Simple2FA : JavaPlugin() {

    val random = Random(System.nanoTime())
    val totp = HOTP()
    val sessions = mutableMapOf<UUID, Session>()
    val storageEngine = StorageEngineBuilder().withFile(File("secrets.yml")).create()

    val issuer = config.getString("issuer", "DreamZ")

    companion object {
        @JvmStatic
        lateinit var instance: Simple2FA
    }

    init {
        instance = this
    }

    override fun onEnable() {
        // register our listeners
        Bukkit.getPluginManager().registerListeners(JoinListener, LeaveListener, PreventionListeners)
    }

    override fun onDisable() {
        storageEngine.save()
    }
}

fun PluginManager.registerListeners(vararg listeners: Listener) {
    for (listener in listeners) {
        this.registerEvents(listener, Simple2FA.instance)
    }
}