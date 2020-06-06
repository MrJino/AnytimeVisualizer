package noh.jinil.app.anytime.library.helper;

import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.library.LibraryPageInfo;

public class HolderAlbumHelper implements IHolderHelper {
    TextView textAlbumName;
    TextView textArtistName;
    ImageView imageAlbumArt;
    int albumID;

    public HolderAlbumHelper(View itemView) {
        textAlbumName = (TextView)itemView.findViewById(R.id.adapter_list_album_albumName);
        textArtistName = (TextView)itemView.findViewById(R.id.adapter_list_album_artistName);
        imageAlbumArt = (ImageView)itemView.findViewById(R.id.adapter_list_album_albumArt);
    }

    @Override
    public void setInfo(Cursor cursor) {
        String albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
        String artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
        textAlbumName.setText(albumName);
        textArtistName.setText(artistName);
        albumID = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
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
        return albumID;
    }

    @Override
    public LibraryPageInfo getDetailPageInfo() {
        return new LibraryPageInfo(LibraryPageInfo.TYPE.TRACK, textAlbumName.getText().toString(), MediaStore.Audio.Media.ALBUM_ID+"="+albumID);
    }
}
