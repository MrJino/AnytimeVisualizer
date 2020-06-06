package noh.jinil.app.anytime.visualizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class TypeWooferBar implements ITypeView {	
	private static final int SPEAKER_NONE 	= 0;
	private static final int SPEAKER_LOW 	= 1;
	private static final int SPEAKER_MEDIUM = 2;
	private static final int SPEAKER_HIGH	= 3;
	private static final int SPEAKER_SUPER	= 4;
	
	private final int WOOFERBAR_SHOW_MAX 	= 46;
	private final int WOOFERBAR_BASE		= 5;
	private final int WOOFERBAR_GAP			= 1;
    private final int WOOFER_TUNING_MAX 	= 250;
	private final int WOOFER_TUNING_MIN 	= 1;
	private final int WOOFER_TUNING_START 	= 80;
	private final int WOOFER_TUNING_END 	= 600;
	private final int WOOFER_TUNING_GAP 	= (WOOFER_TUNING_END-WOOFER_TUNING_START)/(WOOFERBAR_SHOW_MAX-1);
	private int mSCREEN_WIDTH;
	private int mSCREEN_HEIGHT;
	private int mSCREEN_DENSITY;
	private int mFULL_WIDTH = 0;
	private int mFULL_HEIGHT = 0;
	private int mWOOFER_X = 0;
	private int mWOOFER_Y = 0;
	private int mWOOFER_WIDTH = 0;
	private int mWOOFER_HEIGHT = 0;	
	private int mWOOFERBAR_WIDTH = 0;
	private int WOOFERBAR_CENTER = 0; //392;
	private int mWOOFERBAR_EDGE = 0;
	private Bitmap mBmpWooferBG;
	private Bitmap mBmpWooferBar[];	
	private Bitmap mBmpSpeakerSuper[];
	private Bitmap mBmpSpeakerHigh[];
	private Bitmap mBmpSpeakerLow[];
	private Bitmap mBmpSpeakerMedium[];
	
	private double mMaxAverage;
	
	// object class by show type
	private WooferBar[] mWooferBar;
	private WooferSpeaker mWooferSpeaker;
	
	public TypeWooferBar(Context context) {
//		mBmpWooferBG = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_bg)).getBitmap();
//    	
//    	mBmpWooferBar = new Bitmap[46];
//    	mBmpWooferBar[0] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_101)).getBitmap();
//		mBmpWooferBar[1] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_102)).getBitmap();
//		mBmpWooferBar[2] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_103)).getBitmap();
//		mBmpWooferBar[3] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_104)).getBitmap();
//		mBmpWooferBar[4] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_105)).getBitmap();
//		mBmpWooferBar[5] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_106)).getBitmap();
//    	mBmpWooferBar[6] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_107)).getBitmap();
//    	mBmpWooferBar[7] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_108)).getBitmap();
//    	mBmpWooferBar[8] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_109)).getBitmap();
//    	mBmpWooferBar[9] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_110)).getBitmap();
//    	mBmpWooferBar[10] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_111)).getBitmap();
//    	mBmpWooferBar[11] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_112)).getBitmap();
//    	mBmpWooferBar[12] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_113)).getBitmap();
//    	mBmpWooferBar[13] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_114)).getBitmap();
//    	mBmpWooferBar[14] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_115)).getBitmap();
//    	mBmpWooferBar[15] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_116)).getBitmap();
//    	mBmpWooferBar[16] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_117)).getBitmap();
//    	mBmpWooferBar[17] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_118)).getBitmap();
//    	mBmpWooferBar[18] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_119)).getBitmap();
//    	mBmpWooferBar[19] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_120)).getBitmap();
//    	mBmpWooferBar[20] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_121)).getBitmap();
//    	mBmpWooferBar[21] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_122)).getBitmap();
//    	mBmpWooferBar[22] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_123)).getBitmap();
//    	mBmpWooferBar[23] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_124)).getBitmap();
//    	mBmpWooferBar[24] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_125)).getBitmap();
//    	mBmpWooferBar[25] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_126)).getBitmap();
//    	mBmpWooferBar[26] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_127)).getBitmap();
//    	mBmpWooferBar[27] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_128)).getBitmap();
//    	mBmpWooferBar[28] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_129)).getBitmap();
//    	mBmpWooferBar[29] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_130)).getBitmap();
//    	mBmpWooferBar[30] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_131)).getBitmap();
//    	mBmpWooferBar[31] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_132)).getBitmap();
//    	mBmpWooferBar[32] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_133)).getBitmap();
//    	mBmpWooferBar[33] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_134)).getBitmap();
//    	mBmpWooferBar[34] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_135)).getBitmap();
//    	mBmpWooferBar[35] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_136)).getBitmap();
//    	mBmpWooferBar[36] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_137)).getBitmap();
//    	mBmpWooferBar[37] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_138)).getBitmap();
//    	mBmpWooferBar[38] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_139)).getBitmap();
//    	mBmpWooferBar[39] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_140)).getBitmap();
//    	mBmpWooferBar[40] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_141)).getBitmap();
//    	mBmpWooferBar[41] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_142)).getBitmap();
//    	mBmpWooferBar[42] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_143)).getBitmap();
//    	mBmpWooferBar[43] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_144)).getBitmap(); 	
//    	mBmpWooferBar[44] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_145)).getBitmap();
//    	mBmpWooferBar[45] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.vibrate_equalizer_146)).getBitmap();
    	
//    	mBmpSpeakerSuper = new Bitmap[6];
//    	mBmpSpeakerSuper[0] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_super_high_01)).getBitmap();
//    	mBmpSpeakerSuper[1] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_super_high_02)).getBitmap();
//    	mBmpSpeakerSuper[2] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_super_high_03)).getBitmap();
//    	mBmpSpeakerSuper[3] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_super_high_04)).getBitmap();
//    	mBmpSpeakerSuper[4] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_super_high_05)).getBitmap();
//    	mBmpSpeakerSuper[5] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_super_high_06)).getBitmap();
//    	
//    	mBmpSpeakerHigh = new Bitmap[6];
//    	mBmpSpeakerHigh[0] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_high_01)).getBitmap();
//    	mBmpSpeakerHigh[1] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_high_02)).getBitmap();
//    	mBmpSpeakerHigh[2] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_high_03)).getBitmap();
//    	mBmpSpeakerHigh[3] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_high_04)).getBitmap();
//    	mBmpSpeakerHigh[4] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_high_05)).getBitmap();
//    	mBmpSpeakerHigh[5] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_high_06)).getBitmap();
//    	
//    	mBmpSpeakerMedium = new Bitmap[6];
//    	mBmpSpeakerMedium[0] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_medium_01)).getBitmap();
//    	mBmpSpeakerMedium[1] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_medium_02)).getBitmap();
//    	mBmpSpeakerMedium[2] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_medium_03)).getBitmap();
//    	mBmpSpeakerMedium[3] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_medium_04)).getBitmap();
//    	mBmpSpeakerMedium[4] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_medium_05)).getBitmap();
//    	mBmpSpeakerMedium[5] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_medium_06)).getBitmap();
//    	
//    	mBmpSpeakerLow = new Bitmap[6];
//    	mBmpSpeakerLow[0] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_low_01)).getBitmap();
//    	mBmpSpeakerLow[1] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_low_02)).getBitmap();
//    	mBmpSpeakerLow[2] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_low_03)).getBitmap();
//    	mBmpSpeakerLow[3] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_low_04)).getBitmap();
//    	mBmpSpeakerLow[4] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_low_05)).getBitmap();
//    	mBmpSpeakerLow[5] = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker_low_06)).getBitmap();
	}
	@Override
	public void update(byte[] bytes) {
		double average = 0, sum = 0;  // ������ ��հ�		
		for(int i=1; i < bytes.length-1; i++) {
			if(bytes[i] < 0)
				bytes[i] = (byte)(-bytes[i]);
		}
		
		if(mWooferBar == null || mWooferSpeaker == null) 
			return;
		
		/**
		 * ��հ��� �����ش�. Ʃ�װ� ����
		 */
		for(int i=0; i<WOOFERBAR_SHOW_MAX; i++) {	
			//-----------------------------------------------
			average = 0;
			for(int f = WOOFER_TUNING_START+i*WOOFER_TUNING_GAP; f < WOOFER_TUNING_START+i*WOOFER_TUNING_GAP+10; f++){
				average += bytes[f];
			}
			average = Math.log((1+average/10))*150;
			
			if(average > mMaxAverage) {
				mMaxAverage = average;
//				Log.d(TAG, "=>MAX:"+mMaxAverage+", log:"+Math.log(mMaxAverage));
			}
			//average = average / 5 + 1;
			//average = average * 30 - 45;
			//average = average * 2;
			
			if(average > WOOFER_TUNING_MAX) 
				average = WOOFER_TUNING_MAX;
			else if(average < WOOFER_TUNING_MIN)
				average = WOOFER_TUNING_MIN;
			
			sum = sum + average;
			//-----------------------------------------------
			
			if(i % 2 == 0) {
				mWooferBar[((WOOFERBAR_SHOW_MAX-1)-i)/2].updateBar((int)average);
			} else {
				mWooferBar[((WOOFERBAR_SHOW_MAX-1)+i)/2].updateBar((int)average);
			}
			
//			// WooferBar
//			if(mType == TYPE_WOOFER_2) {				
//				if(i % 2 == 0) {
//					mWooferBar[i/2].updateBar((int)average);
//				} else {
//					mWooferBar[(WOOFERBAR_SHOW_MAX-1)-(i/2)].updateBar((int)average);
//				}
//			}
//			else if(mType == TYPE_WOOFER_3) {
//				mWooferBar[i].updateBar((int)average);
//			}
		}
		

		if(sum > (WOOFER_TUNING_MAX*WOOFERBAR_SHOW_MAX)*2/3) {
			mWooferSpeaker.update(SPEAKER_SUPER, sum);
		}
		else if(sum > (WOOFER_TUNING_MAX*WOOFERBAR_SHOW_MAX)/2) {
			mWooferSpeaker.update(SPEAKER_HIGH, sum);
		} else if(sum > (WOOFER_TUNING_MAX*WOOFERBAR_SHOW_MAX)/3) {
			mWooferSpeaker.update(SPEAKER_MEDIUM, sum);
		} else if(sum > (WOOFER_TUNING_MAX*WOOFERBAR_SHOW_MAX)/8) {
			mWooferSpeaker.update(SPEAKER_LOW, sum);
		}
	}

	@Override
	public void draw(Canvas canvas) {
		for(int i=0; i<WOOFERBAR_SHOW_MAX; i++){
			mWooferBar[i].draw(canvas);
		}
		mWooferSpeaker.draw(canvas);
	}

	@Override
	public void onSizeChanged(int width, int height, int density) {
		mFULL_WIDTH  = width;
		mFULL_HEIGHT = height;
		
		mSCREEN_WIDTH = width;
		mSCREEN_HEIGHT = height;
		mSCREEN_DENSITY = density;
		
    	//mWOOFERBAR_WIDTH = mBmpWooferBar[0].getWidth()+3+WOOFERBAR_GAP;    	
    	//mWOOFER_WIDTH  = mWOOFERBAR_WIDTH*WOOFERBAR_SHOW_MAX;
		
    	mWOOFER_WIDTH = (width < 350*density) ? width : 350*density;
		mWOOFER_HEIGHT = mBmpWooferBar[0].getHeight();
		
		mWOOFERBAR_WIDTH = (mWOOFER_WIDTH / WOOFERBAR_SHOW_MAX);
		
		mWOOFER_X = (width - mWOOFER_WIDTH)/2;
		mWOOFER_Y = (height -mWOOFER_HEIGHT)/2; //(height - mWOOFER_HEIGHT)/2;
		
		WOOFERBAR_CENTER = density * 130;
		mWOOFERBAR_EDGE  = (mWOOFER_HEIGHT - WOOFERBAR_CENTER)/2;
		
		if(mWooferSpeaker == null) {
			mWooferSpeaker = new WooferSpeaker();
		}
		mWooferSpeaker.initPos();
		
		mWooferBar = new WooferBar[WOOFERBAR_SHOW_MAX];
		for(int i=0; i<WOOFERBAR_SHOW_MAX; i++) {
			mWooferBar[i] = new WooferBar(i);
		}
	}
	
	@Override
	public void refreshChanged() {
		onSizeChanged(mSCREEN_WIDTH, mSCREEN_HEIGHT, mSCREEN_DENSITY);
	}

	class WooferSpeaker {
    	private int posX;
    	private int posY;
    	private int bmpIndex;
    	private int intensity;
    	private int duration;
    	private Bitmap bmpSpeaker[];
    	
    	//private int mTotal;
    	private Rect srcRect, dstRect;
    	
    	WooferSpeaker() {
    		srcRect = new Rect();
    		dstRect = new Rect();
    		bmpSpeaker = mBmpSpeakerLow;
    	}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			return super.clone();
		}

		void initPos() {
    		posX = mWOOFER_X + mWOOFER_WIDTH/2;
    		posY = mWOOFER_Y + mWOOFER_HEIGHT/2;
    	}
    	
    	public void update(int newIntensity, double total) {
    		//mTotal = (int)total;
    		
    		if(newIntensity < this.intensity)
    			return;
    		
    		this.intensity  = newIntensity;
    		this.bmpIndex   = 0;
    		
    		//Log.d(TAG, "=>intensity:"+intensity);
    		
    		if(newIntensity == SPEAKER_SUPER) {
    			bmpSpeaker = mBmpSpeakerSuper;
    			duration = bmpSpeaker.length * 2;
    		} else if(newIntensity == SPEAKER_HIGH) {
    			bmpSpeaker = mBmpSpeakerHigh;
    			duration = bmpSpeaker.length;
    		} else if(newIntensity == SPEAKER_MEDIUM) {
    			bmpSpeaker = mBmpSpeakerMedium;
    			duration = bmpSpeaker.length;
    		} else {
    			bmpSpeaker = mBmpSpeakerLow;
    			duration = bmpSpeaker.length;
    		}
    	}
    	
    	public void draw(Canvas c)  {
    		if(intensity == SPEAKER_NONE) {
    			c.drawBitmap(bmpSpeaker[0], posX-bmpSpeaker[0].getWidth()/2, posY-(bmpSpeaker[0].getHeight()/2), null);
    		}
    		else {
    			c.drawBitmap(bmpSpeaker[bmpIndex%bmpSpeaker.length], posX-bmpSpeaker[bmpIndex%bmpSpeaker.length].getWidth()/2, posY-(bmpSpeaker[bmpIndex%bmpSpeaker.length].getHeight()/2), null);
        		bmpIndex++;
        		if(bmpIndex >= duration) {
        			intensity = SPEAKER_NONE;
        		}
    		}
    		
    		// landscape intensity
    		//int landIntensity = ((mWIDTH-mWOOFER_WIDTH) * mTotal) / (WOOFER_TUNING_MAX * WOOFERBAR_SHOW_MAX);
			srcRect.set(0, 0, mBmpWooferBG.getWidth()/2, mBmpWooferBG.getHeight());
			dstRect.set(0, (mFULL_HEIGHT - mBmpWooferBG.getHeight())/2, (mFULL_WIDTH-mWOOFER_WIDTH)/2,  (mFULL_HEIGHT + mBmpWooferBG.getHeight())/2);
			c.drawBitmap(mBmpWooferBG, srcRect, dstRect, null);
			
			srcRect.set(mBmpWooferBG.getWidth()/2, 0, mBmpWooferBG.getWidth(), mBmpWooferBG.getHeight());
			dstRect.set((mFULL_WIDTH+mWOOFER_WIDTH)/2, (mFULL_HEIGHT - mBmpWooferBG.getHeight())/2, mFULL_WIDTH,  (mFULL_HEIGHT + mBmpWooferBG.getHeight())/2);
			c.drawBitmap(mBmpWooferBG, srcRect, dstRect, null);
    	}
    }
    
    class WooferBar {
    	private int myIndex;
    	private int bmpIndex;
    	
    	private int prevPos;
		private int currPos;
		private int livePos;
		private int liveStep;
		
		private int posX;
		private int posY;
	
		private int intensity;
		private int edge;
		private int base;
		private double ratio;
		
		private Rect mSrcRect = new Rect();
	    private Rect mDstRect = new Rect();
		
	    WooferBar(int index) {
	    	myIndex = index;
	    	
	    	if(myIndex >= WOOFERBAR_SHOW_MAX)
	    		bmpIndex = mBmpWooferBar.length-1;  // mono bitmap
	    	else
	    		bmpIndex = myIndex;
	    	
	    	posX = (mWOOFER_X)+index*(mWOOFERBAR_WIDTH);
	    	posY = mWOOFER_Y + mWOOFER_HEIGHT/2;
	    	base = WOOFERBAR_BASE;
	    	if(index < WOOFERBAR_SHOW_MAX/2) {
	    		ratio = 0.9 + (1.0-Math.cos(Math.toRadians(((double)index/4)*360/(double)WOOFERBAR_SHOW_MAX)));
	    	} else {
	    		ratio = 0.9 + (1.0-Math.cos(Math.toRadians(((double)(WOOFERBAR_SHOW_MAX-index)/4)*360/(double)WOOFERBAR_SHOW_MAX)));
	    	}
		}
		
		public void updateBar(int value) {
//			if(value < livePos)
//				return;
			
			//value = WOOFER_TUNING_MAX;
			
			prevPos = currPos;
			livePos = currPos;
							
			currPos = value;
			liveStep = 0;  // ��ô�� ī��Ʈ
		}
		
		public void reset() {
			prevPos = 1;
			livePos = 1;
			currPos = 1;
			liveStep = 0;
		}
		
		public void draw(Canvas c)  {
			liveStep++;
			livePos = prevPos + AnimationEffect.getValueFromSineData(liveStep, 5, currPos - prevPos);
			//livePos = currPos + AnimationEffect.getValueFromCurveData(liveStep++, 20, WOOFER_TUNING_MIN - currPos);
			intensity = base+((WOOFERBAR_CENTER-base)/2*livePos)/WOOFER_TUNING_MAX;
			edge = mWOOFERBAR_EDGE/2+(mWOOFERBAR_EDGE/2*livePos)/WOOFER_TUNING_MAX;
			
			mSrcRect.set(0, mWOOFER_HEIGHT/2-intensity, mBmpWooferBar[bmpIndex].getWidth(), mWOOFER_HEIGHT/2+intensity);
			mDstRect.set(posX, posY-(int)(intensity*ratio), posX+(mWOOFERBAR_WIDTH-WOOFERBAR_GAP), posY+(int)(intensity*ratio));
			c.drawBitmap(mBmpWooferBar[bmpIndex], mSrcRect, mDstRect, null);
			
			mSrcRect.set(0, 0, mBmpWooferBar[bmpIndex].getWidth(), mWOOFERBAR_EDGE);
			mDstRect.set(posX, (posY-(int)(intensity*ratio))-(int)(edge*ratio), posX+(mWOOFERBAR_WIDTH-WOOFERBAR_GAP), (posY-(int)(intensity*ratio)));
			c.drawBitmap(mBmpWooferBar[bmpIndex], mSrcRect, mDstRect, null);
			
			mSrcRect.set(0, mWOOFER_HEIGHT-mWOOFERBAR_EDGE, mBmpWooferBar[bmpIndex].getWidth(), mWOOFER_HEIGHT);
			mDstRect.set(posX, (posY+(int)(intensity*ratio)), posX+(mWOOFERBAR_WIDTH-WOOFERBAR_GAP), (posY+(int)(intensity*ratio))+(int)(edge*ratio));
			c.drawBitmap(mBmpWooferBar[bmpIndex], mSrcRect, mDstRect, null);
		}
    }

	@Override
	public float plusRatio() {
		return 0;
	}
	@Override
	public void setAlpha(int value) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setColorSet(int value) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setBarSize(int value) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setStick(boolean show) {
	}

	@Override
	public void setUseMic(boolean use) {
	}

	@Override
	public void setMICSensitivity(int value) {

	}

	@Override
	public int getCustomColorSet() {
		// TODO Auto-generated method stub
		return 0;
	}
}
