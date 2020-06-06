package noh.jinil.app.anytime.music.item;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

public class TrackItem implements Parcelable {
	long audioId;
	long albumId;
	long duration;
	String title;
	String artist;
	
	public TrackItem(long audioId, long albumId, String title, String artist, long duration) {
		this.audioId = audioId;
		this.albumId = albumId;
		this.title = title;
		this.artist = artist;
		this.duration = duration;
	}
	
	public TrackItem(Cursor c) {
		setValues(c);
	}
	
	public TrackItem(Parcel in) {
		readFromParcel(in);
	}
	
	public void setValues(Cursor c) {
		audioId = c.getLong(c.getColumnIndex(MediaStore.Audio.Media._ID));
		albumId = c.getLong(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
		title = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
		artist = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
		duration = c.getLong(c.getColumnIndex(MediaStore.Audio.Media.DURATION));
	}
	
	public long getAlbumId() {
		return albumId;
	}
	
	public long getAudioId() {
		return audioId;
	}

	public long getDuration() {
		return duration;
	}

	public String getTitle() {
		return title;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public static TrackItem newItem(Context context, long audioid) {
		TrackItem item = null;
		ContentResolver res = context.getContentResolver();
        Cursor c = null;
        try {
            c = res.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media._ID + "="+audioid, null, null);
            if (c == null || c.getCount() == 0) {
                return new TrackItem(-1, -1, "", "", 0);
            }
            if(c.moveToNext()) {
            	item = new TrackItem(c);
            }
            return item;
        } 
        catch (RuntimeException ex) {
			ex.printStackTrace();
        } 
        finally {
            if (c != null) {
                c.close();
            }
        }
        return new TrackItem(-1, -1, "", "", 0);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(audioId);
		dest.writeLong(albumId);
		dest.writeLong(duration);
		dest.writeString(title);
		dest.writeString(artist);
	}
	
	private void readFromParcel(Parcel in){
		audioId = in.readLong();
		albumId = in.readLong();
		duration = in.readLong();
		title = in.readString();
		artist = in.readString();
	}
	
	public static final Parcelable.Creator<TrackItem> CREATOR = new Parcelable.Creator<TrackItem>() {
        public TrackItem createFromParcel(Parcel in) {
             return new TrackItem(in);
       }

       public TrackItem[] newArray(int size) {
            return new TrackItem[size];
       }
   };
}
