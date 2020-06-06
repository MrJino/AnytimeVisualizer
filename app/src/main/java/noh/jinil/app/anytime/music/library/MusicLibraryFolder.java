package noh.jinil.app.anytime.music.library;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.music.item.FolderItem;
import noh.jinil.app.anytime.utils.ArrayUtils;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

public class MusicLibraryFolder extends DialogFragment {
	ViewGroup mContainer;
	Cursor mCursor;
	ListView mListView;
	RelativeLayout mLayoutFolderBG;
	RelativeLayout mLayoutMotion;
	
	Hashtable<Integer, String> mCheckTable = new Hashtable<Integer, String>();
	
	static ArrayList<FolderItem> mFolderArray = null;
	
	MusicLibraryListener mListener = null;
	
	public interface MusicLibraryListener {
		public abstract void onRequestEnQueue(long[] list);
	}
	
	public void setOnLibraryListener(MusicLibraryListener listener) {
		mListener = listener;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContainer = container;
		View view = inflater.inflate(R.layout.music_folder, container, false);		
		mListView = (ListView)view.findViewById(R.id.music_library_listview);	
		
		mLayoutMotion = (RelativeLayout)view.findViewById(R.id.music_motion_addtonowplay);	
		
		if(mFolderArray != null) {
			mListView.setAdapter(new FolderAdapter(getActivity(), R.layout.music_folder_item, mFolderArray));
		}
		else {
//			getLoaderManager().initLoader(0, null, new LoaderCallbacks<Cursor>() {
//				@Override
//				public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//					return new CursorLoader(getActivity(), MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.IS_MUSIC+"=1", null, null);
//				}
//
//				@Override
//				public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//					mCursor = cursor;
//					new Thread(new Runnable() {
//						@Override
//						public void run() {
//							Hashtable<String, FolderItem> table = new Hashtable<String, FolderItem>();
//							while(mCursor.moveToNext()) {
//								FolderItem item = new FolderItem(mCursor);
//								table.put(item.getBucketId(), item);
//							}
//							mFolderArray = new ArrayList<FolderItem>(table.values());
//							mHandler.post(mSetAdapter);
//						}
//					}).start();
//				}
//
//				@Override
//				public void onLoaderReset(Loader<Cursor> loader) {
//				}
//			});
		}
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mLayoutFolderBG = (RelativeLayout)view.findViewById(R.id.music_folder_bg);
				if(mCheckTable.get(position) == null) {
					mLayoutFolderBG.setVisibility(View.VISIBLE);
					Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_0_to_1);
					ani.setInterpolator(new DecelerateInterpolator(5.0f));
					mLayoutFolderBG.startAnimation(ani);
					
					mCheckTable.put(position, mFolderArray.get(position).getPath());
				}
				else {
					mLayoutFolderBG.setVisibility(View.INVISIBLE);
					Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_1_to_0);
					ani.setInterpolator(new DecelerateInterpolator(5.0f));
					mLayoutFolderBG.startAnimation(ani);
					
					mCheckTable.remove(position);
				}
			}
		});
	
		showAnimation();
		return view;
	}
	
	@Override
	public void onResume() {		
		super.onResume();
	}
	
	Runnable mSetAdapter = new Runnable() {
		@Override
		public void run() {
			if(mListView != null) {
				mListView.setAdapter(new FolderAdapter(getActivity(), R.layout.music_folder_item, mFolderArray));
			}
		}
	};
	
	private static Handler mHandler = new Handler();
	
	class FolderAdapter extends ArrayAdapter<FolderItem> {		
		public FolderAdapter(Context context, int resource, ArrayList<FolderItem> objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;			
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.music_folder_item, null);
			}			
			mLayoutFolderBG = (RelativeLayout)v.findViewById(R.id.music_folder_bg);
			if(mCheckTable.get(position) != null) {
				mLayoutFolderBG.setVisibility(View.VISIBLE);
			} else {
				mLayoutFolderBG.setVisibility(View.INVISIBLE);
			}
			
			TextView display = (TextView)v.findViewById(R.id.music_folder_display);
			if(display != null) {
				display.setText(getItem(position).getDisplay());
			}
			
			TextView path = (TextView)v.findViewById(R.id.music_folder_path);
			if(path != null) {
				path.setText(getItem(position).getPath());
			}			
			return v;
		}
	}
	
	private void add() {
		ContentResolver res = getActivity().getContentResolver();
        Cursor c = null;
		Enumeration<String> values = mCheckTable.elements();
		ArrayList<Long> idArray = new ArrayList<Long>();
		long[] ids;
		
		while(values.hasMoreElements()) {
			String value = values.nextElement();
	        try {
	            c = res.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.DATA + " like '%"+value+"%'", null, null);
	            if (c == null || c.getCount() == 0) {
	                continue;
	            }
	            while(c.moveToNext()) {
	            	long audioid = c.getLong(c.getColumnIndex(MediaStore.Audio.Media._ID));
	            	idArray.add(audioid);
	            }
	        } 
	        catch (RuntimeException ex) {
	        } 
	        finally {
	            if (c != null) {
	                c.close();
	            }
	        }
		}
		if(idArray.size() > 0) {
			ids = new long[idArray.size()];
			ArrayUtils.parseArrayToLongs(idArray, ids);
			mListener.onRequestEnQueue(ids);
		}
	}
	
	public void addToNowplay() {
		if(mCheckTable.size() == 0) {
			Toast.makeText(getActivity(), R.string.nosong_to_add, Toast.LENGTH_SHORT).show();
		} 
		else {
			Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_down_in);
			ani.setInterpolator(new DecelerateInterpolator(5.0f));
			ani.setStartOffset(0);
			ani.setDuration(1000);
			ani.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {}			
				@Override
				public void onAnimationRepeat(Animation animation) {}			
				@Override
				public void onAnimationEnd(Animation animation) {
					add();
					
					mCheckTable.clear();
					ArrayAdapter<FolderItem> arrayAdapter = (ArrayAdapter<FolderItem>)mListView.getAdapter();
					arrayAdapter.notifyDataSetChanged();
					
					Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_out);
					ani.setInterpolator(new DecelerateInterpolator(3.0f));
					ani.setStartOffset(100);
					ani.setDuration(500);
					mLayoutMotion.setVisibility(View.INVISIBLE);
					mLayoutMotion.startAnimation(ani);
				}
			});
			mLayoutMotion.startAnimation(ani);
			mLayoutMotion.setVisibility(View.VISIBLE);
		}
	}
	
	public void showAnimation() {
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_down_in);
		ani.setInterpolator(new DecelerateInterpolator(4.0f));
		ani.setStartOffset(100);
		ani.setDuration(1000);
		ani.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				
			}			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
//				if(mFolderArray != null) 
//					return;
				
				getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
					@Override
					public Loader<Cursor> onCreateLoader(int id, Bundle args) {
						return new CursorLoader(getActivity(), MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.IS_MUSIC+"=1", null, null);
					}

					@Override
					public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
						mCursor = cursor;
						new Thread(new Runnable() {
							@Override
							public void run() {
								Hashtable<String, FolderItem> table = new Hashtable<String, FolderItem>();
								while(mCursor.moveToNext()) {
									FolderItem item = new FolderItem(mCursor);
									table.put(item.getBucketId(), item);
								}
								mFolderArray = new ArrayList<FolderItem>(table.values());
								mHandler.post(mSetAdapter);
							}
						}).start();
					}

					@Override
					public void onLoaderReset(Loader<Cursor> loader) {
					}
				});
			}
		});
		mContainer.startAnimation(ani);
	}
	
	public void hideAnimation() {
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_out);
		ani.setStartOffset(0);
		ani.setDuration(1000);
		ani.setInterpolator(new DecelerateInterpolator(4.0f));
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
