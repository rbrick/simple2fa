package io.dreamz.simple2fa.listeners

import io.dreamz.simple2fa.Simple2FA
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent

object PreventionListeners : Listener {
    @EventHandler
    fun move(event: PlayerMoveEvent) {
        val session = Simple2FA.instance.sessions[event.player.uniqueId]
        if (session?.needsAuthentication()!!) {
            if (event.to.blockX != event.from.blockX ||
                    event.to.blockY != event.from.blockY ||
                    event.to.blockZ != event.from.blockX) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun interact(event: PlayerInteractEvent) {
        val session = Simple2FA.instance.sessions[event.player.uniqueId]
        if (session?.needsAuthentication()!!) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun attack(event: EntityDamageByEntityEvent) {
        if (event.damager is Player) {
            val session = Simple2FA.instance.sessions[(event.damager as Player).uniqueId]
            if (session?.needsAuthentication()!!) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun damaged(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val session = Simple2FA.instance.sessions[(event.entity as Player).uniqueId]
            if (session?.needsAuthentication()!!) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun inventoryClick(event: InventoryClickEvent) {
        val session = Simple2FA.instance.sessions[event.whoClicked.uniqueId]
        if (session?.needsAuthentication()!!) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun itemDrop(event: PlayerDropItemEvent) {
        val session = Simple2FA.instance.sessions[event.player.uniqueId]
        if (session?.needsAuthentication()!!) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun hunger(event: FoodLevelChangeEvent) {
        val session = Simple2FA.instance.sessions[event.entity.uniqueId]
        if (session?.needsAuthentication()!!) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun command(event: PlayerCommandPreprocessEvent) {
        val session = Simple2FA.instance.sessions[event.player.uniqueId]
        if (session?.needsAuthentication()!!) {
            event.isCancelled = true
        }
    }
}