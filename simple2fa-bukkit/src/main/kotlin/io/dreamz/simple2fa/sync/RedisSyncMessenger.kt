package io.dreamz.simple2fa.sync

import io.dreamz.simple2fa.Simple2FA
import io.dreamz.simple2fa.session.Session
import org.bukkit.Bukkit
import redis.clients.jedis.JedisPool
import java.util.*

class RedisSyncMessenger(private val host: String) : SyncMessenger {

    private val pool: JedisPool = JedisPool(host)

    override fun onSync(uniqueId: UUID?, session: Session?) {
    }

    override fun sync(uniqueId: UUID?, session: Session?) {
        Bukkit.getScheduler().runTaskAsynchronously(Simple2FA.instance) {
            pool.resource.use { it.publish("", "") }
        }
    }
}