package io.dreamz.simple2fa.listeners

import io.dreamz.simple2fa.PLUGIN_PERMISSION
import io.dreamz.simple2fa.Simple2FA
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*

object PreventionListeners : Listener {
    @EventHandler
    fun move(event: PlayerMoveEvent) {
        if (event.player.hasPermission(PLUGIN_PERMISSION)) {
            val session = Simple2FA.instance.sessions[event.player.uniqueId]
            if (session != null && session.needsAuthentication()) {
                if (event.to.blockX != event.from.blockX ||
                        event.to.blockY != event.from.blockY ||
                        event.to.blockZ != event.from.blockZ) {
                    event.to = event.from
                }
            }
        }
    }

    @EventHandler
    fun interact(event: PlayerInteractEvent) {
        if (event.player.hasPermission(PLUGIN_PERMISSION)) {
            val session = Simple2FA.instance.sessions[event.player.uniqueId]
            if (session != null && session.needsAuthentication()) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun attack(event: EntityDamageByEntityEvent) {

        if (event.damager is Player) {
            if ((event.damager as Player).hasPermission(PLUGIN_PERMISSION)) {
                val session = Simple2FA.instance.sessions[(event.damager as Player).uniqueId]
                if (session != null && session.needsAuthentication()) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun damaged(event: EntityDamageEvent) {
        if (event.entity is Player) {
            if ((event.entity as Player).hasPermission(PLUGIN_PERMISSION)) {
                val session = Simple2FA.instance.sessions[(event.entity as Player).uniqueId]
                if (session != null && session.needsAuthentication()) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun inventoryClick(event: InventoryClickEvent) {
        if (event.whoClicked.hasPermission(PLUGIN_PERMISSION)) {
            val session = Simple2FA.instance.sessions[event.whoClicked.uniqueId]
            if (session != null && session.needsAuthentication()) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun itemDrop(event: PlayerDropItemEvent) {
        if (event.player.hasPermission(PLUGIN_PERMISSION)) {
            val session = Simple2FA.instance.sessions[event.player.uniqueId]
            if (session != null && session.needsAuthentication()) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun hunger(event: FoodLevelChangeEvent) {
        if (event.entity.hasPermission(PLUGIN_PERMISSION)) {
            val session = Simple2FA.instance.sessions[event.entity.uniqueId]
            if (session != null && session.needsAuthentication()) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun command(event: PlayerCommandPreprocessEvent) {
        if (event.player.hasPermission(PLUGIN_PERMISSION)) {
            val session = Simple2FA.instance.sessions[event.player.uniqueId]
            if (session != null && session.needsAuthentication()) {
                event.isCancelled = true
                event.player.sendMessage("${ChatColor.RED}You must authenticate before")
            }
        }
    }

    @EventHandler
    fun chat(event: AsyncPlayerChatEvent) {
        if (event.player.hasPermission(PLUGIN_PERMISSION)) {
            val session = Simple2FA.instance.sessions[event.player.uniqueId]
            if (session != null && session.needsAuthentication()) {
                event.isCancelled = true
            }
        }
    }
}