package noh.jinil.app.anytime.visualizer;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AnimationEffect {
	private static final String TAG = "AnimationEffect";
	public static final int EFFECT_NONE 	 = 0;
	public static final int EFFECT_NEXT   	 = 1;
	public static final int EFFECT_PREV   	 = 2;
	public static final int EFFECT_RELOCATE	 = 3;
	public static final int EFFECT_FLING	 = 5;
	public static final int EFFECT_TO_FULL	 = 6;
	public static final int EFFECT_TO_NAVI	 = 7;
	public static final int EFFECT_TO_WIDE	 = 8;
	public static final int EFFECT_TO_NARROW = 9;
	public static final int EFFECT_DISTANCE  = 10;
	
	public static final int EFFECT_PLAY_FORCE   = 100;			// in fact, it is not effect but action
	public static final int EFFECT_ADD_POSITION	= 200;
	public static final int EFFECT_ADD_DISTANCE	= 300;
	public static final int EFFECT_ADD_SIZE		= 400;

	private static final int MSG_EFFECT_END = 1;


	private static final int[]CurveData = new int[]{  0,  85, 159, 231, 295,
											        348, 401, 448, 489, 533,
											        566, 603, 633, 660, 688,
											        711, 734, 755, 774, 794,
											        812, 827, 843, 857, 872,
											        882, 892, 902, 913, 921,
											        929, 937, 944, 948, 955,
											        961, 965, 968, 975, 977,
											        981, 983, 987, 989, 991,
											        993, 995, 995, 997, 998, 1000, 1000};

/*	private static final int[]ReverseData = new int[]{0, 0, 2, 3, 5, 5, 7, 
											        9, 11, 13, 17, 19, 
											        23, 25, 32, 35, 39, 
											        45, 52, 56, 63, 71,
											        79, 87, 98, 108, 118, 
											        128, 143, 157, 173, 188, 
											        206, 226, 245, 266, 289, 
											        312, 340, 367, 397, 434, 
											        467, 511, 552, 599, 652, 
											        705, 769, 841, 915, 1000};
*/

	private static final int[]SineData  = new int[]{  0,  31,  62,  94, 125,
												 	156, 187, 218, 248, 278,
													309, 338, 368, 397, 425,
													453, 481, 509, 535, 562,
													587, 612, 637, 661, 684,
													707, 728, 750, 770, 790,
													809, 827, 844, 860, 876,
													891, 904, 917, 929, 940,
													951, 960, 968, 975, 982,
													987, 992, 995, 998, 999, 1000, 1000};
	

	public AnimationEffect.callback mCallback;

	private int type;   // current effect type
	private int next;   // next-step effect type

	private int  dstPosX;
	private int  dstPosY;
	private int  srcPosX;
	private int  srcPosY;

	private int  srcSize;
	private int  dstSize;
	private int  curSize;
	private int  maxSize;

	private int   mVelocity;
	private float mAcceleration;
	private double mTheta;

	private int  movePosX;
	private int  movePosY;

	private int  curStep;
	private int  maxStep;
	private int  curMoveX;
	private int  curMoveY;
	private int  maxMoveX;
	private int  maxMoveY;
	
	private int idx_of_move;

	private int mGENERAL_MAXSTEP = 15;

	public interface callback {
		public void onAnimationEnd(AnimationEffect detector, int effect, int next);
		public void onAnimationBegin(int effect);
	}
	
	class PosInfo {
		int posX;
		int posY;
		int size;
	}

	public AnimationEffect() {
		type = EFFECT_NONE;
	}

	private int getData(int []dataArray, int step, int maxStep, int maxLen) {
		return (int)(dataArray[(step)*52/maxStep - 1] * ((long)maxLen)/1000);
	}
	
	public static int getValueFromCurveData(int step, int maxStep, int maxLen) {
		if(step >= maxStep) {
			return maxLen;
		}
		return CurveData[(step)*52/maxStep - 1] * (maxLen)/1000;
	}

	public static int getValueFromSineData(int step, int maxStep, int maxLen) {
		if(step >= maxStep) {
			return maxLen;
		}
		return SineData[(step)*52/maxStep - 1] * (maxLen)/1000;
	}

	private void OnEnd() {
		Log.v(TAG, "OnEnd("+type+")");

		Message msg = mHandler.obtainMessage(MSG_EFFECT_END);

		msg.arg1 = type;
		msg.arg2 = next;

		type = EFFECT_NONE;
		next = EFFECT_NONE;

		if(!mHandler.hasMessages(MSG_EFFECT_END)) {
			mHandler.sendMessage(msg);
		}
	}
	
	public void initIndex(int index) {
		idx_of_move = index;
	}

	public void initPosition(int oriPosX, int oriPosY, int dstPosX, int dstPosY, int maxStep) {
		//Log.v(TAG, "init("+oriPosX+","+oriPosY+","+dstPosX+","+dstPosY+")");

		this.srcPosX = oriPosX;
		this.srcPosY = oriPosY;
		this.dstPosX = dstPosX;
		this.dstPosY = dstPosY;
		this.movePosX = oriPosX;
		this.movePosY = oriPosY;

		this.maxMoveX = (dstPosX - oriPosX);
		this.maxMoveY = (dstPosY - oriPosY);

		this.curStep = 0;
		this.maxStep = maxStep;

		if(maxStep <= 0) {
			this.maxStep = mGENERAL_MAXSTEP;
		}
	}

	public void initVelocity(int posX, int posY, int velocity, float acceleration, double theta) {
		//Log.v(TAG, "velocity:"+velocity+", acceleration:"+acceleration+", theta:"+theta);
		
		this.movePosX  = posX;
		this.movePosY  = posY;
		this.mTheta    = theta;
		this.mVelocity = velocity;
		this.mAcceleration = acceleration;		
		
		if(velocity < 0) {
			this.mAcceleration = acceleration;
		}
		else {
			this.mAcceleration = -acceleration;
		}
	}
	
	public void initSize(int oriSize, int dstSize, int maxStep) {
		//Log.v(TAG, "init("+oriSize+","+dstSize+")");

		this.srcSize = oriSize;
		this.dstSize = dstSize;
		this.curSize = oriSize;

		this.maxSize = dstSize - oriSize;

		this.curStep = 0;
		this.maxStep = maxStep;

		if(maxStep <= 0) {
			this.maxStep = mGENERAL_MAXSTEP;
		}
	}
	
	public void addDistance(int oriSize, int addSize, int maxStep) {
		if(type != EFFECT_ADD_DISTANCE) {
			initSize(oriSize, oriSize+addSize, maxStep);
		}
		else {
			initSize(oriSize, this.dstSize+addSize, maxStep);
		}
	}
	
	public void addPosition(int oriPosX, int oriPosY, int addPosX, int addPosY, int maxStep) {
		if(type != EFFECT_ADD_POSITION) {
			initPosition(oriPosX, oriPosY, oriPosX+addPosX, oriPosY+addPosY, maxStep);
		}
		else {
			initPosition(oriPosX, oriPosY, this.dstPosX+addPosX, this.dstPosY+addPosY, maxStep);
		}
	}	
	
	public void addSize(int oriSize, int addSize, int maxStep) {
		if(type != EFFECT_ADD_SIZE) {
			initSize(oriSize, oriSize+addSize, maxStep);
		}
		else {
			initSize(oriSize, this.dstSize+addSize, maxStep);
		}
	}

	/**
	 * You should call init() method before call start method
	 */
	public void start(int effect) {
		Log.v(TAG, "start("+effect+")");
		
		type = effect;
		mHandler.removeMessages(MSG_EFFECT_END);

		if(mCallback != null) {
			mCallback.onAnimationBegin(effect);
		}
	}

	/**
	 * call this method if you really want to stop animation
	 */
	public void stop() {
		if(type == EFFECT_NONE)
			return;
		
		Log.v(TAG, "stop()");
		type = EFFECT_NONE;
	}
	
	/**
	 * multiple action
	 */
	public void setMoreAcceleration(int multiple) {
		this.mAcceleration *= multiple;
	}

	/**
	 * call this method if you want to do something for next action
	 */
	public void next(int effect) {
		Log.v(TAG, "next("+effect+")");
		next = effect;
	}

	/**
	 * animation step value update  
	 */
	public void update() {
		switch(type)
		{
		case EFFECT_NONE:
			// do nothing
			break;

		case EFFECT_NEXT:
		case EFFECT_PREV:
		case EFFECT_ADD_POSITION:
			if((curStep == maxStep) || (srcPosX == dstPosX && srcPosY == dstPosY)) {
				OnEnd();
	    		break;
	    	}

			curStep++;

			curMoveX = getData(CurveData, curStep, maxStep, maxMoveX);
			movePosX = srcPosX + curMoveX;

			curMoveY = getData(CurveData, curStep, maxStep, maxMoveY);
			movePosY = srcPosY + curMoveY;
			break;
			
		case EFFECT_TO_WIDE:
		case EFFECT_TO_NARROW:
		case EFFECT_DISTANCE:
		case EFFECT_ADD_DISTANCE:
			if(calculate_size_step(CurveData)) {
				OnEnd();
	    	}
			break;
			
		case EFFECT_RELOCATE:
			if(calculate_pos_step(SineData)) {
				OnEnd();
			}
			break;
			
		case EFFECT_ADD_SIZE:
		case EFFECT_TO_FULL:
		case EFFECT_TO_NAVI:
			if(calculate_both_step(CurveData, CurveData)) {
				OnEnd();
			}
			break;

		case EFFECT_FLING:
			mVelocity += mAcceleration;
			if((mVelocity > 0 &&  mAcceleration > 0) || (mVelocity < 0 &&  mAcceleration < 0)) {
				OnEnd();
				break;
			}			
			movePosX += mVelocity*Math.cos(mTheta);
			movePosY += mVelocity*Math.sin(mTheta);
			break;
		}
	}
	
	/**
	 * calculate size step
	 * @param data
	 * @return true if this step should finish now
	 */
	private boolean calculate_size_step(int[] data) {
		if((curStep == maxStep) || (srcSize == dstSize)) {
			return true;
    	}
		curStep++;
		curSize = srcSize + getData(CurveData, curStep, maxStep, maxSize);
		return false;
	}
	
	private boolean calculate_pos_step(int[] data) {
		if((curStep == maxStep) || (srcPosX == dstPosX && srcPosY == dstPosY)) {
			return true;
    	}
		curStep++;

		curMoveX = getData(data, curStep, maxStep, maxMoveX);
		movePosX = srcPosX + curMoveX;

		curMoveY = getData(data, curStep, maxStep, maxMoveY);
		movePosY = srcPosY + curMoveY;
		return false;
	}
	
	private boolean calculate_both_step(int[] data1, int[] data2) {
		if((curStep == maxStep)) {  // || (curSize == dstSize) || (movePosX == dstPosX && movePosX == dstPosY)) {
			return true;
    	}
		curStep++;
		
		curSize = srcSize + getData(CurveData, curStep, maxStep, maxSize);

		curMoveX = getData(data1, curStep, maxStep, maxMoveX);
		movePosX = srcPosX + curMoveX;

		curMoveY = getData(data2, curStep, maxStep, maxMoveY);
		movePosY = srcPosY + curMoveY;
		return false;
	}

	public int moveX() {
		return movePosX;
	}

	public int moveY() {
		return movePosY;
	}

	public int getSize() {
		return curSize;
	}
	
	public int getIndex() {
		return idx_of_move;
	}

	public boolean isWorking() {
		return (type != EFFECT_NONE);
	}

	public boolean isMoving() {
		return (type == EFFECT_NEXT ||
				type == EFFECT_PREV ||
				type == EFFECT_RELOCATE ||
				type == EFFECT_FLING ||
				type == EFFECT_ADD_POSITION);
	}
	
	public boolean isFling() {
		return (type == EFFECT_FLING);
	}
	
	public boolean isIndexMove() {
		return (type == EFFECT_TO_FULL ||
				type == EFFECT_TO_NAVI ||
				type == EFFECT_ADD_SIZE);
	}
	
	public boolean isResizing() {
		return (type == EFFECT_TO_FULL ||
				type == EFFECT_TO_NAVI ||
				type == EFFECT_ADD_SIZE);
	}	
	
	public boolean isDistance() {
		return (type == EFFECT_TO_WIDE ||
				type == EFFECT_TO_NARROW ||
				type == EFFECT_DISTANCE ||
				type ==EFFECT_ADD_DISTANCE);
	}

	public int hasNext() {
		return next;
	}

	public void addCallback(AnimationEffect.callback callback) {
		mCallback = callback;
	}

	/**
	 * Handler
	 */
	private Handler mHandler =  new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what)
			{
			case MSG_EFFECT_END:
				if(mCallback != null) {
					mCallback.onAnimationEnd(AnimationEffect.this, msg.arg1, msg.arg2);
				}
				break;

			default:
				Log.e(TAG, "handleMessage() msg : "+msg);
				break;
			}
		}
	};
}
