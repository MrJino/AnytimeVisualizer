package noh.jinil.app.anytime.utils;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class ArcDrawable extends Drawable {
	
	Paint mPaint;
	RectF mRect;
	float mAngle = 0.0f;
	
	public ArcDrawable(int color, int stroke, float angle) {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(color);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(stroke);
		
		mRect = new RectF();
		
		mAngle = angle;
	}	
	
	@Override
	public void draw(Canvas canvas) {
		
		int height = getBounds().height();
	    int width = getBounds().width();
	    int diameter;
	    int x, y;
	    if(width < height) {	    	
	    	diameter = width;
	    } else {
	    	diameter = height;
	    }
	    
	    x = (width-diameter)/2;
    	y = (height-diameter)/2;	    
	    mRect.set(x, y, x+diameter, y+diameter);
		
		canvas.drawArc(mRect, -90, mAngle, false, mPaint);
	}
	
	public void setColor(int color) {
		mPaint.setColor(color);
	}
	
	public void setAngle(float angle) {
		mAngle = angle;
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
	}

	@Override
	public int getOpacity() {
		return 0;
	}
}
