package nl.tjerk.gimme20;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public abstract class SensorBasedPushUpListener extends PushUpDetector implements SensorEventListener {
	private Context ctx;
	private SensorManager sensorManager;
	protected Sensor sensor;
	
	public abstract int getSensorType();

	@Override
	public void onStart(Context ctx) {
		this.ctx = ctx;
		this.initSensorListener();
		this.registerSensorListener();
	}
	
	@Override
	public void onStop() {
		this.unregisterSensorListener();
	}
	
	
	/**
	 * Gets the sensor service
	 */
	private void initSensorListener() {
        if (sensorManager == null) {
            sensorManager = (SensorManager) ctx
                    .getSystemService(Context.SENSOR_SERVICE);
        }
    }

	/**
	 * Tries to register to sensor we need,
	 * otherwise notify the listener
	 */
    private void registerSensorListener() {
        if (sensorManager != null) {
            this.sensor = getSensor(this.getSensorType());
            if (sensor != null) {
                sensorManager.registerListener(this, sensor,
                        SensorManager.SENSOR_DELAY_FASTEST);
            } else {
            	// no sensor available
            	if(listener!=null) {
            		listener.onDetectorInitFailed("Could not init sensor");
            	}
            }
        }
    }

    private Sensor getSensor(int type) {
        List<Sensor> sensors = sensorManager.getSensorList(type);
        if (sensors == null || sensors.size() == 0) {
            return null;
        }
        return sensors.get(0);
    }

    /**
     * Cleanup: stop listening when not needed
     */
    public void unregisterSensorListener() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    public void onAccuracyChanged(Sensor e, int accuracy) {
    }

    /**
     * Override this method and try to detect that a pushup occured
     */
    public abstract void onSensorChanged(SensorEvent e);

}
