package edu.washington.cs.practicegestures;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FinishGame extends Activity {
	
	/** Starts camera gesture activity. */
	private Button mReturnHome;	
	  
	private TextView mSpeed;
	
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.end); 
		mReturnHome = (Button) findViewById(R.id.home);
		mSpeed = (TextView) findViewById(R.id.status);
		
		mReturnHome.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) {
				Intent intent = new Intent(FinishGame.this, StartActivity.class); 
		 		startActivity(intent); 
 			}  
        }); 
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras(); 
		long time = extras.getLong("time");  
		 
		String timeMinSecs = getMinutes(time);
		String timeSeconds = String.valueOf(time);
		 
		//Get their fastest time
		String savedText = getPreferences(MODE_PRIVATE).getString("time", null); 
		 
		if(savedText == null) {
			Log.e("", "saved text is null");
			String message = "Great job! \n Your high score is: \n " + timeMinSecs;
			mSpeed.setText(message); 
	    	SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
	    	editor.putString("time", timeSeconds);
	    	editor.apply();
		} else { 
			if(Long.parseLong(savedText) >= time) {
				Log.e("", "new high score");
				String message = " Great job! \n You beat your old high score. "
								 + "Your new high score is: \n " + timeMinSecs;
				mSpeed.setText(message);
		    	SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
		    	editor.putString("time", timeSeconds);
		    	editor.apply();
			} else {
				Log.e("", "not a new high score");
				String message = " Darn, you didn't beat your high score!";
				message += "\n Your high score is: \n" + getMinutes(Long.parseLong(savedText));
				message += "\n Your score was: \n" + timeMinSecs;
				mSpeed.setText(message); 
			}
		} 
	}

	private String getMinutes(long time) {
		 int seconds = (int) (time / 1000) % 60 ;
         int minutes = (int) ((time / (1000*60)) % 60); 
         String text = String.format("%2d minutes and %02d seconds", minutes, seconds);
         return text;
	} 
	
}
