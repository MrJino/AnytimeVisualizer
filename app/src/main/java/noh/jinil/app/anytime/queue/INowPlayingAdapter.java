package noh.jinil.app.anytime.queue;

import java.util.ArrayList;

import noh.jinil.app.anytime.music.item.TrackItem;

public interface INowPlayingAdapter {
    void setData(ArrayList<TrackItem> data);
    void setData(long[] queueList);
    void setShuffle(int[] data);
    void setActivePosition(int position);
    void setPlayStatus(boolean isPlaying);
    void setEditMode(boolean set);
    void setSelectAll(boolean set);
    void setPlaytime(int playtime);
    void setDuration(int duration);
    boolean[] getSelectionList();
    int getSelectionCount();
    TrackItem getTrackInfo(int position);
}
