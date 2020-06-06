package noh.jinil.app.anytime.music;

import java.util.ArrayList;
import java.util.Hashtable;

import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.music.item.TrackItem;
import noh.jinil.app.anytime.utils.ArrayUtils;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MusicNowplaying extends DialogFragment {
	private static final String TAG = "Nowplaying";
	
	ListView mListView;
	ViewGroup mContainer;
	
	private MusicNowplayingListener mListener;
	
	public interface MusicNowplayingListener {
		public abstract long[] onRequestQueue();
		public abstract int onRequestPosition();
		public abstract void onPositionChanged(int pos);
		public abstract void onAudioIdRemoved(long id);
	}	
	
	public void setOnNowplayingListener(MusicNowplayingListener listener) {
		mListener = listener;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onResume() {		
		super.onResume();
	}
	
	@Override
	public void onStart() {		
		super.onStart();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContainer = container;
		View view = inflater.inflate(R.layout.music_nowplaying_list, container, false);
		
		// background, and so on...
		refreshView(view);
		
		ArrayList<Long> list = new ArrayList<Long>();
		ArrayUtils.parseLongsToArray(list, mListener.onRequestQueue());
		ArrayAdapter<Long> adapter = new NowplayingAdapter(getActivity(), R.layout.music_nowplaying_item, list);
		
		mListView = (ListView)view.findViewById(R.id.nowplaying_listview);
		mListView.setAdapter(adapter);
//		listview.setDivider(getActivity().getResources().getDrawable(R.drawable.listview_divider));
//		listview.setDividerHeight(1);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mListener.onPositionChanged(position);;
			}
		});
		
		mListView.setSelection(mListener.onRequestPosition()-1);
		
		TextView title = (TextView)view.findViewById(R.id.nowplaying_title);
		title.setText(getActivity().getResources().getString(R.string.music_nowplaying_title)+" ["+list.size()+"]");

		showAnimation();
		return view;
	}
	
	class NowplayingAdapter extends ArrayAdapter<Long> {
		private Hashtable<Integer, TrackItem> items;
		
		public NowplayingAdapter(Context context, int resource, ArrayList<Long> objects) {
			super(context, resource, objects);
			items = new Hashtable<Integer, TrackItem>();
		}
		
		@Override
		public void remove(Long object) {
			super.remove(object);
			items.clear();
			mListener.onAudioIdRemoved((long)object);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;			
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.music_nowplaying_item, null);
			}
			
			TrackItem item = items.get(position);
			if(item == null) {
				item = TrackItem.newItem(getContext(), this.getItem(position));// audioids.get(position));
				if(item == null) {
					return null;
				}
				items.put(position, item);
			}
			
			TextView title = (TextView)v.findViewById(R.id.nowplaying_track_title);
			title.setText((position+1)+". "+item.getTitle());
			
			if(mListener.onRequestPosition() == position) {
				title.setTextColor(getResources().getColor(R.color.nowplaying_title_playing));
			} else {
				title.setTextColor(getResources().getColor(R.color.nowplaying_title_normal));
			}
			
			TextView artist = (TextView)v.findViewById(R.id.nowplaying_track_artist);
			artist.setText(item.getArtist());
			return v;
		}
	}
	
	public void refreshView(View view) {
		if(view == null) {
			view = getView();
		}
		
//		LinearLayout bglayout = (LinearLayout)view.findViewById(R.id.nowplaying_background);
//		bglayout.setBackground(new BitmapDrawable(getResources(), mMyActivity.mColorAlbumart));
//		if(mListView != null) {
//			mListView.setBackground(new BitmapDrawable(getResources(), mMyActivity.mColorAlbumart));
//		}
		
		if(mListView != null) {
			mListView.invalidateViews();
//			((ArrayAdapter<Long>)mListView.getAdapter()).notifyDataSetChanged();			
		}
	}
	
	public void showAnimation() {
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_down_in);
		ani.setDuration(500);
		ani.setInterpolator(new DecelerateInterpolator(2.0f));
		ani.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
		mContainer.startAnimation(ani);
	}
	
	public void hideAnimation() {
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_out);
		ani.setDuration(500);
		ani.setInterpolator(new DecelerateInterpolator(2.0f));
		ani.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				dismiss();
			}
		});
		mContainer.startAnimation(ani);
	}
}
