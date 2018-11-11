package io.dreamz.simple2fa.map

import org.bukkit.entity.Player
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import java.awt.Image

class QRCodeMapRenderer(private val renderedImage: Image) : MapRenderer() {
    var hasRendered = false
    override fun render(view: MapView?, canvas: MapCanvas?, player: Player?) = when {
        !hasRendered -> {
            canvas?.drawImage(0, 0, this.renderedImage)
            hasRendered = false
        }
        else -> Unit
    }

}
