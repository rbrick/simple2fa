package io.dreamz.simple2fa

import io.dreamz.simple2fa.listeners.JoinListener
import io.dreamz.simple2fa.listeners.LeaveListener
import io.dreamz.simple2fa.listeners.PreventionListeners
import io.dreamz.simple2fa.session.Session
import io.dreamz.simple2fa.settings.*
import io.dreamz.simple2fa.storage.StorageEngine
import io.dreamz.simple2fa.storage.StorageEngineBuilder
import io.dreamz.simple2fa.utils.HOTP
import io.dreamz.simple2fa.utils.TimeCounter
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*


const val PLUGIN_PREFIX = "&6[&aSimple2FA&6]"
const val PLS_AUTH_MESSAGE = "&cPlease enter your 2FA code."
const val PLUGIN_PERMISSION = "simple2fa.require_auth"

class Simple2FA : JavaPlugin() {

    val random = Random(System.nanoTime())
    val sessions = mutableMapOf<UUID, Session>()

    lateinit var storageEngine: StorageEngine
    lateinit var totp: HOTP

    companion object {
        @JvmStatic
        lateinit var instance: Simple2FA
    }

    override fun onLoad() {
        this.saveDefaultConfig()
    }

    override fun onEnable() {
        instance = this

        storageEngine = when {
            StorageSettings.engine == "mongodb" -> StorageEngineBuilder().mongodb()
                    .withUri(MongoSettings.uri)
                    .withDatabase(MongoSettings.database)
                    .withCollection(MongoSettings.collection)
                    .create()
            StorageSettings.engine == "redis" -> StorageEngineBuilder().redis().withUri( RedisSettings.uri  ).create()
            else -> StorageEngineBuilder().flatfile(File(FlatfileSettings.location)).create()
        }

        totp = HOTP(TimeCounter(), OtpSettings.digits)

        // register our listeners
        Bukkit.getPluginManager().registerListeners(JoinListener, LeaveListener, PreventionListeners)
    }

    override fun onDisable() {
        try {
            storageEngine.save()
        } catch (ignored: Exception) {

        }
    }
}

fun PluginManager.registerListeners(vararg listeners: Listener) {
    for (listener in listeners) {
        this.registerEvents(listener, Simple2FA.instance)
    }
}