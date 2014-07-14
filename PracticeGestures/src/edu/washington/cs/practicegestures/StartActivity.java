package edu.washington.cs.practicegestures;
   
import android.os.Bundle;
import android.view.View;
import android.app.Activity; 
import android.widget.Button;
import android.content.Intent;
import android.view.View.OnClickListener; 

public class StartActivity extends Activity {    
	private Button mStart;	
	  
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
