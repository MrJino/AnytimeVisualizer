package noh.jinil.app.anytime.utils;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

public class FileUtils {
	private static String mLocalpath = null;
	
	public static void fixLocalPath(Context context) {		
		mLocalpath = context.getFilesDir().getAbsolutePath();
		if(!checkLastSeperator(mLocalpath)) {
			mLocalpath = mLocalpath + "/";
		}
	}
	
	public static File getLocalFilesDir(Context context) {
		return context.getFilesDir();
	}
	
	public static File getLocalFilePath(Context context, String filename) {
		return context.getFileStreamPath(filename);
	}
	
	public static File getExternalFilesDir() {
		return Environment.getExternalStorageDirectory();
	}

	/**
	 Environment.DIRECTORY_ALARMS
	 Environment.DIRECTORY_DCIM
	 Environment.DIRECTORY_DOWNLOADS
	 Environment.DIRECTORY_MUSIC
	 Environment.DIRECTORY_MOVIES
	 Environment.DIRECTORY_NOTIFICATIONS
	 Environment.DIRECTORY_PICTURES
	 Environment.DIRECTORY_PODCASTS
	 */
	public static File getPublicFilesDir(String type) {
		return Environment.getExternalStoragePublicDirectory(type);
	}
	
	public static File getPublicLocalFilesDir(Context context, String type) {
		return context.getExternalFilesDir(type);
	}
	
	public static boolean checkLastSeperator(String path) {
		if(path.lastIndexOf('/') == path.length()-1) {
			return true;
		}
		return false;
	}
	
	public static String getFullPath(String filepath) {
		if(mLocalpath != null) {
			return mLocalpath + filepath;
		}
		return null;
	}
	
	public static String getOnlyFolderPath(String fullpath) {
		int lastIdx = fullpath.lastIndexOf('/');
		if(lastIdx == fullpath.length()-1) {
			return fullpath;
		}
		return fullpath.substring(0, lastIdx+1);
	}

	public static String getFolderName(String path) {
		String splitWords[] = TextUtils.split(path, "/");
		if(splitWords == null)
			return null;
		int length = splitWords.length;
		if(splitWords[length-1].equals("") || splitWords[length-1].contains("."))
			return (length < 2) ? null : splitWords[length-2];
		return splitWords[length-1];
	}

	private static final String ROOT_PATH = "/storage/emulated/0";

	public static String getFolderPath(String path) {
		if(path.startsWith(ROOT_PATH)) {
			path = path.substring(ROOT_PATH.length());
		}
		int lastIdx = path.lastIndexOf('/');
		if(lastIdx == path.length()-1) {
			return path;
		}
		return path.substring(0, lastIdx+1);
	}
	
	public static String getLastPath(String fullpath) {
		int lastidx = fullpath.lastIndexOf('/');
		String path;
		if(lastidx == fullpath.length()-1) {
			path = fullpath;
		} else {
			path = fullpath.substring(0, lastidx);
		}
		lastidx = path.lastIndexOf('/');
		return path.substring(lastidx+1);
	}
}
