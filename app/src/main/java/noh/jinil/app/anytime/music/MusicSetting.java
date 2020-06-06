package noh.jinil.app.anytime.music;

import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.main.ColorSetActivity;
import noh.jinil.app.anytime.utils.PreferenceUtils;
import noh.jinil.app.anytime.utils.RoundDrawable;
import noh.jinil.app.anytime.utils.VLog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MusicSetting extends DialogFragment {
	private static final String TAG = "MusicSetting";
	ViewGroup mContainer;
	ImageView mBackground;
	ImageView mAlbumart;
	ImageView mImgVisualizer;
	SeekBar mBGSeekbar;
	SeekBar mAlbumSeekbar;
	SeekBar mViSeekbar;
	SeekBar mSeekbarBarRatio;
	TextView mTextColor;
	Button mBtnColor;
	int mViColor;;
	private MusicSettingListener mListener;
	
	View mLayoutBackground;
	View mLayoutAlbumart;
	
	public static final int REQUEST_CODE_CUSTOMCOLOR = 1;
			
	public interface MusicSettingListener {
		public abstract Bitmap onRequestAlbumart();
		public abstract void onBackgroundAlphaChanged(float alpha);
		public abstract void onAlbumartSizeChanged(int size);
		public abstract void onVisualizerAlphaChanged(int alpha);
		public abstract void onVisualizerColorChanged(int color);
		public abstract void onVisualizerSizeChanged(int size);
	}	
	
	public void setOnSettingListener(MusicSettingListener listener) {
		mListener = listener;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		VLog.i(TAG, "onCreateView()");

		mContainer = container;
		View view = inflater.inflate(R.layout.visualizer_setting, container, false);		
				
//		SharedPreferences prefs = getActivity().getSharedPreferences("Visualizer", Context.MODE_PRIVATE);		
//		mCheckBoxRight = (CheckBox)view.findViewById(R.id.visualizer_setting_rightcheck);
//		if(mCheckBoxRight != null) {
//			mCheckBoxRight.setChecked(mCheckRight);
//			mCheckBoxRight.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//				@Override
//				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//					mCheckRight = isChecked;
//				}
//			});
//		}
		
		mBtnColor = (Button)view.findViewById(R.id.setting_vi_color);
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
				
		mImgVisualizer = (ImageView)view.findViewById(R.id.music_setting_visualizer);
		mImgVisualizer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createColorDialog();
			}
		});
		
		mAlbumart = (ImageView)view.findViewById(R.id.music_setting_albumart);
		if(mAlbumart != null && mListener.onRequestAlbumart() != null) {
			RoundDrawable drawable = new RoundDrawable(mListener.onRequestAlbumart());
			mAlbumart.setImageDrawable(drawable);
		}
		
		mAlbumSeekbar = (SeekBar)view.findViewById(R.id.music_setting_albumart_seekbar);
		if(mAlbumSeekbar != null) {
			mAlbumSeekbar.setMax(100);
			mAlbumSeekbar.setProgress(PreferenceUtils.loadIntegerValue(getActivity(), PreferenceUtils.KEY_ALBUM_SIZE));
			mAlbumSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					PreferenceUtils.saveIntegerValue(getActivity(), PreferenceUtils.KEY_ALBUM_SIZE, seekBar.getProgress());
				}			
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {}
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {				
					if(fromUser) {
						mListener.onAlbumartSizeChanged(progress);
					}
				}
			});				
		}
		
		mBackground = (ImageView)view.findViewById(R.id.music_setting_background);
		mBackground.setImageBitmap(mListener.onRequestAlbumart());
		
		mBGSeekbar = (SeekBar)view.findViewById(R.id.music_setting_background_seekbar);
		mBGSeekbar.setMax(0xff*2/3);
		mBGSeekbar.setProgress(PreferenceUtils.loadIntegerValue(getActivity(), PreferenceUtils.KEY_BG_ALPHA));
		mBGSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int progress = seekBar.getProgress();
				if(progress == seekBar.getMax()) {
					progress = 0xff;
				}
				PreferenceUtils.saveIntegerValue(getActivity(), PreferenceUtils.KEY_BG_ALPHA, progress);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {				
				if(fromUser) {
					if(progress == seekBar.getMax()) {
						progress = 0xff;
					}
					mListener.onBackgroundAlphaChanged((1.0f*progress)/0xff);
				}
			}
		});
		
		mViSeekbar = (SeekBar)view.findViewById(R.id.music_setting_visualizer_seekbar);
		mViSeekbar.setMax(0xff);
		mViSeekbar.setProgress(PreferenceUtils.loadIntegerValue(getActivity(), PreferenceUtils.KEY_VI_ALPHA, 0xff));
		mViSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int progress = seekBar.getProgress();
				PreferenceUtils.saveIntegerValue(getActivity(), PreferenceUtils.KEY_VI_ALPHA, progress);
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {				
				if(fromUser) {
					mListener.onVisualizerAlphaChanged(progress);
				}
			}
		});
		
		int barRatio = PreferenceUtils.loadIntegerValue(getActivity(), PreferenceUtils.KEY_VI_BARRATIO, 0);
		mSeekbarBarRatio = (SeekBar)view.findViewById(R.id.visualizer_size_seekbar);
		mSeekbarBarRatio.setProgress(barRatio);
		mSeekbarBarRatio.setMax(100);
		mSeekbarBarRatio.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				PreferenceUtils.saveIntegerValue(getActivity(), PreferenceUtils.KEY_VI_BARRATIO, seekBar.getProgress());
			}			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(fromUser) {
					mListener.onVisualizerSizeChanged(progress);
				}
			}
		});
		
		mLayoutAlbumart = (View)view.findViewById(R.id.music_setting_albumart_layout);
		mLayoutBackground = (View)view.findViewById(R.id.music_setting_background_layout);
		
		if(PreferenceUtils.loadBooleanValue(getActivity(), PreferenceUtils.KEY_RC_AUTHORIZED, false)) {
			if(mLayoutBackground != null) {
				mLayoutBackground.setVisibility(View.GONE);
			}
			if(mLayoutAlbumart != null) {
				mLayoutAlbumart.setVisibility(View.GONE);
			}
		}
		showAnimation();
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onResume() {		
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	
	AlertDialog mDialog = null;
	private void createColorDialog() {	
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
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
		dialogBuilder.setTitle(R.string.visualizer_color_title);
		dialogBuilder.setSingleChoiceItems(colorAdapter, mViColor, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int color) {
				PreferenceUtils.saveIntegerValue(getActivity(), PreferenceUtils.KEY_VI_COLOR, color);
				mListener.onVisualizerColorChanged(color);		
				
				mBtnColor.setBackgroundResource(colorAdapter.getItem(color));
				if(color == colorAdapter.getCount() - 1) {
					startActivity(new Intent(getActivity(), ColorSetActivity.class));
				}
				dialog.dismiss();
			}
		});
		mDialog = dialogBuilder.show();
		*/
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle(R.string.visualizer_color_title);
		dialog.setSingleChoiceItems(getResources().getStringArray(R.array.visualizer_colorset), mViColor, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int color) {
				mViColor = color;
				PreferenceUtils.saveIntegerValue(getActivity(), PreferenceUtils.KEY_VI_COLOR, color);
				mListener.onVisualizerColorChanged(color);		
				String arrayColor[] = getResources().getStringArray(R.array.visualizer_colorset);
				mBtnColor.setText(arrayColor[color]);
				if(color == arrayColor.length-1) {
					startActivityForResult((new Intent(getActivity(), ColorSetActivity.class)), REQUEST_CODE_CUSTOMCOLOR);
				}
				dialog.dismiss();
			}
		});
		mDialog = dialog.show();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case REQUEST_CODE_CUSTOMCOLOR:
			if(resultCode == Activity.RESULT_OK) {
				Log.e("TEST", "onActivityResult:"+resultCode);
				String arrayColor[] = getResources().getStringArray(R.array.visualizer_colorset);
				mListener.onVisualizerColorChanged(arrayColor.length-1);				
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
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
	
	public void showAnimation() {
		VLog.d(TAG, "showAnimation()");
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_down_in);
		ani.setDuration(500);
		ani.setInterpolator(new DecelerateInterpolator(2.0f));
		ani.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
		mContainer.startAnimation(ani);
	}
		
	public void hideAnimation() {
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_out);
		ani.setDuration(500);
		ani.setInterpolator(new DecelerateInterpolator(2.0f));
		ani.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				dismiss();
			}
		});
		mContainer.startAnimation(ani);
	}
}

