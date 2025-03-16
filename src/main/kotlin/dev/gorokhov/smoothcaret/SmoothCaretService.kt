package dev.gorokhov.smoothcaret

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.Service.Level

@Service(Level.APP)
class SmoothCaretService {
    val settings = SmoothCaretSettings()
}