package io.dreamz.simple2fa.listeners

import io.dreamz.simple2fa.PLUGIN_PERMISSION
import io.dreamz.simple2fa.PLUGIN_PREFIX
import io.dreamz.simple2fa.Simple2FA
import io.dreamz.simple2fa.conversation.CodePrompt
import io.dreamz.simple2fa.events.PlayerAuthenticatedEvent
import io.dreamz.simple2fa.map.QRCodeMapRenderer
import io.dreamz.simple2fa.session.UserSession
import io.dreamz.simple2fa.session.player.Sessions
import io.dreamz.simple2fa.settings.OtpSettings
import io.dreamz.simple2fa.storage.AsyncStorageEngine
import io.dreamz.simple2fa.utils.KeyGenerator
import io.dreamz.simple2fa.utils.OTPAuthUri
import io.dreamz.simple2fa.utils.QRCodeGenerator
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.conversations.ConversationFactory
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import java.awt.Image


object JoinListener : Listener {
    private val factory = ConversationFactory(Simple2FA.instance)
            .withPrefix { ChatColor.translateAlternateColorCodes('&', PLUGIN_PREFIX) + " " }
            .withModality(false)
            .withLocalEcho(false)

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        // the player needs to authenticate before hand.
        if (event.player.hasPermission(PLUGIN_PERMISSION)) {
            if (Simple2FA.instance.storageEngine is AsyncStorageEngine) {
                val fut = (Simple2FA.instance.storageEngine as AsyncStorageEngine).getStoredSessionAsync(event.player)
                fut.whenComplete { session, throwable ->
                    if (throwable != null) {
                        throwable.printStackTrace()
                    } else {
                        if (session != null && session is UserSession) {
                            if (session.isExpired || !session.isAuthenticated) {
                                this.newPlayerSession(event.player)
                            } else {
                                Simple2FA.instance.sessions[event.player.uniqueId] = session
                            }
                        } else {
                            this.newPlayerSession(event.player)
                        }
                    }
                }
            } else {
                val session = Simple2FA.instance.storageEngine.getStoredSession(event.player)
                if (session != null && session is UserSession) {
                    if (session.isExpired || !session.isAuthenticated) {
                        this.newPlayerSession(event.player)
                    } else {
                        Simple2FA.instance.sessions[event.player.uniqueId] = session
                    }
                } else {
                    this.newPlayerSession(event.player)
                }
            }
        }
    }


    @EventHandler
    fun onPlayerAuthenticated(event: PlayerAuthenticatedEvent) {
        if (event.session is UserSession) {
            event.player.inventory.contents = (event.session as UserSession).inventorySnapshot
            event.player.inventory.armorContents = (event.session as UserSession).armorSnapshot

            Simple2FA.instance.storageEngine.storeSession(event.player.spigot().rawAddress.hostName, event.player.uniqueId, event.session as UserSession)
        }
    }

    private fun newPlayerSession(player: Player) {
        val session = Sessions.ofPlayer(player)

        // store the session
        Simple2FA.instance.sessions[player.uniqueId] = session

        // clear their inventory
        player.inventory.clear()
        player.inventory.armorContents = null
        player.updateInventory()

        // immediately begin asking for the code
        factory.withFirstPrompt(CodePrompt())
                .withInitialSessionData(mapOf(Pair("uuid", player.uniqueId)))
                .buildConversation(player).begin()

        if (Simple2FA.instance.storageEngine is AsyncStorageEngine) {
            val future = (Simple2FA.instance.storageEngine as AsyncStorageEngine)
                    .hasSecretAsync(player.uniqueId)

            future.whenComplete { t, _ ->
                if (!t) {
                    this.giveMap(player)
                }
            }

        } else {
            if (!Simple2FA.instance.storageEngine.hasSecret(player.uniqueId)) {
                this.giveMap(player)
            }
        }
    }

    private fun giveMap(player: Player) {
        val view = Bukkit.createMap(player.world)
        val item = ItemStack(Material.MAP, 1, view.id)

        view.renderers.clear()
        view.addRenderer(QRCodeMapRenderer(this.genQR(player)))

        player.itemInHand = item
        player.sendMap(view)
    }

    private fun genQR(player: Player): Image {
        val sharedSecret = KeyGenerator.generate()
        // store the shared secret
        Simple2FA.instance.storageEngine.storeSecret(player.uniqueId, sharedSecret)

        return QRCodeGenerator.generate(
                OTPAuthUri("totp", player.name, mapOf(
                        Pair("secret", sharedSecret),
                        Pair("issuer", OtpSettings.issuer),
                        Pair("digits", OtpSettings.digits)
                )).toString(), 128, 128)

    }
}

object LeaveListener : Listener {
    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        // rip'n'dip
        Simple2FA.instance.sessions.remove(event.player.uniqueId)
    }
}