package com.weberpackage.scribly.widget.utils

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.weberpackage.scribly.widget.presentation.CalendarWidget
import com.weberpackage.scribly.widget.presentation.UpcomingWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun updateAllWidgets() {
        val manager = GlanceAppWidgetManager(context)
        
        manager.getGlanceIds(UpcomingWidget::class.java).forEach { id ->
            UpcomingWidget().update(context, id)
        }
        
        manager.getGlanceIds(CalendarWidget::class.java).forEach { id ->
            CalendarWidget().update(context, id)
        }
    }
}
