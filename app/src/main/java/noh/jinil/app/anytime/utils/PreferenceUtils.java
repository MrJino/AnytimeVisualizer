package noh.jinil.app.anytime.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {
	public static final String CATEGORY_MUSIC = "music";
	public static final String KEY_BG_ALPHA = "music_background_alpha";
	public static final String KEY_VI_ALPHA = "music_visualizer_alpha";
	public static final String KEY_VI_COLOR = "music_visualizer_color";
	public static final String KEY_IGNORE_VIPOPUP = "ignore_popup_for_minivi";
	public static final String KEY_IGNORE_REMOTEAPP_POPUP = "ignore_popup_for_remoteapp";
	public static final String KEY_IGNORE_INITPOPUP = "ignore_popup_for_init";
	public static final String KEY_VI_SHOWSCREEN = "music_visualizer_showonthescreen";
	public static final String KEY_VISUALIZER_RATIO = "music_visualizer_ratio";
	public static final String KEY_NOTIFICATION_HELP = "music_notification_help_confirm";
	public static final String KEY_VI_WIDTH_RATIO = "music_visualizer_width";
	public static final String KEY_VI_HEIGHT_RATIO = "music_visualizer_height";
	public static final String KEY_VI_BARRATIO = "music_visualizer_barratio";
	public static final String KEY_VI_GRAVITY = "music_visualizer_gravity";
	public static final String KEY_VI_CLEANMODE = "music_visualizer_cleanmode";
	public static final String KEY_SEEK_SHOW = "music_seek_show";
	public static final String KEY_VI_STICK = "music_visualizer_stick";
	public static final String KEY_VI_FLOATING_ONOFF = "music_visualizer_floating_onoff";
	public static final String KEY_ALBUM_SIZE = "music_albumart_size";
	public static final String KEY_RC_AUTHORIZED = "deprecated";//"music_remotecontrol_authorized";
	public static final String KEY_MIC_USE = "deprecated"; //"music_mic_input_use";
	public static final String KEY_MIC_SENSITIVITY = "deprecated"; //"music_mic_sensitivity";
	public static final String KEY_BL_ALWAYSON = "music_backlight_alwayson";
	public static final String KEY_NOTI_HIDE = "music_notification_hide";
	public static final String KEY_RC_PACKAGENAME = "deprecated"; //"music_rc_packagename";
	public static final String KEY_VI_COLORSET_NUM = "music_visualizer_colorset_num";
	public static final String KEY_VI_COLORSET = "music_visualizer_colorset";
	public static final String KEY_VI_BOTTOMSET = "music_visualizer_bottomset";
	
	public static int loadIntegerValue(Context context, String key) {
		int value = 0;
		SharedPreferences prefs = context.getSharedPreferences(CATEGORY_MUSIC, Context.MODE_PRIVATE);		
		value = prefs.getInt(key, 0);
		return value;
	}
	
	public static int loadIntegerValue(Context context, String key, int defValue) {
		int value = 0;
		SharedPreferences prefs = context.getSharedPreferences(CATEGORY_MUSIC, Context.MODE_PRIVATE);		
		value = prefs.getInt(key, defValue);
		return value;
	}
	
	public static void saveIntegerValue(Context context, String key, int value) {
		SharedPreferences prefs = context.getSharedPreferences(CATEGORY_MUSIC, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public static float loadFloatValue(Context context, String key, float defValue) {
		float value = 0;
		SharedPreferences prefs = context.getSharedPreferences(CATEGORY_MUSIC, Context.MODE_PRIVATE);		
		value = prefs.getFloat(key, defValue);
		return value;
	}
	
	public static void saveFloatValue(Context context, String key, float value) {
		SharedPreferences prefs = context.getSharedPreferences(CATEGORY_MUSIC, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putFloat(key, value);
		editor.commit();
	}
	
	public static boolean loadBooleanValue(Context context, String key, boolean defValue) {
		boolean value = false;
		SharedPreferences prefs = context.getSharedPreferences(CATEGORY_MUSIC, Context.MODE_PRIVATE);		
		value = prefs.getBoolean(key, defValue);
		return value;
	}
	
	public static void saveBooleanValue(Context context, String key, boolean value) {
		SharedPreferences prefs = context.getSharedPreferences(CATEGORY_MUSIC, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static void saveStringValue(Context context, String key, String value)  {
		SharedPreferences prefs = context.getSharedPreferences(CATEGORY_MUSIC, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static String loadStringValue(Context context, String key) {
		SharedPreferences prefs = context.getSharedPreferences(CATEGORY_MUSIC, Context.MODE_PRIVATE);		
		return prefs.getString(key, "");
	}
}
