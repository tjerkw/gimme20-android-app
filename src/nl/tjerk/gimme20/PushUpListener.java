package nl.tjerk.gimme20;

public interface PushUpListener {
	/**
	 * Called when a push up was detected
	 */
	public void onPushUp();
	
	/**
	 * Called when the detector could not start
	 */
	public void onDetectorInitFailed(String msg);
}
