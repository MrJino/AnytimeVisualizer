package noh.jinil.app.anytime.help;

import noh.jinil.app.anytime.IMainFragment;
import noh.jinil.app.anytime.MainPageHelper;
import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.main.IServiceFragment;
import noh.jinil.app.anytime.service.IMediaPlaybackService;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class HelpFragment extends Fragment implements IServiceFragment, IMainFragment {
	
	private ViewGroup mContainer;
	IMediaPlaybackService mService;
	
	TextView mVersion;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContainer = container;
		View v = inflater.inflate(R.layout.help_main, container, false);
		
		mVersion =  (TextView)v.findViewById(R.id.help_version_info);
		try {
			PackageInfo i = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
			mVersion.setText(i.versionName);
		}
		catch(NameNotFoundException e) { }		
		
		refreshAllView(true);
		return v;
	}
	
	private void refreshAllView(boolean bAnimation) {
    	if(getActivity() == null || mService == null) 
    		return;
    	
        if(bAnimation) {
        	showAnimation();
        }
    }
	
	@Override
	public void hideAnimation() {
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_out);
        ani.setInterpolator(new DecelerateInterpolator(5.0f));
        mContainer.startAnimation(ani);
	}

	@Override
	public void showAnimation() {
		Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in);
        ani.setInterpolator(new DecelerateInterpolator(5.0f));
        mContainer.startAnimation(ani);
	}

	@Override
	public void setService(IMediaPlaybackService service) {
		mService = service;
		if(service != null) {
			refreshAllView(false);
		}
	}

	@Override
	public boolean onBackPressed() {
		return false;		
	}

	@Override
	public boolean show() {
		return true;
	}

	@Override
	public boolean hide(OnHideListener listener) {
		if(listener != null) {
			listener.onAnimationFinished(MainPageHelper.TYPE.INFORMATION);
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
