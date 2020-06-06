package noh.jinil.app.anytime.service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RemoteController;
import android.media.RemoteController.MetadataEditor;
import android.os.Binder;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.view.KeyEvent;
import android.widget.Toast;

import noh.jinil.app.anytime.R;

public class RemoteControlService extends NotificationListenerService implements RemoteController.OnClientUpdateListener {
	
	public static final String BIND_RC_CONTROL_SERVICE = "noh.jinil.app.anytime.BIND_RC_CONTROL_SERVICE";
	
	//dimensions in pixels for artwork
	private static final int BITMAP_HEIGHT = 1024;
	private static final int BITMAP_WIDTH = 1024;

	//Binder for our service.
	private IBinder mBinder = new RCBinder();
	
	private RemoteController mRemoteController;
	private Context mContext;
	private boolean mEnabled = false;;
	
	//external callback provided by user.
	private RemoteController.OnClientUpdateListener mExternalClientUpdateListener;
	
	@Override
	public IBinder onBind(Intent intent) {
		if(intent.getAction().equals(BIND_RC_CONTROL_SERVICE)) {
			return mBinder;
		} else {
			return super.onBind(intent);
		}
	}
	
	@Override
	public void onNotificationPosted(StatusBarNotification notification) {
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification notification) {
	}
	
	@Override
	public void onCreate() {
		//saving the context for further reuse
		mContext = getApplicationContext();
		mRemoteController = new RemoteController(mContext, this);
	}
	
	@Override
	public void onDestroy() {
		setRemoteControllerDisabled();
	}
	
	//Following method will be called by Activity usign IBinder
	
	/**
	 * Enables the RemoteController thus allowing us to receive metadata updates.
	 */
	public void setRemoteControllerEnabled() {
		if(!((AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE)).registerRemoteController(mRemoteController)) {
			Toast.makeText(this, R.string.remotecontrol_not_support, Toast.LENGTH_SHORT).show();
			throw new RuntimeException("Error while registering RemoteController!");
		} else {
			mRemoteController.setArtworkConfiguration(BITMAP_WIDTH, BITMAP_HEIGHT);
			setSynchronizationMode(mRemoteController, RemoteController.POSITION_SYNCHRONIZATION_CHECK);
			mEnabled = true;
		}
	}
	
	/**
	 * Disables RemoteController.
	 */
	public void setRemoteControllerDisabled() {
		((AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE)).unregisterRemoteController(mRemoteController);
		mEnabled = false;
	}
	
	/**
	 * Sets up external callback for client update events.
	 * @param listener External callback.
	 */
	public void setClientUpdateListener(RemoteController.OnClientUpdateListener listener) {
		mExternalClientUpdateListener = listener;
	}
	
	/**
	 * Sends "next" media key press.
	 */
	public boolean sendNextKey() {
		return sendKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT);
	}
	
	/**
	 * Sends "previous" media key press.
	 */
	public boolean sendPreviousKey() {
		return sendKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
	}
	
	/**
	 * Sends "pause" media key press, or, if player ignored this button, "play/pause".
	 */
	public boolean sendPauseKey() {
		if(!sendKeyEvent(KeyEvent.KEYCODE_MEDIA_PAUSE)) {
			return sendKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
		}
		return true;
	}
	
	/**
	 * Sends "play" button press, or, if player ignored it, "play/pause".
	 */
	public boolean sendPlayKey() {
		if(!sendKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY)) {
			return sendKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
		}
		return true;
	}
	
	/**
	 * @return Current song position in milliseconds.
	 */
	public long getEstimatedPosition() {
		return mRemoteController.getEstimatedMediaPosition();
	}
	
	/**
	 * Seeks to given position.
	 * @param ms Position in milliseconds.
	 */
	public void seekTo(long ms) {
		mRemoteController.seekTo(ms);
	}
	//end of Binder methods.
	
	//helper methods

	//this method let us avoid the bug in RemoteController
	//which results in Exception when calling RemoteController#setSynchronizationMode(int)
	//doesn't seem to work though
	private void setSynchronizationMode(RemoteController controller, int sync) {
		if ((sync != RemoteController.POSITION_SYNCHRONIZATION_NONE) && (sync != RemoteController.POSITION_SYNCHRONIZATION_CHECK)) {
            throw new IllegalArgumentException("Unknown synchronization mode " + sync);
        }
		
		Class<?> iRemoteControlDisplayClass;
		
		try {
			iRemoteControlDisplayClass  = Class.forName("android.media.IRemoteControlDisplay");
		} catch (ClassNotFoundException e1) {
			throw new RuntimeException("Class IRemoteControlDisplay doesn't exist, can't access it with reflection");
		}
		
		Method remoteControlDisplayWantsPlaybackPositionSyncMethod;
		try {
			remoteControlDisplayWantsPlaybackPositionSyncMethod = AudioManager.class.getDeclaredMethod("remoteControlDisplayWantsPlaybackPositionSync", iRemoteControlDisplayClass, boolean.class);
			remoteControlDisplayWantsPlaybackPositionSyncMethod.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Method remoteControlDisplayWantsPlaybackPositionSync() doesn't exist, can't access it with reflection");
		}
		
		Object rcDisplay;
		Field rcDisplayField;
		try {
			rcDisplayField = RemoteController.class.getDeclaredField("mRcd");
			rcDisplayField.setAccessible(true);
			rcDisplay = rcDisplayField.get(mRemoteController);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Field mRcd doesn't exist, can't access it with reflection");
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Field mRcd can't be accessed - access denied");
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Field mRcd can't be accessed - invalid argument");
		}
		
		AudioManager am = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
		try {
			remoteControlDisplayWantsPlaybackPositionSyncMethod.invoke(am, iRemoteControlDisplayClass.cast(rcDisplay), true);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Method remoteControlDisplayWantsPlaybackPositionSync() invocation failure - access denied");
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Method remoteControlDisplayWantsPlaybackPositionSync() invocation failure - invalid arguments");
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Method remoteControlDisplayWantsPlaybackPositionSync() invocation failure - invalid invocation target");
		}
	}
	

	private boolean sendKeyEvent(int keyCode) {
		//send "down" and "up" keyevents.
		KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
		boolean first = mRemoteController.sendMediaKeyEvent(keyEvent);
		keyEvent = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
		boolean second = mRemoteController.sendMediaKeyEvent(keyEvent);
		
		return first && second; //if both  clicks were delivered successfully
		}
	//end of helper methods.
	
	
	//the most simple Binder implementation
	public class RCBinder extends Binder {
        public RemoteControlService getService() {
            return RemoteControlService.this;
        }
    }

	//implementation of RemoteController.OnClientUpdateListener. Does nothing other than calling external callback.
	@Override
	public void onClientChange(boolean arg0) {
		if(mEnabled && mExternalClientUpdateListener != null) {
			mExternalClientUpdateListener.onClientChange(arg0);
		}
	}

	@Override
	public void onClientMetadataUpdate(MetadataEditor arg0) {
		if(mEnabled && mExternalClientUpdateListener != null) {
			mExternalClientUpdateListener.onClientMetadataUpdate(arg0);
		}
	}

	@Override
	public void onClientPlaybackStateUpdate(int arg0) {
		if(mEnabled && mExternalClientUpdateListener != null) {
			mExternalClientUpdateListener.onClientPlaybackStateUpdate(arg0);
		}
	}

	@Override
	public void onClientPlaybackStateUpdate(int arg0, long arg1, long arg2, float arg3) {
		if(mEnabled && mExternalClientUpdateListener != null) {
			mExternalClientUpdateListener.onClientPlaybackStateUpdate(arg0, arg1, arg2, arg3);
		}
	}

	@Override
	public void onClientTransportControlUpdate(int arg0) {
		if(mEnabled && mExternalClientUpdateListener != null) {
			mExternalClientUpdateListener.onClientTransportControlUpdate(arg0);
		}
		
	}
}
