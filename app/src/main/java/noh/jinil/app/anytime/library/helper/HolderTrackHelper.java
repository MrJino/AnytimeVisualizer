package noh.jinil.app.anytime.library.helper;

import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.library.LibraryPageInfo;

public class HolderTrackHelper implements IHolderHelper {
    TextView textTrackName;
    TextView textDuration;
    ImageView imageAlbumArt;
    int albumID;
    long audioID;

    public HolderTrackHelper(View itemView) {
        textTrackName = (TextView)itemView.findViewById(R.id.adapter_list_track_trackName);
        textDuration = (TextView)itemView.findViewById(R.id.adapter_list_track_duration);
        imageAlbumArt = (ImageView)itemView.findViewById(R.id.adapter_list_track_albumArt);
    }

    @Override
    public void setInfo(Cursor cursor) {
        String trackName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
        int duration = (cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)) / 1000);
        textTrackName.setText(trackName);
        textDuration.setText(String.format(Locale.KOREA, "%1$02d:%2$02d", duration/60, duration%60));
        albumID = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
    }

    @Override
    public ImageView getArtView() {
        return imageAlbumArt;
    }

    @Override
    public int getAlbumID() {
        return albumID;
    }

    @Override
    public long getItemID() {
        return audioID;
    }

    @Override
    public LibraryPageInfo getDetailPageInfo() {
        return null;
    }
}
