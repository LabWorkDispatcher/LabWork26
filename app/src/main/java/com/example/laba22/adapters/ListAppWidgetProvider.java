package com.example.laba22.adapters;

import static com.example.laba22.data.Constants.APP_LINK_VARIABLE_KEY;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.laba22.R;
import com.example.laba22.UI.MainActivity;
import com.example.laba22.data.Constants;

public class ListAppWidgetProvider extends AppWidgetProvider {
    final static String ACTION_ON_CLICK = "ru.startandroid.develop.p1211listwidget.itemonclick";
    final static String ITEM_POSITION = "item_position";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews rv = new RemoteViews(context.getPackageName(),
                R.layout.list_widget_layout);
        setUpdateTV(rv, context, appWidgetId);
        setList(rv, context, appWidgetId);
        setListClick(rv, context, appWidgetId);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    void setUpdateTV(RemoteViews rv, Context context, int appWidgetId) {
        Intent updIntent = new Intent(context, ListAppWidgetProvider.class);
        updIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                new int[] { appWidgetId });
        PendingIntent updPIntent = PendingIntent.getBroadcast(context,
                appWidgetId, updIntent, PendingIntent.FLAG_IMMUTABLE);
        rv.setOnClickPendingIntent(R.id.widget_edittext, updPIntent);
    }
    void setList(RemoteViews rv, Context context, int appWidgetId) {
        Intent adapter = new Intent(context, ListViewWidgetService.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        Uri data = Uri.parse(adapter.toUri(Intent.URI_INTENT_SCHEME));
        adapter.setData(data);
        rv.setRemoteAdapter(R.id.widget_list_view, adapter);
    }

    void setListClick(RemoteViews rv, Context context, int appWidgetId) {
        Intent listClickIntent = new Intent(context, ListAppWidgetProvider.class);
        listClickIntent.setAction(ACTION_ON_CLICK);
        PendingIntent listClickPIntent = PendingIntent.getBroadcast(context, 0,
                listClickIntent, PendingIntent.FLAG_MUTABLE);
        rv.setPendingIntentTemplate(R.id.widget_list_view, listClickPIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equalsIgnoreCase(ACTION_ON_CLICK)) {
            Log.d("APP_DEBUGGER", "Text view inside of ListView Widget got clicked.");
            int itemPos = -1;
            Bundle b = intent.getExtras();
            if (b != null) {
                itemPos = b.getInt(ITEM_POSITION);
            } else {
                Log.d("APP_DEBUGGER", "But its ID is -1.");
            }

            if (itemPos != -1) {
                Log.d("APP_DEBUGGER", "Clicked on item " + itemPos);
                Intent appLaunchIntent = new Intent(context, MainActivity.class);
                appLaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                appLaunchIntent.putExtra(APP_LINK_VARIABLE_KEY, Constants.APP_WIDGET_ITEM_LIST[itemPos]);
                context.startActivity(appLaunchIntent);
            }
        }
    }
}