@file:Suppress("DEPRECATION_ERROR")

package com.weberpackage.scribly.common.data.interfaces

import com.weberpackage.scribly.common.presentation.utils.UiTextArgList

sealed interface DataError : Error {
    enum class Network : DataError {
        UNKNOWN
    }

    enum class Local : DataError {
        UNKNOWN,
        JSON_ERROR,
    }

    data class Server(val data: String) : DataError
    data class UiText(val resId: Int, val args: UiTextArgList) : DataError
}