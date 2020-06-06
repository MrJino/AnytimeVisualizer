package noh.jinil.app.anytime.visualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class VisualizerMiniView extends View implements IVisualizerView {
	private static final String TAG = "MiniEQView";
		
	// All data form
 	private ITypeView mViewType;
 	private int mDensity = 1;
 	private int mCountdownToStop = 0;
 	private boolean mUpdate = true;

	private Context mContext = null;

    public VisualizerMiniView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        
        mViewType = new TypeGraphBar(context, ITypeView.MINI_VIEW); 
        mDensity = (int)mContext.getResources().getDisplayMetrics().density;
    }

    @Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	Log.d(TAG, "onSizeChanged("+w+", "+h+")");
    	mViewType.onSizeChanged(w, h, mDensity);
		super.onSizeChanged(w, h, oldw, oldh);
	}
    
    @Override
	public void refreshChanged() {
    	mViewType.refreshChanged();
	}

    @Override
    public void update(byte[] bytes) {
        mViewType.update(bytes);
        
        if(bytes == null) {
        	if(mUpdate && mCountdownToStop == 0) {
        		mCountdownToStop = 70;	
        	}
        }
        else {
        	mUpdate = true;
        }
        if(mUpdate) {
        	invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {    	
        super.onDraw(canvas);
        mViewType.draw(canvas);
        
        if(mCountdownToStop > 0) {
        	if(--mCountdownToStop == 0) {
        		mUpdate = false;
        	}
        }
        if(mUpdate) {
        	invalidate();
        }
    }
    
	@Override
	public float plusRatio() {
		return mViewType.plusRatio();
	}

	@Override
	public void setAlpha(int value) {
		mViewType.setAlpha(value);
		if(!mUpdate) {
			mUpdate = true;
			mCountdownToStop = 5;
		}
	}

	@Override
	public void setColorSet(int value) {
		mViewType.setColorSet(value);
		if(!mUpdate) {
			mUpdate = true;
			mCountdownToStop = 5;
		}
	}

	@Override
	public void setBarSize(int value) {
		mViewType.setBarSize(value);
		if(!mUpdate) {
			mUpdate = true;
			mCountdownToStop = 5;
		}
	}
	
	@Override
	public void setStick(boolean show) {
		mViewType.setStick(show);
		if(!mUpdate) {
			mUpdate = true;
			mCountdownToStop = 5;
		}
	}

	@Override
	public void setUseMic(boolean use) {
		mViewType.setUseMic(use);
		if(!mUpdate) {
			mUpdate = true;
			mCountdownToStop = 5;
		}
	}

	@Override
	public void refresh() {
		if(!mUpdate) {
			mUpdate = true;
			mCountdownToStop = 5;
		}
	}

	@Override
	public void setMICSensitivity(int value) {
		mViewType.setMICSensitivity(value);
		if(!mUpdate) {
			mUpdate = true;
			mCountdownToStop = 5;
		}
	}

	@Override
	public int getCustomColorSet() {		
		return mViewType.getCustomColorSet();
	}
}
