package noh.jinil.app.anytime.visualizer;

import android.graphics.Canvas;

public interface ITypeView {
	public static final int FULL_VIEW = 0;
	public static final int MINI_VIEW = 1;
	
	public void update(byte[] bytes);
	public void draw(Canvas canvas);
	public float plusRatio();
	public void onSizeChanged(int width, int height, int density);
	public void refreshChanged();
	public void setBarSize(int value);
	public void setAlpha(int value);
	public void setColorSet(int value);
	public void setStick(boolean show);
	public void setUseMic(boolean use);
	public void setMICSensitivity(int value);
	public int getCustomColorSet();
}
