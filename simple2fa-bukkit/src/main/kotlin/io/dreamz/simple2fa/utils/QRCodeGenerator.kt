package io.dreamz.simple2fa.utils

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import java.awt.image.BufferedImage

val qrCodeWriter = QRCodeWriter()

val encodeHints = hashMapOf(
        Pair(EncodeHintType.CHARACTER_SET, "UTF-8"),
        Pair(EncodeHintType.MARGIN, 0))

object QRCodeGenerator {
    @JvmStatic
    fun generate(str: String, width: Int, height: Int): BufferedImage {
        val bitMatrix = qrCodeWriter.encode(str, BarcodeFormat.QR_CODE, width, height, encodeHints)
        return MatrixToImageWriter.toBufferedImage(bitMatrix)
    }
}