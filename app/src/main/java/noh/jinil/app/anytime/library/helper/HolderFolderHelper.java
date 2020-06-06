package noh.jinil.app.anytime.library.helper;

import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.custom.CustomCursorLoader;
import noh.jinil.app.anytime.library.LibraryPageInfo;
import noh.jinil.app.anytime.utils.FileUtils;
import noh.jinil.app.anytime.utils.VLog;

public class HolderFolderHelper implements IHolderHelper {
    TextView textFolderName;
    TextView textFolderPath;
    TextView textItemCount;

    public HolderFolderHelper(View itemView) {
        textFolderName = (TextView)itemView.findViewById(R.id.adapter_list_folder_folderName);
        textFolderPath = (TextView)itemView.findViewById(R.id.adapter_list_folder_folderPath);
        textItemCount = (TextView)itemView.findViewById(R.id.adapter_list_folder_itemCount);
    }

    @Override
    public void setInfo(Cursor cursor) {
        String folderPath = cursor.getString(cursor.getColumnIndex(CustomCursorLoader.FOLDER_COLUMNS_PATH));
        int itemCount = cursor.getInt(cursor.getColumnIndex(CustomCursorLoader.FOLDER_COLUMNS_COUNT));
        textFolderPath.setText(folderPath);
        textFolderName.setText(FileUtils.getFolderName(folderPath));
        textItemCount.setText(String.valueOf(itemCount));
    }

    @Override
    public ImageView getArtView() {
        return null;
    }

    @Override
    public int getAlbumID() {
        return 0;
    }

    @Override
    public long getItemID() {
        return 0;
    }

    @Override
    public LibraryPageInfo getDetailPageInfo() {
        String selection = MediaStore.Audio.Media.DATA+" like '%"+textFolderPath.getText().toString()+"%'";
        VLog.e("Anytime", "selection:"+selection);
        return new LibraryPageInfo(LibraryPageInfo.TYPE.TRACK, textFolderName.getText().toString(), selection);
    }
}
