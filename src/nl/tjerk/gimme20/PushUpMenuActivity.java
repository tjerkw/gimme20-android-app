package nl.tjerk.gimme20;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;


public class PushUpMenuActivity extends ListActivity {
	//private GoogleAnalyticsTracker tracker;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//tracker = Analytics.getInstance(this);
		//super.setContentView(R.layout.menu);

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//tracker.stop();
	}

	/*
	public void onListItemClick(ListView parent, View v, int position, long id) {
		
		Intent intent = new Intent(this, PushUpCounterActivity.class);
		this.startActivity(intent);
	}*/

}

