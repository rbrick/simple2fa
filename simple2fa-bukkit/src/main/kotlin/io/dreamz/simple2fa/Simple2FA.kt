package io.dreamz.simple2fa

import io.dreamz.simple2fa.map.MapOpenListener
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Simple2FA : JavaPlugin() {
    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(MapOpenListener(), this)
    }
}