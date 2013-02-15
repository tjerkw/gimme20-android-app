package nl.tjerk.gimme20;

import android.content.Context;
import android.util.Log;

public abstract class PushUpDetector {
	protected PushUpListener listener;
	
	public abstract void onStart(Context ctx);
	
	public abstract void onStop();
	
	public void setListener(PushUpListener listener) {
		this.listener = listener;
	}
	
	protected void onPushUpDetected() {
		Log.d("PushUpDetector",  "pushup detected");
		if(listener!=null) {
			listener.onPushUp();
		}
	}
}
