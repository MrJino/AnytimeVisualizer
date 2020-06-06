package noh.jinil.app.anytime;

import android.graphics.Bitmap;

import noh.jinil.app.anytime.service.IMediaPlaybackService;

public interface IMainFragment {
    boolean show();
    boolean hide(OnHideListener listener);

    void onServiceConnected(IMediaPlaybackService iService);
    void onQueueListChanged(long[] queueList);
    void onQueueMetaChanged(int position);
    void onQueueShuffleChanged();
    void onAlbumArtUpdated(Bitmap bmp);
    void onPlaytimeUpdated(int playtime);
    void onDurationUpdated(int duration);
    void onPlayingStatusUpdated(boolean isPlaying);
    void onEditModeChanged(boolean set);
    void onSelectAllClicked(boolean set);

    interface OnHideListener {
        void onAnimationFinished(MainPageHelper.TYPE type);
    }
}