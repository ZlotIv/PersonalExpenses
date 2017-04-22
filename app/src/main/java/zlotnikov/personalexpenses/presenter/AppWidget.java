package zlotnikov.personalexpenses.presenter;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import zlotnikov.personalexpenses.R;
import zlotnikov.personalexpenses.model.Dialogs.DeleteLastItemWidgetActivity;
import zlotnikov.personalexpenses.model.Dialogs.NewEntryWidgetActivity;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // проходим по списку виджетов
        for (int appWidgetId : appWidgetIds) {
            // получаем иерархию View
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

            // интент и PendingIntent для кнопки "нового элемента(плюс)"
            Intent addItemIntent = new Intent(context, NewEntryWidgetActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            PendingIntent addItemPendingIntent = PendingIntent.getActivity(context, 0, addItemIntent, 0);
            // интент и PendingIntent для кнопки "удалить(минус)"
            Intent deleteLastItemIntent = new Intent(context, DeleteLastItemWidgetActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            PendingIntent deleteLastItemPendingIntent = PendingIntent.getActivity(context, 0, deleteLastItemIntent, 0);
            // установка слушателей
            views.setOnClickPendingIntent(R.id.plus_widget, addItemPendingIntent);
            views.setOnClickPendingIntent(R.id.minus_widget, deleteLastItemPendingIntent);
            // обновление виджетов
            appWidgetManager.updateAppWidget(appWidgetIds, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

