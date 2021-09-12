package app.wallfact.service

import app.wallfact.fact.FactService
import app.wallfact.integration.unsplash.model.UnsplashImage
import app.wallfact.integration.unsplash.service.UnsplashService
import java.awt.BasicStroke
import java.awt.BasicStroke.CAP_ROUND
import java.awt.BasicStroke.JOIN_ROUND
import java.awt.Color
import java.awt.Color.white
import java.awt.Dimension
import java.awt.Font.TRUETYPE_FONT
import java.awt.Font.createFont
import java.awt.Graphics2D
import java.awt.RenderingHints.KEY_ANTIALIASING
import java.awt.RenderingHints.KEY_FRACTIONALMETRICS
import java.awt.RenderingHints.KEY_RENDERING
import java.awt.RenderingHints.KEY_TEXT_ANTIALIASING
import java.awt.RenderingHints.VALUE_ANTIALIAS_ON
import java.awt.RenderingHints.VALUE_FRACTIONALMETRICS_ON
import java.awt.RenderingHints.VALUE_RENDER_QUALITY
import java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.GeneralPath
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.io.ByteArrayOutputStream
import java.lang.ClassLoader.getSystemClassLoader
import javax.imageio.ImageIO
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.text.WordUtils.wrap
import org.bson.BsonBinary
import org.bson.BsonDateTime
import org.bson.BsonDocument
import org.bson.BsonString
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class ImageService(
    private val unsplashService: UnsplashService,
    private val factService: FactService,
    private val database: CoroutineDatabase
) {
    private val textColor = white
    private val bubbleColor = Color(66, 66, 66)
    private val roboto = createFont(TRUETYPE_FONT, getSystemClassLoader().getResourceAsStream("Roboto.ttf"))

    suspend fun getWallpaper(dimension: Dimension): ByteArray = withContext(Default) {
        val imagesCache = database.getCollection<BsonDocument>("images_cache")

        val hash = Random.nextInt(0, 3).toString() + dimension.height + dimension.width
        var image = imagesCache.findOne(UnsplashImage::hash eq hash)

        if (image == null) {
            image = getBsonWithFact(dimension, hash)

            launch(IO) {
                imagesCache.insertOne(image)
            }
        }

        image.getBinary("image").data
    }

    private suspend fun getBsonWithFact(dimension: Dimension, hash: String): BsonDocument = withContext(Default) {
        val wallpaper = async { unsplashService.getRandomWallpaper().toCroppedImage(dimension) }
        val fact = async { factService.getRandomFact() }

        wallpaper.await()
            .renderFact(fact.await())
            .toBsonDocument(hash)
    }

    private fun ByteArray.toCroppedImage(dimension: Dimension): BufferedImage {
        if (dimension.width !in 240..3000 || dimension.height !in 240..3000) {
            dimension.setSize(1080, 1920)
        }

        return ImageIO.read(inputStream()).getSubimage(0, 0, dimension.width, dimension.height)
    }

    private fun BufferedImage.renderFact(fact: String): ByteArray {
        val image = BufferedImage(width, height, TYPE_INT_ARGB)

        val graphics2D = setupBaseGraphics(image, this)
        val lineHeight = graphics2D.fontMetrics.height

        val charWidth = (0.66 * graphics2D.fontMetrics.charWidth('W')).roundToInt()
        val bubbleWidth: Int = (image.width * 0.8).toInt()

        val textToWrite = wrap(fact, bubbleWidth / charWidth).split("\n")
        val lines = textToWrite.size + 1
        val bubbleHeight: Int = lineHeight * lines

        val path = GeneralPath()
        val scale = maxOf(lineHeight / 20, 1)
        drawBubble(path, bubbleWidth.toFloat(), bubbleHeight.toFloat(), scale)

        val bubbleX = image.width / 2.0 - (bubbleWidth / 2)
        val bubbleY = image.height / 2.0 - (bubbleHeight / 2)

        val movedBubbleShape = getMovedBubbleShape(bubbleX, bubbleY, path)
        graphics2D.fill(movedBubbleShape)
        graphics2D.drawText(bubbleX, lineHeight, bubbleY, textToWrite)
        graphics2D.draw(Area(BasicStroke(scale / 2f, CAP_ROUND, JOIN_ROUND).createStrokedShape(movedBubbleShape)))

        return image.toByteArray()
    }

    private fun setupBaseGraphics(newImage: BufferedImage, baseImage: BufferedImage) = newImage.createGraphics().apply {
        drawImage(baseImage, 0, 0, null)
        paint = bubbleColor
        setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON)
        setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON)
        setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY)
        setRenderingHint(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON)
        font = roboto.deriveFont(newImage.width / 31f)
    }

    private fun drawBubble(path: GeneralPath, width: Float, height: Float, scale: Int) = with(path) {
        moveTo(scale * 5f, scale * 10f)
        curveTo(scale * 5f, scale * 10f, scale * 7f, scale * 5f, scale * 0f, scale * 0f)
        curveTo(0f, 0f, scale * 12f, 0f, scale * 12f, scale * 5f)
        curveTo(scale * 12f, scale * 5f, scale * 12f, 0f, scale * 20f, 0f)
        lineTo(width - scale * 10, 0f)
        curveTo(width - scale * 10, 0f, width, 0f, width, scale * 10f)
        lineTo(width, height - scale * 10)
        curveTo(width, height - scale * 10, width, height, width - scale * 10, height)
        lineTo(scale * 15f, height)
        curveTo(scale * 15f, height, scale * 5f, height, scale * 5f, height - scale * 10)
        lineTo(scale * 5f, scale * 15f)
        closePath()
    }

    private fun getMovedBubbleShape(bubbleX: Double, bubbleY: Double, path: GeneralPath): Shape {
        val moveTo = AffineTransform.getTranslateInstance(bubbleX, bubbleY)
        return path.createTransformedShape(moveTo)
    }

    private fun Graphics2D.drawText(bubbleX: Double, lineHeight: Int, bubbleY: Double, text: List<String>) {
        color = textColor
        val x = bubbleX + lineHeight
        var y = bubbleY + lineHeight * 1.25

        for (line in text) {
            drawString(line, x.toInt(), y.toInt())
            y += lineHeight
        }
    }

    private fun BufferedImage.toByteArray() = ByteArrayOutputStream().use {
        ImageIO.write(this, "png", it)
        it.toByteArray()
    }

    private fun ByteArray.toBsonDocument(hash: String): BsonDocument =
        BsonDocument("image", BsonBinary(this@toBsonDocument))
            .append("hash", BsonString(hash))
            .append("createdAt", BsonDateTime(System.currentTimeMillis()))
}