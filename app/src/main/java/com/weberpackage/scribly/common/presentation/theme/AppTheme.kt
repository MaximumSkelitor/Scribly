package com.weberpackage.scribly.common.presentation.theme

enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        fun fromName(name: String): AppTheme {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: SYSTEM
        }
    }
}
