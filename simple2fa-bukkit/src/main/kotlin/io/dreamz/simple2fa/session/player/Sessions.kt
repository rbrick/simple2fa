package io.dreamz.simple2fa.session.player

import io.dreamz.simple2fa.Simple2FA
import io.dreamz.simple2fa.session.Session
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.util.*

class PlayerSession(private val uuid: UUID,
                    private val inventory: Inventory,
                    private val location: Location) : Session {

    private var needsAuthentication = true
    private var authenticated = false

    override fun needsAuthentication(): Boolean = needsAuthentication
    override fun isAuthenticated(): Boolean = !needsAuthentication && authenticated
    override fun getInventorySnapshot(): Inventory = inventory
    override fun getLocationSnapshot(): Location = location
    override fun getPlayer(): Player = Bukkit.getPlayer(uuid)

    override fun authenticate(code: String?): Boolean {
        val totp = Simple2FA.instance.totp
        val sessionKey = Simple2FA.instance.storageEngine.getRawSecret(uuid)
        if (totp.verify(code!!, sessionKey!!, 2)) {
            this.authenticated = true
            this.needsAuthentication = false
            return true
        }
        return false
    }
}

object Sessions {
    @JvmStatic
    fun ofPlayer(player: Player) = PlayerSession(player.uniqueId, player.inventory, player.location)
}