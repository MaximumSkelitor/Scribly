package com.weberpackage.scribly.common.data.interfaces

fun interface Mapper<in From, out To> {
    fun map(from: From): To
}