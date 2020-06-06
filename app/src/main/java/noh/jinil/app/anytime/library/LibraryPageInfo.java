package noh.jinil.app.anytime.library;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.library.helper.*;

public class LibraryPageInfo implements Parcelable {
    public static final String BUNDLE_KEY = "LibraryPageInfo";

    public enum TYPE {
        TRACK,
        ALBUM,
        ARTIST,
        FOLDER;

        public static TYPE fromOrdinal(int ordinal) {
            for(TYPE type : values()) {
                if(type.ordinal() == ordinal)
                    return type;
            }
            return TRACK;
        }
    }

    private TYPE type;
    private String title;
    private String DBCondition;

    public LibraryPageInfo(TYPE type) {
        this.type = type;
    }

    public LibraryPageInfo(TYPE type, String title, String DBCondition) {
        this.type = type;
        this.title = title;
        this.DBCondition = DBCondition;
    }

    protected LibraryPageInfo(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type.ordinal());
    }

    private void readFromParcel(Parcel src){
        this.type = TYPE.fromOrdinal(src.readInt());
    }

    public static final Creator<LibraryPageInfo> CREATOR = new Creator<LibraryPageInfo>() {
        @Override
        public LibraryPageInfo createFromParcel(Parcel in) {
            return new LibraryPageInfo(in);
        }

        @Override
        public LibraryPageInfo[] newArray(int size) {
            return new LibraryPageInfo[size];
        }
    };

    public TYPE getType() {
        return type;
    }

    public String getPageTitle(Context context) {
        switch (type) {
            case TRACK:
                return context.getString(R.string.music_library_tab_title_track);
            case ALBUM:
                return context.getString(R.string.music_library_tab_title_album);
            case ARTIST:
                return context.getString(R.string.music_library_tab_title_artist);
            case FOLDER:
                return context.getString(R.string.music_library_tab_title_folder);
        }
        return "";
    }

    public String getTitle() {
        return title;
    }

    public String getDBCondition() {
        return DBCondition;
    }

    public int getBackgroundColor() {
        switch (type) {
            case TRACK:
                return Color.TRANSPARENT;
            case ALBUM:
                return Color.TRANSPARENT;
            case ARTIST:
                return Color.TRANSPARENT;
            case FOLDER:
                return Color.TRANSPARENT;
        }
        return Color.BLUE;
    }

    public int getLayoutID() {
        switch (type) {
            case TRACK:
                return R.layout.adapter_list_track;
            case ALBUM:
                return R.layout.adapter_list_album;
            case ARTIST:
                return R.layout.adapter_list_artist;
            case FOLDER:
                return R.layout.adapter_list_folder;
        }
        return 0;
    }

    public RecyclerView.LayoutManager createLayoutManager(Context context) {
        switch (type) {
            case TRACK:
            case ARTIST:
            case FOLDER:
                return new LinearLayoutManager(context);
            case ALBUM:
                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                int columnCount = (int)(metrics.widthPixels / (120*metrics.density));
                return new GridLayoutManager(context, columnCount);
        }
        return null;
    }

    public IHolderHelper createHolderHelper(View view) {
        switch (type) {
            case TRACK:
                return new HolderTrackHelper(view);
            case ALBUM:
                return new HolderAlbumHelper(view);
            case ARTIST:
                return new HolderArtistHelper(view);
            case FOLDER:
                return new HolderFolderHelper(view);
        }
        return null;
    }
}
