package nl.tjerk.gimme20;

import java.nio.FloatBuffer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

public class AccelerometerPushUpDetector extends SensorBasedPushUpListener {
	private float x,y,z;
	private CircularBuffer buffer = new CircularBuffer();
	private FloatBuffer sinus = FloatBuffer.allocate(20);
	private float[] values = new float[50];
	
	@Override
	public int getSensorType() {
		return Sensor.TYPE_ACCELEROMETER;
	}

	@Override
	public void onSensorChanged(SensorEvent e) {
		if(e.values==null || e.values.length!=3) {
			return;
		}
		x = e.values[0];
		y = e.values[1];
		z = e.values[2];
		float len = (float) Math.sqrt(x*x + y*y + z*z);
		len -= SensorManager.STANDARD_GRAVITY;
		buffer.put(len);
		detect();
	}
	
	private void detect() {
		int count = buffer.getCount();
		if(count < 7) {
			return;
		}
		buffer.toArray(values);
		if(values[0]>=0) {
			return;
		}
		sinus.clear();
		boolean in = false;
		boolean found = false;
		float max = 0;
		float v = 0;

		for(int i=0;i<values.length && i<count;i++) {
			v = values[i];
			if(v>=0) {
				in = true;
				sinus.put(v);
				max = Math.max(v, max);
			} else if(in) {
				found = true;
				break;
			}
		}
		if(!found) {
			return;
		}
		if(max < 1.4) {
			return;
		}
		int n = sinus.position();
		if( n <= 7) {
			// didnt take long enough
			return;
		}
		if(n< 12 && max < 3) {
			return;
		}
		if(n< 10 && max < 4.5) {
			return;
		}
		sinus.position(0);
		double distance = squaredEuclideanDistanceFromSinus(sinus, n, max);
		if(distance <= 3) {
			Log.d("AccelerometerPushUpDetector", "d: "+distance+", max: "+max+", n: "+n);
			buffer.clear();
			this.onPushUpDetected();
		}
	}
	
	/**
	 * Tries to detect a pushup using the the sensor values
	 */
	private void detect2() {
		
		buffer.toArray(values);
		int state = 0;
		int prevState = 0;
		int upCount = 0;
		int downCount = 0;
		for(int i=0;i<values.length;i++) {
			state = getMovement(values[i]);
			switch(prevState) {
			case UP:
				if(prevState != UP) {
					return;
				}
				upCount++;
				break;
			case DOWN:
				// detected!
				if(upCount > 5) {
					downCount++;
					if(downCount > 4) {
						buffer.clear();
						this.onPushUpDetected();
						return;
					}
				} else {
					return;
				}
			case STEADY:
				if(prevState!= UP) {
					return;
				}
				break;
			default:
				if(state != UP) {
					return;
				}
			}
			prevState = state;
		}
	}
	
	private static final int UP = 1;
	private static final int DOWN = 2;
	private static final int STEADY = 3;
	private static final float FORCE_TRESHOLD = 0.5f;
	
	private int getMovement(float v) {
		if(v<=-FORCE_TRESHOLD) {
			return UP;
		} if(v>=FORCE_TRESHOLD) {
			return DOWN;
		}
		return STEADY;
	}

	static double squaredEuclideanDistance(float[] vector1, float[] vector2) {
		double squaredDistance = 0;
		int size = vector1.length;
		for (int i = 0; i < size; i++) {
			float difference = vector1[i] - vector2[i];
			squaredDistance += difference * difference;
		}
		return squaredDistance / size;
	}
	
	static double squaredEuclideanDistanceFromSinus(FloatBuffer buff, int n, float amplitude) {
		double squaredDistance = 0;
		for (int i = 0; i < n; i++) {
			float difference = (float) (buff.get(i) - Math.sin(i * Math.PI / n) * amplitude);
			squaredDistance += difference * difference;
		}
		return squaredDistance / n;
	}

}

class CircularBuffer {
	private FloatBuffer buffer = FloatBuffer.allocate(20);
	private int count;
	
	void put(float v) {
		//Log.d("AccelerometerPushUpDetector", "Value: "+v);
		buffer.put(v);
		count++;
		if(buffer.position()==buffer.limit()) {
			buffer.position(0);
		}
	}
	
	public void clear() {
		buffer.clear();
		count = 0;
	}

	int getCount() {
		return count;
	}
	
	void toArray(float[] vals) {
		int oldPos = buffer.position();
		for(int i=0;i<vals.length;i++) {
			vals[i] = buffer.get(buffer.position());
			if(buffer.position()==0) {
				buffer.position(buffer.limit()-1);
			} else {
				buffer.position(buffer.position()-1);
			}
		}
		buffer.position(oldPos);
	}
	
}
