package noh.jinil.app.anytime.visualizer;

import noh.jinil.app.anytime.utils.PreferenceUtils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.util.Log;


public class TypeGraphBar implements ITypeView {
	private float mHeightRatio = 0.4f;
	private int mAlphaValue = 0xff;
	private int mColorValue = 0;
	private int mMaxStep = 5;
	private int mViewSize = FULL_VIEW;
	private boolean mShowStick = false;
	private boolean mUseMic = false;
	private int mMICSensitivity = 70;
	 	
	private final int CUSTOM_COLORSET = 8;
	private final int GRAPHBAR_SHOW_MAX 	= 46;
	private final int GRAPHBAR_BASE			= 5;
	private final int GRAPHBAR_GAP			= 1;
    private final int GRAPH_TUNING_MAX 	= 250;
	private final int GRAPH_TUNING_MIN 	= 1;

	private int GRAPH_TUNING_START 	= 80;
	private int GRAPH_TUNING_END 	= 600; // 246;
	private int GRAPH_TUNING_GAP 	= (GRAPH_TUNING_END-GRAPH_TUNING_START)/(GRAPHBAR_SHOW_MAX-1);
	
	private int mGRAPHBAR_SHOW_COUNT = 0;
	private int mSCREEN_DENSITY = 0;
	private int mSCREEN_WIDTH = 0;
	private int mSCREEN_HEIGHT = 0;
	private int mGRAPH_X = 0;
	private int mGRAPH_Y = 0;
	private int mGRAPH_WIDTH = 0;
	private int mGRAPH_HEIGHT = 0;
	private int mGRAPHBAR_WIDTH = 0;
	private int mGRAPHBAR_HEIGHT = 0;
	private float mREFLECTION_RATIO = 0.25f;
	private Paint mPaintBarLine;	
	private final int[] mSpectrumColors = new int[] {
		Color.argb(0xff, 250, 15, 15), Color.argb(0xff, 250, 34, 22), Color.argb(0xff, 247, 49, 27), Color.argb(0xff, 247, 65, 37), Color.argb(0xff, 247, 87, 42),
		Color.argb(0xff, 247, 107, 52), Color.argb(0xff, 247, 130, 47), Color.argb(0xff, 245, 151, 44), Color.argb(0xff, 247, 165, 42), Color.argb(0xff, 247, 188, 37),
		Color.argb(0xff, 245, 213, 32), Color.argb(0xff, 245, 220, 29), Color.argb(0xff, 242, 239, 24), Color.argb(0xff, 233, 240, 24), Color.argb(0xff, 188, 217, 28),
		Color.argb(0xff, 153, 204, 33), Color.argb(0xff, 135, 196, 37), Color.argb(0xff, 102, 181, 38), Color.argb(0xff, 73, 168, 44), Color.argb(0xff, 58, 161, 42),
		Color.argb(0xff, 19, 128, 62), Color.argb(0xff, 16, 102, 78), Color.argb(0xff, 18, 92, 92),
//		Color.argb(0xff, 24, 50, 180), 
//		Color.argb(0xff, 15, 15, 200),
//		Color.argb(0xff, 45, 13, 128), Color.argb(0xff, 83, 11, 125), Color.argb(0xff, 118, 10, 128), Color.argb(0xff, 143, 9, 129), Color.argb(0xff, 138, 10, 129),
//		Color.argb(0xff, 124, 11, 128), Color.argb(0xff, 105, 13, 128), Color.argb(0xff, 110, 10, 128), Color.argb(0xff, 96, 9, 130), Color.argb(0xff, 82, 17, 107),
	};
	
	private final int[] mBlueColors = new int[] {
			//Color.argb(0xff, 225, 243, 250), Color.argb(0xff, 196, 232, 245), Color.argb(0xff, 167, 222, 242), Color.argb(0xff, 138, 212, 237), Color.argb(0xff, 108, 201, 235),
			Color.argb(0xff, 80, 190, 230), Color.argb(0xff, 50, 180, 227), Color.argb(0xff, 44, 177, 222), Color.argb(0xff, 35, 173, 219), Color.argb(0xff, 28, 170, 217),
			Color.argb(0xff, 21, 164, 212), Color.argb(0xff, 15, 160, 209), Color.argb(0xff, 6, 156, 207), 
			Color.argb(0xff, 0, 151, 201),
			Color.argb(0xff, 6, 156, 207), Color.argb(0xff, 15, 160, 209), Color.argb(0xff, 21, 164, 212),
			Color.argb(0xff, 28, 170, 217), Color.argb(0xff, 35, 173, 219), Color.argb(0xff, 44, 177, 222), Color.argb(0xff, 50, 180, 227), Color.argb(0xff, 80, 190, 230),
			//Color.argb(0xff, 108, 201, 235), Color.argb(0xff, 138, 212, 237), Color.argb(0xff, 167, 222, 242), Color.argb(0xff, 196, 232, 245), Color.argb(0xff, 225, 243, 250),
	};
	
	private final int[] mPurpleColorSet = new int[] {
			//Color.argb(0xff, 243, 233, 247), Color.argb(0xff, 227, 201, 240), 
			Color.argb(0xff, 217, 185, 235), Color.argb(0xff, 211, 172, 232), Color.argb(0xff, 206, 158, 230),
			Color.argb(0xff, 201, 150, 227), Color.argb(0xff, 196, 139, 224), Color.argb(0xff, 191, 129, 222), Color.argb(0xff, 183, 115, 217), Color.argb(0xff, 175, 103, 214),
			Color.argb(0xff, 169, 89, 212), Color.argb(0xff, 164, 79, 209), Color.argb(0xff, 157, 64, 207), 
			Color.argb(0xff, 151, 50, 201),
			Color.argb(0xff, 157, 64, 207), Color.argb(0xff, 164, 79, 209), Color.argb(0xff, 169, 89, 212),
			Color.argb(0xff, 175, 103, 214), Color.argb(0xff, 183, 115, 217), Color.argb(0xff, 191, 129, 222), Color.argb(0xff, 196, 139, 224), Color.argb(0xff, 201, 150, 227), 
			Color.argb(0xff, 206, 158, 230), Color.argb(0xff, 211, 172, 232), Color.argb(0xff, 217, 185, 235), 
			//Color.argb(0xff, 227, 201, 240), Color.argb(0xff, 243, 233, 247),
	};
	
	private final int[] mGreenColorSet = new int[] {
			//Color.argb(0xff, 238, 245, 218), Color.argb(0xff, 224, 237, 180), 
			Color.argb(0xff, 208, 230, 145), Color.argb(0xff, 197, 224, 108), Color.argb(0xff, 181, 217, 74),
			Color.argb(0xff, 169, 209, 36), Color.argb(0xff, 151, 201, 0), Color.argb(0xff, 145, 194, 0), Color.argb(0xff, 137, 186, 0), Color.argb(0xff, 131, 179, 0),
			Color.argb(0xff, 124, 173, 0), Color.argb(0xff, 116, 166, 0), Color.argb(0xff, 108, 158, 0), 
			Color.argb(0xff, 100, 150, 0),
			Color.argb(0xff, 108, 158, 0), Color.argb(0xff, 116, 166, 0), Color.argb(0xff, 124, 173, 0),
			Color.argb(0xff, 131, 179, 0), Color.argb(0xff, 137, 186, 0), Color.argb(0xff, 145, 194, 0), Color.argb(0xff, 151, 201, 0), Color.argb(0xff, 169, 209, 36),
			Color.argb(0xff, 181, 217, 74), Color.argb(0xff, 197, 224, 108), Color.argb(0xff, 208, 230, 145), 
			//Color.argb(0xff, 224, 237, 180), Color.argb(0xff, 238, 245, 218), 
	};
	
	private final int[] mYellowColorSet = new int[] {
			//Color.argb(0xff, 252, 244, 222), Color.argb(0xff, 252, 233, 192),
			Color.argb(0xff, 252, 224, 159), Color.argb(0xff, 252, 215, 126), Color.argb(0xff, 252, 205, 96),
			Color.argb(0xff, 252, 196, 66), Color.argb(0xff, 252, 187, 33), Color.argb(0xff, 252, 178, 28), Color.argb(0xff, 252, 169, 25), Color.argb(0xff, 252, 163, 20),
			Color.argb(0xff, 252, 158, 15), Color.argb(0xff, 252, 151, 10), Color.argb(0xff, 252, 141, 5), 
			Color.argb(0xff, 252, 135, 0),
			Color.argb(0xff, 252, 141, 5), Color.argb(0xff, 252, 151, 10), Color.argb(0xff, 252, 158, 15), 
			Color.argb(0xff, 252, 163, 20), Color.argb(0xff, 252, 169, 25), Color.argb(0xff, 252, 178, 28),Color.argb(0xff, 252, 187, 33),Color.argb(0xff, 252, 196, 66),
			Color.argb(0xff, 252, 205, 96),Color.argb(0xff, 252, 215, 126),Color.argb(0xff, 252, 224, 159),
			//Color.argb(0xff, 252, 233, 192),Color.argb(0xff, 252, 244, 222),
	};
	private final int[] mRedColorSet = new int[] {
			//Color.argb(0xff, 252, 227, 227), Color.argb(0xff, 252, 202, 202), 
			Color.argb(0xff, 252, 174, 174), Color.argb(0xff, 252, 146, 146), Color.argb(0xff, 252, 121, 121),
			Color.argb(0xff, 252, 96, 96), Color.argb(0xff, 252, 68, 68), Color.argb(0xff, 245, 59, 59), Color.argb(0xff, 237, 50, 50), Color.argb(0xff, 230, 39, 39),
			Color.argb(0xff, 224, 29, 29), Color.argb(0xff, 217, 20, 20), Color.argb(0xff, 209, 10, 10), 
			Color.argb(0xff, 201, 0, 0),
			Color.argb(0xff, 209, 10, 10), Color.argb(0xff, 217, 20, 20), Color.argb(0xff, 224, 29, 29), 
			Color.argb(0xff, 230, 39, 39),Color.argb(0xff, 237, 50, 50), Color.argb(0xff, 245, 59, 59),Color.argb(0xff, 252, 68, 68), Color.argb(0xff, 252, 96, 96),
			Color.argb(0xff, 252, 121, 121),Color.argb(0xff, 252, 146, 146),Color.argb(0xff, 252, 174, 174),
			//Color.argb(0xff, 252, 202, 202),Color.argb(0xff, 252, 227, 227),
	};	
	
	
	// object class by show type
	private GraphBar[] mGraphBar;
	private Woofer[] mWoofer;
	
	private Context mContext;
	
	public TypeGraphBar(Context context, int viewsize) {
		mContext = context;
		mViewSize = viewsize;
		
		mPaintBarLine = new Paint();
		//mPaintBarLine.setARGB(255, 231, 142, 34);
		mPaintBarLine.setARGB(255, 255, 255, 255);
		
		mAlphaValue = PreferenceUtils.loadIntegerValue(mContext, PreferenceUtils.KEY_VI_ALPHA, 0xff);
		mColorValue = PreferenceUtils.loadIntegerValue(mContext, PreferenceUtils.KEY_VI_COLOR, 0x00);
		mShowStick  = PreferenceUtils.loadBooleanValue(mContext, PreferenceUtils.KEY_VI_STICK, true);
		mUseMic = PreferenceUtils.loadBooleanValue(mContext, PreferenceUtils.KEY_MIC_USE, false);
		mMICSensitivity = PreferenceUtils.loadIntegerValue(mContext, PreferenceUtils.KEY_MIC_SENSITIVITY, 80);
		
		int barRatio = PreferenceUtils.loadIntegerValue(mContext, PreferenceUtils.KEY_VI_BARRATIO, 0);
		mHeightRatio = 0.6f + (float)barRatio * 0.3f / 100;

		setUseMic(mUseMic);
	}
	
	@Override
	public void onSizeChanged(int width, int height, int density) {
		updateMaxStep(mHeightRatio);
		
		// 1. graph width, height
		mSCREEN_WIDTH = width;
		mSCREEN_HEIGHT = height;
		mSCREEN_DENSITY = density;
				
		// 2. graphbar, graph width
		mGRAPHBAR_WIDTH = (width / GRAPHBAR_SHOW_MAX);
		
		float ratio = (mHeightRatio-0.6f)*3;
		mGRAPHBAR_WIDTH = (int)(mGRAPHBAR_WIDTH*(1+ratio));
		
		if(mGRAPHBAR_WIDTH <= 0)
			mGRAPHBAR_WIDTH = 1;
		
		mGRAPHBAR_SHOW_COUNT = mSCREEN_WIDTH / mGRAPHBAR_WIDTH;
		if(mGRAPHBAR_SHOW_COUNT == 0)
			mGRAPHBAR_SHOW_COUNT = 1;
		
		mGRAPHBAR_HEIGHT = (int)(height * mHeightRatio);
		mGRAPH_WIDTH = mGRAPHBAR_WIDTH*mGRAPHBAR_SHOW_COUNT;
		
		// 3. graph x, y
		mGRAPH_X = (width - mGRAPH_WIDTH)/2;				
		if(mViewSize == MINI_VIEW) {
			mGRAPH_Y = height - mGRAPHBAR_HEIGHT;
		} else {
			mGRAPH_Y = (height - mGRAPHBAR_HEIGHT)/2;
		}
		
        if(mGraphBar == null)  {        	
        	mGraphBar = new GraphBar[GRAPHBAR_SHOW_MAX];
        	for(int i=0; i<GRAPHBAR_SHOW_MAX; i++) {
    			mGraphBar[i] = new GraphBar(i);
    		}
        }
        else {
        	for(int i=0; i<GRAPHBAR_SHOW_MAX; i++) {
    			mGraphBar[i].updatePosition();
    			mGraphBar[i].updateColorSet(mColorValue);
    			mGraphBar[i].updateAlpha(mAlphaValue);
    		}
        }

		if(mWoofer == null) {
			mWoofer = new Woofer[100];
			for(int i=0; i<100; i++) {
				mWoofer[i] = new Woofer(i);
			}
		}
	}
	
	@Override
	public void refreshChanged() {
		if(mSCREEN_WIDTH <= 0 || mSCREEN_HEIGHT <= 0)
			return;
		onSizeChanged(mSCREEN_WIDTH, mSCREEN_HEIGHT, mSCREEN_DENSITY);
	}

	@Override
	public void update(byte[] bytes) {
		if(mGraphBar == null) 
			return;
		 
		if(bytes == null) {
    		for(int i=0; i<GRAPHBAR_SHOW_MAX; i++) {
    			mGraphBar[i].updateBar(0);
    		}
    		return;    		
    	}

		for(int i=1; i < bytes.length-1; i++) {
			if(bytes[i] < 0)
				bytes[i] = (byte)(-bytes[i]);
		}
		
		int availableCount = 0;
		for(int i=0; i<GRAPHBAR_SHOW_MAX; i++) {
			if(mGraphBar[i].isAvailable()) {
				availableCount++;
			}
		}
		
		int midValue = 0;
		if(availableCount % 2 == 0) {
			midValue = availableCount - 1;
		} else {
			midValue = availableCount;
		}

		double average, sum = 0, maxValue;
		/**
		 * Average
		 */
		for(int i=0; i<availableCount; i++) {
			//-----------------------------------------------
			average = 0;
			maxValue = 0;
			for(int f = GRAPH_TUNING_START+i*GRAPH_TUNING_GAP; f < GRAPH_TUNING_START+i*GRAPH_TUNING_GAP+10 && f < bytes.length; f++){
				average += bytes[f];
			}

			if(mUseMic) {
				//average = Math.log((1+average/5))*500;
				average = Math.log((1 + average)) * (30+(double)mMICSensitivity/2);
			} else {
				average = Math.log((1 + average / 10)) * 150;
			}
						
			if(average > GRAPH_TUNING_MAX) { 
				average = GRAPH_TUNING_MAX+(Math.log10(average-GRAPH_TUNING_MAX)*10);
			}
			else if(average < GRAPH_TUNING_MIN) {
				average = GRAPH_TUNING_MIN;
			}
			
			sum = sum + average;
			//-----------------------------------------------
			if(false) {//if(mHeightRatio > 1.3) {
				mGraphBar[i].updateBar((int)average);
			}
			else {
				if(i % 2 == 0) {
					mGraphBar[((midValue)-i)/2].updateBar((int)average);
				} else {
					mGraphBar[((midValue)+i)/2].updateBar((int)average);
				}
			}			
		}

		// High
		for(int i=0;i<5;i++) {
			average = 0;
			for (int f = i*GRAPH_TUNING_GAP; f < (i+1)*GRAPH_TUNING_GAP; f++) {
				average += bytes[f];
			}
			average = Math.log((1 + average / 10)) * 20;
			if(i == 0 || i == 1)
				mWoofer[i].updateBase((int) average);
			else
				mWoofer[i].updateBase((int) average);
		}

		// Low
		for(int i=5;i<10;i++) {

			if(GRAPH_TUNING_END+(i+1)*GRAPH_TUNING_GAP >= bytes.length)
				break;

			average = 0;
			for (int f = GRAPH_TUNING_END + i*GRAPH_TUNING_GAP; f < GRAPH_TUNING_END+(i+1)*GRAPH_TUNING_GAP; f++) {
				average += bytes[f];
			}
			mWoofer[i].updateBase((int) average * 2);
		}
	}

	@Override
	public void draw(Canvas canvas) {
		/*
		for(int i=0; i<10; i++){
			mWoofer[i].draw(canvas);
		}
		*/
		for(int i=0; i<GRAPHBAR_SHOW_MAX; i++){
    		mGraphBar[i].draw(canvas);
		}
	}
	
	private void updateMaxStep(float ratio) {
		if(ratio <= 0.6f) {
			mMaxStep = 6;
		} else if(ratio <= 0.9f) {
			mMaxStep = 7;
		} else {
			mMaxStep = 8;
		}
	}

	class Woofer {
		private Paint mWoPaint = new Paint();
		private int prevPos;
		private int currPos;
		private int livePos;
		private int liveStep;

		private int myIndex;

		Woofer(int index) {
			myIndex = index;
		}

		protected void updateBase(int value) {
			prevPos = currPos;
			livePos = currPos;
			currPos = value;
			liveStep = 0;
		}

		public void draw(Canvas c)  {
			liveStep++;
			if(currPos <= prevPos) {
				livePos = livePos - 1; //AnimationEffect.getValueFromSineData(liveStep, mMaxStep*3, currPos - prevPos);
			} else {
				livePos = currPos; //prevPos + AnimationEffect.getValueFromSineData(liveStep, 2, currPos - prevPos);
			}
			mWoPaint.setColor(Color.RED);
			c.drawCircle(mSCREEN_WIDTH/20 + myIndex*mSCREEN_WIDTH/10, mSCREEN_HEIGHT/2, (float)livePos, mWoPaint);
		}
	}
	
	class GraphBar {
    	private int myIndex;
    	private int imgColorUp;
    	private int imgColorDown;
    	
    	private int prevPos;
		private int currPos;
		private int livePos;
		private int liveStep;
		
		private int posX;
		private int posY;
		private int width;
		private int gap;
		
		private int stickH;
				
		private int linePos;
		private int stayCnt;
		private float velocity;
		
		private boolean bAvailable = true;
		
	    private Rect mDstRect = new Rect();
	    
	    private Paint mRePaint = new Paint();
	    private Paint mOrPaint = new Paint();
		
	    GraphBar(int index) {
	    	myIndex = index;
	    	
	    	updatePosition();
	    	
	    	linePos = mGRAPH_Y + mGRAPHBAR_HEIGHT;
	    		    	
	    	updateColorSet(mColorValue);
	    	updateAlpha(mAlphaValue);
		}
	    
	    public void updateColorSet(int value) {
	    	if(value == 0) {
	    		imgColorUp = mSpectrumColors[((myIndex*mSpectrumColors.length)/mGRAPHBAR_SHOW_COUNT)%mSpectrumColors.length];
	    		imgColorDown = imgColorUp;
	    		
	    		mOrPaint.setColor(imgColorUp);
		    	mRePaint.setColor(imgColorUp);
		    	mOrPaint.setColorFilter(null);
		    	mRePaint.setColorFilter(null);
		    	mOrPaint.setShader(null);
		    	mRePaint.setShader(null);
		    	
//		    	Log.e("TypeGraphBar", "imgColorUp:"+imgColorUp);
//		    	Log.e("TypeGraphBar", "imgColorDown:"+imgColorDown);
	    	}
	    	else if(value == 7) {
		    	imgColorUp = mSpectrumColors[((myIndex*mSpectrumColors.length)/mGRAPHBAR_SHOW_COUNT)%mSpectrumColors.length];
		    	imgColorDown = imgColorUp;
		    	mOrPaint.setColor(imgColorUp);
		    	mRePaint.setColor(imgColorUp);
		    	mOrPaint.setShader(null);
		    	mRePaint.setShader(null);
		    	
				ColorMatrix cm = new ColorMatrix();
				cm.setSaturation(0);
				ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
				mOrPaint.setColorFilter(f);
				mRePaint.setColorFilter(f);
	    	}
	    	else if(value == CUSTOM_COLORSET) {	    		
    			int num = PreferenceUtils.loadIntegerValue(mContext, PreferenceUtils.KEY_VI_COLORSET_NUM, 20);
    			int colorset[] = new int[num]; 
    			for(int i=0;i<colorset.length;i++) {
    				int color = PreferenceUtils.loadIntegerValue(mContext, PreferenceUtils.KEY_VI_COLORSET+(i+1));
    				colorset[i] = color; 
    			}
    			imgColorUp = colorset[((myIndex*colorset.length)/mGRAPHBAR_SHOW_COUNT)%colorset.length];
    			for(int i=0;i<colorset.length;i++) {
    				int color = PreferenceUtils.loadIntegerValue(mContext, PreferenceUtils.KEY_VI_BOTTOMSET+(i+1));
    				colorset[i] = color; 
    			}
	    		imgColorDown = colorset[((myIndex*colorset.length)/mGRAPHBAR_SHOW_COUNT)%colorset.length];
	    		
	    		mOrPaint.setColorFilter(null);
		    	mRePaint.setColorFilter(null);
    		}
	    	else {
	    		int[] colorset = mSpectrumColors; 
	    		switch(value) {	    		
	    		case 0:
	    			colorset = mSpectrumColors;
	    			break;
	    		case 2:
	    			colorset = mBlueColors;
	    			break;
	    		case 3:
	    			colorset = mPurpleColorSet;
	    			break;
	    		case 4:
	    			colorset = mGreenColorSet;
	    			break;
	    		case 5:
	    			colorset = mYellowColorSet;
	    			break;
	    		case 6:
	    			colorset = mRedColorSet;
	    			break;
	    		}
	    		imgColorUp = colorset[((myIndex*colorset.length)/mGRAPHBAR_SHOW_COUNT)%colorset.length];
	    		imgColorDown = Color.WHITE;
	    		
	    		mOrPaint.setColor(imgColorUp);
		    	mRePaint.setColor(imgColorUp);
		    	mOrPaint.setColorFilter(null);
		    	mRePaint.setColorFilter(null);
	    	}
	    	
	    	
//	    	else {
//	    		mOrPaint.setColor(value);
//		    	mRePaint.setColor(value);
//		    	mOrPaint.setColorFilter(null);
//		    	mRePaint.setColorFilter(null);
//	    	}
	    }
	    
	    public void updateAlpha(int value) {
	    	mAlphaValue = value;
	    	
	    	mOrPaint.setAlpha(value);
	    	mRePaint.setAlpha(value*2/5);
	    }
	    
	    public void updatePosition() {
//	    	float ratio = (mHeightRatio-0.6f)*3;
	    	width = mGRAPHBAR_WIDTH; //(int)(mGRAPHBAR_WIDTH*(1+ratio));
    		posX = (mGRAPH_X)+myIndex*width;
    		
    		stickH = 1+width/16;
    		gap = 1+width/20;
    		
    		if(mViewSize == MINI_VIEW) {
    			posY = mSCREEN_HEIGHT;
    		}
    		else {
    			posY = (int)(mSCREEN_HEIGHT*(1-mREFLECTION_RATIO));
    		}
	    	
	    	if(posX+width > mSCREEN_WIDTH) {
	    		bAvailable = false;
	    	}
	    	else {
	    		bAvailable = true;
	    	}
	    }
		
		public void updateBar(int value) {			
			prevPos = currPos;
			livePos = currPos;
							
			currPos = value;
			liveStep = 0;
		}
		
		public boolean isAvailable() {
			return bAvailable;
		}
		
//		public void reset() {
//			prevPos = 1;
//			livePos = 1;
//			currPos = 1;
//			liveStep = 0;
//			linePos = 2;
//		}
		
		public void draw(Canvas c)  {	
			if(!bAvailable)
				return;
			
			liveStep++;
			if(currPos < prevPos) {
				livePos = prevPos + AnimationEffect.getValueFromSineData(liveStep, mMaxStep*3/2, currPos - prevPos);
			} else {
				livePos = prevPos + AnimationEffect.getValueFromSineData(liveStep, mMaxStep, currPos - prevPos);
			}
//			livePos = prevPos + AnimationEffect.getValueFromSineData(liveStep, mMaxStep, currPos - prevPos);
			
			int intensity = (mGRAPHBAR_HEIGHT*livePos)/GRAPH_TUNING_MAX;
			if(intensity < GRAPHBAR_BASE) {
				intensity = GRAPHBAR_BASE;
			}
			
			if(imgColorUp == imgColorDown) {
				mOrPaint.setColor(imgColorUp);
		    	mRePaint.setColor(imgColorUp);
		    	updateAlpha(mAlphaValue);
		    	mOrPaint.setShader(null);
		    	mRePaint.setShader(null);
			} else {
				mOrPaint.setShader(new LinearGradient(0, posY-(int)(intensity*(1-mREFLECTION_RATIO)), 0, posY, imgColorUp, imgColorDown, TileMode.CLAMP));
				mRePaint.setShader(new LinearGradient(0, posY+5, 0, posY+5+(int)(intensity*mREFLECTION_RATIO), imgColorDown, imgColorUp, TileMode.CLAMP));
			}
			
			// reflection
			if(mViewSize == FULL_VIEW) {
				mDstRect.set(posX, posY+5, posX+(width-gap), posY+5+(int)(intensity*mREFLECTION_RATIO));
				c.drawRect(mDstRect, mRePaint);
				
				// original
				mDstRect.set(posX, posY-(int)(intensity*(1-mREFLECTION_RATIO)), posX+(width-gap), posY);
				c.drawRect(mDstRect, mOrPaint);
			}
			else {
				// original
				mDstRect.set(posX, posY-(int)(intensity), posX+(width-gap), posY);
				c.drawRect(mDstRect, mOrPaint);
			}
			
			// draw UpToDown lines
			if(mViewSize == FULL_VIEW || mShowStick) {
				if(stayCnt > 0) {
					stayCnt--;
				}
				else  {
					velocity = velocity + (float)0.20; //(float)0.04;
					linePos += velocity;
				}

				if(linePos >= mDstRect.top-10) {
					linePos = mDstRect.top-10;
					stayCnt = 10; //15;
					velocity = 0;
				}
				c.drawRect(posX+1, linePos-stickH, posX+(width-gap)-1, linePos, mOrPaint);
//				c.drawLine(posX+1, linePos, posX+(width-gap)-1, linePos, mOrPaint);
			}
		}
	}

	@Override
	public float plusRatio() {
		if(mHeightRatio >= 0.9f) {
			mHeightRatio = 0.6f;
		}
		else {
			mHeightRatio = mHeightRatio + 0.1f;
		}
		onSizeChanged(mSCREEN_WIDTH, mSCREEN_HEIGHT, mSCREEN_DENSITY);
		
//		updateMaxStep(mHeightRatio);		
//		for(int i=0; i<GRAPHBAR_SHOW_MAX; i++){
//    		mGraphBar[i].updatePosition();
//		}
		
		PreferenceUtils.saveFloatValue(mContext, PreferenceUtils.KEY_VISUALIZER_RATIO, mHeightRatio);
		
		return mHeightRatio;
	}

	@Override
	public void setAlpha(int value) {
		if(mGraphBar == null)
			return;
		
		for(int i=0; i<GRAPHBAR_SHOW_MAX; i++) {
			mGraphBar[i].updateAlpha(value);
		}
	}

	@Override
	public void setColorSet(int value) {
		if(mGraphBar == null)
			return;
		
		mAlphaValue = PreferenceUtils.loadIntegerValue(mContext, PreferenceUtils.KEY_VI_ALPHA, 0xff);
		mColorValue = PreferenceUtils.loadIntegerValue(mContext, PreferenceUtils.KEY_VI_COLOR, 0x00);
		
		mColorValue = value;
		for(int i=0; i<GRAPHBAR_SHOW_MAX; i++){
    		mGraphBar[i].updateColorSet(value);
    		mGraphBar[i].updateAlpha(mAlphaValue);
		}
	}

	@Override
	public void setBarSize(int value) {
		mHeightRatio = 0.6f + (float)value * 0.3f / 100;
		onSizeChanged(mSCREEN_WIDTH, mSCREEN_HEIGHT, mSCREEN_DENSITY);
	}

	@Override
	public void setStick(boolean show) {
		mShowStick = show;
	}

	@Override
	public void setUseMic(boolean use) {
		mUseMic = use;
		Log.e("TEST", "mUseMic:" + mUseMic);

		if(mUseMic) {
			GRAPH_TUNING_START = 0;
			GRAPH_TUNING_END = 240;
		} else {
			GRAPH_TUNING_START = 80;
			GRAPH_TUNING_END = 600;
		}
		GRAPH_TUNING_GAP = (GRAPH_TUNING_END - GRAPH_TUNING_START) / (GRAPHBAR_SHOW_MAX - 1);
	}

	@Override
	public void setMICSensitivity(int value) {
		mMICSensitivity = value;
	}

	@Override
	public int getCustomColorSet() {
		return CUSTOM_COLORSET;
	}
}