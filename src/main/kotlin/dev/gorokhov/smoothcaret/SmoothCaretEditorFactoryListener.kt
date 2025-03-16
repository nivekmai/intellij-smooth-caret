package dev.gorokhov.smoothcaret

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea

class SmoothCaretEditorFactoryListener : EditorFactoryListener {
    private val settings = service<SmoothCaretService>().settings

    override fun editorCreated(event: EditorFactoryEvent) {
        setupSmoothCaret(event.editor)
    }

    private fun setupSmoothCaret(editor: Editor) {
        if (!settings.isEnabled) return

        editor.settings.isBlinkCaret = false
        if (settings.replaceDefaultCaret) {
            editor.settings.isCaretRowShown = false
        }

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
}