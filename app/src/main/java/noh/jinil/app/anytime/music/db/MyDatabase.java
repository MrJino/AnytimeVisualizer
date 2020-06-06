package noh.jinil.app.anytime.music.db;

import android.provider.BaseColumns;

public class MyDatabase {
	public static final class CreateDB implements BaseColumns {
		public static final String AUDIOID = "audioid";
		public static final String ALBUMID = "albumid";
		public static final String TITLE = "title";
		public static final String ARTIST = "artist";
		public static final String _TABLE_NOWPLAYING = "nowplaying";		
		
		public static final String _CREATE_NOWPLAYING = "create table " + _TABLE_NOWPLAYING + "(" 
													+_ID + " integer primary key autoincrement, "
													+ AUDIOID + " integer, "
													+ ALBUMID + " integer, "
												    + TITLE + " text, "
												    + ARTIST + " text );";
	}
}