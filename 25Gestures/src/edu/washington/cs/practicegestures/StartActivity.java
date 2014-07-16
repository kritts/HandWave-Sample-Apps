package edu.washington.cs.practicegestures;
   
import android.os.Bundle;
import android.view.View;
import android.app.Activity; 
import android.widget.Button;
import android.content.Intent;
import android.view.View.OnClickListener; 

/** This is the first screen in the gesture game application.
 *  The user starts the game whenever they are ready by clicking on the 
 *  start button.
 *  
 *  @author Krittika D'Silva (krittika.dsilva@gmail.com) */
public class StartActivity extends Activity {
	
	/** Starts camera gesture activity. */
	private Button mStart;	
	  
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.swipe_start); 
		mStart = (Button) findViewById(R.id.touchfree);
		
		mStart.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) {
				Intent intent = new Intent(StartActivity.this, PracticeGesturesActivity.class); 
		 		startActivity(intent); 
 			}  
        });  
	} 
}
