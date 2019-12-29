package io.dreamz.simple2fa.session.player

import io.dreamz.simple2fa.Simple2FA
import io.dreamz.simple2fa.session.Session
import io.dreamz.simple2fa.session.UserSession
import io.dreamz.simple2fa.storage.AsyncStorageEngine
import io.dreamz.simple2fa.utils.Base32String
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.function.Consumer

class PlayerSession(private val uuid: UUID,
                    private val inventory: Array<ItemStack>,
                    private val armor: Array<ItemStack>,
                    private val location: Location) : UserSession {

    private var authenticated = false

    override fun needsAuthentication(): Boolean = !authenticated
    override fun isAuthenticated(): Boolean = authenticated
    override fun getInventorySnapshot(): Array<out ItemStack>? = inventory
    override fun getArmorSnapshot(): Array<out ItemStack>? = armor
    override fun getLocationSnapshot(): Location = location
    override fun getPlayer(): Player = Bukkit.getPlayer(uuid)

    fun authenticate(code: String?): Boolean {
        val totp = Simple2FA.instance.totp
        val sessionKey = Simple2FA.instance.storageEngine.getRawSecret(uuid)
        if (totp.verify(code!!, sessionKey!!, 2)) {
            this.authenticated = true
        }
        return this.authenticated
    }

    override fun authenticate(code: String?, callback: Consumer<Boolean>?) {
        when {
            (Simple2FA.instance.storageEngine is AsyncStorageEngine) -> {
                val asyncStore = Simple2FA.instance.storageEngine as AsyncStorageEngine
                val future = asyncStore.getSecretAsync(uuid)

                if (future.isDone) {
                    val key = Base32String.decode(future.get())
                    this.verify(key, code!!, callback)
                } else {
                    future.whenComplete { key, error ->
                        if (error != null) {
                            error.printStackTrace()
                        } else {
                            this.verify(Base32String.decode(key), code!!, callback)
                        }
                    }
                }
            }
            else -> {
                callback?.accept(authenticate(code))
            }
        }
    }

    private fun verify(key: ByteArray, code: String, cb: Consumer<Boolean>?) {
        val totp = Simple2FA.instance.totp
        if (totp.verify(code, key, 2)) {
            this.authenticated = true
        }

        cb?.accept(this.authenticated)
    }
}

class RemoteSession(private val authStatus: Boolean) : Session {
    override fun needsAuthentication(): Boolean {
        return !authStatus
    }

    override fun isAuthenticated(): Boolean {
        return authStatus
    }
}

object Sessions {
    @JvmStatic
    fun ofPlayer(player: Player) = PlayerSession(player.uniqueId, player.inventory.contents.clone(), player.inventory.armorContents.clone(), player.location.clone())
}