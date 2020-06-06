package noh.jinil.app.anytime.main;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import noh.jinil.app.anytime.IMainActivity;
import noh.jinil.app.anytime.IMainFragment;
import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.service.IMediaPlaybackService;
import noh.jinil.app.anytime.utils.VLog;

public class MainPlaybackFragment extends Fragment implements IMainFragment {
    private static final String TAG = "MainPlaybackFragment";

    Button mToListBtn;
    IMediaPlaybackService iMediaService;
    IMainActivity iMainActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        VLog.i(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_playback, container, false);
        mToListBtn = (Button)view.findViewById(R.id.fragment_playback_menuButton_toList);
        mToListBtn.setOnClickListener(mMenuClickListener);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iMainActivity = (IMainActivity)getActivity();
    }

    View.OnClickListener mMenuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view == mToListBtn) {
                iMainActivity.requestToShowLibrary();
            }
        }
    };

    @Override
    public void onServiceConnected(IMediaPlaybackService iService) {
        iMediaService = iService;
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

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public boolean hide(OnHideListener listener) {
        return false;
    }
}
