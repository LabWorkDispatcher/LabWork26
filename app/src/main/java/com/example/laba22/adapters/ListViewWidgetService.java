package com.example.laba22.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.laba22.data.Constants;
import com.example.laba22.R;

public class ListViewWidgetService extends RemoteViewsService {
    private final String[] itemList;
    public ListViewWidgetService() {
        this.itemList = Constants.APP_WIDGET_ITEM_LIST;
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(itemList, this.getApplicationContext(), intent);
    }

    private class ListRemoteViewsFactory implements RemoteViewsFactory {
        private String[] itemList;
        private Context context;

        private Intent intent;

        public ListRemoteViewsFactory(String[] itemList, Context applicationContext, Intent intent) {
            //super(applicationContext, intent);
            this.itemList = itemList;
            this.context = applicationContext;
            this.intent = intent;
        }


        @Override
        public int getCount() {
            return itemList.length;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.list_widget_item_layout);
            views.setTextViewText(R.id.mainText, itemList[i]);
            Intent clickIntent = new Intent();
            clickIntent.putExtra(ListAppWidgetProvider.ITEM_POSITION, i);
            views.setOnClickFillInIntent(R.id.mainText, clickIntent);
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public void onCreate() {
            Log.d("APP_DEBUGGER", "ListView Factory created.");
        }

        @Override
        public void onDataSetChanged() {
            Log.d("APP_DEBUGGER", "ListView Factory data set got changed.");
        }

        @Override
        public void onDestroy() {
            Log.d("APP_DEBUGGER", "ListView Factory destroyed.");
        }
    }
}
