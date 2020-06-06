package noh.jinil.app.anytime.album;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.utils.VLog;

public class ArtExtractor {
    private static final String TAG = "ArtExtractor";

    private ConcurrentHashMap<String, Bitmap> mArtMap = new ConcurrentHashMap<>();
    private Bitmap defaultArt;

    ExtractorHandler mExtractorHandler;
    MainUIHandler mMainUIHandler;

    private static ArtExtractor sInstance;
    private static Context sApplicationContext;

    public static ArtExtractor getInstance(Context context) {
        if(sInstance == null) {
            sApplicationContext = context.getApplicationContext();
            sInstance = new ArtExtractor(context.getApplicationContext());
        }
        return sInstance;
    }

    public interface OnExtractListener {
        void onDownloadCompleted(RequestInfo info, boolean fromServer);
    }

    public ArtExtractor(Context context) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.albumart_mp_unknown);
        if(bitmapDrawable != null) {
            defaultArt = bitmapDrawable.getBitmap();
        }
        HandlerThread handlerThread = new HandlerThread("ArtExtractor", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        mExtractorHandler = new ExtractorHandler(handlerThread.getLooper());
        mMainUIHandler = new MainUIHandler(context.getMainLooper());
    }

    public static class RequestInfo {
        public String inArtUrl;
        public ImageView inView;
        public int inIndex;
        Bitmap outBitmap;
        OnExtractListener listener;

        public Bitmap getBitmap() {
            return outBitmap;
        }
    }

    public boolean requestAlbumArt(RequestInfo info, OnExtractListener listener) {
        if(info.inArtUrl == null)
            return false;

        if(mArtMap.containsKey(info.inArtUrl)) {
            if(listener != null) {
                info.outBitmap = mArtMap.get(info.inArtUrl);
                listener.onDownloadCompleted(info, false);
            }
            return true;
        }

        VLog.v(TAG, "requestAlbumArt() "+info.inArtUrl);

        info.listener = listener;
        Message msg = mExtractorHandler.obtainMessage(ExtractorHandler.EXTRACT_REQUEST_MSG, info);
        mExtractorHandler.sendMessage(msg);
        return false;
    }

    class ExtractorHandler extends Handler {
        static final int EXTRACT_REQUEST_MSG = 1000;

        ExtractorHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            RequestInfo info = (RequestInfo)msg.obj;
            switch(what) {
                case EXTRACT_REQUEST_MSG:
                    Bitmap bmp = null;
                    if(mArtMap.containsKey(info.inArtUrl)) {
                        bmp = mArtMap.get(info.inArtUrl);
                    }
                    else {
                        try {
                            int albumID = Integer.parseInt(info.inArtUrl);
                            bmp = ArtUtils.getArtwork(sApplicationContext, -1, albumID);
                        } catch(NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    if(bmp == null) {
                        bmp = defaultArt;
                    }
                    else if(bmp.getWidth() > 100 && bmp.getHeight() > 100) {
                        bmp = Bitmap.createScaledBitmap(bmp, 100, 100, false);
                    }

                    info.outBitmap = bmp;
                    Message resultMsg = mMainUIHandler.obtainMessage(MainUIHandler.EXTRACT_RESULT_MSG, info);
                    mMainUIHandler.sendMessage(resultMsg);
                    break;
            }
        }
    }

    class MainUIHandler extends Handler {
        static final int EXTRACT_RESULT_MSG = 1000;

        MainUIHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            RequestInfo info = (RequestInfo)msg.obj;

            // 앨범아트를 캐시에 저장
            if(mArtMap != null && info.getBitmap() != null) {
                mArtMap.put(info.inArtUrl, info.getBitmap());
            }

            if(info.listener != null) {
                info.listener.onDownloadCompleted(info, true);
            }
        }
    }

    public void clearCache() {
        defaultArt = null;
        if(mArtMap != null) {
            mArtMap.clear();
        }
        mExtractorHandler.removeMessages(ExtractorHandler.EXTRACT_REQUEST_MSG);
        mMainUIHandler.removeMessages(MainUIHandler.EXTRACT_RESULT_MSG);
    }
}
