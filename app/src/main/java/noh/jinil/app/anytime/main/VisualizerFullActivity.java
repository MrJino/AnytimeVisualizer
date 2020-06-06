package noh.jinil.app.anytime.main;

import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.music.VisualizerManager;
import noh.jinil.app.anytime.utils.PreferenceUtils;
import noh.jinil.app.anytime.utils.TouchUtils;
import noh.jinil.app.anytime.utils.TouchUtils.OnTouchHandleListener;
import noh.jinil.app.anytime.visualizer.IVisualizerView;
import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class VisualizerFullActivity extends Activity {
	
	IVisualizerView mVisualizerView = null;
	private TouchUtils mTouchBackgound;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(PreferenceUtils.loadBooleanValue(this, PreferenceUtils.KEY_BL_ALWAYSON, true)) { 
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		setContentView(R.layout.visualizer_fullmode);
		
		mVisualizerView = (IVisualizerView)findViewById(R.id.visualizer_fullview);
		
		VisualizerManager.getInstance().setupView(mVisualizerView);
		
		overridePendingTransition(0, 0);
		
		// TouchUtils
		mTouchBackgound = new TouchUtils(new OnTouchHandleListener() {
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
			}
			
			@Override
			public void onTouchFling(TouchUtils detector) {
				if(detector.getDirection() == TouchUtils.DIRECTION_DOWN) {
				}
				else if(detector.getDirection() == TouchUtils.DIRECTION_UP) {
				}
				else if(detector.getDirection() == TouchUtils.DIRECTION_LEFT) {
//					sendMediaButtonClick(KeyEvent.KEYCODE_MEDIA_NEXT);
				}
				else if(detector.getDirection() == TouchUtils.DIRECTION_RIGHT) {
//					sendMediaButtonClick(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
				}
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}
}
