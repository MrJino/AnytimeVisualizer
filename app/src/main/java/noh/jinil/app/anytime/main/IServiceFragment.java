package noh.jinil.app.anytime.main;

import noh.jinil.app.anytime.service.IMediaPlaybackService;

public interface IServiceFragment {
	public static final String ARG_VERY_FIRST = "veryfirst";
	public static final String ARG_POSITION = "position";
	
	public void setService(IMediaPlaybackService service);
	public void showAnimation();
	public void hideAnimation();
	public boolean onBackPressed();
}
