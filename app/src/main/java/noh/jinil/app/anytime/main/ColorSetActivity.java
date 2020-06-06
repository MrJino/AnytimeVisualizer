package noh.jinil.app.anytime.main;

import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.utils.PreferenceUtils;
import afzkl.development.colorpickerview.view.ColorPickerView;
import afzkl.development.colorpickerview.view.ColorPickerView.OnColorChangedListener;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ColorSetActivity extends Activity implements OnColorChangedListener, OnClickListener {
	private ColorPickerView	mColorPickerView;
	private int mColorPos;
	private Button mConfirm;
	private Button mCancel;
	private Button mColorLoad;
	private BarSet mBarSet[] = null;
	private int mBarNum = 0;
	
	private final int BAR_MAX = 45;
	
	class BarSet {
		int mColorUp;
		int mColorDown;
		ImageView mBar;
	}
	
	private RadioButton mRadioBoth;
	private RadioButton mRadioUp;
	private RadioButton mRadioDown;
	
	private SeekBar mSeekbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setTitle(R.string.visualizer_color_title);
		
		setContentView(R.layout.visualizer_colorset);		
		
		mConfirm = (Button)findViewById(R.id.visualizer_colorset_confirm);
		mCancel = (Button)findViewById(R.id.visualizer_colorset_cancel);
		mColorLoad = (Button)findViewById(R.id.visualizer_colorset_colorload);
		
		mConfirm.setOnClickListener(this);
		mCancel.setOnClickListener(this);
		mColorLoad.setOnClickListener(this);

		mColorPickerView = (ColorPickerView)findViewById(R.id.color_picker_view);
		mColorPickerView.setOnColorChangedListener(this);
		
		mBarNum = PreferenceUtils.loadIntegerValue(getApplicationContext(), PreferenceUtils.KEY_VI_COLORSET_NUM, 20);
		
 		mSeekbar = (SeekBar)findViewById(R.id.visualizer_dynamic_seekbar);
 		mSeekbar.setMax(BAR_MAX);
 		mSeekbar.setProgress(mBarNum);
 		mSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(fromUser) {
					mBarNum = progress;
					for(int i=0; i<BAR_MAX;i++) {
						if(i < mBarNum) {
							mBarSet[i].mBar.setVisibility(View.VISIBLE);
						} else {
							mBarSet[i].mBar.setVisibility(View.GONE);
						}
					}
				}
			}
		});
		
		mRadioBoth = (RadioButton)findViewById(R.id.radio_barpos_both);
		mRadioUp = (RadioButton)findViewById(R.id.radio_barpos_up);
		mRadioDown = (RadioButton)findViewById(R.id.radio_barpos_down);
		
		mRadioUp.setChecked(true);
				
		LinearLayout LayoutDynamic = (LinearLayout)findViewById(R.id.visualizer_dynamic_colorset);
		
		mBarSet = new BarSet[BAR_MAX];
		for(int i=0;i<BAR_MAX;i++) {
			mBarSet[i] = new BarSet();
			mBarSet[i].mColorUp = (PreferenceUtils.loadIntegerValue(getApplicationContext(), PreferenceUtils.KEY_VI_COLORSET+(i+1), Color.BLACK) | 0xff000000);
			mBarSet[i].mColorDown = (PreferenceUtils.loadIntegerValue(getApplicationContext(), PreferenceUtils.KEY_VI_BOTTOMSET+(i+1), Color.WHITE) | 0xff000000);
			mBarSet[i].mBar = new ImageView(getApplicationContext());
			
			ImageView bar = mBarSet[i].mBar;
			bar.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
			bar.setBackgroundResource(R.drawable.visualizer_colorset_bar);
			bar.setPadding(2, 2, 2, 2);
			bar.setImageDrawable(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] {mBarSet[i].mColorUp, mBarSet[i].mColorDown}));
			bar.setOnClickListener(this);
			bar.setSelected(false);
			
			LayoutDynamic.addView(bar);
			
			if(i >= mBarNum) {
				mBarSet[i].mBar.setVisibility(View.GONE);
			}
		}				
		
		setColorPos(0, true);
	}

	@Override
	public void onColorChanged(int color) {
		if(mRadioUp.isChecked()) {
			mBarSet[mColorPos].mColorUp = color;
		} else if(mRadioDown.isChecked()) {
			mBarSet[mColorPos].mColorDown = color;
		} else if(mRadioBoth.isChecked()) {
			mBarSet[mColorPos].mColorUp = color;
			mBarSet[mColorPos].mColorDown = color;
		}
		mBarSet[mColorPos].mBar.setImageDrawable(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] {mBarSet[mColorPos].mColorUp, mBarSet[mColorPos].mColorDown}));
	}

	@Override
	public void onClick(View v) {
		if(mConfirm == v) {
			for(int i=0;i<BAR_MAX;i++) {
				PreferenceUtils.saveIntegerValue(getApplicationContext(), PreferenceUtils.KEY_VI_COLORSET+(i+1), mBarSet[i].mColorUp);
				PreferenceUtils.saveIntegerValue(getApplicationContext(), PreferenceUtils.KEY_VI_BOTTOMSET+(i+1), mBarSet[i].mColorDown);
			}
			PreferenceUtils.saveIntegerValue(getApplicationContext(), PreferenceUtils.KEY_VI_COLORSET_NUM, mBarNum);
			
			setResult(RESULT_OK);
			finish();
		}
		else if(mCancel == v) {
			finish();
		}
		else if(mColorLoad == v) {
			setColorPos(mColorPos, true);
		}
		else {
			for(int i=0;i<mBarNum;i++) {
				if(mBarSet[i].mBar == v) {
					setColorPos(i, false);
				}
				else {
					mBarSet[i].mBar.setSelected(false);
				}
			}
		}
	}
	
	@Override
	protected void onStop() {
		MainActivity.bHideVisualizerMode = false;
		super.onStop();
	}
	
	@Override
	protected void onResume() {
		MainActivity.bHideVisualizerMode = true;
		super.onResume();
	}
	
	private void setColorPos(int pos, boolean pickerset) {
		mColorPos = pos;
		mBarSet[mColorPos].mBar.setSelected(true);
		if(pickerset) {
			if(mRadioUp.isChecked()) {
				mColorPickerView.setColor(mBarSet[mColorPos].mColorUp);
			} else if(mRadioDown.isChecked()) {
				mColorPickerView.setColor(mBarSet[mColorPos].mColorDown);
			} else if(mRadioBoth.isChecked()) {
				mColorPickerView.setColor(mBarSet[mColorPos].mColorUp);
			}
		}
	}
}
