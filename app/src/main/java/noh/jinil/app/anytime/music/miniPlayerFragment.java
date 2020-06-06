package noh.jinil.app.anytime.music;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import noh.jinil.app.anytime.IMainActivity;
import noh.jinil.app.anytime.IMainFragment;
import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.service.IMediaPlaybackService;

public class miniPlayerFragment extends Fragment implements IMainFragment {

    ImageButton mBtnNowPlaying;
    IMainActivity iActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_buttons_control, container, false);
        mBtnNowPlaying = (ImageButton)view.findViewById(R.id.music_button_nowPlaying);
        if(mBtnNowPlaying != null) {
            mBtnNowPlaying.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iActivity.requestToShowNowPlaying();
                }
            });
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        iActivity = (IMainActivity)getActivity();
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public boolean hide(OnHideListener listener) {
        return false;
    }

    @Override
    public void onServiceConnected(IMediaPlaybackService iService) {

    }

    @Override
    public void onQueueListChanged(long[] queueList) {

    }

    @Override
    public void onQueueMetaChanged(int position) {

    }

    @Override
    public void onQueueShuffleChanged() {

    }

    @Override
    public void onAlbumArtUpdated(Bitmap bmp) {

    }

    @Override
    public void onPlaytimeUpdated(int playtime) {

    }

    @Override
    public void onDurationUpdated(int duration) {

    }

    @Override
    public void onPlayingStatusUpdated(boolean isPlaying) {

    }

    @Override
    public void onEditModeChanged(boolean set) {

    }

    @Override
    public void onSelectAllClicked(boolean set) {

    }
}
