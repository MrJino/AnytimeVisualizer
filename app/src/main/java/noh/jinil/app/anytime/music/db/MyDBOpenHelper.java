package noh.jinil.app.anytime.music.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBOpenHelper {
	private static final String DATABASE_NAME = "MusicVisualizer.db";
	private static final int DATABASE_VERSION = 2;
	
	private Context mContext;
	private DataBaseHelper mDBHelper;
	private SQLiteDatabase mDB;
	
	private class DataBaseHelper extends SQLiteOpenHelper {
		public DataBaseHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(MyDatabase.CreateDB._CREATE_NOWPLAYING);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + MyDatabase.CreateDB._TABLE_NOWPLAYING);
			onCreate(db);
		}
	}
	
	public MyDBOpenHelper(Context context) {
		mContext = context;
	}
	
	public MyDBOpenHelper open() {
		mDBHelper = new DataBaseHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION);
		mDB = mDBHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		mDB.close();
	}
	
	public long insert(String table, ContentValues values) {
		return mDB.insert(table, null, values);		
	}
	
	public int update(String table, ContentValues values, long whereId) {
		return mDB.update(table, values, MyDatabase.CreateDB._ID+"="+whereId, null);		
	}
	
	public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
		return mDB.update(table, values, whereClause, whereArgs);		
	}
	
	public Cursor query(String table) {
		return mDB.query(table, null, null, null, null, null, null);
	}
	 
	public Cursor query(String table, String selection) {
		return mDB.query(table, null, selection, null, null, null, null);
	}
}
