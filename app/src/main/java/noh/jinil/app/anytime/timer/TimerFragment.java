package noh.jinil.app.anytime.timer;

import noh.jinil.app.anytime.R;
import noh.jinil.app.anytime.main.IServiceFragment;
import noh.jinil.app.anytime.service.IMediaPlaybackService;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

public class TimerFragment extends Fragment implements IServiceFragment {
	
	private ViewGroup mContainer;
	IMediaPlaybackService mService;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContainer = container;
		View v = inflater.inflate(R.layout.timer_main, container, false);
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
}
