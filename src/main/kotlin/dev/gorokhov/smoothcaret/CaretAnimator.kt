package dev.gorokhov.smoothcaret

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import java.awt.Point
import javax.swing.Timer

class CaretAnimator(private val editor: Editor, private val onUpdate: () -> Unit) : Disposable {
    private var currentX: Float = 0f
    private var currentY: Float = 0f
    private var targetX: Float = 0f
    private var targetY: Float = 0f
    private val animationSpeed = 0.3f
    private val timer: Timer

    init {
        timer = Timer(16) { // ~60 FPS
            updatePosition()
        }
        timer.isRepeats = true
        timer.start()
    }

    fun updateTarget(point: Point) {
        if (currentX == 0f && currentY == 0f) {
            currentX = point.x.toFloat()
            currentY = point.y.toFloat()
        }
        targetX = point.x.toFloat()
        targetY = point.y.toFloat()
    }

    private fun updatePosition() {
        ApplicationManager.getApplication().invokeLater {
            if (!editor.isDisposed) {
                currentX += (targetX - currentX) * animationSpeed
                currentY += (targetY - currentY) * animationSpeed
                onUpdate()
            }
        }
    }

    fun getCurrentPosition(): Pair<Float, Float> = Pair(currentX, currentY)

    override fun dispose() {
        timer.stop()
    }
}