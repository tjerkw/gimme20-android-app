package nl.tjerk.gimme20;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;

public class PushUpCounterActivity extends Activity implements PushUpListener, OnInitListener {
	private static final int TTS_CHECK = 1;
    private TextToSpeech tts;
    private boolean ttsLoaded = false;

    private PushUpDetector detector;
	private PushUpState state = new PushUpState();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counter);
        //detector = new ProximityPushUpDetector();
        detector = new AccelerometerPushUpDetector();
        detector.setListener(this);
        
        // check for t2s library
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, TTS_CHECK);
    }
    
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (requestCode == TTS_CHECK) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                tts = new TextToSpeech(this, this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                    TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }
    
    /**
     * Called when tts is succesfully initialized
     */
	@Override
	public void onInit(int status) {
		ttsLoaded = true;
		tts.speak("Drop and gimme 20!", TextToSpeech.QUEUE_FLUSH, null);
	}
  
    @Override
    public void onResume() {
    	super.onResume();
    	detector.onStart(this);
    }
    
    public void onPause() {
    	detector.onStop();
    	super.onPause();
    }
    
    public void onDestroy() {
    	if(tts!=null) {
    		tts.shutdown();
    	}
    	super.onDestroy();
    }

	@Override
	public void onPushUp() {
		state.count++;
		if(ttsLoaded) {
			tts.speak(state.count + "", TextToSpeech.QUEUE_FLUSH, null);
		}
	}

	@Override
	public void onDetectorInitFailed(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

}