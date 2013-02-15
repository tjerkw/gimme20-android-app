package nl.tjerk.gimme20;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

public class ProximityPushUpDetector extends SensorBasedPushUpListener {
	/**
	 * If closer than this distance we detect a pushup
	 */
	private float pushTreshHold = 3f; // cm
	private float prevDistance = 50f;
	
	@Override
	public int getSensorType() {
		return Sensor.TYPE_PROXIMITY;
	}

	@Override
	public void onSensorChanged(SensorEvent e) {
		if(e==null || e.values.length==0);
		float distanceInCm = e.values[0];
		Log.d("ProximiDetector", "distance: "+distanceInCm);
		float max = sensor.getMaximumRange();
		if(distanceInCm != max) {
			if(prevDistance >= pushTreshHold && distanceInCm < pushTreshHold) {
				this.onPushUpDetected();
			}
		} else {
			pushTreshHold = max;
		}
		prevDistance = distanceInCm;
	}
	
}
