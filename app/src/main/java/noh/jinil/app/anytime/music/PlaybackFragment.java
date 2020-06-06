package noh.jinil.app.anytime.music;

import java.util.Formatter;
import java.util.Locale;

import noh.jinil.app.anytime.IMainActivity;
import noh.jinil.app.anytime.IMainFragment;
import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.main.MainLibraryFragment;
import noh.jinil.app.anytime.main.IServiceFragment;
import noh.jinil.app.anytime.main.MainActivity;
import noh.jinil.app.anytime.main.VisualizerFullActivity;
import noh.jinil.app.anytime.music.MusicNowplaying.MusicNowplayingListener;
import noh.jinil.app.anytime.music.MusicSetting.MusicSettingListener;
import noh.jinil.app.anytime.music.item.AppInfo;
import noh.jinil.app.anytime.music.item.TrackItem;
import noh.jinil.app.anytime.music.library.MusicLibraryFolder;
import noh.jinil.app.anytime.music.library.MusicLibraryFolder.MusicLibraryListener;
import noh.jinil.app.anytime.service.IMediaPlaybackService;
import noh.jinil.app.anytime.service.MediaPlaybackService;
import noh.jinil.app.anytime.service.RemoteControlService;
import noh.jinil.app.anytime.album.ArtUtils;
import noh.jinil.app.anytime.utils.ArcDrawable;
import noh.jinil.app.anytime.utils.ArrayUtils;
import noh.jinil.app.anytime.utils.PreferenceUtils;
import noh.jinil.app.anytime.utils.RoundDrawable;
import noh.jinil.app.anytime.utils.SystemUtils;
import noh.jinil.app.anytime.utils.TouchUtils;
import noh.jinil.app.anytime.utils.TouchUtils.OnTouchHandleListener;
import noh.jinil.app.anytime.utils.VLog;
import noh.jinil.app.anytime.visualizer.IVisualizerView;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class PlaybackFragment extends Fragment implements IServiceFragment, IMainFragment {
	private static final String TAG = "PlaybackFragment";
	private static final int REQUEST_CODE_AUDIOEFFECT = 1;

	IMainActivity iMainActivity;

	private ViewGroup mContainer;
	
	IVisualizerView mVisualizerView = null;
	ImageView mAlbumartImage = null;
	ImageView mAlbumartBG = null;
	
	boolean  isRemoteService = false;
	boolean leaveSettingLive = false;
	
	protected RemoteControlService mRCService;
	protected boolean mBound = false; //flag indicating if service is bound to Activity
	private boolean mRCActivated = false;
	private boolean mIsRemotePlaying = false;
	private String mRemoteTracktitle;
	private long remote_duration;
	
	IMediaPlaybackService mService;
	TrackItem mPlayTrack = null;
	int mPlayPos = -1;
	int mDirection = 0;
	private long mDuration;	
	private boolean paused;
		
	private SeekBar mSeekBarPlaytime;
	
	private ImageButton mRemotePauseResume;
	private ImageButton mRemoteNext;
	private ImageButton mRemotePrev;
	private ImageButton mRemoteExpand;
	private SeekBar mRemoteSeekBar;
	private TextView mRemotePlaytime;
	private TextView mRemoteDuration;
	
	
	private ImageButton mBtnPauseResume;
	private ImageButton mCtrPauseResume;
	private ImageButton mCtrNext;
	private ImageButton mCtrPrev;
//	private ImageButton mBtnRatio;
	private ImageButton mBtnEQ;
	private ImageButton mBtnSeek;
	private ImageButton mBtnVisualizerSetting;
	private ImageButton mBtnMainSettings;
	private ImageButton mBtnSettingRemote;
	private ImageButton mBtnNowplaying;
	private ImageButton mBtnRepeat;
	private ImageButton mBtnShuffle;
	private ImageButton mBtnLibrary;
	private ImageButton mBtnVolume;
	private ImageButton mBtnExpand;
	private ImageButton mBtnAppInfo;
	private TextView mTextTitle;
	private View mLayoutFull;
	private View mLayoutBackground;
	private LinearLayout mLayoutMenuButton;
	private LinearLayout mLayoutTopButton;
	private LinearLayout mLayoutADparent;
	private LinearLayout mLayoutSeek;
	
	private RelativeLayout mLayoutVisualizer;
	private LinearLayout mLayoutPlayback;
	private RelativeLayout mLayoutCover;
	private RelativeLayout mLayoutToggleButton;
	private RelativeLayout mLayoutMidButton;
	private ImageView mImgBackground;
	private ImageButton mBtnAddToNowplay;
	private ImageButton mBtnPlayAll;
	private ImageButton mBtnResetNowplay;
	private ImageButton mBtnSearch;
	private LinearLayout mLayoutMiniPlayer;
	private TextView mMiniNowplayInfo;
	private TextView mTextPlaytime;
	private TextView mTextDuration;
	private ImageView mRCAppIcon;
	private TextView mRCAppName;
		
	private ImageView mImgProgress;
	private ImageView mImgProgressDim;
	private ImageView mImgRemoteArt;
	
	private TouchUtils mTouchAlbumart;
	private TouchUtils mTouchVisualizer;
	private TouchUtils mTouchMini;
	
	private View mLayoutRemoteControl;
	private View mLayoutLocalControl;

	// albumart
	private Bitmap mDefaultAlbumart;
	public Bitmap mColorAlbumart;
	
	MusicSetting mSettingFragment = null;
	MusicNowplaying mNowplayingFragment = null;
	MusicLibraryFolder mLibraryFragment = null;
	MainLibraryFragment mLibraryTabFragment = null;
	
	private boolean mPosOverride = false;	
	
	String mPackageName;
	AppInfo mRemoteAppInfo;
	
	public void setContentView(View v) {
		mImgRemoteArt = (ImageView)v.findViewById(R.id.remotecontrol_albumart);
		
		mRCAppName = (TextView)v.findViewById(R.id.remotecontrol_app_name);
		mRCAppIcon = (ImageView)v.findViewById(R.id.remotecontrol_app_icon);

		/*
		mPackageName = PreferenceUtils.loadStringValue(getActivity(), PreferenceUtils.KEY_RC_PACKAGENAME);
		if(mPackageName == null || mPackageName.equals("")) {
			mRCAppName.setText(R.string.remotecontrol_title2);
		}
		else {
			PackageManager pm = getActivity().getPackageManager();
			try {
				ApplicationInfo appinfo = pm.getApplicationInfo(mPackageName, 0);
				mRCAppName.setText(appinfo.loadLabel(pm));
				mRCAppIcon.setImageDrawable(appinfo.loadIcon(pm));
				mRemoteAppInfo = new AppInfo(appinfo, pm);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
			mRCAppIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(mPackageName);
						startActivity(intent);
					} catch(Exception e) {
						Toast.makeText(getActivity(), getString(R.string.rc_unable_to_run)+"\n"+mPackageName, Toast.LENGTH_SHORT).show();
						Log.e(TAG, "PackageName:"+mPackageName);
						e.printStackTrace();
					}
				}
			});
		}
		
		mRemotePauseResume = (ImageButton)v.findViewById(R.id.remote_control_pauseresume);
		if(mRemotePauseResume != null) {
			mRemotePauseResume.setOnClickListener(mRemoteButtonListener);
		}
		mRemotePrev = (ImageButton)v.findViewById(R.id.remote_control_prev);
		if(mRemotePrev != null) {
			mRemotePrev.setOnClickListener(mRemoteButtonListener);
		}
		mRemoteNext = (ImageButton)v.findViewById(R.id.remote_control_next);
		if(mRemoteNext != null) {
			mRemoteNext.setOnClickListener(mRemoteButtonListener);
		}
		mRemotePlaytime = (TextView)v.findViewById(R.id.remote_seek_playtime);
		mRemoteDuration = (TextView)v.findViewById(R.id.remote_seek_duration);
		
		mRemoteSeekBar = (SeekBar)v.findViewById(R.id.remote_seek_progress);
		mRemoteSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mPosOverride = false;
				mRCService.seekTo(seekBar.getProgress());
//				try {
//					mService.remoteSeek(seekBar.getProgress());
//				} catch(RemoteException e) {}
			}			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				mPosOverride = true;
			}			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}
		});
		*/
		
		
		mLayoutMidButton = (RelativeLayout)v.findViewById(R.id.music_layout_midbuttons);
		mLayoutTopButton = (LinearLayout)v.findViewById(R.id.music_layout_topbuttons);
		mLayoutMenuButton = (LinearLayout)v.findViewById(R.id.music_layout_menubuttons);

		mLayoutRemoteControl = v.findViewById(R.id.music_layout_remotecontrol);
		mLayoutLocalControl = v.findViewById(R.id.music_layout_controls);
				
		mSeekBarPlaytime = (SeekBar)v.findViewById(R.id.music_seek_progress);
		if(mSeekBarPlaytime != null) {
			mSeekBarPlaytime.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					mPosOverride = false;
					doSeek(seekBar.getProgress());
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					mPosOverride = true;
				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				}
			});
		}
		
		mTextPlaytime = (TextView)v.findViewById(R.id.music_seek_playtime);
		mTextDuration = (TextView)v.findViewById(R.id.music_seek_duration);
		
		mLayoutSeek = (LinearLayout)v.findViewById(R.id.music_layout_seek);

		mRemoteExpand = (ImageButton)v.findViewById(R.id.remote_button_expand);
		if(mRemoteExpand != null) {
			mRemoteExpand.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					MainActivity.bHideVisualizerMode = true;
					startActivity(new Intent(getActivity(), VisualizerFullActivity.class));
				}
			});
		}
		
		mBtnExpand = (ImageButton)v.findViewById(R.id.music_button_expand);
		if(mBtnExpand != null) {
			mBtnExpand.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					MainActivity.bHideVisualizerMode = true;
					startActivity(new Intent(getActivity(), VisualizerFullActivity.class));
				}
			});
		}

		mBtnVolume = (ImageButton)v.findViewById(R.id.music_button_volume);
		mBtnVolume.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AudioManager audiomanager = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
				int volume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
				audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
			}
		});

		mBtnAppInfo = (ImageButton)v.findViewById(R.id.music_button_info);
		mBtnAppInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				iMainActivity.requestToShowAppInfo();
			}
		});
		
		mBtnSearch = (ImageButton)v.findViewById(R.id.music_button_search);
		if(mBtnSearch != null) {
			mBtnSearch.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(getActivity(), R.string.under_development, Toast.LENGTH_SHORT).show();
				}
			});
		}
		
		mLayoutCover = (RelativeLayout)v.findViewById(R.id.music_layout_cover);
		
		mMiniNowplayInfo = (TextView)v.findViewById(R.id.mini_nowplaying_info);		
		
		mLayoutMiniPlayer = (LinearLayout)v.findViewById(R.id.music_layout_miniplayer);
		
		mBtnAddToNowplay = (ImageButton)v.findViewById(R.id.music_button_addtonowplay);
		if(mBtnAddToNowplay != null) {
			mBtnAddToNowplay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mLibraryFragment.addToNowplay();
				}
			});
		}

		mBtnResetNowplay = (ImageButton)v.findViewById(R.id.music_button_resetnowplay);
		if(mBtnResetNowplay != null) {
			mBtnResetNowplay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					resetNowplay();
				}
			});
		}
		
		mLayoutPlayback = (LinearLayout)v.findViewById(R.id.music_layout_playback);
		mLayoutPlayback.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mTouchAlbumart.onTouchEvent(event);
				return true;
			}
		});

		View usemicBG = v.findViewById(R.id.usemic_background);
		if(PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_MIC_USE, false)) {
			usemicBG.setVisibility(View.VISIBLE);
			mLayoutTopButton.setVisibility(View.GONE);
			mLayoutPlayback.setVisibility(View.GONE);
		}
		
		mBtnLibrary = (ImageButton)v.findViewById(R.id.music_button_library);
		mBtnLibrary.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				iMainActivity.requestToShowLibrary();
			}
		});
		
//		mBtnRatio = (ImageButton)v.findViewById(R.id.fullview_button_ratio);
//		mBtnRatio.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				plusVisualizerRatio();
//			}
//		});
//		updateVisualizerRatioButton();
		
		mBtnEQ = (ImageButton)v.findViewById(R.id.music_button_equalizer);
		if(mBtnEQ != null) {
			mBtnEQ.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mService == null)
						return;
					try {
						Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
						intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, mService.getAudioSessionId());
						intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getActivity().getPackageName());
						startActivityForResult(intent, REQUEST_CODE_AUDIOEFFECT);
					}
					catch (RemoteException e) {
						e.printStackTrace();
					}
					catch (ActivityNotFoundException e) {
						Toast.makeText(getActivity(), R.string.toast_not_available_eq, Toast.LENGTH_SHORT).show();
					}
				}
			});
		}

		/*
		mBtnSeek = (ImageButton)v.findViewById(R.id.music_button_seek);
		if(mBtnSeek != null) {
			mBtnSeek.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					toggleSeekLayout();
				}
			});
		}
		*/
		
		mBtnSettingRemote = (ImageButton)v.findViewById(R.id.remote_visualizer_setting);
		if(mBtnSettingRemote != null) {
			mBtnSettingRemote.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mSettingFragment != null && mSettingFragment.isAdded()) {
						hideVisualizerSetting(true);
					} else {
						showVisualizerSetting();
					}
				}
			});
		}

		mBtnMainSettings = (ImageButton)v.findViewById(R.id.music_button_mainSettings);
		if(mBtnMainSettings != null) {
			mBtnMainSettings.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					iMainActivity.requestToShowSettings();
				}
			});
		}

		mBtnVisualizerSetting = (ImageButton)v.findViewById(R.id.music_button_visualizerSettings);
		if(mBtnVisualizerSetting != null) {
			mBtnVisualizerSetting.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mSettingFragment != null && mSettingFragment.isAdded()) {
						hideVisualizerSetting(true);
					} else {
						showVisualizerSetting();
					}
				}
			});
		}
		
		mBtnNowplaying = (ImageButton)v.findViewById(R.id.music_button_nowPlaying);
		if(mBtnNowplaying != null) {
			mBtnNowplaying.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mNowplayingFragment != null && mNowplayingFragment.isAdded()) {
						hideNowplaying(true);
					} else {
						showNowplaying();
					}
				}
			});
		}

		/*
		mBtnRepeat = (ImageButton)v.findViewById(R.id.music_button_repeat);
		if(mBtnRepeat != null) {
			mBtnRepeat.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mPlayPos >= 0) {
						toggleRepeat();
					}
				}
			});
		}
		
		mBtnShuffle = (ImageButton)v.findViewById(R.id.music_button_shuffle);
		if(mBtnShuffle != null) {
			mBtnShuffle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mPlayPos >= 0) {
						toggleShuffle();
					}
				}
			});
		}
		*/
		
		mLayoutToggleButton = (RelativeLayout)v.findViewById(R.id.music_layout_togglebutton);
		if(mLayoutToggleButton != null) {
			mLayoutToggleButton.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					mTouchMini.onTouchEvent(event);
					return true;
				}
			});
		}
		
		mBtnPauseResume = (ImageButton)v.findViewById(R.id.button_pauseresume);
		if(mBtnPauseResume != null) {
			mBtnPauseResume.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					doPauseResume();
				}
			});
		}
		
		mCtrPauseResume = (ImageButton)v.findViewById(R.id.music_control_pauseresume);
		if(mCtrPauseResume != null) {
			mCtrPauseResume.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					doPauseResume();
				}
			});
		}
		
		mCtrNext = (ImageButton)v.findViewById(R.id.music_control_next);
		if(mCtrNext != null) {
			mCtrNext.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					doNext();
				}
			});
		}
		
		mCtrPrev = (ImageButton)v.findViewById(R.id.music_control_prev);
		if(mCtrPrev != null) {
			mCtrPrev.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					doPrev();
				}
			});
		}
		
		mBtnPlayAll = (ImageButton)v.findViewById(R.id.music_button_playall);
		if(mBtnPlayAll != null) {
			mBtnPlayAll.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					requestPlayAll();
				}
			});
		}
		
		int alpha = PreferenceUtils.loadIntegerValue(getActivity(), PreferenceUtils.KEY_BG_ALPHA);
		
		mLayoutFull = (View)v.findViewById(R.id.music_layout_full);
		
		mLayoutBackground = (View)v.findViewById(R.id.music_layout_background);
		mLayoutBackground.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mTouchVisualizer.onTouchEvent(event);
				return true;
			}
		});
		
		mLayoutVisualizer = (RelativeLayout)v.findViewById(R.id.music_layout_visualizer);
		mLayoutVisualizer.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mTouchVisualizer.onTouchEvent(event);
				return true;
			}
		});
		
//		mImgBackground = (ImageView)v.findViewById(R.id.music_full_background);
		if(mImgBackground != null) {			
			mImgBackground.setAlpha((1.0f*alpha)/0xff);
		}
		((MainActivity)getActivity()).setBackgroundAlpha((1.0f*alpha)/0xff);
		
//		mTextTitle = (TextView)findViewById(R.id.track_title);
		if(mTextTitle != null) {
			mTextTitle.setSelected(true);
		}
		
		mImgProgressDim = (ImageView)v.findViewById(R.id.rectange_progress_dim);
		mImgProgressDim.setImageDrawable(new ArcDrawable(Color.argb(0xff, 255, 255, 255), (int)(5*getActivity().getResources().getDisplayMetrics().density), 360));
		
		mImgProgress = (ImageView)v.findViewById(R.id.rectange_progress);
		mImgProgress.setImageDrawable(new ArcDrawable(Color.argb(0xff, 70, 132, 232), (int)(5*getActivity().getResources().getDisplayMetrics().density), 0));
				
		mVisualizerView = (IVisualizerView)v.findViewById(R.id.visualizer_fullview);
		mAlbumartImage = (ImageView)v.findViewById(R.id.track_albumart);
		mAlbumartBG = (ImageView)v.findViewById(R.id.track_albumart_bg);
			
		
		if(PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_VI_CLEANMODE, true)) {
			toggleMenuNController();
		}				
		setAlbumartSize(PreferenceUtils.loadIntegerValue(getActivity(), PreferenceUtils.KEY_ALBUM_SIZE, 50));
		
		if(PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_SEEK_SHOW, false)) {
			toggleSeekLayout();
		}
		
		// TouchUtils
		mTouchAlbumart = new TouchUtils(new OnTouchHandleListener() {
			@Override
			public void onTouchNone(TouchUtils detector) {}
			@Override
			public void onTouchMove(TouchUtils detector) {}
			@Override
			public void onTouchEnd(TouchUtils detector) {}
			@Override
			public void onTouchDClick(TouchUtils detector) {}			
			@Override
			public void onTouchBegin(TouchUtils detector) {}
			
			@Override
			public void onTouchFling(TouchUtils detector) {
				if(detector.getDirection() == TouchUtils.DIRECTION_DOWN) {
					showNowplaying();
//					if(detector.getY() < mLayoutPlayback.getHeight()/2) {
//						showNowplaying();
//					}
//					else {
//						showLibrary();
//					}
				}
				else if(detector.getDirection() == TouchUtils.DIRECTION_UP) {
					// do nothing
				}
				else {
					if(detector.getY() > mLayoutPlayback.getHeight()/2) {
						if(detector.getVelocityX() < 0) { 
							doNext();
						} else {
							doPrev();
						}
					} else {
						if(detector.getVelocityX() < 0) { 
							doPrev();
						} else {
							doNext();
						}
					}
				}
			}
			
			@Override
			public void onTouchSelect(TouchUtils detector) {
				float density = getActivity().getResources().getDisplayMetrics().density;
				int x = detector.getX() - mLayoutPlayback.getWidth()/2;
				int y = detector.getY() - mLayoutPlayback.getHeight()/2;
				double distance = Math.sqrt(x*x+y*y);
				if(distance > mImgProgress.getWidth()/2 - 10*density && distance < mImgProgress.getWidth()/2 + 10*density)  {
					
				}
				else {
					if(detector.getX() > mAlbumartBG.getWidth()/2) {						
						doNext();
					} else {					 
						doPrev();
					}
				}
			}
		});
		
		mTouchMini = new TouchUtils(new OnTouchHandleListener() {
			@Override
			public void onTouchNone(TouchUtils detector) {}
			@Override
			public void onTouchMove(TouchUtils detector) {}
			@Override
			public void onTouchEnd(TouchUtils detector) {}
			@Override
			public void onTouchDClick(TouchUtils detector) {}			
			@Override
			public void onTouchBegin(TouchUtils detector) {}
			
			@Override
			public void onTouchSelect(TouchUtils detector) {
				doPauseResume();
			}
			
			@Override
			public void onTouchFling(TouchUtils detector) {
				if(detector.getDirection() == TouchUtils.DIRECTION_UP) {
					hideLibrary(true);
				}
				else if(detector.getDirection() == TouchUtils.DIRECTION_LEFT) {
					doNext();
				}
				else if(detector.getDirection() == TouchUtils.DIRECTION_RIGHT) {
					doPrev();
				}
			}
		}); 
		
		// TouchUtils
		mTouchVisualizer = new TouchUtils(new OnTouchHandleListener() {
			@Override
			public void onTouchNone(TouchUtils detector) {}
			@Override
			public void onTouchMove(TouchUtils detector) {}
			@Override
			public void onTouchEnd(TouchUtils detector) {}
			@Override
			public void onTouchDClick(TouchUtils detector) {}			
			@Override
			public void onTouchBegin(TouchUtils detector) {}
			@Override
			public void onTouchSelect(TouchUtils detector) {
				toggleMenuNController();
			}
			
			@Override
			public void onTouchFling(TouchUtils detector) {
				if(detector.getDirection() == TouchUtils.DIRECTION_DOWN) {
					showLibrary();
				}
				else if(detector.getDirection() == TouchUtils.DIRECTION_UP) {
//					showVisualizerSetting();
				}
				else if(detector.getDirection() == TouchUtils.DIRECTION_LEFT) {
					/*if(mRemoteControl != null) {
						if(mRemoteControl.sendMediaButtonClick(KeyEvent.KEYCODE_MEDIA_NEXT)) {
				    	   Toast.makeText(getActivity(), R.string.toast_rc_noti_preplay, Toast.LENGTH_SHORT).show();
				    	   getActivity().sendBroadcast(new Intent("com.android.music.musicservicecommand.togglepause"));
				        }
					}
					else */{
						doNext();
					}
				}
				else if(detector.getDirection() == TouchUtils.DIRECTION_RIGHT) {
					/*if(mRemoteControl != null) {
						if(mRemoteControl.sendMediaButtonClick(KeyEvent.KEYCODE_MEDIA_PREVIOUS)) {
				    	   Toast.makeText(getActivity(), R.string.toast_rc_noti_preplay, Toast.LENGTH_SHORT).show();
				    	   getActivity().sendBroadcast(new Intent("com.android.music.musicservicecommand.togglepause"));
				        }
					}
					else*/ {
						doPrev();
					}
				}
			}
		});
		
		// default albumart
		mDefaultAlbumart = BitmapFactory.decodeResource(getResources(), R.drawable.albumart_mp_unknown);

		if(isRemoteService) {
			if(SystemUtils.isPortraitMode(getActivity())) {
				mLayoutRemoteControl.setVisibility(View.VISIBLE);
				mLayoutMidButton.setVisibility(View.GONE);
				mLayoutTopButton.setVisibility(View.GONE);
			}
			else {
				if(PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_VI_CLEANMODE, true)) {
					mLayoutRemoteControl.setVisibility(View.GONE);
				} 
				else {
					mLayoutRemoteControl.setVisibility(View.VISIBLE);
				}
				
				if(mLayoutMenuButton != null) {
					mLayoutMenuButton.setVisibility(View.INVISIBLE);
				}
			}
			mLayoutPlayback.setVisibility(View.GONE);
		} 
//		else {
//			mLayoutRemoteControl.setVisibility(View.GONE);
//			mLayoutPlayback.setVisibility(View.VISIBLE);
//		}
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		iMainActivity = (IMainActivity)getActivity();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CODE_AUDIOEFFECT) {
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		Log.i(TAG, "onCreateView()");
        mContainer = container;
        
		View v = inflater.inflate(R.layout.music_m_fullview, container, false);
		setContentView(v);        
        
		refreshAllView(true);
		return v;
	}	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate()");
		super.onCreate(savedInstanceState);

		// 조명 항상 켜짐 설정
		if(PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_BL_ALWAYSON, true)) {
			getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		
		//getActivity().getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		
		isRemoteService = PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_RC_AUTHORIZED, false);
		if(isRemoteService) {
			if(android.os.Build.VERSION.SDK_INT < 19 || android.os.Build.VERSION.SDK_INT > 20) {
				isRemoteService = false;
				PreferenceUtils.saveBooleanValue(getActivity(), PreferenceUtils.KEY_RC_AUTHORIZED, false);
			}
		}
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}	

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStart() {		
		Log.i(TAG, "onStart()");
		super.onStart();
				
		paused = false;
		
		VisualizerManager.getInstance().setupView(mVisualizerView);
		if(mVisualizerView != null) {
			mVisualizerView.refreshChanged();
		}
		
		IntentFilter f = new IntentFilter();
        f.addAction(MediaPlaybackService.PLAYSTATE_CHANGED);
        f.addAction(MediaPlaybackService.META_CHANGED);
        f.addAction(MediaPlaybackService.INFO_CHANGED);
        f.addAction(MediaPlaybackService.SHUFFLE_CHANGED);
        f.addAction(MediaPlaybackService.REPEAT_CHANGED);
        getActivity().registerReceiver(mStatusListener, f);
        
        MainActivity.bHideVisualizerMode = false;
        
        if(isRemoteService) {
        	Intent intent = new Intent(RemoteControlService.BIND_RC_CONTROL_SERVICE);
        	getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
	}

	@Override
	public void onStop() {
		VLog.i(TAG, "onStop()");
		super.onStop();
		paused = true;
		
		getActivity().unregisterReceiver(mStatusListener);
		
		if(mBound) {
			mRCService.setRemoteControllerDisabled();
			getActivity().unbindService(mConnection);
			mBound = false;
		}
	}

	@Override
	public boolean onBackPressed() {
		/*
		boolean done = false;
		if(mSettingFragment != null && mSettingFragment.isAdded()) {
			hideVisualizerSetting(true);
			done = true;
		}
		if(mNowplayingFragment != null && mNowplayingFragment.isAdded()) {
			hideNowplaying(true);
			done = true;
		}
		return done;
		*/
		return false;
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {	       
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i(TAG, "onServiceConnected:"+className);
			mBound = true;
			mHandler.postDelayed(mRunnableCheckRemoteClient, 2000);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "onServiceDisconnected:"+name);
			mBound = false;
		}
	};
	
	void createRemoteAppDialog() {
//		if(PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_IGNORE_REMOTEAPP_POPUP, false))
//			return;
		
		if(mRemoteAppInfo == null) {
			Toast.makeText(getActivity(), R.string.remotecontrol_title2, Toast.LENGTH_SHORT).show();
			return;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setIcon(mRemoteAppInfo.getIcon());
		builder.setTitle(mRemoteAppInfo.getAppName())
				.setMessage(R.string.music_popup_remote_app_content)
			   .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(mPackageName);
						startActivity(intent);
					}
				})
			   .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		
//		LayoutInflater inflater = getActivity().getLayoutInflater();
//		View view = inflater.inflate(R.layout.remoteapp_show_dialog, null);
//		builder.setView(view);
//		CheckBox ignoreBox = (CheckBox)view.findViewById(R.id.visualizer_donotshowagain);
//		ignoreBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				PreferenceUtils.saveBooleanValue(getActivity(), PreferenceUtils.KEY_IGNORE_REMOTEAPP_POPUP, isChecked);
//			}
//		});
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	Runnable mRunnableCheckRemoteClient = new Runnable() {
		@Override
		public void run() {
			Log.d(TAG, "RC playtime:"+mRCService.getEstimatedPosition());
			if(mRCService.getEstimatedPosition() >= 0) {
				mRCActivated = true;
			}
			
//			if(!mRCActivated) {				
//				createRemoteAppDialog();
//			}
		}
	};

	private void changeAlbumart(int direction) {
		if(direction == 0 || paused || isNowMiniAlbumart() || mLayoutPlayback.getVisibility() != View.VISIBLE) {
			setAlbumart();
			return;
		}	
		
		AnimationSet set = new AnimationSet(false);		
		Animation dummy = AnimationUtils.loadAnimation(getActivity(), R.anim.dummy);
		dummy.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}			
			@Override
			public void onAnimationEnd(Animation animation) {
				setAlbumart();
			}
		});
		Animation ani = null;
		if(direction > 0) {
			ani = AnimationUtils.loadAnimation(getActivity(), R.anim.rotation_cw_360);
		} else if(direction < 0) {
			ani = AnimationUtils.loadAnimation(getActivity(), R.anim.rotation_ccw_360);
		}
		ani.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}			
			@Override
			public void onAnimationEnd(Animation animation) {
				//mLayoutPlayback.setAlpha(PreferenceUtils.loadIntegerValue(getActivity(), PreferenceUtils.KEY_ALBUM_ALPHA)*1.0f/0xff);
			}
		});
		
		
		set.addAnimation(ani);
		set.addAnimation(dummy);
		
		//mLayoutPlayback.setAlpha(1.0f);
		mAlbumartImage.startAnimation(set);
	}
	
	/*
	private void plusVisualizerRatio() {
		mVisualizerView.plusRatio();
		
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.rotation_cw_90);
		ani.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}			
			@Override
			public void onAnimationEnd(Animation animation) {
				updateVisualizerRatioButton();
			}
		});
		mBtnRatio.startAnimation(ani);
	}
	
	private void updateVisualizerRatioButton() {
		float value = PreferenceUtils.loadFloatValue(getActivity(), PreferenceUtils.KEY_VISUALIZER_RATIO, 0.6f);
		if(value >= 0.9f) {
			mBtnRatio.setImageResource(R.drawable.ratio_button9_icon);
		}
		else if(value >= 0.8f) {
			mBtnRatio.setImageResource(R.drawable.ratio_button8_icon);
		}
		else if(value >= 0.7f) {
			mBtnRatio.setImageResource(R.drawable.ratio_button7_icon);
		}
		else {
			mBtnRatio.setImageResource(R.drawable.ratio_button_icon);
		}
	}
	*/
	
	private void toggleCover() {
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_in);
		ani.setInterpolator(new DecelerateInterpolator(5.0f));
		ani.setStartOffset(0);
		ani.setDuration(1000);
		ani.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}			
			@Override
			public void onAnimationRepeat(Animation animation) {}			
			@Override
			public void onAnimationEnd(Animation animation) {
				Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_down_out);
				ani.setInterpolator(new DecelerateInterpolator(3.0f));
				ani.setStartOffset(100);
				ani.setDuration(500);
				mLayoutCover.setVisibility(View.INVISIBLE);
				mLayoutCover.startAnimation(ani);
			}
		});
		mLayoutCover.startAnimation(ani);
		mLayoutCover.setVisibility(View.VISIBLE);
		mLayoutCover.startAnimation(ani);
	}
	
	private void showVisualizerSetting() {
		VLog.d(TAG, "showVisualizerSetting()");
		if(mSettingFragment != null && mSettingFragment.isAdded()) 
			return;
		
		hideNowplaying(true);
		
		mSettingFragment = new MusicSetting();
		mSettingFragment.setOnSettingListener(new MusicSettingListener() {
			@Override
			public Bitmap onRequestAlbumart() {
				return mColorAlbumart;
			}
			@Override
			public void onBackgroundAlphaChanged(float alpha) {
				setBackgroundAlpha(alpha);
				((MainActivity)getActivity()).setBackgroundAlpha(alpha);
			}
			@Override				
			public void onAlbumartSizeChanged(int size) {
				setAlbumartSize(size);
			}
			@Override
			public void onVisualizerAlphaChanged(int alpha) {
				setVisualizerAlpha(alpha);
			}
			@Override
			public void onVisualizerColorChanged(int color) {
				setVisualizerColor(color);
			}
			@Override
			public void onVisualizerSizeChanged(int size) {
				setVisualizerSize(size);
			}
		});
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		fragmentTransaction.add(R.id.fragment_playback_popup_layout, mSettingFragment, "VisualizerSetting");
		fragmentTransaction.commit();

		mBtnVisualizerSetting.setSelected(true);
	}
	
	private void hideVisualizerSetting(boolean withAni) {
		if(mSettingFragment == null || !mSettingFragment.isAdded()) 
			return;

		if(withAni) {
			mSettingFragment.hideAnimation();
		} else {
			mSettingFragment.dismiss();
		}
		mBtnVisualizerSetting.setSelected(false);
		mSettingFragment = null;
	}
	
	private boolean isNowMiniAlbumart() {
		return (mLibraryFragment != null && mLibraryFragment.isAdded() && SystemUtils.isPortraitMode(getActivity())); 
	}
	
	private void showDialogAutoNowplay() {
		if(getActivity() != null && !PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_IGNORE_INITPOPUP, false)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.music_popup_ask_autolist_title)
				   .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							doPauseResume();
						}
					})
				   .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View view = inflater.inflate(R.layout.auto_nowplaying_dialog, null);
			builder.setView(view);
			CheckBox ignoreBox = (CheckBox)view.findViewById(R.id.autolist_donotshowagain);
			ignoreBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					PreferenceUtils.saveBooleanValue(getActivity(), PreferenceUtils.KEY_IGNORE_INITPOPUP, isChecked);
				}
			});
			
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}

	private boolean mIsLibraryTabShown = false;
	private void showLibraryTab() {
		if(mIsLibraryTabShown)
			return;
		mIsLibraryTabShown = true;

		if(mLibraryTabFragment == null) {
			mLibraryTabFragment = new MainLibraryFragment();
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.music_fragment_library, mLibraryTabFragment, "Library");
			fragmentTransaction.commit();
		}

		View view = getView();
		if(view != null) {
			view.findViewById(R.id.music_fragment_library);
			view.setVisibility(View.VISIBLE);
		}
	}

	private void hideLibraryTab() {
		if(!mIsLibraryTabShown)
			return;
		mIsLibraryTabShown = false;

		View view = getView();
		if(view != null) {
			view.findViewById(R.id.music_fragment_library);
			view.setVisibility(View.GONE);
		}
	}
		
	private void showLibrary() {
		if(mLibraryFragment != null && mLibraryFragment.isAdded()) 
			return;
		
		// Fragment
		mLibraryFragment = new MusicLibraryFolder();
		mLibraryFragment.setOnLibraryListener(new MusicLibraryListener() {
			@Override
			public void onRequestEnQueue(long[] list) {				
				try {
					long[] newlist;
					long[] queue = mService.getQueue();
					int oldLength = queue.length;
					int addLength = 0;
					
					if(oldLength > 0) {
						newlist = ArrayUtils.checkDuplicate(queue, list);
						addLength = mService.enqueue(newlist, MediaPlaybackService.NOW);
					}
					else {
						newlist = list;
						mService.open(newlist, 0);
						mService.play();
						addLength = newlist.length;
					}
					if(mMiniNowplayInfo != null) {						
						queue = mService.getQueue();
						if(queue != null) {
							mMiniNowplayInfo.setText(oldLength+" + "+list.length+" - "+(list.length-addLength) +" = "+queue.length);
						}
					}
				}
				catch(RemoteException e) {					
				}
			}
		});
		
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		fragmentTransaction.add(R.id.music_fragment_library, mLibraryFragment, "Library");
		fragmentTransaction.commit();

		int top = 0;		
		float ratio = 1.0f;
		if(SystemUtils.isPortraitMode(getActivity())) {
			Rect rect = new Rect();
			mLayoutPlayback.getGlobalVisibleRect(rect);
			top = (int)mLayoutMiniPlayer.getY();			
			int height = rect.height();
//			if(mLayoutADparent != null) {
//				height += mLayoutADparent.getHeight();
//			}
			ratio = (mLayoutMiniPlayer.getHeight())*1.0f/height;
		}
		else {
			Point size = new Point();
			getActivity().getWindowManager().getDefaultDisplay().getSize(size);
			top = size.y;
			ratio = 1.0f;
		}		
		
		if(mLayoutFull != null) {
			TranslateAnimation trans = new TranslateAnimation(0, 0, 0, top);
			trans.setStartOffset(100);
			trans.setDuration(1000);
			trans.setInterpolator(new DecelerateInterpolator(4.0f));
			trans.setFillAfter(true);
			mLayoutFull.startAnimation(trans);
		}
		if(mLayoutPlayback != null) {
			ScaleAnimation scale = new ScaleAnimation(1.0f, ratio, 1.0f, ratio);
			scale.setStartOffset(100);
			scale.setDuration(1000);
			scale.setInterpolator(new DecelerateInterpolator(4.0f));
			scale.setFillAfter(true);
			mLayoutPlayback.startAnimation(scale);
		}
//		if(mLayoutVisualizer != null) {
//			Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_down_out);
//			ani.setStartOffset(100);
//			ani.setDuration(1000);
//			ani.setInterpolator(new DecelerateInterpolator(4.0f));
//			ani.setFillAfter(true);
//			mLayoutVisualizer.startAnimation(ani);
//		}
		
		// Button
		if(mLayoutTopButton != null && SystemUtils.isPortraitMode(getActivity())) {	
			try {
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLayoutTopButton.getLayoutParams();    			
				params.topMargin = top;
				mLayoutTopButton.setLayoutParams(params);
				
				TranslateAnimation trans = new TranslateAnimation(0, 0, -top, 0);
				trans.setStartOffset(100);
				trans.setDuration(1000);
				trans.setInterpolator(new DecelerateInterpolator(4.0f));
				trans.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {}
					@Override
					public void onAnimationRepeat(Animation animation) {}			
					@Override
					public void onAnimationEnd(Animation animation) {}
				});
				mLayoutTopButton.startAnimation(trans);
			}
			catch(ClassCastException e) {
			}
		}
		mBtnLibrary.setSelected(true);
		
		// MiniPlayer
		if(mLayoutMiniPlayer != null) {
			Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_in);
			ani.setStartOffset(100);
			ani.setDuration(1000);
			ani.setInterpolator(new DecelerateInterpolator(4.0f));
			mLayoutMiniPlayer.startAnimation(ani);
			mLayoutMiniPlayer.setVisibility(View.VISIBLE);
		}
		
//		if(mLayoutMenuButton != null) {
//			Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_down_out);
//			ani.setStartOffset(100);
//			ani.setDuration(1000);
//			ani.setInterpolator(new DecelerateInterpolator(4.0f));
//			mLayoutMenuButton.startAnimation(ani);
//			mLayoutMenuButton.setVisibility(View.INVISIBLE);
//		}
		
		if(mMiniNowplayInfo != null) {
			long[] queue = null;;
			try {
				queue = mService.getQueue();
			} catch(RemoteException e) {
			}
			if(queue != null) {
				mMiniNowplayInfo.setText(String.valueOf(queue.length));
			}
		}
		
		// NowPlaying
		hideNowplaying(false);
		hideVisualizerSetting(false);
	}
	
	private void hideLibrary(boolean withani) {
		if(mLibraryFragment == null || !mLibraryFragment.isAdded()) 
			return;
		
		if(withani) {
			mLibraryFragment.hideAnimation();
		} else {
			mLibraryFragment.dismiss();
		}
		mLibraryFragment = null;
				
		int top = 0;		
		float ratio = 1.0f;
		if(SystemUtils.isPortraitMode(getActivity())) {
			Rect rect = new Rect();
			mLayoutPlayback.getGlobalVisibleRect(rect);
			top = (int)mLayoutMiniPlayer.getY();
			ratio = (mLayoutMiniPlayer.getHeight())*1.0f/rect.height();
		}
		else {
			Point size = new Point();
			getActivity().getWindowManager().getDefaultDisplay().getSize(size);
			top = size.y;
			ratio = 1.0f;
		}
		
		if(mLayoutFull != null) {
			TranslateAnimation trans = new TranslateAnimation(0, 0, top, 0);
			trans.setStartOffset(0);
			trans.setDuration(1000);
			trans.setInterpolator(new DecelerateInterpolator(4.0f));
			mLayoutFull.startAnimation(trans);
		}
		if(mLayoutPlayback != null) {
			ScaleAnimation scale = new ScaleAnimation(ratio, 1.0f, ratio, 1.0f);
			scale.setStartOffset(0);
			scale.setDuration(1000);
			scale.setInterpolator(new DecelerateInterpolator(4.0f));
			scale.setFillAfter(false);
			mLayoutPlayback.startAnimation(scale);
		}
//		if(mLayoutVisualizer != null) {
//			Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_in);
//			ani.setStartOffset(0);
//			ani.setDuration(1000);
//			ani.setInterpolator(new DecelerateInterpolator(4.0f));
//			ani.setFillAfter(true);
//			mLayoutVisualizer.startAnimation(ani);
//		}
		
		// Button
		if(mLayoutTopButton != null && SystemUtils.isPortraitMode(getActivity())) {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLayoutTopButton.getLayoutParams();    			
			params.topMargin = 0;
			mLayoutTopButton.setLayoutParams(params);
			
			TranslateAnimation trans = new TranslateAnimation(0, 0, top, 0);
			trans.setStartOffset(0);
			trans.setDuration(1000);
			trans.setInterpolator(new DecelerateInterpolator(4.0f));
			trans.setFillAfter(false);
			trans.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {}
				@Override
				public void onAnimationRepeat(Animation animation) {}			
				@Override
				public void onAnimationEnd(Animation animation) {
				}
			});
			mLayoutTopButton.startAnimation(trans);
			mBtnLibrary.setSelected(false);
		}
		
		mBtnLibrary.setSelected(false);
		
		if(mLayoutMiniPlayer != null) {
			mLayoutMiniPlayer.setVisibility(View.INVISIBLE);
			Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_down_out);
			ani.setStartOffset(0);
			ani.setDuration(1000);
			ani.setInterpolator(new DecelerateInterpolator(4.0f));
			mLayoutMiniPlayer.startAnimation(ani);
		}
		
//		if(mLayoutMenuButton != null) {
//			Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_in);
//			ani.setStartOffset(100);
//			ani.setDuration(1000);
//			ani.setInterpolator(new DecelerateInterpolator(4.0f));
//			mLayoutMenuButton.startAnimation(ani);
//			mLayoutMenuButton.setVisibility(View.VISIBLE);
//		}
	}
	
	private void resetNowplay() {
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_in);
		ani.setInterpolator(new DecelerateInterpolator(3.0f));
		ani.setStartOffset(0);
		ani.setDuration(500);
		ani.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}			
			@Override
			public void onAnimationRepeat(Animation animation) {}			
			@Override
			public void onAnimationEnd(Animation animation) {
				Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_down_out);
				ani.setInterpolator(new DecelerateInterpolator(3.0f));
				ani.setStartOffset(100);
				ani.setDuration(500);
				mLayoutCover.setVisibility(View.INVISIBLE);
				mLayoutCover.startAnimation(ani);
				
				try {
		        	if(mService != null) {	            		
		        		mService.removeTracks(0, 100000);
						long[] queue = mService.getQueue();
						if(queue != null) {
							mMiniNowplayInfo.setText(String.valueOf(queue.length));
						}
		        	}
		        } catch (RemoteException ex) {
		        }
			}
		});
		mLayoutCover.startAnimation(ani);
		mLayoutCover.setVisibility(View.VISIBLE);
		mLayoutCover.startAnimation(ani);
	}
	
	private void requestPlayAll() {
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_in);
		ani.setInterpolator(new DecelerateInterpolator(3.0f));
		ani.setStartOffset(0);
		ani.setDuration(500);
		ani.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}			
			@Override
			public void onAnimationRepeat(Animation animation) {}			
			@Override
			public void onAnimationEnd(Animation animation) {
				Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_down_out);
				ani.setInterpolator(new DecelerateInterpolator(3.0f));
				ani.setStartOffset(100);
				ani.setDuration(500);
				mLayoutCover.setVisibility(View.INVISIBLE);
				mLayoutCover.startAnimation(ani);
				
				long[] queue = null;
				try {
		        	if(mService != null) {	            		
		        		mService.playAutoShuffleList();
		        		queue = mService.getQueue();
		        	}
		        } catch (RemoteException ex) {
		        }
				if(queue != null) {
					mMiniNowplayInfo.setText(String.valueOf(queue.length));
				}
			}
		});
		mLayoutCover.startAnimation(ani);
		mLayoutCover.setVisibility(View.VISIBLE);
		mLayoutCover.startAnimation(ani);
		
		/*
		try {
            if(mService != null) {
                if (mService.isPlaying()) {
                    mService.pause();
                }
                setPauseButtonImage();
            }
        } catch (RemoteException ex) {
        }		
		
		// status bar
		Rect statusRect = new Rect();
		getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(statusRect);
		
		// nowplaying button
		Rect rect = new Rect();
		mBtnNowplaying.getGlobalVisibleRect(rect);
		rect.top -= (getActivity().getActionBar().getHeight()+statusRect.top);
		
		AnimationSet set = new AnimationSet(false);
		
		// scale
		Animation scale = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_0_to_1);		
		scale.setStartOffset(0);
		scale.setDuration(500);
		scale.setInterpolator(new OvershootInterpolator(1.0f));
		
		// translate
		TranslateAnimation trans = new TranslateAnimation(0, rect.left, 0, rect.top);
		trans.setStartOffset(500);
		trans.setDuration(800);
		trans.setInterpolator(new DecelerateInterpolator(5.0f));
		
		set.addAnimation(scale);
		set.addAnimation(trans);
		
		set.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				mBtnNowplaying.setVisibility(View.INVISIBLE);
				mBtnNowplayingTemp.setVisibility(View.VISIBLE);
			}			
			@Override
			public void onAnimationRepeat(Animation animation) {}			
			@Override
			public void onAnimationEnd(Animation animation) {
				mBtnNowplayingTemp.setVisibility(View.GONE);
				mBtnNowplaying.setVisibility(View.VISIBLE);
				try {
	            	if(mService != null) {	            		
	            		mService.playAutoShuffleList();
	            	}
	            } catch (RemoteException ex) {
	            }
			}
		});
		mBtnNowplayingTemp.startAnimation(set);
		
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.move_right_to_ori);	
		ani.setInterpolator(new DecelerateInterpolator(5.0f));
		mBtnPlayAll.startAnimation(ani);
		*/
	}
	
	private void toggleRepeat() {
		if (mService == null) {
            return;
        }
        try {
            int mode = mService.getRepeatMode();
            if (mode == MediaPlaybackService.REPEAT_NONE) {
                mService.setRepeatMode(MediaPlaybackService.REPEAT_ALL);
            } else if (mode == MediaPlaybackService.REPEAT_ALL) {
                mService.setRepeatMode(MediaPlaybackService.REPEAT_CURRENT);
            } else {
                mService.setRepeatMode(MediaPlaybackService.REPEAT_NONE);
            }
        } catch (RemoteException ex) {
        }
        
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.rotation_cw_120);
		ani.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}			
			@Override
			public void onAnimationEnd(Animation animation) {
				setRepeatButtonImage();
			}
		});
		mBtnRepeat.startAnimation(ani);
	}
	
    private void toggleShuffle() {
        if (mService == null) {
            return;
        }
        try {
            int shuffle = mService.getShuffleMode();
            if (shuffle == MediaPlaybackService.SHUFFLE_NONE) {
                mService.setShuffleMode(MediaPlaybackService.SHUFFLE_NORMAL);
            } else if (shuffle == MediaPlaybackService.SHUFFLE_NORMAL) {
                mService.setShuffleMode(MediaPlaybackService.SHUFFLE_NONE);
            }
        } catch (RemoteException ex) {
        }
        
        Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.rotation_cw_360);
		ani.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}			
			@Override
			public void onAnimationEnd(Animation animation) {
				setShuffleButtonImage();
			}
		});
		mBtnShuffle.startAnimation(ani);
    }
	
	private void showNowplaying() {
		if(mNowplayingFragment != null && mNowplayingFragment.isAdded()) 
			return;
		
		hideVisualizerSetting(true);
		
		mNowplayingFragment = new MusicNowplaying();
		mNowplayingFragment.setOnNowplayingListener(new MusicNowplayingListener() {
			@Override
			public long[] onRequestQueue() {
				if(mService != null) {
					try {
						return mService.getQueue();
					} catch (RemoteException e) {
					}
		   		}
				return null;
			}
			@Override
			public int onRequestPosition() {
				if(mService != null) {
					try {
						return mService.getQueuePosition();
					} catch (RemoteException e) {
					}
		   		}
				return -1;
			}
			@Override
			public void onPositionChanged(int pos) {
				try {
			           if(mService != null) {
			        	   mService.setQueuePosition(pos);
			           }
			       } catch (RemoteException ex) {
			    }
			}
			@Override
			public void onAudioIdRemoved(long id) {
				if(mService != null) {
					try {
						mService.removeTrack(id);
					} catch (RemoteException e) {
					}
		   		}
			}
		});
		
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		fragmentTransaction.add(R.id.nowplaying_fragment, mNowplayingFragment, "Nowplaying");
		fragmentTransaction.commit();
		
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.rotation_cw_180);
		ani.setDuration(500);
		ani.setInterpolator(new DecelerateInterpolator(2.0f));
		ani.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}			
			@Override
			public void onAnimationEnd(Animation animation) {
				mBtnNowplaying.setSelected(true);
			}
		});
		mBtnNowplaying.startAnimation(ani);
	}
	
	private void hideNowplaying(boolean withani) {
		if(mNowplayingFragment == null || !mNowplayingFragment.isAdded()) 
			return;
		
		if(withani) {
			mNowplayingFragment.hideAnimation();
		} else {
			mNowplayingFragment.dismiss();
		}
		mNowplayingFragment = null;
		
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.rotation_ccw_180);
		ani.setDuration(500);
		ani.setInterpolator(new DecelerateInterpolator(2.0f));
		ani.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}			
			@Override
			public void onAnimationEnd(Animation animation) {
				mBtnNowplaying.setSelected(false);
			}
		});
		mBtnNowplaying.startAnimation(ani);
	}
	
	private void toggleMenuNController() {
		if(SystemUtils.isPortraitMode(getActivity()))
			return;
		
		if(mLayoutMenuButton == null)
			return;
		
		Log.d(TAG, "toggleMenuNController()");
		if(isRemoteService) {
			if(mLayoutRemoteControl.getVisibility() == View.VISIBLE) {
				mLayoutRemoteControl.setVisibility(View.INVISIBLE);
				PreferenceUtils.saveBooleanValue(getActivity(), PreferenceUtils.KEY_VI_CLEANMODE, true);
			} else {
				mLayoutRemoteControl.setVisibility(View.VISIBLE);
				PreferenceUtils.saveBooleanValue(getActivity(), PreferenceUtils.KEY_VI_CLEANMODE, false);
			}
		} else {
			/*
			if(mLayoutMenuButton.getVisibility() == View.VISIBLE) {
				mLayoutMenuButton.setVisibility(View.INVISIBLE);
				mLayoutSeek.setVisibility(View.INVISIBLE);
				PreferenceUtils.saveBooleanValue(getActivity(), PreferenceUtils.KEY_VI_CLEANMODE, true);
			}
			else {
				mLayoutMenuButton.setVisibility(View.VISIBLE);
				PreferenceUtils.saveBooleanValue(getActivity(), PreferenceUtils.KEY_VI_CLEANMODE, false);
				
				if(PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_SEEK_SHOW, false)) {
					toggleSeekLayout();
				}
			}
			*/
		}
	}
	
	private void toggleSeekLayout() {
		Log.d(TAG, "toggleSeekLayout()");
		if(mLayoutSeek == null)
			return;
		
		if(mLayoutSeek.getVisibility() == View.VISIBLE) {
			mLayoutSeek.setVisibility(View.GONE);
			mBtnSeek.setSelected(false);
			PreferenceUtils.saveBooleanValue(getActivity(), PreferenceUtils.KEY_SEEK_SHOW, false);
		}
		else {
			mLayoutSeek.setVisibility(View.VISIBLE);	
			mBtnSeek.setSelected(true);
			PreferenceUtils.saveBooleanValue(getActivity(), PreferenceUtils.KEY_SEEK_SHOW, true);
		}
	}
	
	private void refreshNowPlaying() {
		if(mNowplayingFragment == null || !mNowplayingFragment.isAdded()) 
			return;
		
		mNowplayingFragment.refreshView(null);
	}

	private void doNext() {
		if (mService == null) return;
		if (mPlayPos < 0)  return;
        try {
            mService.next();
            mDirection = 1;
        } catch (RemoteException ex) {
			ex.printStackTrace();
        }
	}
	
	private void doPrev() {
		if (mService == null) return;
		if (mPlayPos < 0)  return;
        try {
            if (mService.position() < 10000) {
                mService.prev();
                mDirection = -1;
            } else {
                mService.seek(0);
                mService.play();
            }
        } catch (RemoteException ex) {
			ex.printStackTrace();
        }
	}
	
   private void doPauseResume() {
       try {
            if(mService != null) {
                if (mService.isPlaying()) {
                    mService.pause();
                } else {
                    mService.play();
                }
//              refreshNow();
//              setPauseButtonImage();
            }
        } catch (RemoteException ex) {
		   ex.printStackTrace();
        }
    }
   
   private void doSeek(long seek) {
	   try {
		   if(mService != null) {
			   mService.seek(seek);
		   }
	   } catch (RemoteException ex) {
		   ex.printStackTrace();
       }
   }
   
   private void setRepeatButtonImage() {
	   if(mService == null)
		   return;
	   
	   try {
			if(mService.getRepeatMode() == MediaPlaybackService.REPEAT_NONE) {
				mBtnRepeat.setActivated(false);
				mBtnRepeat.setSelected(false);
			} else if(mService.getRepeatMode() == MediaPlaybackService.REPEAT_ALL) {
				mBtnRepeat.setActivated(true);
				mBtnRepeat.setSelected(false);
			} else if(mService.getRepeatMode() == MediaPlaybackService.REPEAT_CURRENT) {
				mBtnRepeat.setActivated(false);
				mBtnRepeat.setSelected(true);
			}
		} catch (RemoteException ex) {
		   ex.printStackTrace();
       }
   }
   
   private void setShuffleButtonImage() {
	   if (mService == null) return;
       try {
           switch (mService.getShuffleMode()) {
           case MediaPlaybackService.SHUFFLE_NONE:
        	   mBtnShuffle.setSelected(false);
               break;
           default:
        	   mBtnShuffle.setSelected(true);
               break;
           }
       } catch (RemoteException ex) {
		   ex.printStackTrace();
       }
   }
   
   	private void setAlbumart() {
   		if(mColorAlbumart == null)
   			return;
   		
   		try {
   			RoundDrawable drawable = new RoundDrawable(mColorAlbumart);
   			if(mService.isPlaying()) {
   				mAlbumartImage.setVisibility(View.VISIBLE);
//   			mImgBackground.setVisibility(View.VISIBLE);
   				
   				drawable.setGray(false);
				mAlbumartImage.setImageDrawable(drawable);
				
				if(mImgBackground != null) {
					mImgBackground.setImageBitmap(mColorAlbumart);
				}
				((MainActivity)getActivity()).setBackgroundBitmap(mColorAlbumart);
								
				ArcDrawable arc = (ArcDrawable)mImgProgress.getDrawable();
				arc.setColor(Color.argb(0xff, 70, 132, 232));
            	mImgProgress.invalidate();
   	   		} else {
//   	   			if(VisualizerManager.getInstance().isMusicActive()) {   	   				
//   	   				mAlbumartImage.setVisibility(View.GONE);
//   	   				getActionBar().setTitle(R.string.anotherapp_is_playing);
//   	   			}
   	   			
				drawable.setGray(true);
				mAlbumartImage.setImageDrawable(drawable);
				if(mImgBackground != null) {
					mImgBackground.setImageBitmap(mColorAlbumart);
				}
				((MainActivity)getActivity()).setBackgroundBitmap(mColorAlbumart);
				
				ArcDrawable arc = (ArcDrawable)mImgProgress.getDrawable();   
				arc.setColor(Color.argb(0xff, 189, 189, 189));
            	mImgProgress.invalidate();
   	   		}
	   	} catch (RemoteException ex) {
	    } 
   	}
   	
   	public void setAlbumartSize(int percent) {
   		if(SystemUtils.isPortraitMode(getActivity()) || mLayoutPlayback.getVisibility() == View.GONE)
   			return;
   		
   		if(isRemoteService)
   			return;
   		
   		float density = getActivity().getResources().getDisplayMetrics().density;
   		int minSize = (int)(80*density);
   		int maxSize = (int)(250*density);
   		int size = minSize+percent*(maxSize-minSize)/100;
   		
   		if(size == minSize) {
   			mLayoutPlayback.setVisibility(View.INVISIBLE);
   		}
   		else {
   			mLayoutPlayback.setVisibility(View.VISIBLE);

   			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLayoutPlayback.getLayoutParams();
   			params.width = size;
   			params.height = size;
   			mLayoutPlayback.setLayoutParams(params);
   		}
		setPauseButtonImage();
   	}
   	
   	public void setBackgroundAlpha(float alpha) {
   		if(mImgBackground == null) 
   			return;
   		
   		if(alpha == 0.0f) {
   			mImgBackground.setVisibility(View.GONE);
   		} else {
   			mImgBackground.setVisibility(View.VISIBLE);
   			mImgBackground.setAlpha(alpha);
   		}
   	}
   	
   	public void setVisualizerAlpha(int alpha) {   		
   		mVisualizerView.setAlpha(alpha);   		
   	}
   	
   	public void setVisualizerColor(int color) {   		
   		mVisualizerView.setColorSet(color);
   	}
   	
   	public void setVisualizerSize(int size) {   		
   		mVisualizerView.setBarSize(size);
   	}
   
   	private void setPauseButtonImage() {
	   if(mService == null)
		   return;
	   
	   boolean isPlaying = false;
	   try {
		   isPlaying = mService.isPlaying();
	   } catch (RemoteException ex) {}
	   
	   if(mBtnPauseResume != null) {
		   if(mLayoutPlayback.getWidth() < mBtnPauseResume.getWidth()*3) {
			   mBtnPauseResume.setImageBitmap(null);
		   }
		   else {
			   if(isPlaying) {
				   mBtnPauseResume.setImageResource(R.drawable.btn_pause_new);
	           } else {
	       		   mBtnPauseResume.setImageResource(R.drawable.btn_play_new);
	           }
		   }
	   }
	   if(mCtrPauseResume != null) {
		   if(isPlaying) {
			   mCtrPauseResume.setImageResource(R.drawable.music_button_pause);
           } else {
        	   mCtrPauseResume.setImageResource(R.drawable.music_button_play);
           }
	   }
   	}
   	
   	private void setRemoteButtonImage(boolean isPlaying) {
   		if(mRemotePauseResume != null) {
 		   if(isPlaying) {
 			  mRemotePauseResume.setImageResource(R.drawable.music_button_pause);
            } else {
            	mRemotePauseResume.setImageResource(R.drawable.music_button_play);
            }
 	   }
   	}
	
	private void setPlayTrack(long audioid) {
		Log.d(TAG, "setPlayTrack("+audioid+")");
		
		ContentResolver res = getActivity().getContentResolver();
        Cursor c = null;
        try {
            c = res.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media._ID + "="+audioid, null, null);
            if (c == null || c.getCount() == 0) {
            	mPlayTrack = null;
                return;
            }
            while(c.moveToNext()) {
            	if(mPlayTrack == null) {
    				mPlayTrack = new TrackItem(c);
    			} else {
    				mPlayTrack.setValues(c);
    			}
            }
        } catch (RuntimeException ex) {
        } finally {
            if (c != null) {
                c.close();
            }
        }
	}
	
	private void updateRemoteClientInfo() {
		if(getActivity() == null || mRCService == null)
			return;
		
		getActivity().getActionBar().setTitle(" "+mRemoteTracktitle);
		mRemotePlaytime.setText(makeTimeString(getActivity(), (mRCService.getEstimatedPosition() + 500) / 1000));
		mRemoteDuration.setText(makeTimeString(getActivity(), (remote_duration + 500) / 1000));
		mRemoteSeekBar.setMax((int)remote_duration);
	}
	
	private void updateTrackInfo() {
		if (mService == null)
            return;
		
		if(isRemoteService)
			return;
		
		int oldPos = mPlayPos;
		
		try {
			setPlayTrack(mService.getAudioId());
			
			mPlayPos = mService.getQueuePosition();
			if(mDirection != 0) {
				// do nothing
			}
			else if(oldPos == -1) {
				mDirection = 0;
			}
			else if(mPlayPos > oldPos) {
				mDirection = 1;
			}
			else if(mPlayPos < oldPos) {
				mDirection = -1;
			}
			mDuration = mService.duration();
			
			if(mSeekBarPlaytime != null) {
				mSeekBarPlaytime.setMax((int)mDuration);
				mTextDuration.setText(makeTimeString(getActivity(), (mDuration + 500) / 1000));
			}
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		if(mPlayTrack != null) {			
			mColorAlbumart = ArtUtils.getCachedArtwork(getActivity(), mPlayTrack.getAlbumId(), mDefaultAlbumart);
			mAlbumartImage.setVisibility(View.VISIBLE);
			
			if(mTextTitle != null) {
				mTextTitle.setText(mPlayTrack.getTitle()+" - "+mPlayTrack.getArtist());
			}
			
			changeAlbumart(mDirection);
			mDirection = 0;

			//getActivity().getActionBar().setTitle(" "+mPlayTrack.getTitle());
		} else {
			mAlbumartImage.setVisibility(View.GONE);
			((MainActivity)getActivity()).setBackgroundBitmap(null);
//				mImgBackground.setVisibility(View.GONE);
		}
	}	
	
	public void refreshVisualizer() {
		mVisualizerView.refreshChanged();
	}
	
	private static final int REFRESH = 1;
	
	private long refreshNow() {
		if(mService == null || getActivity() == null || mPosOverride)
            return 500;
		
		long pos = 0;
		if(isRemoteService) {			
			if(mRCService != null) {
				pos = mRCService.getEstimatedPosition();
	            if ((pos >= 0) && (remote_duration > 0)) {
	            	if(mRemoteSeekBar != null) {
	            		mRemoteSeekBar.setProgress((int)pos);
	            		mRemotePlaytime.setText(makeTimeString(getActivity(), (pos + 500) / 1000));
	    			}
	            } else {
	            	mRemotePlaytime.setText(makeTimeString(getActivity(), 0));
	            }
			}
            // calculate the number of milliseconds until the next full second, so
            // the counter can be updated at just the right time
            long remaining = 100 - (pos % 100);
            return remaining;
		}
		else {	        
	        try {
	            pos = mService.position();
	            if ((pos >= 0) && (mDuration > 0)) {
	            	float angle = (360 * (float)pos)/(float)mDuration;
	            	ArcDrawable arc = (ArcDrawable)mImgProgress.getDrawable();            	
	            	arc.setAngle(angle);
	            	mImgProgress.invalidate();
	            	
	            	if(mSeekBarPlaytime != null) {
	    				mSeekBarPlaytime.setProgress((int)pos);
	    				mTextPlaytime.setText(makeTimeString(getActivity(), (pos + 500) / 1000));
	    			}
	            } else {                
	            }
	            // calculate the number of milliseconds until the next full second, so
	            // the counter can be updated at just the right time
//	            long remaining = 100 - (pos % 100);
//	            return remaining;
	            if(mService.isPlaying()) {
	            	return 200;
	            } else {
	            	return 5000;
	            }
	        } catch (RemoteException ex) {
	        }	
		}
        
        return 500;
    }
	
	public void deleteSong() {
		long audioid;
		try {
			audioid = mService.getAudioId();
		}
		catch(RemoteException e) {
			audioid = -1;
		}
		
		final long delaudioid = audioid;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.action_delete_song);
        builder.setMessage(R.string.popup_delete_ask);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				try {
					mService.removeTrack(delaudioid);
				}
				catch(RemoteException e) { }
				getActivity().getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + "="+delaudioid, null);
			}
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.cancel();
			}
        });
        builder.show();
	}
	
	 /*
     * Try to use String.format() as little as possible, because it creates a
     * new Formatter every time you call it, which is very inefficient. Reusing
     * an existing Formatter more than tripled the speed of makeTimeString().
     * This Formatter/StringBuilder are also used by makeAlbumSongsLabel()
     */
    private StringBuilder sFormatBuilder = new StringBuilder();
    private Formatter sFormatter = new Formatter(sFormatBuilder, Locale.getDefault());
    private final Object[] sTimeArgs = new Object[5];
	
	public String makeTimeString(Context context, long secs) {
        String durationformat = context.getString(secs < 3600 ? R.string.durationformatshort : R.string.durationformatlong);
        /*
         * Provide multiple arguments so the format can be changed easily by
         * modifying the xml.
         */
        sFormatBuilder.setLength(0);

        final Object[] timeArgs = sTimeArgs;
        timeArgs[0] = secs / 3600;
        timeArgs[1] = secs / 60;
        timeArgs[2] = (secs / 60) % 60;
        timeArgs[3] = secs;
        timeArgs[4] = secs % 60;

        return sFormatter.format(durationformat, timeArgs).toString();
    }
	
	private void queueNextRefresh(long delay) {
        if (!paused) {
            Message msg = mHandler.obtainMessage(REFRESH);
            mHandler.removeMessages(REFRESH);
            mHandler.sendMessageDelayed(msg, delay);
        }
    }
	
	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH:
                    long next = refreshNow();
                    queueNextRefresh(next);
                    break;

                default:
                    break;
            }
        }
    };
	
    private BroadcastReceiver mStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "->action:"+action);
            if (action.equals(MediaPlaybackService.META_CHANGED)) {
                // redraw the artist/title info and
                // set new max for progress bar
                updateTrackInfo();
                refreshNowPlaying();
//                queueNextRefresh(1);
            } else if (action.equals(MediaPlaybackService.PLAYSTATE_CHANGED)) {
            	setPauseButtonImage();
                setAlbumart();
                queueNextRefresh(0);
            } 
//            else if (action.equals(MediaPlaybackService.SHUFFLE_CHANGED)) {
//                setShuffleButtonImage();
//            } else if (action.equals(MediaPlaybackService.REPEAT_CHANGED)) {
//                setRepeatButtonImage();
//            }
        }
    };
    
    
    private void refreshAllView(boolean bAnimation) {
    	if(getActivity() == null || mService == null) 
    		return;
    
    	if(isRemoteService) {
    		updateRemoteClientInfo();
    	}
    	else { 
	    	updateTrackInfo();
			setRepeatButtonImage();
			setPauseButtonImage();
			setShuffleButtonImage();
    	}
		
		long next = refreshNow();
        queueNextRefresh(next);
		
        if(bAnimation) {
        	showAnimation();
        }
    }
        
    private RemoteButtonListener mRemoteButtonListener = new RemoteButtonListener();

	@Override
	public boolean show() {
		return true;
	}

	@Override
	public boolean hide(OnHideListener listener) {
		return false;
	}

	@Override
	public void onServiceConnected(IMediaPlaybackService iService) {
		mService = iService;
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

	class RemoteButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			boolean bSuccess = false;
			if(v == mRemotePauseResume) {
				if(mIsRemotePlaying) 
					bSuccess = mRCService.sendPauseKey();
				else
					bSuccess = mRCService.sendPlayKey();
			} else if(v == mRemotePrev) {
				bSuccess = mRCService.sendPreviousKey();
			} else if(v == mRemoteNext) {
				bSuccess = mRCService.sendNextKey();
			}
			
			if(!bSuccess) {
				createRemoteAppDialog();
			}
		}
    }

	@Override
	public void setService(IMediaPlaybackService service) {
		mService = service;
		if(service != null) {
			try {
				refreshAllView(false);
				if(!isRemoteService) {
					long queue[] = mService.getQueue();					
					if(queue != null && queue.length == 0) {
						showDialogAutoNowplay();
					}
				}
			} catch(RemoteException e) {				
			}
			
			if(getActivity() != null) {
				if(PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_BL_ALWAYSON, true)) {
					getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				} else {
					getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				}	
			}
		}
	}

	@Override
	public void hideAnimation() {
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_out);
        ani.setInterpolator(new DecelerateInterpolator(5.0f));
        mContainer.startAnimation(ani);
	}

	@Override
	public void showAnimation() {
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in);
        ani.setInterpolator(new DecelerateInterpolator(5.0f));
        mContainer.startAnimation(ani);
	}
}


/*
private boolean isNLServiceRunning() {
ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
     if (NotificationListener.class.getName().equals(service.service.getClassName())) {
          return true;
     }
}

return false;
}*/