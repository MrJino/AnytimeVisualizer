package noh.jinil.app.anytime.music;

import java.lang.reflect.Method;

import ca.uol.aig.fftpack.RealDoubleFFT;
import noh.jinil.app.anytime.utils.GIFView;
import noh.jinil.app.anytime.utils.VLog;
import noh.jinil.app.anytime.visualizer.IVisualizerView;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.AsyncTask;
import android.util.Log;


public class VisualizerManager {
	private static final String TAG = "VisualizerManager";
	
	public static final String PACKAGE_NAME = "noh.jinil.app.anytime";
	
	private Equalizer mEqualizer;
	private Visualizer mVisualizer;
	IVisualizerView mView;
	byte[] mBytes;
	GIFView mGifView;
	private AudioManager mAudioManager;
	
	private int mSessionID = 0;
	private boolean bLocal = false;
	
	private static VisualizerManager mVisualizerUtil = new VisualizerManager();
	
	public static VisualizerManager getInstance() {
		return mVisualizerUtil;
	}
	
	VisualizerManager() {
		turnoffLPASystemProperty();
	}
	
	public void setupView(IVisualizerView view) {
		Log.d(TAG, "setupView()");
		mView = view;
	}
	
	public void setupGIF(GIFView gifview) {
		mGifView = gifview;
	}
	
	public void setAudioManager(AudioManager am, int sessionid) {
		mAudioManager = am;
		mSessionID = sessionid;
	}
	
//	public void setSessionID(int sessionid) {
//		release();
//		setup(sessionid);
//	}
	
	public boolean toggleSessionID() {
		Log.d(TAG, "toggleSessionID()");
		if(bLocal) {
			bLocal = false;
			releaseSession();
			setupSession(0);
			//setupSession(mSessionID);
		} else {
			bLocal = true;
			releaseSession();
			setupSession(mSessionID);
		}
		return bLocal;
	}
	
	public boolean isMusicActive() {
		if(mAudioManager != null && mAudioManager.isMusicActive()) {
    		return true;
    	}
		return false;
	}
	
	public boolean isVisualizerActive() {
		// if volume is zero, you should ignore result
		if(mAudioManager != null && mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
    		return true;
    	}
		
		if(mBytes == null)
			return false;
		
		for(int i=0; i<mBytes.length; i++) {			
			if(mBytes[i] != 0)
				return true;
		}
		return false;
	}

	public void setupMic() {
		Log.d(TAG, "setupMic()");
		// AudioSession Release
		releaseSession();

		if(transformer ==  null) {
			transformer = new RealDoubleFFT(blockSize);
		}

		recordTask = new RecordAudio();
		recordTask.execute();
	}

	public void releaseMic() {
		Log.d(TAG, "releaseMic()");
		if(recordTask != null) {
			CANCELLED_FLAG = true;
		}
	}

	public void setupSession() {
		bLocal = false;
		releaseSession();
		//setupSession(mSessionID);
		setupSession(0);
	}
	
	public void setupSession(int sessionId) {
		Log.d(TAG, "setupSession() "+sessionId);

		releaseMic();

		if(mVisualizer == null) {
			try {
				mVisualizer = new Visualizer(sessionId);
				mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
		        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
		            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
		            }
		            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
						VLog.v(TAG, "onFftDataCapture() value:"+bytes[bytes.length/2]+", length:"+bytes.length+", samplingRate:"+samplingRate);
		            	if(!isMusicActive()) {
		            		bytes = null;
		            	}
		            	if(mView != null) {
		            		mView.update(bytes);
		            	}
		            	mBytes = bytes;
		            }
		        }, Visualizer.getMaxCaptureRate() / 2, false, true);

		        VLog.d(TAG, "=>CaptureSizeRangeMin:"+Visualizer.getCaptureSizeRange()[0]);
		        VLog.d(TAG, "=>CaptureSizeRangeMax:"+Visualizer.getCaptureSizeRange()[1]);
		        VLog.d(TAG, "=>MaxCaptureRate:"+Visualizer.getMaxCaptureRate());
		        
		        mVisualizer.setEnabled(true);
		        //mEqualizer = new Equalizer(0, 0);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void releaseSession() {
		Log.d(TAG, "release()");

		if(mVisualizer != null) {
			mVisualizer.setEnabled(false);
			mVisualizer.release();
			mVisualizer = null;
		}
		if(mEqualizer != null) {
			Log.d(TAG, "=>equalizer for visualizer is released!!");
			mEqualizer.release();
			mEqualizer = null;
		}
	}

	public void release() {
		releaseSession();
		releaseMic();
	}
	
	private void turnoffLPASystemProperty() {
		try{
//			if(android.os.Build.VERSION.SDK_INT >= 19) {
				Class<?> properties = Class.forName("android.os.SystemProperties");
				Object obj = properties.getConstructor().newInstance();
				
				Method get = obj.getClass().getMethod("get", new Class[]{String.class});
				Method set = obj.getClass().getMethod("set", new Class[]{String.class, String.class});
				
				boolean disable = Boolean.valueOf((String)(get.invoke(obj, "audio.offload.disable")));
				
				Log.w(TAG, "disable!!:"+disable);
				if(!disable) {
					Log.w(TAG, "turnoffOffloadSystemProperty()");
					set.invoke(obj, "audio.offload.disable", "true");
				}
//			}
//			else {
//				Class<?> properties = Class.forName("android.os.SystemProperties");
//				Object obj = properties.getConstructor().newInstance();
				
//				Method get = obj.getClass().getMethod("get", new Class[]{String.class});
//				Method set = obj.getClass().getMethod("set", new Class[]{String.class, String.class});
				
				boolean lpaMode = Boolean.valueOf((String)(get.invoke(obj, "lpa.decode")));				
				if(lpaMode) {
					Log.w(TAG, "turnoffLPASystemProperty()");
					set.invoke(obj, "lpa.decode", "false");
				}
//			}			
		} 
		catch(Exception e){
			e.printStackTrace();
		}
	}

	RecordAudio recordTask;
	int frequency = 8000;
	int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	AudioRecord audioRecord;
	private RealDoubleFFT transformer;
	int blockSize = 256;

	boolean CANCELLED_FLAG = false;

	class RecordAudio extends AsyncTask<Void, double[], Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			Log.d(TAG, "doInBackground()");
			int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, frequency, channelConfiguration, audioEncoding, bufferSize);
			int bufferReadResult;
			short[] buffer = new short[blockSize];
			double[] toTransform = new double[blockSize];
			try {
				audioRecord.startRecording();
			} catch (IllegalStateException e) {
				Log.e("Recording failed", e.toString());
			}
			while (true) {
				if(CANCELLED_FLAG || isCancelled()) {
//					started = false;
					//publishProgress(cancelledResult);
					Log.d("doInBackground", "Cancelling the RecordTask");
					CANCELLED_FLAG = false;
					break;
				}

				bufferReadResult = audioRecord.read(buffer, 0, blockSize);

				for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
					toTransform[i] = (double) buffer[i] / 32768.0; // signed 16 bit
				}
				transformer.ft(toTransform);
				publishProgress(toTransform);
			}
			return true;
		}

		@Override
		protected void onProgressUpdate(double[]...progress) {
			byte bytes[] =  new byte[progress[0].length];
			for(int i=0; i<progress[0].length; i++) {
				bytes[i] = (byte)progress[0][i];
			}

			if(mView != null) {
				mView.update(bytes);
			}
			mBytes = bytes;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			Log.d(TAG, "onPostExecute()");
			try{
				audioRecord.stop();
			}
			catch(IllegalStateException e){
				Log.e("Stop failed", e.toString());
			}
		}
	}
}
