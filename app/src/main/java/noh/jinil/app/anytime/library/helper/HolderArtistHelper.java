package noh.jinil.app.anytime.library.helper;

import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.album.ArtExtractor;
import noh.jinil.app.anytime.library.LibraryPageInfo;

public class HolderArtistHelper implements IHolderHelper {
    TextView textArtistName;
    TextView textArtistInfo;
    int artistID;

    public HolderArtistHelper(View itemView) {
        textArtistName = (TextView)itemView.findViewById(R.id.adapter_list_artist_artistName);
        textArtistInfo = (TextView)itemView.findViewById(R.id.adapter_list_artist_artistInfo);
    }

    @Override
    public void setInfo(Cursor cursor) {
        artistID = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists._ID));
        String trackName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST));
        textArtistName.setText(trackName);
        String trackInfo = String.format(Locale.KOREA, "%2$d/%2$d",
                cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS)),
                cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS)));
        textArtistInfo.setText(trackInfo);
    }

    @Override
    public ImageView getArtView() {
        return null;
    }

    @Override
    public int getAlbumID() {
        return -1;
    }

    @Override
    public long getItemID() {
        return artistID;
    }

    @Override
    public LibraryPageInfo getDetailPageInfo() {
        return new LibraryPageInfo(LibraryPageInfo.TYPE.TRACK, textArtistName.getText().toString(), MediaStore.Audio.Media.ARTIST_ID+"="+artistID);
    }
}
