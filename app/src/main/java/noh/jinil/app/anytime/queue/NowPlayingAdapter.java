package noh.jinil.app.anytime.queue;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.album.ArtExtractor;
import noh.jinil.app.anytime.helper.ItemTouchHelperAdapter;
import noh.jinil.app.anytime.helper.ItemTouchHelperViewHolder;
import noh.jinil.app.anytime.helper.OnStartDragListener;
import noh.jinil.app.anytime.music.item.TrackItem;
import noh.jinil.app.anytime.utils.VLog;

public class NowPlayingAdapter extends RecyclerView.Adapter<NowPlayingAdapter.ViewHolder> implements INowPlayingAdapter, ItemTouchHelperAdapter {
    private static final String TAG = "NowPlayingAdapter";

    int mActivePosition = -1;
    int mActivePlaytime = 0;
    int mActiveDuration = 0;
    boolean mEditMode = false;
    boolean mSwitchMode = false;
    boolean mIsPlaying = false;

    Context mContext;
    ArrayList<TrackItem> mItemArray;
    long[] mQueueList;
    OnItemClickListener mOnListener;
    ArtExtractor mArtExtractor;

    boolean[] selectionList = null;
    int selectCount = 0;

    private final OnStartDragListener mDragStartListener;

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //VLog.d(TAG, "onItemMove() "+fromPosition+", "+toPosition);
        notifyItemMoved(fromPosition, toPosition);
        // swap selection list
        boolean temp = selectionList[fromPosition];
        selectionList[fromPosition] = selectionList[toPosition];
        selectionList[toPosition] = temp;
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        VLog.d(TAG, "onItemDismiss() "+position);
    }

    @Override
    public void onItemSelected(int position) {
        mSwitchMode = true;
    }

    @Override
    public void onItemSwitched(int fromPosition, int toPosition) {
        VLog.d(TAG, "onItemSwitched() "+fromPosition+", "+toPosition);
        mSwitchMode = false;
        if(fromPosition < 0 || toPosition < 0)
            return;
        if(mOnListener != null) {
            mOnListener.onItemSwapped(fromPosition, toPosition);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(int position);
        void onItemLongClicked(int position);
        void onItemSelected(int position, int selCount, int totCount);
        void onItemSwapped(int fromPosition, int toPosition);
    }

    public NowPlayingAdapter(Context context, long queueList[], OnItemClickListener listener, OnStartDragListener dragStartListener) {
        mContext = context;
        mOnListener = listener;
        mArtExtractor = ArtExtractor.getInstance(context);
        mDragStartListener = dragStartListener;
        setData(queueList);
    }

    @Override
    public NowPlayingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_now_playing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //VLog.v(TAG, "onBindViewHolder() "+position);
        TrackItem item = null;
        if(position < mItemArray.size()) {
            item = mItemArray.get(position);
        }
        if(item == null) {
            item = TrackItem.newItem(mContext, mQueueList[position]);
            mItemArray.set(position, item);
        }
        holder.setInfo(item, position);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        if(mQueueList != null) {
            //VLog.d(TAG, "getItemCount():"+mItemArray.size());
            return mQueueList.length;
        }
        return 0;
    }

    @Override
    public void setData(ArrayList<TrackItem> data) {
        VLog.d(TAG, "setData() size:"+data.size());
        mItemArray = data;
        notifyDataSetChanged();
    }

    @Override
    public void setData(long[] queueList) {
        mQueueList = queueList;
        if(mItemArray != null) {
            mItemArray.clear();
        } else {
            mItemArray = new ArrayList<>();
        }
    }

    @Override
    public void setShuffle(int[] data) {
    }

    @Override
    public void setActivePosition(int position) {
        if(mSwitchMode)
            return;
        int oldPosition = mActivePosition;
        mActivePosition = position;
        notifyItemChanged(mActivePosition);
        notifyItemChanged(oldPosition);
    }

    @Override
    public void setPlayStatus(boolean isPlaying) {
        if(mSwitchMode)
            return;
        mIsPlaying = isPlaying;
        notifyItemChanged(mActivePosition);
    }

    @Override
    public void setEditMode(boolean set) {
        mEditMode = set;
        selectionList = new boolean[mItemArray.size()];
        selectCount = 0;
        notifyDataSetChanged();
    }

    @Override
    public void setSelectAll(boolean set) {
        if(!mEditMode)
            return;

        for(int i = 0; i < selectionList.length; i++) {
            selectionList[i] = set;
        }
        if(set)
            selectCount = selectionList.length;
        else
            selectCount = 0;

        if (mOnListener != null) {
            mOnListener.onItemSelected(-1, selectCount, selectionList.length);
        }
        notifyDataSetChanged();
    }

    @Override
    public void setPlaytime(int playtime) {
        if(mSwitchMode)
            return;

        if(mActiveDuration == 0)
            playtime = 0;
        else
            playtime = (playtime+500) / 1000;

        if(mActivePlaytime != playtime) {
            mActivePlaytime = playtime;
            notifyItemChanged(mActivePosition);
        }
    }

    @Override
    public void setDuration(int duration) {
        duration = (duration+500)/1000;
        mActiveDuration = duration;
        if(mActiveDuration == 0) {
            mActivePlaytime = 0;
            notifyItemChanged(mActivePosition);
        }
    }

    @Override
    public boolean[] getSelectionList() {
        return selectionList;
    }

    @Override
    public int getSelectionCount() {
        return selectCount;
    }

    @Override
    public TrackItem getTrackInfo(int position) {
        return mItemArray.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        TextView itemTitle;
        TextView itemPlaytime;
        TextView itemDuration;
        View mainLayout;
        View timeLayout;
        View artViewSel;
        ImageView effectView;
        ImageView effectStop;
        ImageView selectImg;
        ImageView artView;
        ImageButton rearrangeBtn;
        AnimationDrawable effectAni;
        ImageView localIcon;

        ArtExtractor.RequestInfo requestInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            itemTitle = (TextView)itemView.findViewById(R.id.adapter_nowPlaying_itemTitle);
            itemPlaytime = (TextView)itemView.findViewById(R.id.adapter_nowPlaying_itemPlaytime);
            itemDuration = (TextView)itemView.findViewById(R.id.adapter_nowPlaying_itemDuration);
            mainLayout = itemView.findViewById(R.id.adapter_nowPlaying_layout);
            timeLayout = itemView.findViewById(R.id.adapter_nowPlaying_timeLayout);
            effectView = (ImageView)itemView.findViewById(R.id.adapter_nowPlaying_effect);
            effectStop = (ImageView)itemView.findViewById(R.id.adapter_nowPlaying_effect_stop);
            effectAni = (AnimationDrawable)effectView.getDrawable();
            selectImg = (ImageView)itemView.findViewById(R.id.adapter_nowPlaying_select);
            artView = (ImageView)itemView.findViewById(R.id.adapter_nowPlaying_albumArt);
            rearrangeBtn = (ImageButton)itemView.findViewById(R.id.adapter_nowPlaying_rearrangeBtn);
            artViewSel = itemView.findViewById(R.id.adapter_nowPlaying_albumArtSel);
            localIcon = (ImageView)itemView.findViewById(R.id.nowPlaying_adapter_local_icon);

            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mEditMode) {
                        toggleSelect();
                    }
                    else {
                        if (mOnListener != null) {
                            mOnListener.onItemClicked(getAdapterPosition());
                        }
                    }
                }
            });

            mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(!mEditMode) {
                        if(mOnListener != null) {
                            mOnListener.onItemLongClicked(getAdapterPosition());
                        }
                        toggleSelect();
                    }
                    return true;
                }
            });

            rearrangeBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(ViewHolder.this);
                    }
                    return false;
                }
            });
        }

        void toggleSelect() {
            VLog.d(TAG, "toggleSelect() position:"+getAdapterPosition());
            if(getAdapterPosition() < 0)
                return;
            selectionList[getAdapterPosition()] = !selectionList[getAdapterPosition()];
            if(selectionList[getAdapterPosition()])
                selectCount++;
            else
                selectCount--;

            notifyItemChanged(getAdapterPosition());
            if (mOnListener != null) {
                mOnListener.onItemSelected(getAdapterPosition(), selectCount, selectionList.length);
            }
        }

        void setInfo(TrackItem info, int position) {
            itemTitle.setText(info.getTitle());

            if(mActivePosition == position) {
                if(mEditMode)
                    mainLayout.setSelected(false);
                else
                    mainLayout.setSelected(true);

                effectView.setVisibility(View.VISIBLE);
                artViewSel.setSelected(true);
                if(mActivePlaytime > 0 && mIsPlaying) {
                    effectAni.start();
                    effectStop.setVisibility(View.INVISIBLE);
                    effectView.setVisibility(View.VISIBLE);
                } else {
                    effectAni.stop();
                    effectAni.selectDrawable(0);
                    effectStop.setVisibility(View.VISIBLE);
                    effectView.setVisibility(View.INVISIBLE);
                }
                String strPlaytime = String.format(Locale.KOREA, "%02d:%02d / ", mActivePlaytime / 60, mActivePlaytime % 60);
                itemPlaytime.setText(strPlaytime);
                itemPlaytime.setVisibility(View.VISIBLE);
                String strDuration = String.format(Locale.KOREA, "%02d:%02d", mActiveDuration / 60, mActiveDuration % 60);
                itemDuration.setText(String.valueOf(strDuration));
            }
            else {
                selectImg.setVisibility(View.INVISIBLE);
                effectView.setVisibility(View.INVISIBLE);
                effectStop.setVisibility(View.INVISIBLE);
                effectAni.stop();
                mainLayout.setSelected(false);
                artViewSel.setSelected(false);
                itemPlaytime.setVisibility(View.GONE);
                String strDuration = String.format(Locale.KOREA, "%02d:%02d", info.getDuration() / 60, info.getDuration() % 60);
                itemDuration.setText(String.valueOf(strDuration));
            }
            if(mEditMode && position < selectionList.length && selectionList[position]) {
                mainLayout.setSelected(true);
                if(mActivePosition == position)
                    selectImg.setVisibility(View.INVISIBLE);
                else
                    selectImg.setVisibility(View.VISIBLE);
                //effectView.setVisibility(View.INVISIBLE);
                //effectStop.setVisibility(View.INVISIBLE);
            }
            else {
                selectImg.setVisibility(View.INVISIBLE);
            }

            if(mEditMode) {
                timeLayout.setVisibility(View.GONE);
                rearrangeBtn.setVisibility(View.VISIBLE);
            } else {
                timeLayout.setVisibility(View.VISIBLE);
                rearrangeBtn.setVisibility(View.GONE);
            }

            // 앨범아트
            requestInfo = new ArtExtractor.RequestInfo();
            requestInfo.inArtUrl = String.valueOf(info.getAlbumId());
            requestInfo.inView = artView;
            requestInfo.inIndex = getAdapterPosition();

            boolean fromCache = mArtExtractor.requestAlbumArt(requestInfo, new ArtExtractor.OnExtractListener() {
                @Override
                public void onDownloadCompleted(ArtExtractor.RequestInfo info, boolean fromServer) {
                    if(info.inView == artView && info.inIndex == getAdapterPosition()) {
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

        @Override
        public void onItemSelected() {
            VLog.d(TAG, "onItemSelected()");
        }

        @Override
        public void onItemClear() {
            VLog.d(TAG, "onItemClear()");
        }

        @Override
        public void onItemMoved(int index) {
            if(requestInfo != null) {
                requestInfo.inIndex = index;
            }
        }
    }
}
