package eu.ezytarget.micopi.common.engine

import android.graphics.Canvas
import android.util.Log
import eu.ezytarget.matthew.CanvasSizeQuantifier
import eu.ezytarget.matthew.Color
import eu.ezytarget.matthew.Matthew
import eu.ezytarget.micopi.common.RandomNumberGenerator
import eu.ezytarget.micopi.common.engine.Calculator.Companion.TWO_PIF
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class TightDiskPainter(
    private val canvasSizeQuantifier: CanvasSizeQuantifier = CanvasSizeQuantifier(),
    private val calculator: Calculator = Calculator()
) {

    lateinit var matthew: Matthew

    var numberOfDisks = 48
    var lastDiskToImageRatio = 1f / 54f
    var centerRelativeXPosition = 0.5f
    var centerRelativeYPosition = 0.5f
    var twirlRadiusToImageRatio = 1f / 6f
    var twirlXRatio = 1f
    var twirlYRatio = 1f

    fun configureRandomly(randomNumberGenerator: RandomNumberGenerator = RandomNumberGenerator()) {
        numberOfDisks = randomNumberGenerator.int(
            from = MIN_RANDOM_NUMBER_OF_DISKS,
            until = MAX_RANDOM_NUMBER_OF_DISKS
        )

        centerRelativeXPosition = randomNumberGenerator.float(
            from = MIN_RELATIVE_POSITION,
            until = MAX_RELATIVE_POSITION
        )
        centerRelativeYPosition = randomNumberGenerator.float(
            from = MIN_RELATIVE_POSITION,
            until = MAX_RELATIVE_POSITION
        )
        twirlRadiusToImageRatio = randomNumberGenerator.float(
            from = MIN_TWIRL_RADIUS_TO_IMAGE_RATIO,
            until = MAX_TWIRL_RADIUS_TO_IMAGE_RATIO
        )
        twirlXRatio = randomNumberGenerator.float(
            from = MIN_TWIRL_RATIO,
            until = MAX_TWIRL_RATIO
        )
        twirlYRatio = -twirlXRatio
    }

    fun paint(canvas: Canvas, oneColor: Color? = null) {
        if (numberOfDisks !in numberOfDisksRange) {
            Log.e(tag, "paint(): numberOfDisks is out of range, $numberOfDisks.")
            return
        }

        val imageSize = canvasSizeQuantifier.valueForCanvas(canvas)

        val firstDiskRadius = calculator.circleCoveringSquare(imageSize)
        val lastDiskRadius = imageSize * lastDiskToImageRatio
        val stackCenterX = imageSize * centerRelativeXPosition
        val stackCenterY = imageSize * centerRelativeYPosition
        val maxDistanceOffset = imageSize * twirlRadiusToImageRatio

        for (diskCounter in 0..numberOfDisks) {
            val color: Color = oneColor ?: matthew.colorAtModuloIndex(diskCounter)
            val radius = calculator.lerp(
                firstDiskRadius,
                lastDiskRadius,
                diskCounter,
                numberOfDisks
            )
            val normalizedCounter = diskCounter.toFloat() / numberOfDisks.toFloat()
            val twirlXOffset = sin(twirlXRatio * normalizedCounter * TWO_PIF) * maxDistanceOffset
            val twirlYOffset = cos(twirlYRatio * normalizedCounter * TWO_PIF) * maxDistanceOffset
            val diskX = stackCenterX + twirlXOffset
            val diskY = stackCenterY + twirlYOffset

            matthew.paintCircularShapeWithRadius(
                diskX,
                diskY,
                radius,
                color,
                canvas
            )
        }
    }

    companion object {
        const val MIN_NUMBER_OF_DISKS = 1
        const val MAX_NUMBER_OF_DISKS = 1000
        const val MIN_RANDOM_NUMBER_OF_DISKS = 48
        const val MAX_RANDOM_NUMBER_OF_DISKS = 64
        const val MIN_RELATIVE_POSITION = 0.25f
        const val MAX_RELATIVE_POSITION = 0.75f
        const val MIN_TWIRL_RADIUS_TO_IMAGE_RATIO = 1f / 7f
        const val MAX_TWIRL_RADIUS_TO_IMAGE_RATIO = 1f / 4f
        const val MIN_TWIRL_RATIO = -1f
        const val MAX_TWIRL_RATIO = 1f
        val tag = TightDiskPainter::class.java.simpleName
        private val numberOfDisksRange = MIN_NUMBER_OF_DISKS..MAX_NUMBER_OF_DISKS
    }
}