package noh.jinil.app.anytime.main;

import noh.jinil.app.anytime.IMainActivity;
import noh.jinil.app.anytime.IMainFragment;
import noh.jinil.app.anytime.MainPageHelper;
import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.library.LibraryPageInfo;
import noh.jinil.app.anytime.music.PlaybackFragment;
import noh.jinil.app.anytime.music.VisualizerManager;
import noh.jinil.app.anytime.service.IMediaPlaybackService;
import noh.jinil.app.anytime.service.MediaPlaybackService;
import noh.jinil.app.anytime.service.MusicUtils;
import noh.jinil.app.anytime.service.MusicUtils.ServiceToken;
import noh.jinil.app.anytime.utils.PreferenceUtils;
import noh.jinil.app.anytime.utils.VLog;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements IMainActivity {
	private static final String TAG = "MainActivity";
	
	public static boolean bHideVisualizerMode = false;
	
	private ImageView mBackground;

    Fragment mFragment;
    IMediaPlaybackService mService;
    private ServiceToken mToken;

	private static final int MY_PERMISSION_REQUEST_STORAGE = 1;
	private static final int MY_PERMISSION_REQUEST_RECORD_AUDIO = 2;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		VLog.i(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBackground = (ImageView) findViewById(R.id.activity_main_bgImage);

		showFragment(MainPageHelper.TYPE.PLAYBACK, null);
		showFragment(MainPageHelper.TYPE.miniPLAYER, null);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		VLog.i(TAG, "onRequestPermissionsResult() "+requestCode);
		switch(requestCode) {
			case MY_PERMISSION_REQUEST_STORAGE:
				if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					boolean permissionOK = checkPermission(Manifest.permission.RECORD_AUDIO, MY_PERMISSION_REQUEST_RECORD_AUDIO);
					if(permissionOK) {
						bindToService();
					}
				} else {
					finish();
				}
				break;

			case MY_PERMISSION_REQUEST_RECORD_AUDIO:
				if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					bindToService();
				} else {
					finish();
				}
				break;
		}
	}

	public boolean checkPermission(String permission, int requestCode) {
		int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
		if(permissionCheck == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(this, new String[] {permission}, requestCode);
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		VLog.i(TAG, "onConfigurationChanged()");
		super.onSaveInstanceState(outState);
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
		VLog.i(TAG, "onPostCreate()");
        super.onPostCreate(savedInstanceState);
    }
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
		VLog.i(TAG, "onConfigurationChanged()");
        super.onConfigurationChanged(newConfig);
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		VLog.i(TAG, "onCreateOptionsMenu()");
		if(!PreferenceUtils.loadBooleanValue(this, PreferenceUtils.KEY_MIC_USE, false)) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.music, menu);
		}
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action buttons
        switch(item.getItemId()) 
        {
        case R.id.action_delete_song:
        	if(mFragment instanceof PlaybackFragment) {
        		PlaybackFragment musicFragment = (PlaybackFragment)mFragment;
        		musicFragment.deleteSong();
        	}
            return true;
            
        case R.id.action_toggle_visualizer:
        	if(VisualizerManager.getInstance().toggleSessionID()) {
        		Toast.makeText(getApplicationContext(), R.string.noti_local_visualizer_setting2, Toast.LENGTH_LONG).show();
        	} else {
        		Toast.makeText(getApplicationContext(), R.string.noti_global_visualizer_setting2, Toast.LENGTH_LONG).show();
        	}
        	return true;
            
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void setBackgroundBitmap(Bitmap bm) {
    	mBackground.setImageBitmap(bm);
    }
    
    public void setBackgroundAlpha(float alpha) {
    	if(alpha == 0.0f) {
    		mBackground.setVisibility(View.GONE);
   		} else {
   			mBackground.setVisibility(View.VISIBLE);
   			mBackground.setAlpha(alpha);
   		}
    }
    
    @Override
    protected void onStart() {
    	super.onStart();

		boolean permissionOK = checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSION_REQUEST_STORAGE);
		if(permissionOK) {
			permissionOK = checkPermission(Manifest.permission.RECORD_AUDIO, MY_PERMISSION_REQUEST_RECORD_AUDIO);
		}
		if(permissionOK) {
			bindToService();
		}
    }
    
    @Override
    protected void onStop() {
    	super.onStop();

    	boolean bShowMini = PreferenceUtils.loadBooleanValue(this, PreferenceUtils.KEY_VI_SHOWSCREEN, true);
		try {
			if(mService != null && !bHideVisualizerMode) {
				mService.showFloatingView(bShowMini);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

    	unBindFromService();
    }
    
    @Override
    public void onBackPressed() {
		Log.d(TAG, "onBackPressed()");

		if(hideFragment(MainPageHelper.TYPE.LIBRARY_PAGE))
			return;
		if(hideFragment(MainPageHelper.TYPE.LIBRARY))
			return;
		if(hideFragment(MainPageHelper.TYPE.SETTINGS))
			return;
		if(hideFragment(MainPageHelper.TYPE.INFORMATION))
			return;

		if(!PreferenceUtils.loadBooleanValue(getApplicationContext(), PreferenceUtils.KEY_IGNORE_VIPOPUP, false)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.music_popup_visualizer_show_title);
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					PreferenceUtils.saveBooleanValue(getApplicationContext(), PreferenceUtils.KEY_VI_SHOWSCREEN, true);
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						if (Settings.canDrawOverlays(getApplicationContext())) {
							finish();
						} else {
							startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION), 1);
						}
					}
					else {
						finish();
					}
				}
			});
			builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					PreferenceUtils.saveBooleanValue(getApplicationContext(), PreferenceUtils.KEY_VI_SHOWSCREEN, false);
					finish();
				}
			});

			LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(R.layout.visualizer_show_dialog, null);
			builder.setView(view);
			CheckBox ignoreBox = (CheckBox)view.findViewById(R.id.visualizer_donotshowagain);
			ignoreBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					PreferenceUtils.saveBooleanValue(getApplicationContext(), PreferenceUtils.KEY_IGNORE_VIPOPUP, isChecked);
				}
			});

			AlertDialog dialog = builder.create();
			dialog.show();
			return;
		}
		super.onBackPressed();
    }

	private void bindToService() {
		mToken = MusicUtils.bindToService(this, mConnection);
	}

	private void unBindFromService() {
		MusicUtils.unbindFromService(mToken);
		mService = null;
	}
    
    private final ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "onServiceDisconnected()");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "onServiceConnected()");
			mService = IMediaPlaybackService.Stub.asInterface(service);
			try {
				mService.hideFloatingView();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			for(IMainFragment iMainFragment: mainFragmentMap.values()) {
				iMainFragment.onServiceConnected(mService);
			}
		}
	};

	HashMap<MainPageHelper.TYPE, IMainFragment> mainFragmentMap = new HashMap<>();
	private boolean showFragment(MainPageHelper.TYPE type, Bundle bundle) {
		IMainFragment iMainFragment = MainPageHelper.attachFragment(getSupportFragmentManager(), type, bundle);
		if(iMainFragment != null) {
			mainFragmentMap.put(type, iMainFragment);
			return iMainFragment.show();
		}
		return false;
	}

	private boolean hideFragment(MainPageHelper.TYPE type) {
		mainFragmentMap.remove(type);
		IMainFragment iMainPageFragment = MainPageHelper.findFragment(getSupportFragmentManager(), type);
		if(iMainPageFragment == null)
			return false;
		boolean success = iMainPageFragment.hide(new IMainFragment.OnHideListener() {
			@Override
			public void onAnimationFinished(MainPageHelper.TYPE type) {
				MainPageHelper.detachFragment(getSupportFragmentManager(), type);
			}
		});
		return success;
	}

	@Override
	public void requestToShowSettings() {
		VLog.d(TAG, "requestToShowSettings()");
		showFragment(MainPageHelper.TYPE.SETTINGS, null);
	}

	@Override
	public void requestToShowAppInfo() {
		VLog.d(TAG, "requestToShowAppInfo()");
		showFragment(MainPageHelper.TYPE.INFORMATION, null);
	}

	@Override
	public void requestToShowLibrary() {
		VLog.d(TAG, "requestToShowLibrary()");
		showFragment(MainPageHelper.TYPE.LIBRARY, null);
	}

	@Override
	public void requestToShowLibraryPage(LibraryPageInfo pageInfo) {
		VLog.d(TAG, "requestToShowLibraryPage() "+pageInfo.getTitle());
		Bundle bundle = new Bundle();
		bundle.putParcelable(LibraryPageInfo.BUNDLE_KEY, pageInfo);
		showFragment(MainPageHelper.TYPE.LIBRARY_PAGE, bundle);
	}

	@Override
	public void requestToShowNowPlaying() {
		showFragment(MainPageHelper.TYPE.nowPLAYING, null);
	}

	@Override
	public void requestToPlayItem(long[] audioIDs) {
		try {
			mService.enqueue(audioIDs, MediaPlaybackService.NOW);
		}catch(RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public IMediaPlaybackService getServiceObject() {
		return mService;
	}
}
