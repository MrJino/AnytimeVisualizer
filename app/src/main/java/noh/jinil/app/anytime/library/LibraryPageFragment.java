package noh.jinil.app.anytime.library;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import noh.jinil.app.anytime.IMainActivity;
import noh.jinil.app.anytime.IMainFragment;
import noh.jinil.app.anytime.MainPageHelper;
import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.custom.CustomCursorLoader;
import noh.jinil.app.anytime.service.IMediaPlaybackService;
import noh.jinil.app.anytime.utils.VLog;

public class LibraryPageFragment extends Fragment implements IMainFragment, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "LibraryPageFragment";

    LibraryPageInfo pageInfo;
    RecyclerView mRecyclerView;
    Toolbar mToolbar;
    RecyclerView.LayoutManager mLayoutManager;
    ILibraryListAdapter iAdapter;
    IMainActivity iActivity;
    boolean bShown = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        VLog.i(TAG, "onCreate() "+ ((pageInfo != null) ? pageInfo.getType() : ""));
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        VLog.i(TAG, "onCreateView() "+pageInfo.getType());
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        view.setBackgroundColor(pageInfo.getBackgroundColor());

        mLayoutManager = pageInfo.createLayoutManager(getContext());
        LibraryListAdapter adapter = new LibraryListAdapter(pageInfo, mOnItemListener);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);

        mToolbar = (Toolbar)view.findViewById(R.id.toolBar);
        if(pageInfo.getTitle() != null) {
            mToolbar.setTitle(pageInfo.getTitle());
        } else {
            mToolbar.setVisibility(View.GONE);
        }

        iAdapter = adapter;
        getLoaderManager().initLoader(pageInfo.getType().ordinal(), null, this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        pageInfo = getArguments().getParcelable(LibraryPageInfo.BUNDLE_KEY);
        iActivity = (IMainActivity)getActivity();
        VLog.i(TAG, "onAttach() "+ ((pageInfo != null) ? pageInfo.getType() : ""));
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        VLog.i(TAG, "onDetach() "+ ((pageInfo != null) ? pageInfo.getType() : ""));
        super.onDetach();
        getLoaderManager().destroyLoader(pageInfo.getType().ordinal());
        iAdapter.setData(null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        VLog.i(TAG, "onActivityCreated() "+ ((pageInfo != null) ? pageInfo.getType() : ""));
        super.onActivityCreated(savedInstanceState);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        LibraryPageInfo.TYPE type = LibraryPageInfo.TYPE.fromOrdinal(id);
        VLog.i(TAG, "onCreateLoader() "+type);
        CustomCursorLoader customCursorLoader = new CustomCursorLoader(getContext());
        customCursorLoader.setPageInfo(pageInfo);
        return customCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        VLog.i(TAG, "onLoadFinished() "+pageInfo.getType()+" data"+data);
        iAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        VLog.i(TAG, "onLoaderReset() "+pageInfo.getType());
        iAdapter.setData(null);
    }

    ILibraryListAdapter.OnItemListener mOnItemListener = new ILibraryListAdapter.OnItemListener() {
        @Override
        public void onItemPlay(long[] ids) {
            VLog.d(TAG, "onItemPlay() count:"+ids.length);
            iActivity.requestToPlayItem(ids);
        }

        @Override
        public void onItemDetail(LibraryPageInfo pageInfo) {
            VLog.d(TAG, "onItemDetail "+pageInfo.getType());
            iActivity.requestToShowLibraryPage(pageInfo);
        }

        @Override
        public void onItemLongClicked(int position) {
        }

        @Override
        public void onItemSelected(int position, int selCount, int totCount) {
        }

        @Override
        public void onItemSwapped(int fromPosition, int toPosition) {
        }
    };

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
            listener.onAnimationFinished(MainPageHelper.TYPE.LIBRARY_PAGE);
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
