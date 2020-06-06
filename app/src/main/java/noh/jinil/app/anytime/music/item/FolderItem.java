package noh.jinil.app.anytime.music.item;

import noh.jinil.app.anytime.utils.FileUtils;

import android.database.Cursor;
import android.provider.MediaStore;

public class FolderItem  {
	
	private static final String BUCKET_ID = "bucket_id";
	private static final String BUCKET_DISPLAY_NAME = "bucket_display_name";
	
	String mBucketId;
	String mDisplay;
	String mPath;
	
	public FolderItem(String bucketid, String display, String path) {
		mBucketId = bucketid;
		mDisplay = display;
		mPath = path;
	}
	
	public FolderItem(Cursor c) {
		setValues(c);
	}
	
	public void setValues(Cursor c) {
		try {
			mBucketId = c.getString(c.getColumnIndex(BUCKET_ID));
		}
		catch(Exception e) {
			mBucketId = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
			mBucketId = FileUtils.getOnlyFolderPath(mBucketId);
		}
		try {
			mDisplay = c.getString(c.getColumnIndex(BUCKET_DISPLAY_NAME));
		}
		catch(Exception e) {
			mDisplay = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
			mDisplay = FileUtils.getLastPath(mDisplay);
		}
		mPath = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
		mPath = FileUtils.getOnlyFolderPath(mPath);
	}
	
	public String getBucketId() {
		return mBucketId;
	}
	
	public String getDisplay() {
		return mDisplay;
	}
	
	public String getPath() {
		return mPath;
	}
}
