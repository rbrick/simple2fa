package io.dreamz.simple2fa

import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.event.ServerSwitchEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler
import java.util.*

// TODO: 1.13 has changed plugin messaging names; rename to 'simple2fa:auth'

// The plugin message channel to use
const val PLUGIN_MESSAGE_CHANNEL = "s2fa"

val authenticatedUsers = mutableMapOf<UUID, Boolean>()

class Simple2FA : Plugin() {

    object ConnectListener : Listener {
        @EventHandler
        fun onPlayerJoin(event: ServerConnectEvent) = Unit

        @EventHandler
        fun onServerSwitch(event: ServerSwitchEvent) = Unit
    }


    override fun onEnable() {
        this.proxy.registerChannel(PLUGIN_MESSAGE_CHANNEL)
        this.proxy.pluginManager.registerListener(this, ConnectListener)
    }
}