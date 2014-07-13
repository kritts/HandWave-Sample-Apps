package edu.washington.cs.practicegestures;
   
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log; 
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StartActivity extends Activity { 
	private boolean touchFreeDone = false;
	private boolean touchDone = false;
	private int order = 0;

	private Button touch;
	private Button touchfree;	
	
	private EditText subject;
	private EditText session;
	
	private String subjectNumber;
	private String sessionNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.swipe_start); 
		touchfree = (Button) findViewById(R.id.touchfree);
		touch = (Button) findViewById(R.id.touch); 
		subject = (EditText) findViewById(R.id.subject);
		session = (EditText) findViewById(R.id.session);
		setupListeners();
	}


	private void setupListeners() {
		subject.addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Log.e("", s.toString());
				subjectNumber = s.toString();
			}

			@Override 
			public void afterTextChanged(Editable s) { }  
		});

		session.addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Log.e("", s.toString());
				sessionNumber = s.toString();
			} 

			public void afterTextChanged(Editable s)  { }  
		});  
	} 

	public void openTouchFree(View view) { 
		order ++;
		touchFreeDone = true;
		Intent intent = new Intent(this, PracticeGesturesActivity.class);	 
		Bundle extras = new Bundle();
		extras.putString("userID", subjectNumber);		
		extras.putString("sessNumb", sessionNumber); 
		extras.putInt("touchFree", 1);
		extras.putInt("order", order);
 
		intent.putExtras(extras); 
 		startActivity(intent);  
	}   
	
	public void openTouchMode(View view) { 
		order ++;
		touchDone = true; 
		Intent intent = new Intent(this, PracticeGesturesActivity.class);	 
		Bundle extras = new Bundle();
		extras.putString("userID", subjectNumber);		
		extras.putString("sessNumb", sessionNumber); 
		extras.putInt("touchFree", 0);
		extras.putInt("order", order);
		
		intent.putExtras(extras); 
 		startActivity(intent);  
	} 
	 
	@Override
	public void onResume() {
		super.onResume(); 
		if (touchFreeDone)
			touchfree.setEnabled(false);

		if (touchDone)
			touch.setEnabled(false);

		if ((touchFreeDone && touchDone) || (!touchFreeDone && !touchDone)) {
			touchFreeDone = false;
			touchDone = false; 
			order = 0;
			touchfree.setEnabled(true);
			touch.setEnabled(true);
		}
	} 
}
