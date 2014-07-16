package edu.washington.cs.practicegestures;
 
import java.util.Random; 
import android.util.Log;  
import android.os.Bundle;  
import android.view.View;    
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.widget.TextView;      
import android.widget.ImageView;
import android.widget.RelativeLayout; 
import org.opencv.android.OpenCVLoader;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;  
import edu.washington.cs.touchfreelibrary.sensors.ClickSensor;
import edu.washington.cs.touchfreelibrary.sensors.CameraGestureSensor;
  

/** This game has multiple rounds. In each round, a direction (left, right,
 *  up, down, or a click) is displayed to the user. The user moves onto the 
 *  next round when they make the prompted gesture in front of the camera.  
 * 
 *  @author Krittika D'Silva (krittika.dsilva@gmail.com) */
public class PracticeGesturesActivity extends Activity 
				implements ClickSensor.Listener, CameraGestureSensor.Listener {
	private static final String TAG = "PracticeGesturesActivity";

	/** The total number of rounds played. */
	private static final int NUMBER_OF_ROUNDS = 25; 
	
	/** Sensor that detects gestures. Calls the appropriate 
	 *  functions when the motions are recognized. */
	private CameraGestureSensor mGestureSensor;
	
	/** The current round that the user is playing on. */
	private int mCurrentRound;
	
	/** Produces a random number - used to change the background. */
	private Random mRandom;
	
	/** True if the openCV library has been initiated. 
	 *  False otherwise*/
	private boolean mOpenCVInitiated = false;
	
	/** The current direction that the user is being 
	 *  prompted to gesture towards.*/
	private Direction mCurrentDirection;
	
	/** */
	private long mStartTime;
	
	/** Called when the activity is first created. */
	@Override	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		// Initialize openCV
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
		 
		mGestureSensor = new CameraGestureSensor(this);
		mGestureSensor.addGestureListener(this);
		mGestureSensor.addClickListener(this);	 

		mRandom = new Random();
		mCurrentRound = 0;
		mStartTime = System.currentTimeMillis();
		setRandomDirection();
	}

    /** Moves onto the next screen. If all of the rounds are over,
     *  opens the final screen. */
	protected void setRandomDirection() {  
		if(mCurrentRound >= NUMBER_OF_ROUNDS) { 
			Intent intent = new Intent(this, FinishGame.class);
			Log.e(TAG, "" + System.currentTimeMillis() );
			Log.e(TAG, "" + mStartTime );
			
			long totalTime = System.currentTimeMillis() - mStartTime; 
			intent.putExtra("time", totalTime);
	    	startActivity(intent);
	    	
			// Game is over   
			runOnUiThread(new Runnable() {    
				@Override
				public void run() { 
					mGestureSensor.stop();
				}
			}); 
		} else {       
			runOnUiThread(new Runnable() {     
				@Override
				public void run() {   
					TextView mCurrentScore = (TextView) findViewById(R.id.textView3);
					TextView mDirection = (TextView) findViewById(R.id.textView4); 
					ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);  
					mCurrentRound++; 
					mCurrentScore.setText("" + (mCurrentRound) + "/" + NUMBER_OF_ROUNDS);
					
					int randomNumb = mRandom.nextInt(5);
					switch(Integer.valueOf(randomNumb)) {
					case 0:  
						mCurrentDirection = Direction.Up;
						imageView1.setImageResource(R.drawable.arrow_up);
						break;
					case 1:   
						mCurrentDirection = Direction.Down;
						imageView1.setImageResource(R.drawable.arrow_down);
						break;
					case 2:    
						mCurrentDirection = Direction.Left;
						imageView1.setImageResource(R.drawable.arrow_left);
						break;
					case 3:      
						mCurrentDirection = Direction.Right;
						imageView1.setImageResource(R.drawable.arrow_right);
						break;
					case 4:    
						mCurrentDirection = Direction.Click;
						imageView1.setImageResource(R.drawable.click);
						break;
					}
					
					// Makes the background color look quite nice
					int red = mRandom.nextInt(256-150) + 150;
					int green = mRandom.nextInt(256-150) + 150;
					int blue = mRandom.nextInt(256-150) + 150;

					RelativeLayout wholeScreen = (RelativeLayout) findViewById(R.id.setArrows);	
					wholeScreen.setBackgroundColor(Color.rgb(red,green,blue));
					imageView1.setBackgroundColor(Color.rgb(red,green,blue));
					mDirection.setText("" + directionToString(mCurrentDirection)); 
				}
			});
		} 
	}
	 
	/** Closes the current activity. This method is called when the 
	 *  user is finished with the game and is on the final screen; 
	 *  closing that activity takes them back to the main screen. */
	public void handleFinish(View v) {
		finish();
	}  
	
	/** Given a Direction, checks whether that direction is the 
	 *  same as the currently anticipated gesture. Moves onto the next 
	 *  screen if it is. */
	private void checkGestureAccuracy(Direction direction) {
			final Direction userDirection = direction;
			runOnUiThread(new Runnable() {    
				@Override
				public void run() {   
					if(mCurrentDirection == userDirection) { 
						setRandomDirection();
					}  
				} 
			}); 
		  
	}
	/** Moves onto the next screen if an upwards gesture is received. */
	@Override
	public void onGestureUp(CameraGestureSensor caller, long gestureLength) { 
		Log.e(TAG, "Up gesture detected");
		checkGestureAccuracy(Direction.Up);
	}

	/** Moves onto the next screen if an downwards gesture is received. */
	@Override
	public void onGestureDown(CameraGestureSensor caller, long gestureLength) { 
		Log.e(TAG, "Down gesture detected"); 
		checkGestureAccuracy(Direction.Down);
	}

	/** Moves onto the next screen if an leftwards gesture is received. */
	@Override
	public void onGestureLeft(CameraGestureSensor caller, long gestureLength) { 
		Log.e(TAG, "Left gesture detected"); 
		checkGestureAccuracy(Direction.Left);
	}

	/** Moves onto the next screen if an rightwards gesture is received. */
	@Override
	public void onGestureRight(CameraGestureSensor caller, long gestureLength) { 
		Log.e(TAG, "Right gesture detected"); 
		checkGestureAccuracy(Direction.Right);
	}
	 
	/** Moves onto the next screen if an click is received. */
	@Override
	public void onSensorClick(ClickSensor caller) { 	
		checkGestureAccuracy(Direction.Click);
	}
  
	/** List of the five directions that the camera sensor can detect. */
	private enum Direction {
		Up, Down, Left, Right, Click
	}

	/** Given a direction, returns the direction as a string. */
	private String directionToString(Direction d) {
		switch(d) {
		case Up:
			return "Up";
		case Down:
			return "Down";
		case Left:
			return "Left";
		case Click:
			return "Click";
		default:
			return "Right";
		}
	}

	/** OpenCV library initialization. */
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
			{
				mOpenCVInitiated = true; 
				CameraGestureSensor.loadLibrary();
				mGestureSensor.start();
			} break;
			default:
			{
				super.onManagerConnected(status);
			} break;
			}
		}
	};  

	/** Called when the activity is resumed. The gesture detector is initialized. */
	@Override
	public void onResume() {
		super.onResume(); 
		if(!mOpenCVInitiated)
			return; 
		mGestureSensor.start();
	}
	
	/** Called when the activity is paused. The gesture detector is stopped
	 *  so that the camera is no longer working to recognize gestures. */
	@Override
	public void onPause() {
		super.onPause(); 
		if(!mOpenCVInitiated)
			return; 
		mGestureSensor.stop();
	}  
} 