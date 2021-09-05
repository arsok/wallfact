package app.wallfact.service

import app.wallfact.fact.FactService
import app.wallfact.integration.unsplash.model.UnsplashImage
import app.wallfact.integration.unsplash.service.UnsplashService
import java.awt.Color
import java.awt.Color.white
import java.awt.Dimension
import java.awt.Font.TRUETYPE_FONT
import java.awt.Font.createFont
import java.awt.Graphics2D
import java.awt.RenderingHints.KEY_ANTIALIASING
import java.awt.RenderingHints.KEY_RENDERING
import java.awt.RenderingHints.VALUE_ANTIALIAS_ON
import java.awt.RenderingHints.VALUE_RENDER_QUALITY
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.GeneralPath
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.io.ByteArrayOutputStream
import java.lang.ClassLoader.getSystemClassLoader
import javax.imageio.ImageIO
import kotlin.random.Random
import org.apache.commons.text.WordUtils.wrap
import org.bson.BsonBinary
import org.bson.BsonDocument
import org.bson.BsonString
import org.bson.BsonTimestamp
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.slf4j.LoggerFactory


class ImageService(
    private val unsplashService: UnsplashService,
    private val factService: FactService,
    private val database: CoroutineDatabase
) {
    private val textColor = white
    private val bubbleColor = Color(99, 99, 102)

    private val roboto = createFont(TRUETYPE_FONT, getSystemClassLoader().getResourceAsStream("Roboto.ttf"))
    private val log = LoggerFactory.getLogger(this::javaClass.get())

    suspend fun getWallpaper(dimension: Dimension): ByteArray {
        val imagesCache = database.getCollection<BsonDocument>("images_cache")

        val prefix = Random.nextInt(0, 3).toString()
        val hash = prefix + dimension.height + dimension.width
        var image = imagesCache.findOne(UnsplashImage::hash eq hash)

        if (image == null) {
            val imageBson = unsplashService.getRandomWallpaper()
                .toCroppedImage(dimension)
                .renderFact()
                .toBsonDocument(hash)
            imagesCache.save(imageBson)

            log.info("Saved rendered image with hash {} to cache", hash)
            image = imagesCache.findOne(UnsplashImage::hash eq hash)!!
        } else log.info("Retrieved rendered image with hash {} from cache", hash)

        return image.getBinary("image").data
    }

    private fun ByteArray.toCroppedImage(dimension: Dimension): BufferedImage = with(dimension) {
        return if (isEmpty() || width < 240 || height < 240) ImageIO.read(inputStream())
        else crop(this)
    }

    private fun ByteArray.toBsonDocument(hash: String) = BsonDocument("image", BsonBinary(this))
        .append("hash", BsonString(hash))
        .append("created_at", BsonTimestamp(System.currentTimeMillis()))

    private fun ByteArray.crop(dimension: Dimension): BufferedImage {
        val bufferedImage = ImageIO.read(inputStream())

        return if (bufferedImage.width >= dimension.width && bufferedImage.height >= dimension.height) {
            bufferedImage.getSubimage(0, 0, dimension.width, dimension.height)
        } else bufferedImage
    }

    private suspend fun BufferedImage.renderFact(): ByteArray {
        val fact = factService.getRandomFact()
        log.info("Retrieved fact from db with text '{}'", fact)

        val image = BufferedImage(width, height, TYPE_INT_ARGB)

        val graphics2D = setupBaseGraphics(image, this)

        val textToWrite = wrap(fact, 40).split("\n")
        val lines = textToWrite.size + 1

        val lineHeight = graphics2D.fontMetrics.height

        val bubbleHeight: Int = lineHeight * lines
        val bubbleWidth: Int = (image.width * 0.85).toInt()

        val path = GeneralPath()
        val scale = maxOf(lineHeight / 20, 1)
        drawBubble(path, bubbleWidth.toFloat(), bubbleHeight.toFloat(), scale)

        val bubbleX = image.width / 2.0 - (bubbleWidth / 2)
        val bubbleY = image.height / 2.0 - (bubbleHeight / 2)

        graphics2D.fill(getMovedBubble(bubbleX, bubbleY, path))

        drawText(graphics2D, bubbleX, lineHeight, bubbleY, textToWrite)

        return image.toByteArray()
    }

    private fun setupBaseGraphics(newImage: BufferedImage, baseImage: BufferedImage) = newImage.createGraphics().apply {
        drawImage(baseImage, 0, 0, null)
        paint = bubbleColor
        setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON)
        setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY)
        font = roboto.deriveFont(newImage.width / 30f)
    }

    private fun drawBubble(path: GeneralPath, width: Float, height: Float, scale: Int) {
        path.moveTo(scale * 5f, scale * 10f)
        path.curveTo(scale * 5f, scale * 10f, scale * 7f, scale * 5f, scale * 0f, scale * 0f)
        path.curveTo(0f, 0f, scale * 12f, 0f, scale * 12f, scale * 5f)
        path.curveTo(scale * 12f, scale * 5f, scale * 12f, 0f, scale * 20f, 0f)
        path.lineTo(width - scale * 10, 0f)
        path.curveTo(width - scale * 10, 0f, width, 0f, width, scale * 10f)
        path.lineTo(width, height - scale * 10)
        path.curveTo(width, height - scale * 10, width, height, width - scale * 10, height)
        path.lineTo(scale * 15f, height)
        path.curveTo(scale * 15f, height, scale * 5f, height, scale * 5f, height - scale * 10)
        path.lineTo(scale * 5f, scale * 15f)
        path.closePath()
    }

    private fun getMovedBubble(bubbleX: Double, bubbleY: Double, path: GeneralPath): Shape {
        val moveTo = AffineTransform.getTranslateInstance(bubbleX, bubbleY)
        return path.createTransformedShape(moveTo)
    }

    private fun drawText(graphics: Graphics2D, bubbleX: Double, lineHeight: Int, bubbleY: Double, text: List<String>) {
        graphics.color = textColor
        val x = bubbleX + lineHeight
        var y = bubbleY + lineHeight * 1.25

        for (line in text) {
            graphics.drawString(line, x.toInt(), y.toInt())
            y += lineHeight
        }
    }

    private fun BufferedImage.toByteArray() = ByteArrayOutputStream().use {
        ImageIO.write(this, "png", it)
        it.toByteArray()
    }
}