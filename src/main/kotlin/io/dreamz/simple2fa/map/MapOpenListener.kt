package io.dreamz.simple2fa.map

import io.dreamz.simple2fa.utils.QRCodeGenerator
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.MapInitializeEvent

class MapOpenListener : Listener {

    @EventHandler
    fun onMapRender(event: MapInitializeEvent) {
        event.map.addRenderer(QRCodeMapRenderer(QRCodeGenerator.generate("Hello, World", 128, 128)))
    }

}