package io.dreamz.simple2fa.listeners

import io.dreamz.simple2fa.PLUGIN_PERMISSION
import io.dreamz.simple2fa.PLUGIN_PREFIX
import io.dreamz.simple2fa.Simple2FA
import io.dreamz.simple2fa.conversation.CodePrompt
import io.dreamz.simple2fa.map.QRCodeMapRenderer
import io.dreamz.simple2fa.session.player.Sessions
import io.dreamz.simple2fa.utils.KeyGenerator
import io.dreamz.simple2fa.utils.OTPAuthUri
import io.dreamz.simple2fa.utils.QRCodeGenerator
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.conversations.ConversationFactory
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack


object JoinListener : Listener {
    private val factory = ConversationFactory(Simple2FA.instance)
            .withPrefix { ChatColor.translateAlternateColorCodes('&', PLUGIN_PREFIX) + " " }
            .withModality(false)
            .withLocalEcho(false)

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        // the player needs to authenticate before hand.
        if (event.player.hasPermission(PLUGIN_PERMISSION)) {
            val session = Sessions.ofPlayer(event.player)

            // store the session
            Simple2FA.instance.sessions[event.player.uniqueId] = session

            // clear their inventory
            event.player.inventory.clear()
            event.player.updateInventory()

            if (Simple2FA.instance.storageEngine.hasSecret(event.player.uniqueId)) {
                // immediately begin asking for the code
                factory.withFirstPrompt(CodePrompt())
                        .withInitialSessionData(mapOf(Pair("uuid", event.player.uniqueId)))
                        .buildConversation(event.player).begin()
            } else {

                event.player.itemInHand = ItemStack(Material.MAP)
                val view = Bukkit.createMap(event.player.world)

                view.renderers.clear()

                val sharedSecret = KeyGenerator.generate()

                Simple2FA.instance.storageEngine.storeSecret(event.player.uniqueId, sharedSecret)

                val qrCode = QRCodeGenerator.generate(
                        OTPAuthUri("totp", event.player.name, mapOf(
                                Pair("secret", sharedSecret),
                                Pair("issuer", "DreamZ")
                        )).toString(), 128, 128)

                view.addRenderer(QRCodeMapRenderer(qrCode))

                event.player.itemInHand.durability = view.id


                event.player.sendMap(view)
            }
        }
    }
}

object LeaveListener : Listener {
    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        // rip'n'dip
        Simple2FA.instance.sessions.remove(event.player.uniqueId)
    }
}