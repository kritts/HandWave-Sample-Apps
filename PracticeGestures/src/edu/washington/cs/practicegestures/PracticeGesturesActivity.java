package edu.washington.cs.practicegestures;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;      

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;   

import android.app.Activity; 
import android.graphics.Color; 
import android.os.Bundle;  
import android.view.View;     
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;      
import edu.washington.cs.touchfreelibrary.sensors.CameraGestureSensor;
import edu.washington.cs.touchfreelibrary.sensors.ClickSensor;
  
public class PracticeGesturesActivity extends Activity 
				implements ClickSensor.Listener, CameraGestureSensor.Listener {

	private static final int NUMBER_OF_ROUNDS = 40; 

	private CameraGestureSensor mGestureSensor;
 
	private int touchFree;

	private int screenNumber;

	private int mCurrentRound; 

	private Random mRandom;
	private boolean mOpenCVInitiated = false; 

	protected boolean mIsRunning;  
	private Direction mCurrentDirection; 
	View imageView1;

	@Override	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 

		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
		mIsRunning = true;
		screenNumber = 0;  
		mCurrentRound = 0;
		mGestureSensor = new CameraGestureSensor(this);
		mGestureSensor.addGestureListener(this);
		mGestureSensor.addClickListener(this);	 

		mRandom = new Random(); 			
		openIntroductoryScreen(); 
	}

	private void openIntroductoryScreen() { 
		if (screenNumber == 0 & mIsRunning) {
			setContentView(R.layout.activity_screen_zero);
			if (touchFree == 1) {
				TextView textView = (TextView)findViewById(R.id.textView1);
				if (textView != null) {
					textView.setText("Use touch-free gestures to perform the actions indicated on screen.");
				}
			}
			else {
				TextView textView = (TextView)findViewById(R.id.textView1);
				if (textView != null) {
					textView.setText("Use swipe and single tap touch gestures to perform the actions indicated on screen.");
				}
			}
		} else { 
			handleNextScreen();
		} 
	}

	public void handleNextScreen() {  
		if (screenNumber != 0 & mIsRunning) 
		{
			setContentView(R.layout.activity_start);
			if (touchFree == 1 & mIsRunning) { 
				setRandomDirection();    
			} else {   
				setRandomDirection(); 
				View imageView1 = findViewById(R.id.imageView1);  
				if (imageView1 != null) {  
//					setUpSwipeListener();  
				}
			}   
		}
	} 

	protected void setRandomDirection() {  
		if(mCurrentRound >= NUMBER_OF_ROUNDS) 
		{  
			mIsRunning = false;
  

			if (touchFree == 0) { 
				View imageView1 = findViewById(R.id.imageView1);
				imageView1.setOnTouchListener(null);
			} else {  
				mGestureSensor.removeGestureListener(this); 
			}
			runOnUiThread(new Runnable() {    
				@Override
				public void run() {  
					setContentView(R.layout.end); 
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
					
					int dir = mRandom.nextInt(5);  
					if(dir == 0) {  
						mCurrentDirection = Direction.Up;
						imageView1.setImageResource(R.drawable.arrow_up); 
					}
					else if(dir == 1) { 
						mCurrentDirection = Direction.Down;
						imageView1.setImageResource(R.drawable.arrow_down); 
					}
					else if(dir == 2) { 
						mCurrentDirection = Direction.Left;
						imageView1.setImageResource(R.drawable.arrow_left); 
					}
					else if(dir == 3) {   
						mCurrentDirection = Direction.Right;
						imageView1.setImageResource(R.drawable.arrow_right); 
					} else if(dir == 4){ 
						mCurrentDirection = Direction.Click;
						imageView1.setImageResource(R.drawable.click);
					}
					
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
	 
	public void handleStart(View v) { 
		screenNumber ++;
		openIntroductoryScreen();
	} 
	
	public void handleFinish(View v) {
		finish();
	}  

	@Override
	public void onGestureUp(CameraGestureSensor caller, long gestureLength) { 
		if (touchFree == 1 & mIsRunning) {  
			runOnUiThread(new Runnable() {    
				@Override
				public void run() { 
					if(mCurrentDirection == Direction.Up) { 
						setRandomDirection();
					}  
				}
			}); 
		}
	}

	@Override
	public void onGestureDown(CameraGestureSensor caller, long gestureLength) { 
		if (touchFree == 1 & mIsRunning) {   
			runOnUiThread(new Runnable() {    
				@Override
				public void run() {    
					if(mCurrentDirection == Direction.Down) { 
						setRandomDirection();
					}  
				}
			}); 
		}
	}

	@Override
	public void onGestureLeft(CameraGestureSensor caller, long gestureLength) { 
		if (touchFree == 1 & mIsRunning) {  
			runOnUiThread(new Runnable() {    
				@Override
				public void run() {   
					if(mCurrentDirection == Direction.Left) { 
						setRandomDirection();
					}  
				} 
			});

		} 
	}

	@Override
	public void onGestureRight(CameraGestureSensor caller, long gestureLength) { 
		if (touchFree == 1 & mIsRunning) { 
			runOnUiThread(new Runnable() {    
				@Override
				public void run() {   
					if(mCurrentDirection == Direction.Right) { 
						setRandomDirection();
					} 
				}
			});

		}
	}

	@Override
	public void onSensorClick(ClickSensor caller) { 	
		if (touchFree == 1 & mIsRunning) {  
			runOnUiThread(new Runnable() {    
				@Override
				public void run() {  
					if(mCurrentDirection == Direction.Click) {  
						setRandomDirection();
					}  
				} 
			}); 
		} 
	}

	private enum Direction {
		Up, Down, Left, Right, Click
	}

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

	@Override
	public void onWindowFocusChanged (boolean hasFocus) {
		if(!mOpenCVInitiated)
			return; 
		if(hasFocus) {
			mGestureSensor.start();
		}
		else {
			mGestureSensor.stop();
		}
	}

	@Override
	public void onResume() {
		super.onResume(); 
		if(!mOpenCVInitiated)
			return; 
		mGestureSensor.start();
	}

	@Override
	public void onPause() {
		super.onPause(); 
		if(!mOpenCVInitiated)
			return; 
		mGestureSensor.stop();
	}  
} 