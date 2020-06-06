package noh.jinil.app.anytime.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;

public class GIFIcon {
	public static final int ALIGN_BOTTOM = 0x01;
	public static final int ALIGN_RIGHT = 0x02;
	public static final int ALIGN_BOTTOM_RIGHT = ALIGN_BOTTOM | ALIGN_RIGHT;
	
	int mMargin = 0;
	Movie movie;
    long moviestart;
    boolean play;
    
    int posX;
    int posY;
    
    private Context mContext;

    public GIFIcon(Context context) {
    	mContext = context;	
    }
    
    public void setAlign(int align, int width, int height) {
    	if((align & ALIGN_BOTTOM) == ALIGN_BOTTOM) {
    		posY = height - movie.height() - mMargin;
    	}
    	if((align & ALIGN_RIGHT) == ALIGN_RIGHT) {
    		posX = width - movie.width() - mMargin;
    	}
    }
    
    public void setMargin(int pixel) {    	
    	mMargin = pixel;
    	posX = mMargin;
    	posY = mMargin;
    }
    
    public void loadGIFResource(int id) {
    	play = false;
        //turn off hardware acceleration
        //this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        InputStream is = mContext.getResources().openRawResource(id);
        movie = Movie.decodeStream(is);
    }

    public void loadGIFAsset(String filename) {
        InputStream is;
        try {
            is = mContext.getResources().getAssets().open(filename);
            movie = Movie.decodeStream(is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public int width() {
    	if(movie != null) {
    		return movie.width();
    	}
    	return 0;
    }
    
    public int height() {
    	if(movie != null) {
    		return movie.height();
    	}
    	return 0;
    }
    
    public void setPlay(boolean state) {
    	play = state;
    }
    
    public void update(byte[] bytes) {
    	
    }
    
    public void draw(Canvas canvas) {
        if (movie == null) {
        	return;
        }

        long now=android.os.SystemClock.uptimeMillis();
        if (moviestart == 0) moviestart = now;

        if(play) {
	        int relTime;
	        relTime = (int)((now - moviestart) % (movie.duration()));        
	        movie.setTime(relTime);
        }
        else {
        	movie.setTime(0);
        }
        movie.draw(canvas, posX, posY);
    }
}
