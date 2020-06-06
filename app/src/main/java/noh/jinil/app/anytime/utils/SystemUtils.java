package noh.jinil.app.anytime.utils;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.view.Surface;

public class SystemUtils {
	public static boolean isAuthNotiService(Context context, String classname) {
		String authSystemNotiService = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
		if(authSystemNotiService != null && authSystemNotiService.contains(classname)) {
			return true;
		}
        return false;
    }
	
	public static boolean isPortraitMode(Activity activity) {
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation() ;
		 return (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180);
	}
}
