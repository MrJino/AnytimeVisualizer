package noh.jinil.app.anytime.utils;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

public class TouchUtils {
//	private static final String TAG = "MusicTouch";
	
	private static final int mVELOCITY_VALUE = 30; //30; //21; // 50;
	private static final int mDCLICK_TIMEOUT = 250;
	private static final int mREVISION_VALUE = 10;
	
	public static final int DIRECTION_NONE  = 0;
	public static final int DIRECTION_LEFT  = 1;
	public static final int DIRECTION_RIGHT = 2;
	public static final int DIRECTION_UP  	= 3;
	public static final int DIRECTION_DOWN  = 4;

	private OnTouchHandleListener mListener;
	
	public interface OnTouchHandleListener {
		public void onTouchBegin(TouchUtils detector);
		public void onTouchMove(TouchUtils detector);
		public void onTouchEnd(TouchUtils detector);
		public void onTouchSelect(TouchUtils detector);		
		public void onTouchFling(TouchUtils detector);
		public void onTouchDClick(TouchUtils detector);
		public void onTouchNone(TouchUtils detector);
	}
	
	private MotionEvent mCurrEvent;
	private MotionEvent mPrevEvent;
	private Handler mHandler;

	public double velocity;
	public double distance;
	public double theta;
	
	private boolean isClicked = false;
	private boolean isActionMove = false;
	private boolean ignoreTouchAction = false;
	private boolean ignoreSelectAction = false;
	private boolean availableDClick = false;
	
	private int touchCnt = 0; 
	private float touchX[] = new float[100];
	private float touchY[] = new float[100];
	private long clicktime[] = new long[100]; 
	
	public TouchUtils(OnTouchHandleListener listener) {
		mListener = listener;
		mHandler = new Handler();
	}
	
	public void setAvailableDClick(boolean useDClick) {
		availableDClick = useDClick;
	}
	
	public void reset() {
		touchCnt = 0;
		isActionMove = false;
		ignoreTouchAction = false;
		ignoreSelectAction = false;
	}
	
	public void setIgnoreTouch(boolean set) {
		ignoreTouchAction = set;
	}
	
	public void setIgnoreSelect(boolean set)  {
		ignoreSelectAction = set;
	}
		
	private boolean calibration(int value) {
		if(isActionMove == false && Math.abs(value) > 20) {
			return true;
		}
		return false;
	}
	
	public void setMotionEvent(MotionEvent event) {
		if(event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			if(mPrevEvent != null) {
				mPrevEvent.recycle();
	        }
			mPrevEvent = mCurrEvent;
			mCurrEvent = MotionEvent.obtain(event);
		}
	}
	
	public int getX() {
		return (int)mCurrEvent.getX();		
	}
	
	public int getY() {
		return (int)mCurrEvent.getY();
	}
	
	public int getVelocity() {
		return (int)velocity;
	}
	
	public int getVelocityX() {
		return (int)(velocity*Math.cos(theta));
	}
	
	public int getVelocityY() {
		return (int)(velocity*Math.sin(theta));
	}
	
	public int getDirection() {
		if(theta >= Math.PI*1/4 && theta < Math.PI*3/4) {
			return DIRECTION_DOWN;
		}
		else if(theta >= -(Math.PI*3/4) && theta < -(Math.PI*1/4)) {
			return DIRECTION_UP;
		}
		else if(theta >= -(Math.PI*1/4) && theta < Math.PI*1/4) {
			return DIRECTION_RIGHT;
		}
		return DIRECTION_LEFT;
	}
	
	public double getTheta() {
		return theta;
	}
	
	public int getSpanX() {
		if(touchCnt-2 < 0)
			return 0;
		
		int spanX = (int)(touchX[touchCnt-1] - touchX[touchCnt-2]);
		if(spanX == 0) {
			if(touchX[touchCnt-1] > touchX[touchCnt-2])
				return 1;
			else if(touchX[touchCnt-1] < touchX[touchCnt-2])
				return -1;
		}
		return spanX;
	}
	
	public int getSpanY() {
		if(touchCnt-2 < 0)
			return 0;
		
		int spanY = (int)(touchY[touchCnt-1] - touchY[touchCnt-2]);
		if(spanY == 0) {
			if(touchY[touchCnt-1] > touchY[touchCnt-2])
				return 1;
			else if(touchY[touchCnt-1] < touchY[touchCnt-2])
				return -1;
		}
		return spanY;
	}	
	
	public boolean isMoveAction() {
		return isActionMove;
	}
	
	public boolean isNotSelectAction() {
		return false;
	}
	
	public boolean isPrevCurrSameArea() {
		if(Math.abs(mPrevEvent.getX() - mCurrEvent.getX()) < 50 &&
		   Math.abs(mPrevEvent.getY() - mCurrEvent.getY()) < 50)
			return true;
		return false;
	}
	
	private void push(float x, float y, long time) {
		if(touchCnt >= 100)
			touchCnt = 0;
		
		touchX[touchCnt] = x;
		touchY[touchCnt] = y;
		clicktime[touchCnt] = time;
		touchCnt++;		
	}
	
	private double getDistance() {
		double spanX = touchX[touchCnt-1] - touchX[0];
		double spanY = touchY[touchCnt-1] - touchY[0];
		
		return Math.sqrt(spanX*spanX+spanY*spanY);
	}
	
	public double getDistanceY() {
		double spanY = touchY[touchCnt-1] - touchY[0];
		
		return spanY;
	}
	
	public void calculate() {
		distance = 0;
		velocity = 0;

		if(touchCnt == 0)
			return;

		int lastIdx = touchCnt-1;
		int startIdx = 0;
		for(int i=lastIdx; i>=0; i--) {
			if(clicktime[lastIdx] - clicktime[i] >= 50) {
				startIdx = i;
				break;
			}
		}
		double elapsedtime = clicktime[lastIdx] - clicktime[startIdx];
		double spanX = touchX[lastIdx] - touchX[startIdx];
		double spanY = touchY[lastIdx] - touchY[startIdx];
		
		distance = Math.sqrt(spanX*spanX+spanY*spanY);
		velocity = (distance * mVELOCITY_VALUE) / elapsedtime;
		theta = Math.atan2(spanY, spanX);
	}
	
	public void onTouchEvent(MotionEvent event) {
		setMotionEvent(event);
		
		switch(event.getAction() & MotionEvent.ACTION_MASK) 
		{ 
		case MotionEvent.ACTION_DOWN:
			reset();
			push(event.getX(), event.getY(), event.getEventTime());
			mListener.onTouchBegin(this);
			break;
			
		case MotionEvent.ACTION_MOVE:
			push(event.getX(), event.getY(), event.getEventTime());
			
			if(ignoreTouchAction)
				break;
			
			if(calibration(getSpanX()))
				break;
			
			isActionMove = true;
			mListener.onTouchMove(this);
			break;
			
		case MotionEvent.ACTION_UP:
			isActionMove = false;
			push(event.getX(), event.getY(), event.getEventTime());
			calculate();
			mListener.onTouchEnd(this);
			
			if(ignoreTouchAction)
				break;
			
			if(Math.abs(velocity) < 10) {
				if(getDistance() > mREVISION_VALUE || ignoreSelectAction) {
					mListener.onTouchNone(this);
				}
				else if(!availableDClick) {
					mListener.onTouchSelect(this);
				}
				else if(isClicked && isPrevCurrSameArea()) {
					isClicked = false;
					mHandler.removeCallbacks(mClickAction);					
					mListener.onTouchDClick(this);
				}
				else {
					isClicked = true;
					mHandler.postDelayed(mClickAction, mDCLICK_TIMEOUT);
				}
			}
			else {
				mListener.onTouchFling(this);
			}
			break;
		}
	}
	
	private Runnable mClickAction = new Runnable() {
        @Override
        public void run() {
        	isClicked = false;
        	mListener.onTouchSelect(TouchUtils.this);
        }
    };
}
