package dev.gorokhov.smoothcaret

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import java.awt.Color

class SmoothCaretEditorFactoryListener : EditorFactoryListener {
    private val settings = service<SmoothCaretService>().settings

    override fun editorCreated(event: EditorFactoryEvent) {
        setupSmoothCaret(event.editor)
    }

    private fun setupSmoothCaret(editor: Editor) {
        if (!settings.isEnabled) return

        editor.settings.apply {
            isBlinkCaret = false
            isBlockCursor = false
            isRightMarginShown = false
            lineCursorWidth = 0
            if (settings.replaceDefaultCaret) {
                isCaretRowShown = false
            }
        }

        editor.colorsScheme.setColor(EditorColors.CARET_COLOR, Color(0, 0, 0, 0))

        val markupModel = editor.markupModel
        val highlighter = markupModel.addRangeHighlighter(
            0,
            0,
            HighlighterLayer.LAST + 1,
            null,
            HighlighterTargetArea.EXACT_RANGE
        )

        highlighter.customRenderer = SmoothCaretRenderer(settings)
    }

    override fun editorReleased(event: EditorFactoryEvent) {
        // Restore default caret color when editor is released
        event.editor.colorsScheme.setColor(EditorColors.CARET_COLOR, null)
    }
}