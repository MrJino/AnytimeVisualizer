package noh.jinil.app.anytime.library.helper;

import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;

import noh.jinil.app.anytime.library.LibraryPageInfo;

public interface IHolderHelper {
    void setInfo(Cursor cursor);
    ImageView getArtView();
    int getAlbumID();
    long getItemID();
    LibraryPageInfo getDetailPageInfo();
}
