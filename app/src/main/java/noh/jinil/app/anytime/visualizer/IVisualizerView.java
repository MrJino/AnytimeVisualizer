package noh.jinil.app.anytime.visualizer;

public interface IVisualizerView {
	public void update(byte[] bytes);
	public float plusRatio();
	public void refreshChanged();
	public void setBarSize(int value);
	public void setAlpha(int value);
	public void setColorSet(int value);
	public void setStick(boolean show);
	public void setUseMic(boolean use);
	public void setMICSensitivity(int value);
	public void refresh();
	public int getCustomColorSet();
}
