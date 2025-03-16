package dev.gorokhov.smoothcaret

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.editor.markup.CustomHighlighterRenderer
import com.intellij.openapi.editor.markup.RangeHighlighter
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.Timer

class SmoothCaretRenderer(private val settings: SmoothCaretSettings) : CustomHighlighterRenderer {
    private var currentX: Double = 0.0
    private var currentY: Double = 0.0
    private var targetX: Double = 0.0
    private var targetY: Double = 0.0
    private var timer: Timer? = null

    override fun paint(editor: Editor, highlighter: RangeHighlighter, g: Graphics) {
        if (!settings.isEnabled) return

        val g2d = g as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        val point = editor.caretModel.visualPosition.let { editor.visualPositionToXY(it) }

        if (timer == null) {
            currentX = point.x.toDouble()
            currentY = point.y.toDouble()
            targetX = currentX
            targetY = currentY

            timer = Timer(16) { // ~60 FPS
                if (!editor.isDisposed) {
                    val dx = targetX - currentX
                    val dy = targetY - currentY
                    if (Math.abs(dx) > 0.01 || Math.abs(dy) > 0.01) {
                        currentX += dx * 0.2
                        currentY += dy * 0.2
                        editor.contentComponent.repaint()
                    }
                }
            }
            timer?.start()
        }

        targetX = point.x.toDouble()
        targetY = point.y.toDouble()

        val caretColor = editor.colorsScheme.getColor(EditorColors.CARET_COLOR)
            ?: editor.colorsScheme.defaultForeground

        g2d.color = caretColor

        val metrics = editor.contentComponent.getFontMetrics(editor.colorsScheme.getFont(null))
        val height = metrics.height

        when (settings.caretStyle) {
            SmoothCaretSettings.CaretStyle.BLOCK -> {
                g2d.fillRect(
                    currentX.toInt(),
                    currentY.toInt(),
                    settings.caretWidth,
                    height
                )
            }
            SmoothCaretSettings.CaretStyle.LINE -> {
                g2d.fillRect(
                    currentX.toInt(),
                    currentY.toInt(),
                    settings.caretWidth,
                    height
                )
            }
            SmoothCaretSettings.CaretStyle.UNDERSCORE -> {
                g2d.fillRect(
                    currentX.toInt(),
                    currentY.toInt() + height - 2,
                    settings.caretWidth * 2,
                    2
                )
            }
        }
    }
}