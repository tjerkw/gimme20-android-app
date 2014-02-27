package nl.tjerk.gimme20;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

public class PushUpCounterActivity extends Activity implements PushUpListener, OnInitListener {
	private static final int TTS_CHECK = 1;
    private TextToSpeech tts;
    private boolean ttsLoaded = false;
    private Timer timer;
    private Random random = new Random();

    private PushUpDetector detector;
	private PushUpState state = new PushUpState();

    private String[] startText = {
        "Ga je nog beginnen?",
        "Begin met opdrukken!",
        "Kom op, Je kunt het!",
        "Kweek die spieren",
        "Kerel kom op",
        "Doe je shit",
        "Gast"
    };
    private String[] goodText = {
        "Goedzo",
        "Top",
        "Held",
        "Klasse",
        "Cool"
    };
    private String[] continueText = {
        "Ga door",
        "Niet ophouden",
        "Geef je op?",
        "Kom op!",
        "Doorgaan kerel!"
    };

    private final String getRandom(String[] texts) {
        return texts[random.nextInt(texts.length)];
    }
	
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
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                tts.speak(getRandom(startText), TextToSpeech.QUEUE_FLUSH, null);
            }
        }, 4000, 4000 + random.nextInt(6000));
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
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                tts.speak(getRandom(continueText), TextToSpeech.QUEUE_FLUSH, null);
            }
        }, 2500, 3000 + random.nextInt(6000));
		if(ttsLoaded) {
            String extraText = "";
            if (state.count % 10 == 0) {
                extraText = ", " + getRandom(goodText);
            }
			tts.speak(state.count + extraText, TextToSpeech.QUEUE_FLUSH, null);
		}
	}

	@Override
	public void onDetectorInitFailed(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

}