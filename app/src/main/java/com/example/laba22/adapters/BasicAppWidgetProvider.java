package com.example.laba22.adapters;

import static com.example.laba22.data.Constants.APP_LINK_VARIABLE_KEY;
import static com.example.laba22.data.Constants.APP_PREFERENCE_FILE_KEY;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.laba22.UI.MainActivity;
import com.example.laba22.R;

public class BasicAppWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, MainActivity.class);
            SharedPreferences sPreferences = context.getSharedPreferences(APP_PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
            String defaultLink = sPreferences.getString(APP_LINK_VARIABLE_KEY, "");
            //Log.d("APP_DEBUGGER", "Widget was updated. Default link = " + defaultLink);
            intent.putExtra(APP_LINK_VARIABLE_KEY, defaultLink);

            Uri data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME));
            intent.setData(data);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    /* context = */ context,
                    /* requestCode = */ 0,
                    /* intent = */ intent,
                    /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.simple_widget_layout);
            views.setOnClickPendingIntent(R.id.widget_button, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}