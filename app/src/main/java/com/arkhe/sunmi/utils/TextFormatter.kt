package com.arkhe.sunmi.utils

data class FontSizeConfig(
    val fontSize: Int,
    val maxLength: Int
)

class TextFormatter {
    private val fontSizeConfigs = mapOf(
        16 to FontSizeConfig(16, 45),
        20 to FontSizeConfig(20, 40),
        24 to FontSizeConfig(24, 35),
        32 to FontSizeConfig(32, 30)
    )

    fun formatText(textStart: String, textEnd: String, fontSize: Int): String {
        val config = fontSizeConfigs[fontSize]
            ?: throw IllegalArgumentException("Font size $fontSize not defined. use: ${fontSizeConfigs.keys}")

        val maxLength = config.maxLength
        val totalTextLength = textStart.length + textEnd.length

        val spaceNeeded = maxLength - totalTextLength

        val spaceCenter = if (spaceNeeded < 1) {
            " "
        } else {
            " ".repeat(spaceNeeded)
        }

        return "$textStart$spaceCenter$textEnd"
    }
}