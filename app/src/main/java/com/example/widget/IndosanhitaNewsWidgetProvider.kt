package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IndosanhitaNewsWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (widgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, widgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_WIDGET) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, IndosanhitaNewsWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (widgetId in allWidgetIds) {
                updateAppWidget(context, appWidgetManager, widgetId, isRefresh = true)
            }
        }
    }

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.example.widget.ACTION_REFRESH_WIDGET"

        private val headlines = listOf(
            Triple(
                "Global Clean Energy Pact Signed by 85 Nations in New Delhi",
                "TOP STORIES",
                "INDOSANHITA Wire Service • Just now"
            ),
            Triple(
                "ISRO & NASA Advance Joint NISAR Radar Processing Architecture",
                "INDIA",
                "ISRO Science Desk • 25m ago"
            ),
            Triple(
                "Next-Gen On-Device Multimodal AI Announced with Ultra-Low Latency",
                "TECHNOLOGY",
                "Global Tech Review • 1h ago"
            )
        )
        private var headlineIndex = 0

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            isRefresh: Boolean = false
        ) {
            if (isRefresh) {
                headlineIndex = (headlineIndex + 1) % headlines.size
            }
            val (headline, category, source) = headlines[headlineIndex]

            val views = RemoteViews(context.packageName, R.layout.news_widget_layout).apply {
                setTextViewText(R.id.widget_headline, headline)
                setTextViewText(R.id.widget_category, category)
                setTextViewText(R.id.widget_source, source)

                // Tap story to open MainActivity directly
                val clickIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("EXTRA_WIDGET_STORY_CLICK", true)
                }
                val clickPendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                setOnClickPendingIntent(R.id.widget_headline, clickPendingIntent)
                setOnClickPendingIntent(R.id.widget_root, clickPendingIntent)

                // Refresh button click
                val refreshIntent = Intent(context, IndosanhitaNewsWidgetProvider::class.java).apply {
                    action = ACTION_REFRESH_WIDGET
                }
                val refreshPendingIntent = PendingIntent.getBroadcast(
                    context,
                    1,
                    refreshIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                setOnClickPendingIntent(R.id.widget_btn_refresh, refreshPendingIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
