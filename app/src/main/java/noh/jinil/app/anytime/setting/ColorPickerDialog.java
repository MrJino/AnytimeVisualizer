package noh.jinil.app.anytime.setting;

import noh.jinil.app.anytime.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class ColorPickerDialog extends Dialog {
	
    public interface OnColorChangedListener {
        void colorChanged(int color);
    }
 
    private OnColorChangedListener mListener;
    private int mInitialColor;
    
    public ColorPickerDialog(Context context, OnColorChangedListener listener, int initialColor) {
		super(context);
		
		mListener = listener;
		mInitialColor = initialColor;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		OnColorChangedListener l = new OnColorChangedListener() {
			public void colorChanged(int color) {
				mListener.colorChanged(color);
				dismiss();
			}
		};
		setContentView(R.layout.music_color_picker);
		ColorPickerView picker = (ColorPickerView)findViewById(R.id.color_picker_view);
		picker.setColorChangeListener(l, mInitialColor);
		
		setTitle("Pick a Color");
		
		ImageView preview1 = (ImageView)findViewById(R.id.visualizer_preview1);
		preview1.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				mListener.colorChanged(0);
				dismiss();
			}
		});
		
		ImageView preview2 = (ImageView)findViewById(R.id.visualizer_preview2);
		preview2.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				mListener.colorChanged(1);
				dismiss();
			}
		});
		
		ImageView preview3 = (ImageView)findViewById(R.id.visualizer_preview3);
		preview3.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				mListener.colorChanged(2);
				dismiss();
			}
		});
	}      
}
