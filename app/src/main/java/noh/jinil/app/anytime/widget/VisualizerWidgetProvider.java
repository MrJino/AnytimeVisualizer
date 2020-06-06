package noh.jinil.app.anytime.widget;

import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.music.VisualizerAnytime;
import noh.jinil.app.anytime.service.MediaPlaybackService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class VisualizerWidgetProvider extends AppWidgetProvider {
	private final String TAG = "VisualizerWidgetProvider";
	
	public static final String CMDAPPWIDGETUPDATE = "appwidgetupdate";
	private static final String BROADCAST_VIEW_SWITCH_ON = "com.rslearning.app.visualizer.widget.viewswitch.on";
	private static final String BROADCAST_VIEW_SWITCH_OFF = "com.rslearning.app.visualizer.widget.viewswitch.off";
	private static boolean bShowNext = false;
		
	@Override
	public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();
		Log.d(TAG, "onReceive()->"+action);		
		if(BROADCAST_VIEW_SWITCH_ON.equals(action)) {
			bShowNext = true;
			final ComponentName serviceName = new ComponentName(context, MediaPlaybackService.class);
			Intent updateIntent = new Intent(VisualizerAnytime.SHOW_FLOATING_VIEW);
			updateIntent.setClassName(serviceName.getPackageName(), serviceName.getClassName());
			context.startService(updateIntent);
		}
		else if(BROADCAST_VIEW_SWITCH_OFF.equals(action)) {
			bShowNext = true;
			final ComponentName serviceName = new ComponentName(context, MediaPlaybackService.class);
			Intent updateIntent = new Intent(VisualizerAnytime.HIDE_FLOATING_VIEW);
			updateIntent.setClassName(serviceName.getPackageName(), serviceName.getClassName());
			context.startService(updateIntent);
		}
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.d(TAG, "onUpdate()");
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
		linkButtons(context, views);
		pushUpdate(context, appWidgetIds, views);
		
		refreshWidget(context, appWidgetIds);
	}

	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
		Log.d(TAG, "onAppWidgetOptionsChanged("+appWidgetId+")");
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
	}	

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d(TAG, "onDeleted()");
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		Log.d(TAG, "onEnabled()");
	}

	@Override
	public void onDisabled(Context context) {
		Log.d(TAG, "onDisabled()");
		super.onDisabled(context);
	}

	private static VisualizerWidgetProvider sInstance;	
    public static synchronized VisualizerWidgetProvider getInstance() {
        if (sInstance == null) {
            sInstance = new VisualizerWidgetProvider();
        }
        return sInstance;
    }
    
	public void performUpdate(MediaPlaybackService service, int[] appWidgetIds) {
		Log.d(TAG, "performUpdate()");
		
		RemoteViews views = new RemoteViews(service.getPackageName(), R.layout.widget);
		
//		if(bShowNext) {
//        	views.showNext(R.id.viewflipper_onoff);
//        	bShowNext = false;
//        }
//		else {
//			if(service.isFloatingView()) {	
//				Log.e(TAG, "isFloatingView:"+service.isFloatingView());
//				views.setDisplayedChild(R.id.viewflipper_onoff, 1);
//			}
//			else {
//				views.setDisplayedChild(R.id.viewflipper_onoff, 0);
//			}
//		}
		
		if(service.isFloatingView()) {	
			views.setDisplayedChild(R.id.viewflipper_onoff, 1);
		}
		else {
			views.setDisplayedChild(R.id.viewflipper_onoff, 0);
		}
		
		linkButtons(service, views);
		pushUpdate(service, appWidgetIds, views);
	}
	
	private void linkButtons(Context context, RemoteViews views) {
        // Connect up various buttons and touch events
        Intent intent;
        PendingIntent pendingIntent;
        
        final ComponentName serviceName = new ComponentName(context, MediaPlaybackService.class);
                
        intent = new Intent(BROADCAST_VIEW_SWITCH_ON);
		pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		views.setOnClickPendingIntent(R.id.widget_visualizer_show, pendingIntent);
		
		intent = new Intent(BROADCAST_VIEW_SWITCH_OFF);
		pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		views.setOnClickPendingIntent(R.id.widget_visualizer_hide, pendingIntent);
    }
	
	private void pushUpdate(Context context, int[] appWidgetIds, RemoteViews views) {
        // Update specific list of appWidgetIds if given, otherwise default to all
        final AppWidgetManager gm = AppWidgetManager.getInstance(context);
        if (appWidgetIds != null) {
            gm.updateAppWidget(appWidgetIds, views);
        } else {
            gm.updateAppWidget(new ComponentName(context, this.getClass()), views);
        }
    }
	
	private void refreshWidget(Context context, int[] appWidgetIds) {
		// Send broadcast intent to any running MediaPlaybackService so it can
        // wrap around with an immediate update.
        Intent updateIntent = new Intent(MediaPlaybackService.SERVICECMD);
        updateIntent.putExtra(MediaPlaybackService.CMDNAME, CMDAPPWIDGETUPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        updateIntent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        context.sendBroadcast(updateIntent);
	}
}