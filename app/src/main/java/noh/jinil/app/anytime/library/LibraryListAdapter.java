package noh.jinil.app.anytime.library;

import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import noh.jinil.app.anytime.album.ArtExtractor;
import noh.jinil.app.anytime.library.helper.IHolderHelper;
import noh.jinil.app.anytime.utils.VLog;

public class LibraryListAdapter extends RecyclerView.Adapter<LibraryListAdapter.ViewHolder> implements ILibraryListAdapter {
    private static final String TAG = "LibraryListAdapter";

    Cursor mCursor;
    LibraryPageInfo mPageInfo;
    ArtExtractor mArtExtractor;
    OnItemListener mOnItemListener;

    public LibraryListAdapter(LibraryPageInfo pageInfo, OnItemListener onItemListener) {
        mPageInfo = pageInfo;
        mOnItemListener = onItemListener;
    }

    @Override
    public LibraryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        VLog.v(TAG, "onCreateViewHolder");
        if(mPageInfo == null)
            return null;
        mArtExtractor = ArtExtractor.getInstance(parent.getContext());
        View itemView = LayoutInflater.from(parent.getContext()).inflate(mPageInfo.getLayoutID(), parent, false);
        return new ViewHolder(itemView, mPageInfo.createHolderHelper(itemView));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(mCursor == null || mCursor.isClosed())
            return;
        mCursor.moveToPosition(position);
        holder.setInfo(mCursor);
    }

    @Override
    public int getItemCount() {
        if(mCursor == null || mCursor.isClosed())
            return 0;
        return mCursor.getCount();
    }

    @Override
    public void setData(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public long[] getItemIDs() {
        if(mCursor == null || mCursor.getCount() <= 0)
            return null;
        long[] itemIDs = new long[mCursor.getCount()];
        mCursor.moveToFirst();
        for(int i = 0; i< itemIDs.length;i++) {
            itemIDs[i] = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media._ID));
        }
        return itemIDs;
    }

    public long[] getItemID(int position) {
        mCursor.moveToPosition(position);
        long[] itemIDs = new long[1];
        itemIDs[0] = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media._ID));
        return itemIDs;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        IHolderHelper helper;
        ImageView artView;
        public ViewHolder(View itemView, final IHolderHelper iHolderHelper) {
            super(itemView);
            helper = iHolderHelper;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VLog.i(TAG, "ViewHolder onClick()");
                    if(mOnItemListener == null)
                        return;
                    LibraryPageInfo pageInfo = iHolderHelper.getDetailPageInfo();
                    if(pageInfo != null) {
                        mOnItemListener.onItemDetail(pageInfo);
                    } else {
                        mOnItemListener.onItemPlay(getItemID(getAdapterPosition()));
                    }
                }
            });
        }

        public void setInfo(Cursor cursor) {
            helper.setInfo(cursor);
            artView = helper.getArtView();
            if(artView != null) {
                ArtExtractor.RequestInfo reqInfo = new ArtExtractor.RequestInfo();
                reqInfo.inView = artView;
                reqInfo.inIndex = getAdapterPosition();
                reqInfo.inArtUrl = String.valueOf(helper.getAlbumID());
                boolean fromCache = mArtExtractor.requestAlbumArt(reqInfo, new ArtExtractor.OnExtractListener() {
                    @Override
                    public void onDownloadCompleted(ArtExtractor.RequestInfo info, boolean fromServer) {
                        if(info.inIndex == getAdapterPosition() && info.inView == artView) {
                            if(fromServer) {
                                Animation aniArt = new AlphaAnimation(0f, 1.0f);
                                aniArt.setDuration(600);
                                artView.startAnimation(aniArt);
                            }
                            artView.setImageBitmap(info.getBitmap());
                        }
                    }
                });
                if(!fromCache) {
                    artView.setImageBitmap(null);
                }
            }
        }
    }
}
