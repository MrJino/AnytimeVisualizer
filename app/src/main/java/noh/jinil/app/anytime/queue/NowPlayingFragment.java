package noh.jinil.app.anytime.queue;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import noh.jinil.app.anytime.IMainActivity;
import noh.jinil.app.anytime.IMainFragment;
import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.helper.OnStartDragListener;
import noh.jinil.app.anytime.helper.SimpleItemTouchHelperCallback;
import noh.jinil.app.anytime.service.IMediaPlaybackService;
import noh.jinil.app.anytime.utils.VLog;

public class NowPlayingFragment extends Fragment implements IMainFragment, OnStartDragListener {
    private static final String TAG = "NowPlayingFragment";

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private TextView mNoContentText;
    private IMainActivity iActivity;
    private INowPlayingAdapter iAdapter;
    private IMediaPlaybackService iService;

    private ItemTouchHelper mItemTouchHelper;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        VLog.i(TAG, "onCreateView() ");
        View view = inflater.inflate(R.layout.fragment_list_recycler, container, false);
        mNoContentText = (TextView) view.findViewById(R.id.fragment_list_noContentText);
        mLayoutManager = new LinearLayoutManager(getActivity());

        // Recycler View
        mRecyclerView = (RecyclerView)view.findViewById(R.id.fragment_list_recyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager);

        onServiceConnected(iActivity.getServiceObject());
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        iActivity = (IMainActivity)getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onQueueListChanged(long[] queueList) {
        if(iAdapter != null) {
            iAdapter.setData(queueList);
        }
        if(mNoContentText != null) {
            if (queueList.length == 0)
                mNoContentText.setVisibility(View.VISIBLE);
            else
                mNoContentText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onQueueMetaChanged(int position) {
        if(iAdapter != null) {
            iAdapter.setActivePosition(position);
        }
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
        if (iAdapter != null) {
            iAdapter.setDuration(duration);
        }
    }

    @Override
    public void onPlayingStatusUpdated(boolean isPlaying) {
        if(iAdapter != null) {
            iAdapter.setPlayStatus(isPlaying);
        }
    }

    @Override
    public void onEditModeChanged(boolean set) {
        if(iAdapter != null) {
            iAdapter.setEditMode(set);
        }
    }


    @Override
    public void onSelectAllClicked(boolean set) {
        if(iAdapter != null) {
            iAdapter.setSelectAll(set);
        }
    }

    private void setNowPlayingAdapter() {
        if(iService == null)
            return;

        long[] queueList = null;
        int queuePos = 0;
        boolean isPlaying = false;
        try {
            queueList = iService.getQueue();
            queuePos = iService.getQueuePosition();
            isPlaying = iService.isPlaying();
        } catch(RemoteException e) {
            e.printStackTrace();
        }

        // 현재 재생곡 리스트
        NowPlayingAdapter adapter = new NowPlayingAdapter(getActivity(), queueList, mOnNowPlayingClinkListener, this);
        mRecyclerView.setAdapter(adapter);
        //mRecyclerView.getItemAnimator().setSupportsChangeAnimations(false);
        iAdapter = adapter;

        // 현재 재생곡 위치
        iAdapter.setActivePosition(queuePos);

        // 현재재생중인 곡으로 리스트 위치이동
        mLayoutManager.scrollToPositionWithOffset(queuePos-1, 0);

        // 현재 재생 상태
        iAdapter.setPlayStatus(isPlaying);

        // 재생곡이 없을 경우 No Content 표시
        if(mNoContentText != null) {
            if (queueList == null || queueList.length == 0)
                mNoContentText.setVisibility(View.VISIBLE);
            else
                mNoContentText.setVisibility(View.GONE);
        }

        // Drag & Drop
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    static double start_time = 0;
    NowPlayingAdapter.OnItemClickListener mOnNowPlayingClinkListener =  new NowPlayingAdapter.OnItemClickListener() {
        @Override
        public void onItemClicked(int position) {
            if(System.currentTimeMillis() - start_time < 500) {
                VLog.d(TAG, "click too fast!!");
                return;
            }

            try {
                if (iService.getQueuePosition() == position) {
                    //iActivity.requestStopTrack();
                } else {
                    iService.setQueuePosition(position);
                    //iActivity.requestPlayTrack(mQueueProcess.getQueueAt());
                }
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }

            start_time = System.currentTimeMillis();
        }

        @Override
        public void onItemLongClicked(int position) {
            //iActivity.requestEditMode(true);
        }

        @Override
        public void onItemSelected(int position, int selCount, int totCount) {
            //iActivity.updateSelectInfo(position, selCount, totCount);
        }

        @Override
        public void onItemSwapped(int fromPosition, int toPosition) {
            //mQueueProcess.swapQueueArray(fromPosition, toPosition);
            try {
                iService.moveQueueItem(fromPosition, toPosition);
            } catch(RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private ExpandableListView.OnGroupClickListener mRadioGroupClick = new ExpandableListView.OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPos, long id) {
            //GrLog.i(TAG, "onGroupClick() pos="+groupPos);
            return false;
        }
    };

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
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
        if(iService != this.iService) {
            this.iService = iService;
            setNowPlayingAdapter();
        }
    }
}
