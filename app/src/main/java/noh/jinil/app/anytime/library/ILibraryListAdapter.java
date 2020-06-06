package noh.jinil.app.anytime.library;

import android.database.Cursor;

public interface ILibraryListAdapter {
    void setData(Cursor cursor);

    interface OnItemListener {
        void onItemPlay(long[]ids);
        void onItemDetail(LibraryPageInfo pageInfo);
        void onItemLongClicked(int position);
        void onItemSelected(int position, int selCount, int totCount);
        void onItemSwapped(int fromPosition, int toPosition);
    }
}