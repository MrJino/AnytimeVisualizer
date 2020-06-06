package noh.jinil.app.anytime.visualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;



/**
 * A simple class that draws waveform data received from a
 * {@link Visualizer.OnDataCaptureListener#onWaveFormDataCapture }
 */
public class VisualizerFullView extends View implements SurfaceHolder.Callback, IVisualizerView {
	private static final String TAG = "VisualizerFullView";
			
	private final boolean USE_SURFACEVIEW = false;
	
	// type
	private ITypeView mViewType;
	private int mDensity = 1;
	private int mCountdownToStop = 0;
	private boolean mUpdate = true;
		
	private Context mContext = null;
	private SurfaceHolder mHolder;
	private ViewThread thread = null;

    public VisualizerFullView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        
        mDensity = (int)mContext.getResources().getDisplayMetrics().density;
        
        if(USE_SURFACEVIEW) {
//	        mHolder = this.getHolder();
			mHolder.addCallback(this);
        }
        
//      mViewType = new TypeWooferBar(context);
        mViewType = new TypeGraphBar(context, ITypeView.FULL_VIEW);
    }
    
    // USE_SURFACEVIEW
    @Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated()");		
		mHolder = holder;		
		startViewThread();
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "surfaceDestroyed()");
		/**
		 * you must not change mHolder reference here
		 * you should be careful and check using mHolder as synchronized object 
		 * if you really want to change
		*/
		//mHolder = holder;
		stopViewThread();
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.d(TAG, "surfaceChanged("+width+", "+height+")");		
		mHolder = holder;
	}
	
	private void startViewThread() {	
		if(thread != null)
			return;
		
		Log.d(TAG, "startThread()");
		        
        // start the thread here so that we don't busy-wait in run()
     	// waiting for the surface to be created
		thread = new ViewThread(mHolder, mContext);
		thread.setName("SoundFlip ViewThread");
		thread.setRunning(true);
		thread.start();
	}
	
	private void stopViewThread()  {
		if(thread == null)
			return;
		
		Log.d(TAG, "stopThread()");
		Log.d(TAG, "=>time check1");
		
		// view thread
		thread.setRunning(false);
		thread.interrupt();
		
		boolean retry = true;
		while(retry) {
			try {
				thread.join();
				retry = false;
			}
			catch(InterruptedException e) {
				Log.e(TAG, "InterruptedException");
			}
		}
		thread.interrupt();
		thread = null;
				
		Log.d(TAG, "=>time check2");
	}
	
	class ViewThread extends Thread { 
		
		private boolean mRun = false;
		
		public ViewThread(SurfaceHolder holder, Context context) {
			mHolder = holder;
		}
		
		@Override
		public void run() {			
			Log.d(TAG, "run("+mRun+")");			
			
			while(mRun) {
				try {
					sleep(10);
				}
				catch(InterruptedException ex) {
				}
				
				Canvas c = null;
				try {
					c = mHolder.lockCanvas(null);
					synchronized(mHolder) {
						doDraw(c);
					}
				}
				catch(Exception ex) {
					Log.e(TAG, "Exception!!:"+ex);
				}
				finally {
					if(c != null) {
						mHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
		
		public void setRunning(boolean b) {
			mRun = b;
		}
		
		private void doDraw(Canvas canvas) {
			if(!mRun)
				return;
			canvas.drawColor(Color.BLACK);
			mViewType.draw(canvas);
		}
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
//    	Log.e(TAG, "update()");
        mViewType.update(bytes);
        
        if(!USE_SURFACEVIEW) {
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
    }
    
    @Override
    protected void onDraw(Canvas canvas) {    	
        super.onDraw(canvas);
        
        mViewType.draw(canvas);
        
        if(!USE_SURFACEVIEW) {
            if(mCountdownToStop > 0) {
            	if(--mCountdownToStop == 0) {
            		mUpdate = false;
            	}
            }
            if(mUpdate) {
            	invalidate();
            }
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