package com.weberpackage.scribly.core.calendar

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import com.weberpackage.scribly.R
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarHelper @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {

    fun createCalendarIntent(
        name: String,
        price: Double,
        date: Long,
        notes: String?
    ): Intent {
        val priceText = context.getString(R.string.price_format, String.format(Locale.US, "%.2f", price))
        val title = context.getString(R.string.calendar_event_title, name)
        val description = context.getString(R.string.calendar_event_description, name, priceText, notes ?: "")
        
        return Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.Events.DESCRIPTION, description)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, date + 30 * 60 * 1000) // 30 mins
            putExtra(CalendarContract.Events.ALL_DAY, false)
            putExtra(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }
    }
}
