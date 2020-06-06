package noh.jinil.app.anytime.main;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import noh.jinil.app.anytime.IMainFragment;
import noh.jinil.app.anytime.MainPageHelper;
import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.library.LibraryPageAdapter;
import noh.jinil.app.anytime.service.IMediaPlaybackService;
import noh.jinil.app.anytime.utils.VLog;

public class MainLibraryFragment extends Fragment implements IMainFragment {
    private static final String TAG = "MainLibraryFragment";

    boolean bShown = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        VLog.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        VLog.i(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        LibraryPageAdapter pageAdapter = new LibraryPageAdapter(getActivity().getSupportFragmentManager(), getActivity());
        ViewPager viewPager = (ViewPager)view.findViewById(R.id.fragment_library_viewPager);
        viewPager.setAdapter(pageAdapter);

        TabLayout tabLayout = (TabLayout)view.findViewById(R.id.fragment_library_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public boolean show() {
        if(bShown)
            return false;
        bShown = true;
        if(getView() != null) {
            getView().setVisibility(View.VISIBLE);
        }
        return true;
    }

    @Override
    public boolean hide(OnHideListener listener) {
        if(!bShown)
            return false;
        bShown = false;
        if(getView() != null) {
            getView().setVisibility(View.GONE);
        }
        if(listener != null) {
            listener.onAnimationFinished(MainPageHelper.TYPE.LIBRARY);
        }
        return true;
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
