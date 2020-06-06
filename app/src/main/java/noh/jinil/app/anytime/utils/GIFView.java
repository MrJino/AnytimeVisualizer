package noh.jinil.app.anytime.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

public class GIFView extends View {

    Movie movie;
    long moviestart;
    boolean play;

    public GIFView(Context context) throws IOException { 
        super(context);
    }
    public GIFView(Context context, AttributeSet attrs) throws IOException{
        super(context, attrs);
    }
    public GIFView(Context context, AttributeSet attrs, int defStyle) throws IOException {
        super(context, attrs, defStyle);
    }

    public void loadGIFResource(Context context, int id)
    {
    	play = false;
        //turn off hardware acceleration
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        InputStream is=context.getResources().openRawResource(id);
        movie = Movie.decodeStream(is);
    }

    public void loadGIFAsset(Context context, String filename)
    {
        InputStream is;
        try {
            is = context.getResources().getAssets().open(filename);
            movie = Movie.decodeStream(is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void setPlay(boolean state) {
    	play = state;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (movie == null) {
        	return;
        }
        if(play == false) {
        	movie.draw(canvas, 0, 0);
        	return;
        }
        
        long now=android.os.SystemClock.uptimeMillis();
        if (moviestart == 0) moviestart = now;

        int relTime;
        relTime = (int)((now - moviestart) % (movie.duration()));
        movie.setTime(relTime);
        
        movie.draw(canvas, 0, 0);
        this.invalidate();
    }
}