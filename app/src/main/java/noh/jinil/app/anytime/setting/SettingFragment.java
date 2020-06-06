package noh.jinil.app.anytime.setting;

import java.util.List;

import noh.jinil.app.anytime.IMainFragment;
import noh.jinil.app.anytime.MainPageHelper;
import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.main.ColorSetActivity;
import noh.jinil.app.anytime.main.IServiceFragment;
import noh.jinil.app.anytime.music.VisualizerManager;
import noh.jinil.app.anytime.music.item.AppInfo;
import noh.jinil.app.anytime.service.IMediaPlaybackService;
import noh.jinil.app.anytime.service.RemoteControlService;
import noh.jinil.app.anytime.utils.PreferenceUtils;
import noh.jinil.app.anytime.utils.SystemUtils;
import noh.jinil.app.anytime.utils.VLog;
import noh.jinil.app.anytime.visualizer.IVisualizerView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SettingFragment extends Fragment implements IServiceFragment, IMainFragment {
	private static final String TAG = "SettingFragment";
	private ViewGroup mContainer;
	IMediaPlaybackService mService;
	
	public static final int REQUEST_CODE_CUSTOMCOLOR = 1;
	
	IVisualizerView mVisualizerView = null;
	SeekBar mSeekbarHeight;
	SeekBar mSeekbarWidth;
	SeekBar mSeekbarBarRatio;
	SeekBar mSeekbarTrans;
	SeekBar mSeekbarMIC;
	CheckBox mCheckBoxUseMic;
	CheckBox mCheckBoxMiniVi;
	CheckBox mCheckBoxStick;
	CheckBox mCheckBoxOnoff;
	CheckBox mCheckBoxRC;
	CheckBox mCheckBoxBacklight;
	CheckBox mCheckBoxNoti;
	View mRCLayout;
	View mOnOffButton;
	View mViSubMenu;
	View mMICLayout;
	ImageView mRCAppIcon;
	TextView mRCAppName;
	Button mBtnColor;
	Button mBtnGravity;
	int mViColor;
	int mGravity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		VLog.i(TAG, "onCreateView()");
		mContainer = container;
		View v = inflater.inflate(R.layout.setting_main, container, false);
		mVisualizerView = (IVisualizerView)v.findViewById(R.id.setting_vi_preview);
		
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		final int screenWidth = metrics.widthPixels;
		final int screenHeight = metrics.heightPixels;
		
		int barRatio = PreferenceUtils.loadIntegerValue(getActivity(), PreferenceUtils.KEY_VI_BARRATIO, 0);
		int wRatio = PreferenceUtils.loadIntegerValue(getActivity(), PreferenceUtils.KEY_VI_WIDTH_RATIO, 100);
		int hRatio = PreferenceUtils.loadIntegerValue(getActivity(), PreferenceUtils.KEY_VI_HEIGHT_RATIO, 10);		
		int transparency = PreferenceUtils.loadIntegerValue(getActivity(), PreferenceUtils.KEY_VI_ALPHA, 0xFF);
		int micSensitivity = PreferenceUtils.loadIntegerValue(getActivity(), PreferenceUtils.KEY_MIC_SENSITIVITY, 80);
		mGravity = PreferenceUtils.loadIntegerValue(getActivity(), PreferenceUtils.KEY_VI_GRAVITY, 0);
		setVisualizerSize(screenWidth*wRatio/100, screenHeight*hRatio/100, mGravity);
		
		mOnOffButton = (View)v.findViewById(R.id.setting_floating_onoff_layout);
		mViSubMenu = (View)v.findViewById(R.id.setting_vi_submenu);
		
		boolean isRemoteControl = PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_RC_AUTHORIZED, false);
		if(isRemoteControl && !SystemUtils.isAuthNotiService(getActivity(), RemoteControlService.class.getName())) {
			PreferenceUtils.saveBooleanValue(getActivity(), PreferenceUtils.KEY_RC_AUTHORIZED, false);
			isRemoteControl = false;
		}

		/*
		mRCLayout = v.findViewById(R.id.setting_rc_app_layout);
		mRCLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createAppsListDialog();
			}
		});
		
		if(!isRemoteControl) {
			mRCLayout.setVisibility(View.GONE);
		}
		
		mRCAppName = (TextView)v.findViewById(R.id.setting_rc_app_name);
		mRCAppIcon = (ImageView)v.findViewById(R.id.setting_rc_app_icon);
		
		String packageName = PreferenceUtils.loadStringValue(getActivity(), PreferenceUtils.KEY_RC_PACKAGENAME);
		PackageManager pm = getActivity().getPackageManager();
		try {
			ApplicationInfo appinfo = pm.getApplicationInfo(packageName, 0);
			mRCAppName.setText(appinfo.loadLabel(pm));
			mRCAppIcon.setImageDrawable(appinfo.loadIcon(pm));
		} catch (NameNotFoundException e) {
			mRCAppIcon.setVisibility(View.GONE);
		}
		*/
		
		mBtnGravity = (Button)v.findViewById(R.id.setting_vi_gravity);
		mBtnGravity.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(R.string.visualizer_select_gravity);
				builder.setSingleChoiceItems(R.array.gravity_array, mGravity, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mGravity = which;
						CharSequence sequence[] = getResources().getTextArray(R.array.gravity_array); 
						mBtnGravity.setText(sequence[mGravity]);
						PreferenceUtils.saveIntegerValue(getActivity(), PreferenceUtils.KEY_VI_GRAVITY, mGravity);
						setVisualizerSize(-1, -1, mGravity);
						dialog.dismiss();
					}
				});
				builder.show();
			}
		});
		CharSequence sequence[] = getResources().getTextArray(R.array.gravity_array); 
		mBtnGravity.setText(sequence[mGravity]);
				
		mBtnColor = (Button)v.findViewById(R.id.setting_vi_color);
		mBtnColor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createColorDialog();
			}
		});
		
		mViColor = PreferenceUtils.loadIntegerValue(getActivity(), PreferenceUtils.KEY_VI_COLOR, 0);
		String arrayColor[] = getResources().getStringArray(R.array.visualizer_colorset);
		if(mViColor < 0 || mViColor >= arrayColor.length) {
			mViColor = 0;
		}
		mBtnColor.setText(arrayColor[mViColor]);
		/*
		if(mViColor == 0) {
			mBtnColor.setBackgroundResource(R.drawable.visualizer_preview1);
		} else if(mViColor == 1) {
			mBtnColor.setBackgroundResource(R.drawable.visualizer_preview2);
		} else {
			mBtnColor.setBackgroundColor(mViColor);
		}
		*/
		
		mCheckBoxBacklight = (CheckBox)v.findViewById(R.id.setting_backlight_onoff);
		mCheckBoxBacklight.setChecked(PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_BL_ALWAYSON, true));
		mCheckBoxBacklight.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PreferenceUtils.saveBooleanValue(getActivity(), PreferenceUtils.KEY_BL_ALWAYSON, isChecked);
			}
		});
		
		mCheckBoxNoti = (CheckBox)v.findViewById(R.id.setting_noti_hide);
		mCheckBoxNoti.setChecked(PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_NOTI_HIDE, false));
		mCheckBoxNoti.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PreferenceUtils.saveBooleanValue(getActivity(), PreferenceUtils.KEY_NOTI_HIDE, isChecked);
			}
		});

		/*
		mCheckBoxRC = (CheckBox)v.findViewById(R.id.setting_rc_onoff);
		mCheckBoxRC.setChecked(PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_RC_AUTHORIZED, false));
		mCheckBoxRC.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PreferenceUtils.saveBooleanValue(getActivity(), PreferenceUtils.KEY_RC_AUTHORIZED, isChecked);
				if (isChecked && !SystemUtils.isAuthNotiService(getActivity(), RemoteControlService.class.getName())) {
					Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
					startActivity(intent);
				}
				if (isChecked) {
					mRCLayout.setVisibility(View.VISIBLE);
					if (mCheckBoxUseMic.isChecked()) {
						mCheckBoxUseMic.setChecked(false);
					}
				} else {
					mRCLayout.setVisibility(View.GONE);
				}
			}
		});

		mCheckBoxUseMic = (CheckBox)v.findViewById(R.id.setting_usemic);
		mCheckBoxUseMic.setChecked(PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_MIC_USE, false));
		mCheckBoxUseMic.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PreferenceUtils.saveBooleanValue(getActivity(), PreferenceUtils.KEY_MIC_USE, isChecked);
				if (isChecked) {
					VisualizerManager.getInstance().setupMic();
					mVisualizerView.setUseMic(true);
					if (mCheckBoxRC.isChecked()) {
						mCheckBoxRC.setChecked(false);
					}
					mMICLayout.setVisibility(View.VISIBLE);
				} else {
					VisualizerManager.getInstance().setupSession();
					mVisualizerView.setUseMic(false);
					mMICLayout.setVisibility(View.GONE);
				}
			}
		});

		mMICLayout = (View)v.findViewById(R.id.setting_mic_layout);
		if(mCheckBoxUseMic.isChecked()) {
			mMICLayout.setVisibility(View.VISIBLE);
		}

		mSeekbarMIC = (SeekBar) v.findViewById(R.id.setting_mic_sensitivity);
		mSeekbarMIC.setMax(100);
		mSeekbarMIC.setProgress(micSensitivity);
		mSeekbarMIC.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				PreferenceUtils.saveIntegerValue(getActivity(), PreferenceUtils.KEY_MIC_SENSITIVITY, seekBar.getProgress());
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(fromUser) {
					mVisualizerView.setMICSensitivity(progress);
				}
			}
		});
		
		if(android.os.Build.VERSION.SDK_INT >= 19 && android.os.Build.VERSION.SDK_INT <= 20) {
			mCheckBoxRC.setEnabled(true);
		} else {		
			mRCLayout.setVisibility(View.VISIBLE);
			mRCAppName.setText(R.string.remotecontrol_title3);
			mCheckBoxRC.setEnabled(false);
			mRCLayout.setClickable(false);
			mRCAppIcon.setVisibility(View.GONE);
		}
		*/
		
		mCheckBoxStick = (CheckBox)v.findViewById(R.id.setting_vi_screen_stick);
		mCheckBoxStick.setChecked(PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_VI_STICK, true));
		mCheckBoxStick.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PreferenceUtils.saveBooleanValue(getActivity(), PreferenceUtils.KEY_VI_STICK, isChecked);
				mVisualizerView.setStick(isChecked);
			}
		});
		
		mCheckBoxOnoff = (CheckBox)v.findViewById(R.id.setting_vi_floating_onoff);
		mCheckBoxOnoff.setChecked(PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_VI_FLOATING_ONOFF, false));
		mCheckBoxOnoff.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PreferenceUtils.saveBooleanValue(getActivity(), PreferenceUtils.KEY_VI_FLOATING_ONOFF, isChecked);
				if(isChecked) {
					mOnOffButton.setVisibility(View.VISIBLE);
				} else {
					mOnOffButton.setVisibility(View.GONE);
				}
			}
		});
		if(mCheckBoxOnoff.isChecked()) {
			mOnOffButton.setVisibility(View.VISIBLE);
		}
		
		
		mCheckBoxMiniVi = (CheckBox)v.findViewById(R.id.setting_vi_screen_onoff);
		mCheckBoxMiniVi.setChecked(PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_VI_SHOWSCREEN, true));
		mCheckBoxMiniVi.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PreferenceUtils.saveBooleanValue(getActivity(), PreferenceUtils.KEY_VI_SHOWSCREEN, isChecked);
				View view = (View)mVisualizerView;
				if(isChecked) {
					view.setVisibility(View.VISIBLE);
					mViSubMenu.setVisibility(View.VISIBLE);
					if(mCheckBoxOnoff.isChecked()) {
						mOnOffButton.setVisibility(View.VISIBLE);
					}
				} else {
					view.setVisibility(View.GONE);
					mViSubMenu.setVisibility(View.GONE);
					mOnOffButton.setVisibility(View.GONE);
				}
			}
		});
		
		if(!mCheckBoxMiniVi.isChecked()) {
			View view = (View)mVisualizerView;
			view.setVisibility(View.GONE);			
			mViSubMenu.setVisibility(View.GONE);
			mOnOffButton.setVisibility(View.GONE);
		}
		
		mSeekbarWidth = (SeekBar)v.findViewById(R.id.setting_vi_width);
		mSeekbarWidth.setProgress(wRatio);
		mSeekbarWidth.setMax(100);
		mSeekbarWidth.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				PreferenceUtils.saveIntegerValue(getActivity(), PreferenceUtils.KEY_VI_WIDTH_RATIO, seekBar.getProgress());
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(fromUser) {
					int width = screenWidth * progress / 100;
					setVisualizerSize(width, -1, mGravity);
				}
			}
		});
		
		mSeekbarHeight = (SeekBar)v.findViewById(R.id.setting_vi_height);
		mSeekbarHeight.setProgress(hRatio);
		mSeekbarHeight.setMax(50);
		mSeekbarHeight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				PreferenceUtils.saveIntegerValue(getActivity(), PreferenceUtils.KEY_VI_HEIGHT_RATIO, seekBar.getProgress());
			}			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(fromUser) {
					int height = screenHeight * progress / 100;
					setVisualizerSize(-1, height, mGravity);
				}
			}
		});
		
		mSeekbarBarRatio = (SeekBar)v.findViewById(R.id.setting_vi_barratio);
		mSeekbarBarRatio.setProgress(barRatio);
		mSeekbarBarRatio.setMax(100);
		mSeekbarBarRatio.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				PreferenceUtils.saveIntegerValue(getActivity(), PreferenceUtils.KEY_VI_BARRATIO, seekBar.getProgress());
			}		
						
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(fromUser) {
					mVisualizerView.setBarSize(progress);
				}
			}
		});
		
		mSeekbarTrans = (SeekBar)v.findViewById(R.id.setting_vi_transparency);
		mSeekbarTrans.setMax(0xff);
		mSeekbarTrans.setProgress(transparency);
		mSeekbarTrans.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				PreferenceUtils.saveIntegerValue(getActivity(), PreferenceUtils.KEY_VI_ALPHA, seekBar.getProgress());
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {				
				if(fromUser) {
					mVisualizerView.setAlpha(progress);
				}
			}
		});
		
		
		refreshAllView(true);		
		return v;
	}
	
	
	
	private void createColorDialog() {
//		new ColorPickerDialog(getActivity(), new ColorPickerDialog.OnColorChangedListener() {
//			@Override
//			public void colorChanged(int color) {
//				PreferenceUtils.saveIntegerValue(getActivity(), PreferenceUtils.KEY_VI_COLOR, color);
//				mVisualizerView.setColorSet(color);
//				
//				if(color == 0) {
//					mBtnColor.setBackgroundResource(R.drawable.visualizer_preview1);
//				} else if(color == 1) {
//					mBtnColor.setBackgroundResource(R.drawable.visualizer_preview2);
//				} else if(color == 2) {
//					mBtnColor.setBackgroundResource(R.drawable.visualizer_preview2);
//				} else {
//					mBtnColor.setBackgroundColor(color);
//				}
//			}
//		}, mViColor).show();
			
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle(R.string.visualizer_color_title);
		dialog.setSingleChoiceItems(getResources().getStringArray(R.array.visualizer_colorset), mViColor, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int color) {
				mViColor = color;
				PreferenceUtils.saveIntegerValue(getActivity(), PreferenceUtils.KEY_VI_COLOR, color);
				mVisualizerView.setColorSet(color);
				String arrayColor[] = getResources().getStringArray(R.array.visualizer_colorset);
				mBtnColor.setText(arrayColor[color]);
				if(color == mVisualizerView.getCustomColorSet()) {
					startActivityForResult((new Intent(getActivity(), ColorSetActivity.class)), REQUEST_CODE_CUSTOMCOLOR);
				}
				dialog.dismiss();
			}
		});
		dialog.show();
		
		/*
		final ColorSetAdapter colorAdapter = new ColorSetAdapter(getActivity(), R.layout.setting_dialog_colorset);
		colorAdapter.add(Integer.valueOf(R.drawable.visualizer_preview1));
		colorAdapter.add(Integer.valueOf(R.drawable.visualizer_preview2));
		colorAdapter.add(Integer.valueOf(R.drawable.visualizer_preview2));
		colorAdapter.add(Integer.valueOf(R.drawable.visualizer_preview2));
		colorAdapter.add(Integer.valueOf(R.drawable.visualizer_preview2));
		colorAdapter.add(Integer.valueOf(R.drawable.visualizer_preview2));
		colorAdapter.add(Integer.valueOf(R.drawable.visualizer_preview2));
		colorAdapter.add(Integer.valueOf(R.drawable.visualizer_preview1));
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle(R.string.visualizer_color_title);
		dialog.setSingleChoiceItems(colorAdapter, mViColor, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int color) {
				PreferenceUtils.saveIntegerValue(getActivity(), PreferenceUtils.KEY_VI_COLOR, color);
				mVisualizerView.setColorSet(color);				
				mBtnColor.setBackgroundResource(colorAdapter.getItem(color));
				if(color == mVisualizerView.getCustomColorSet()) {
					startActivityForResult((new Intent(getActivity(), ColorSetActivity.class)), REQUEST_CODE_CUSTOMCOLOR);
				}
				dialog.dismiss();
			}
		});
		dialog.show();
		*/
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case REQUEST_CODE_CUSTOMCOLOR:
			if(resultCode == Activity.RESULT_OK) {
				mVisualizerView.setColorSet(mVisualizerView.getCustomColorSet());				
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean show() {
		return true;
	}

	@Override
	public boolean hide(OnHideListener listener) {
		if(listener != null) {
			listener.onAnimationFinished(MainPageHelper.TYPE.SETTINGS);
		}
		return true;
	}

	@Override
	public void onServiceConnected(IMediaPlaybackService iService) {

	}

	@Override
	public void onQueueListChanged(long[] queueList) {

	}

	@Override
	public void onQueueMetaChanged(int position) {

	}

	@Override
	public void onQueueShuffleChanged() {

	}

	@Override
	public void onAlbumArtUpdated(Bitmap bmp) {

	}

	@Override
	public void onPlaytimeUpdated(int playtime) {

	}

	@Override
	public void onDurationUpdated(int duration) {

	}

	@Override
	public void onPlayingStatusUpdated(boolean isPlaying) {

	}

	@Override
	public void onEditModeChanged(boolean set) {

	}

	@Override
	public void onSelectAllClicked(boolean set) {

	}
	
	/*
	class ColorSetAdapter extends ArrayAdapter<Integer> {
		public ColorSetAdapter(Context context, int resource) {
			super(context, resource);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.setting_dialog_colorset, null);
			}
			ImageView iv = (ImageView)convertView.findViewById(R.id.setting_dialog_colorset);
			iv.setImageResource(getItem(position));
			return convertView;
		}
	}
	*/
	
	class ApplistAdapter extends ArrayAdapter<AppInfo> {
		public ApplistAdapter(Context context, int resource) {
			super(context, resource);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.e("SettingFragment", "VIEW:"+convertView);
			if(convertView == null) {
				LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.setting_dialog_applist, null);
			}
			PackageManager pm = getActivity().getPackageManager();
			
			TextView tv = (TextView)convertView.findViewById(R.id.setting_dialog_apptitle);
			tv.setText(getItem(position).getAppName());
			ImageView iv = (ImageView)convertView.findViewById(R.id.setting_dialog_appicon);
			iv.setImageDrawable(getItem(position).getIcon());
			return convertView;
		}
	}
	
	private void createAppsListDialog() {
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle(R.string.setting_rc_select_app);
        final ApplistAdapter arrayAdapter = new ApplistAdapter(getActivity(), R.layout.setting_dialog_applist);
        
        PackageManager pm = getActivity().getPackageManager();
        List<PackageInfo> packs = pm.getInstalledPackages(PackageManager.PERMISSION_GRANTED);

        for(PackageInfo pack : packs) {
        	if(pack.packageName != null && (pack.packageName.contains("music") || pack.packageName.contains("radio") || pack.packageName.contains("song") || pack.packageName.contains("player"))) {
        		AppInfo appInfo = new AppInfo();
        		appInfo.setAppId(pack.applicationInfo.loadDescription(pm)+"");
                appInfo.setAppName(pack.applicationInfo.loadLabel(pm)+"");
                appInfo.setPackageName(pack.applicationInfo.packageName);     
                appInfo.setIcon(pack.applicationInfo.loadIcon(pm));
                arrayAdapter.add(appInfo);
        	}
        }
        
        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AppInfo appinfo = arrayAdapter.getItem(which);
				PreferenceUtils.saveStringValue(getActivity(), PreferenceUtils.KEY_RC_PACKAGENAME, appinfo.getPackageName());
				mRCAppName.setText(appinfo.getAppName());
				mRCAppIcon.setImageDrawable(appinfo.getIcon());
				mRCAppIcon.setVisibility(View.VISIBLE);
//                        String strName = arrayAdapter.getItem(which);
//                        AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
//                        builderInner.setMessage(strName);
//                        builderInner.setTitle("Your Selected Item is");
//                        builderInner.setPositiveButton("Ok",
//                                new DialogInterface.OnClickListener() {
//
//                                    @Override
//                                    public void onClick(
//                                            DialogInterface dialog,
//                                            int which) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                        builderInner.show();
                    }
        });
        builderSingle.show();
	}
	
	void setVisualizerSize(int width, int height, int gravity) {
		View view = (View)mVisualizerView;
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)view.getLayoutParams();
		
		if(width < 0) {
			width = params.width;
		}
		else if(width < 40*3) {
			width = 40*3;
		}
		if(height < 0) {
			height = params.height;
		}
		else if(height < 40*3) {
			height = 40*3;
		}
		if(gravity == 1) {
			gravity = Gravity.START;
		} else if(gravity == 2) {
			gravity = Gravity.END;
		} else {
			gravity = Gravity.CENTER_HORIZONTAL;
		}
		
		params.width = width;
		params.height = height;
		params.gravity = gravity;
		view.setLayoutParams(params);
	}
	
	@Override
	public void onStart() {
		VisualizerManager.getInstance().setupView(mVisualizerView);
		
		boolean isRemoteControl = PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_RC_AUTHORIZED, false);
		if(isRemoteControl && !SystemUtils.isAuthNotiService(getActivity(), RemoteControlService.class.getName())) {
			PreferenceUtils.saveBooleanValue(getActivity(), PreferenceUtils.KEY_RC_AUTHORIZED, false);
			if(mCheckBoxRC != null) {
				mCheckBoxRC.setChecked(false);
			}
		}		
		super.onStart();
	}
	
	private void refreshAllView(boolean bAnimation) {
    	if(getActivity() == null || mService == null) 
    		return;
    	
        if(bAnimation) {
        	showAnimation();
        }
    }
	
	@Override
	public void hideAnimation() {
		VLog.d(TAG, "hideAnimation()");
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_out);
        ani.setInterpolator(new DecelerateInterpolator(5.0f));
        mContainer.startAnimation(ani);
	}

	@Override
	public void showAnimation() {
		VLog.d(TAG, "showAnimation()");
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in);
        ani.setInterpolator(new DecelerateInterpolator(5.0f));
        mContainer.startAnimation(ani);
	}

	@Override
	public void setService(IMediaPlaybackService service) {
		mService = service;
		if(service != null) {
			refreshAllView(false);
		}
	}

	@Override
	public boolean onBackPressed() {
		return false;		
	}
}
