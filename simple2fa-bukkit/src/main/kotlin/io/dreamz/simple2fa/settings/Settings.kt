package io.dreamz.simple2fa.settings

import io.dreamz.simple2fa.Simple2FA
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Property<A>(val path: () -> String) : ReadOnlyProperty<Any, A> {
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any, property: KProperty<*>): A = Simple2FA.instance.config[path()] as A
}

object OtpSettings {
    val issuer by Property<String> { "otp_settings.issuer" }
    val digits by Property<Int> { "otp_settings.digits" }
}

object MongoSettings {
    val uri by Property<String> { "storage_settings.mongodb.uri" }
    val database by Property<String> { "storage_settings.mongodb.database" }
    val collection by Property<String> { "storage_settings.mongodb.collection" }
}

object FlatfileSettings {
    val location by Property<String> { "storage_settings.flatfile.location" }
}

object RedisSettings {
    val uri by Property<String> { "storage_settings.redis.uri" }
}

object StorageSettings {
    val engine by Property<String> { "storage_settings.engine" }
}
