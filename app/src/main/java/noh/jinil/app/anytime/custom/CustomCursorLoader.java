package noh.jinil.app.anytime.custom;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import noh.jinil.app.anytime.library.LibraryPageInfo;
import noh.jinil.app.anytime.utils.FileUtils;

public class CustomCursorLoader extends CursorLoader{
    public static final String FOLDER_COLUMNS_PATH = "path";
    public static final String FOLDER_COLUMNS_COUNT = "item_count";

    LibraryPageInfo mPageInfo = null;

    public CustomCursorLoader(Context context) {
        super(context);
    }

    public void setPageInfo(LibraryPageInfo pageInfo) {
        mPageInfo = pageInfo;
        switch(mPageInfo.getType()) {
            case TRACK:
                setUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                setSelection(pageInfo.getDBCondition());
                break;
            case ALBUM:
                setUri(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI);
                break;
            case ARTIST:
                setUri(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI);
                break;
            case FOLDER:
                setUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                setSortOrder(MediaStore.Audio.Media.DATA+" asc");
                break;
        }

    }

    @Override
    protected Cursor onLoadInBackground() {
        Cursor cursor = super.onLoadInBackground();
        if(mPageInfo == null)
            return cursor;

        switch(mPageInfo.getType()) {
            case FOLDER:
                String columnNames[] = new String[] { FOLDER_COLUMNS_PATH, FOLDER_COLUMNS_COUNT };
                MatrixCursor matrixCursor = new MatrixCursor(columnNames);
                String oldPath = null;
                int count = 0;
                while(cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    path = FileUtils.getFolderPath(path);
                    count++;
                    if(oldPath == null || !oldPath.equals(path)) {
                        matrixCursor.addRow(new Object[] {path, count});
                        count = 0;
                    }
                    oldPath = path;
                }
                cursor.close();
                cursor = matrixCursor;
                break;
        }
        return cursor;
    }
}
