package noh.jinil.app.anytime.music;

import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.utils.PreferenceUtils;
import noh.jinil.app.anytime.visualizer.VisualizerMiniView;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VisualizerAnytime {
	private static final String TAG = "VisualizerAnytime";
	
	public static final String TOGGLE_FLOATING_VIEW = VisualizerManager.PACKAGE_NAME+".togglefloatingview";
	public static final String SHOW_FLOATING_VIEW = VisualizerManager.PACKAGE_NAME+".showfloatingview";
	public static final String HIDE_FLOATING_VIEW = VisualizerManager.PACKAGE_NAME+".hidefloatingview";
	public static final String CLOSE_FLOATING_VIEW = VisualizerManager.PACKAGE_NAME+".xfloatingview";
	public static final String TOGGLE_VIEW_RATIO = VisualizerManager.PACKAGE_NAME+".viewratio";
	public static final String TOGGLE_MIC_USE = VisualizerManager.PACKAGE_NAME+".micuse";
		
	RelativeLayout mFloatingLayout = null;
	LinearLayout mFloatingButtons = null;
	VisualizerMiniView mVisualizerView = null;
	ImageView mFloatingOnOff;
	ImageView mMicOnOff;
	LinearLayout mOnOffLayout;
	WindowManager mWindowManager = null;
	
	private boolean bShowWindow = false;
	private boolean bShowVisualizer = false;
	
	WindowManager.LayoutParams mBtnParams;
	
	public VisualizerAnytime() {		
	}
	
	public VisualizerAnytime(Context context, int sessionID) {
		Log.w(TAG, "VisualizerAnytime()");		
		VisualizerManager.getInstance().setAudioManager((AudioManager) context.getSystemService(Context.AUDIO_SERVICE), sessionID);

		if(PreferenceUtils.loadBooleanValue(context, PreferenceUtils.KEY_MIC_USE, false)) {
			VisualizerManager.getInstance().setupMic();
		} else {
			VisualizerManager.getInstance().setupSession(0);
		}
		
//		LayoutInflater inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );		
//		mFloatingLayout = (RelativeLayout) inflater.inflate( R.layout.visualizer_miniview, null );		
//		mVisualizerView = (VisualizerMiniView)mFloatingLayout.findViewById(R.id.visualizer_mini_view);		
		mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
	}
	
	public void release()  {
		Log.w(TAG, "release()");
		VisualizerManager.getInstance().release();
	}

	public void setupMic() {
		VisualizerManager.getInstance().setupMic();
		mVisualizerView.setUseMic(true);
	}

	public void setupSession() {
		VisualizerManager.getInstance().setupSession();
		mVisualizerView.setUseMic(false);
	}
	
	public void setSessionID(int sessionid) {
		VisualizerManager.getInstance().setupSession(sessionid);
	}
	
	public void plusViewRatio() {
		mVisualizerView.plusRatio();
	}
	
	public void showFloatingWindow(Context context) {
		bShowWindow = true;
		if(PreferenceUtils.loadBooleanValue(context, PreferenceUtils.KEY_VI_FLOATING_ONOFF, false)) {
			showFloatingButton(context);
		}
		showFloatingVisualizer(context);
	}

	public void hideFloatingWindow(Context context) {
		bShowWindow = false;
		hideFloatingButton(context);
		hideFloatingVisualizer(context);
	}
	
	private void showFloatingButton(final Context context) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
		mFloatingButtons = (LinearLayout) inflater.inflate( R.layout.visualizer_onoffbutton, null);
		mOnOffLayout = (LinearLayout)mFloatingButtons.findViewById(R.id.visualizer_floating_onoff_layout);
		mMicOnOff = (ImageView)mFloatingButtons.findViewById(R.id.visualizer_mic_onoff);
		mFloatingOnOff = (ImageView)mFloatingButtons.findViewById(R.id.visualizer_floating_onoff);

		mFloatingOnOff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(hasMoved)
					return;

				if(bShowVisualizer) {
					hideFloatingVisualizer(context);
					mFloatingOnOff.setImageResource(R.drawable.noti_visualizer_off);
				} else {
					showFloatingVisualizer(context);
					mFloatingOnOff.setImageResource(R.drawable.noti_visualizer_on);
				}
			}
		});
		mMicOnOff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(hasMoved)
					return;

				if(PreferenceUtils.loadBooleanValue(context, PreferenceUtils.KEY_MIC_USE, false)) {
					setupSession();
					PreferenceUtils.saveBooleanValue(context, PreferenceUtils.KEY_MIC_USE, false);
					mMicOnOff.setImageResource(R.drawable.microphone_off);
				} else {
					setupMic();
					PreferenceUtils.saveBooleanValue(context, PreferenceUtils.KEY_MIC_USE, true);
					mMicOnOff.setImageResource(R.drawable.microphone_on);
				}
			}
		});

		mFloatingOnOff.setImageResource(R.drawable.noti_visualizer_on);
		if(PreferenceUtils.loadBooleanValue(context, PreferenceUtils.KEY_MIC_USE, false)) {
			mMicOnOff.setImageResource(R.drawable.microphone_on);
		} else {
			mMicOnOff.setImageResource(R.drawable.microphone_off);
		}

		mMicOnOff.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_MOVE:
						mViewTouchListener.onTouch(v, event);
						break;
				}
				return false;
			}
		});

		mFloatingOnOff.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction() & MotionEvent.ACTION_MASK)
				{
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_MOVE:
						mViewTouchListener.onTouch(v, event);
						break;
				}
				return false;
			}
		});
				
		mBtnParams = getWindowParamsButton(context);			
		try {
			mWindowManager.addView(mFloatingButtons, mBtnParams);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void hideFloatingButton(Context context) {
		if(mFloatingButtons != null) {
			try {
				mWindowManager.removeView(mFloatingButtons);
				mFloatingButtons = null;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void showFloatingVisualizer(Context context) {
		bShowVisualizer = true;
		
		if(mFloatingLayout == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
			mFloatingLayout = (RelativeLayout) inflater.inflate( R.layout.visualizer_miniview, null);
			mVisualizerView = (VisualizerMiniView)mFloatingLayout.findViewById(R.id.visualizer_mini_view);
					
			WindowManager.LayoutParams params = getWindowParamsVisualizer(context);			
			try {
				mWindowManager.addView(mFloatingLayout, params);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
//		mVisualizerView.refreshChanged();			
		VisualizerManager.getInstance().setupView(mVisualizerView);
		Animation ani = AnimationUtils.loadAnimation(context, R.anim.push_up_in);
		mVisualizerView.startAnimation(ani);
		mVisualizerView.setVisibility(View.VISIBLE);
	}
	
	private void hideFloatingVisualizer(Context context) {
		bShowVisualizer = false;
		
		if(mFloatingLayout != null) {		
			Animation ani = AnimationUtils.loadAnimation(context, R.anim.push_down_out);
			ani.setFillAfter(true);
			mVisualizerView.startAnimation(ani);
			mVisualizerView.setVisibility(View.INVISIBLE);
			
			mHandler.postDelayed(mHideFloatingWindow, 400);
		}
	}
	
	public void updateFloatingWindow(Context context) {
		if(mFloatingLayout != null && bShowVisualizer) {
			WindowManager.LayoutParams params = getWindowParamsVisualizer(context);		
			mWindowManager.updateViewLayout(mFloatingLayout, params);
		}
	}
	
	float START_X, START_Y, PREV_X, PREV_Y;
	boolean hasMoved = false;
	
	private OnTouchListener mViewTouchListener = new OnTouchListener() {
	    @Override public boolean onTouch(View v, MotionEvent event) {
	        switch(event.getAction()) {
	            case MotionEvent.ACTION_DOWN:
	                START_X = event.getRawX();
	                START_Y = event.getRawY();
	                PREV_X = mBtnParams.x;
	                PREV_Y = mBtnParams.y;
	                hasMoved = false;
	                break;
	                
	            case MotionEvent.ACTION_UP:
	            	break;
	                
	            case MotionEvent.ACTION_MOVE:
	                float x = (int)(event.getRawX() - START_X);
	                float y = (int)(event.getRawY() - START_Y);

	                mBtnParams.x = (int)(PREV_X + x);
	                mBtnParams.y = (int)(PREV_Y + y);
	                
	                mWindowManager.updateViewLayout(mFloatingButtons, mBtnParams);    //�� ������Ʈ
	                hasMoved = true;
	                break;
	        }
	        
	        return true;
	    }
	};
	
	WindowManager.LayoutParams getWindowParamsButton(Context context) {		
		WindowManager mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display mDisplay = mWindowManager.getDefaultDisplay();
		
		DisplayMetrics metrics = new DisplayMetrics();
		mDisplay.getMetrics(metrics);

		return new WindowManager.LayoutParams(40*(int)metrics.density,
												40*(int)metrics.density,
												WindowManager.LayoutParams.TYPE_PHONE,
												WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
												PixelFormat.TRANSLUCENT);
	}	
	
	WindowManager.LayoutParams getWindowParamsVisualizer(Context context) {
		Log.e(TAG, "getWindowParams()");
		
		Point mPoint = new Point();
		WindowManager mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display mDisplay = mWindowManager.getDefaultDisplay();
		mDisplay.getSize(mPoint);
		
		DisplayMetrics metrics = new DisplayMetrics();
		mDisplay.getMetrics(metrics);	
				
		int maxWidth = mPoint.x; // * 96 / 100;
		int maxHeight = mPoint.y;

		int wRatio = PreferenceUtils.loadIntegerValue(context, PreferenceUtils.KEY_VI_WIDTH_RATIO, 100);
		int hRatio = PreferenceUtils.loadIntegerValue(context, PreferenceUtils.KEY_VI_HEIGHT_RATIO, 10);
		
		int width = maxWidth*wRatio/100;
		int height = maxHeight*hRatio/100;
		
		if(width < 40*metrics.density) {
			width = 40*(int)metrics.density;
		}
		if(height < 40*metrics.density) {
			height = 40*(int)metrics.density;
		}
		
		int gravity = PreferenceUtils.loadIntegerValue(context, PreferenceUtils.KEY_VI_GRAVITY, 0);
		if(gravity == 1) {
			gravity = Gravity.START;
		} else if(gravity == 2) {
			gravity = Gravity.END;
		} else {
			gravity = Gravity.CENTER_HORIZONTAL;
		}
		
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				//WindowManager.LayoutParams.MATCH_PARENT, 80*3,
				width, height,
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.BOTTOM | gravity;
		
		return params;
	}	
	
	public boolean isFloatingView() {
		return bShowWindow;
	}
	
	
	private Runnable mHideFloatingWindow = new Runnable() {
		@Override
		public void run() {
			try {
				mWindowManager.removeView(mFloatingLayout);
				mFloatingLayout = null;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	Handler mHandler = new Handler() {
	};
}
