package com.sam_chordas.android.stockhawk.widget;
import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import com.sam_chordas.android.stockhawk.R;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetProvider  extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_collection);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                setRemoteAdapter(context, views);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }

        }

    @Override
    public void onReceive (Context context, Intent intent){
        super.onReceive(context, intent);

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter (Context context,@NonNull final RemoteViews views){
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, WidgetRemoteViewsService.class));
    }

}

