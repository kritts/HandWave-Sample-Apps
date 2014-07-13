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
	private long taskStartTime = 0;

	private String subject;
	private String session;
	private int order = 0;
	private int touchFree;

	private int screenNumber;

	private int mCurrentRound;
	protected int mRightGesture;
	protected int mWrongGesture;  
	protected int mClickCorrect;
	protected int mClickIncorrect;
	private int mNumbGesturesReceived = 0;
	private int mNumbClicksReceived = 0;
	private int mNumbGesturesIdeal = 0;
	private int mNumbClickIdeal = 0;
	private String allGesReceived;
	private long timeOnScreen;
	private int eachScreenClick;
	private int eachScreenGes;

	private Random mRandom;
	private boolean mOpenCVInitiated = false; 

	protected boolean mIsRunning;  
	private Direction mCurrentDirection;
	private long taskEndTime = 0;
	protected List<GestureResult> mGestureHistory;  
	View imageView1;

	@Override	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();  
		subject = bundle.getString("userID");
		session = bundle.getString("sessNumb"); 
		order = bundle.getInt("order");
		touchFree = bundle.getInt("touchFree");

		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
		mIsRunning = true;
		screenNumber = 0; 
		mRightGesture = mWrongGesture = 0;
		mCurrentRound = 0;
		mGestureSensor = new CameraGestureSensor(this);
		mGestureSensor.addGestureListener(this);
		mGestureSensor.addClickListener(this);	 

		mRandom = new Random();  
		mGestureHistory = new LinkedList<GestureResult>();			
		openIntroductoryScreen();
		allGesReceived = "";
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
					setUpSwipeListener();  
				}
			}  
			taskStartTime = System.currentTimeMillis();
		}
	} 

	protected void setRandomDirection() {  
		if(mCurrentRound >= NUMBER_OF_ROUNDS) 
		{
			taskEndTime = System.currentTimeMillis(); 
			long taskTime = taskEndTime - taskStartTime; 
			mIsRunning = false;

			String outputFile = "User_" + subject + "_Learning_" + session + "_";
			if (touchFree == 1) {
				outputFile += "touchfree_";
			} else {
				outputFile += "touch_"; 
			} 
			writeResultsToFile(outputFile, taskTime); 

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
						mNumbGesturesIdeal++;
						mCurrentDirection = Direction.Up;
						imageView1.setImageResource(R.drawable.arrow_up); 
					}
					else if(dir == 1) {
						mNumbGesturesIdeal++;
						mCurrentDirection = Direction.Down;
						imageView1.setImageResource(R.drawable.arrow_down); 
					}
					else if(dir == 2) {
						mNumbGesturesIdeal++;
						mCurrentDirection = Direction.Left;
						imageView1.setImageResource(R.drawable.arrow_left); 
					}
					else if(dir == 3) {
						mNumbGesturesIdeal++;
						mCurrentDirection = Direction.Right;
						imageView1.setImageResource(R.drawable.arrow_right); 
					} else if(dir == 4){
						mNumbClickIdeal++;
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
					
					reinitialize();
				}
			});
		} 
	}
	
	
	private void reinitialize() { 
		allGesReceived = "";  
		eachScreenClick = 0;
		eachScreenGes = 0; 
		timeOnScreen = System.currentTimeMillis();
	}

	public void handleStart(View v) { 
		screenNumber ++;
		openIntroductoryScreen();
	} 
	
	public void handleFinish(View v) {
		finish();
	} 
   
	protected void writeResultsToFile(String fileName, long taskTime) {
		File root = android.os.Environment.getExternalStorageDirectory(); 
		File dir = new File (root.getAbsolutePath() + "/HandWave/LearningExperiment");
		dir.mkdirs();
		File file = new File(dir, fileName);

		try {
			FileOutputStream fos = new FileOutputStream(file + "stats.csv");
			PrintWriter writer = new PrintWriter(fos);

			// user || session || touchFree || order || totalTime || # gestures received || 
			// # ideal gestures || # clicks received || # ideal click  
			
			String stats = ""; 
			stats +=  "" + subject + "," + session + "," + touchFree + "," + order;
			stats += "," + taskTime + "," + mNumbGesturesReceived + ",";
			stats += mNumbGesturesIdeal + "," + mNumbClicksReceived + "," + mNumbClickIdeal;
			writer.println(stats);
			
			writer.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
		dir = new File (root.getAbsolutePath() + "/HandWave/LearningExperiment/Detailed");
		dir.mkdirs();
		File fileSecond = new File(dir, fileName + "details_stats.csv");
		try { 
			FileOutputStream fosSecond = new FileOutputStream(fileSecond);
			PrintWriter writerSecond = new PrintWriter(fosSecond);
			
			// user || session || touchFree || order || round # || expected
			// time to complete || total gestures || total clicks || directions received
  
			String statsDetails = ""; 
			//statsDetails +=  "";
			
			for(GestureResult gr : mGestureHistory) {
				statsDetails += subject + "," + session + "," + touchFree + "," + order + gr.writeResult() + "\n";
		 	} 
			
			writerSecond.println(statsDetails); 
		 	writerSecond.close();
		 	fosSecond.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	} 
 	
	public class GestureResult {
		private int mRoundNumber;
		private String mExpectedGesture;
		private String mDirectionReceived;
		private long timeFromScreen; 
		private int numberOfGestures;
		private int numberOfClicks; 
		  
		public GestureResult(int round, String expected, String received, long timeOnScreen, 
				int numbGestures, int numbClicks) { 
			mRoundNumber = round;
			mExpectedGesture = expected;
			mDirectionReceived = received;
			timeFromScreen = timeOnScreen; 
			numberOfGestures = numbGestures;
			numberOfClicks = numbClicks; 
		}
	 
		public String writeResult() { 
			return "," + mRoundNumber + "," + timeFromScreen + "," + numberOfGestures + "," 
					+ numberOfClicks + "," +  mExpectedGesture + "," + mDirectionReceived;
		}
	}
  
	private void setUpSwipeListener() { 
		final View imageView1 = findViewById(R.id.imageView1); 
		imageView1.setOnTouchListener(new OnSwipeTouchListener() 
		{  
			public void onSwipeUp() { 
				if (touchFree == 0 & mIsRunning) {  
					final long timeCorrectrecieved = System.currentTimeMillis();
					eachScreenGes++;
					mNumbGesturesReceived ++; 
					if(mCurrentDirection == Direction.Up) {
						mRightGesture++;    
						allGesReceived += "UP";
						//long timeCorrectrecieved = System.currentTimeMillis();
						long timeToComplete = timeCorrectrecieved - timeOnScreen;
						mGestureHistory.add(new GestureResult(mCurrentRound, "UP", allGesReceived,
								timeToComplete, eachScreenGes, eachScreenClick));
						setRandomDirection();
					} else {
						mWrongGesture++;  
						allGesReceived += "UP" + ",";
					} 
				} 
			}
			
			public void onSwipeRight() { 
				if (touchFree == 0 & mIsRunning) {  
					final long timeCorrectrecieved = System.currentTimeMillis();
					mNumbGesturesReceived ++;
					eachScreenGes++;
					if(mCurrentDirection == Direction.Right) {
						mRightGesture++;   
						allGesReceived += "RIGHT";
						//long timeCorrectrecieved = System.currentTimeMillis();
						long timeToComplete = timeCorrectrecieved - timeOnScreen;
						mGestureHistory.add(new GestureResult(mCurrentRound, "RIGHT", allGesReceived,
								timeToComplete, eachScreenGes, eachScreenClick));
						setRandomDirection();
					} else {
						mWrongGesture++; 
						allGesReceived += "RIGHT" + ",";
					}    
				} 
			}

			public void onSwipeLeft() { 
				if (touchFree == 0 & mIsRunning) {  
					final long timeCorrectrecieved = System.currentTimeMillis();
					mNumbGesturesReceived ++; 
					eachScreenGes++; 
					if(mCurrentDirection == Direction.Left) {
						mRightGesture++;  
						allGesReceived += "LEFT";
						//long timeCorrectrecieved = System.currentTimeMillis();
						long timeToComplete = timeCorrectrecieved - timeOnScreen;
						mGestureHistory.add(new GestureResult(mCurrentRound, "LEFT", allGesReceived,
								timeToComplete, eachScreenGes, eachScreenClick));
						setRandomDirection();
					} else {
						mWrongGesture++; 
						allGesReceived += "LEFT" + ",";
					}    
				} 
			}

			public void onSwipeDown() { 
				if (touchFree == 0 & mIsRunning) {  
					final long timeCorrectrecieved = System.currentTimeMillis();
					mNumbGesturesReceived ++;
					eachScreenGes++; 
					if(mCurrentDirection == Direction.Down) {
						allGesReceived += "DOWN";
						mRightGesture++; 
						//long timeCorrectrecieved = System.currentTimeMillis();
						long timeToComplete = timeCorrectrecieved - timeOnScreen;
						mGestureHistory.add(new GestureResult(mCurrentRound, "DOWN", allGesReceived,
								timeToComplete, eachScreenGes, eachScreenClick));
						setRandomDirection();
					} else {
						mWrongGesture++; 
						allGesReceived += "DOWN" + ",";
					}  
				} 
			}

			public void singleTap() { 
				if (touchFree == 0 & mIsRunning) {  
					final long timeCorrectrecieved = System.currentTimeMillis();
					mNumbClicksReceived++;
					eachScreenClick++; 
					runOnUiThread(new Runnable() {    
						@Override
						public void run() {    
							if(mCurrentDirection == Direction.Click) {
								mClickCorrect++;  
								allGesReceived += "CLICK";
								//long timeCorrectrecieved = System.currentTimeMillis();
								long timeToComplete = timeCorrectrecieved - timeOnScreen;
								mGestureHistory.add(new GestureResult(mCurrentRound, "CLICK", allGesReceived,
										timeToComplete, eachScreenGes, eachScreenClick));
								setRandomDirection();
							} else {
								mClickIncorrect++;
								allGesReceived += "CLICK" + ",";
							}   
 
						}
					}); 
				} 
			}

		});    
	}  
	
	@Override
	public void onGestureUp(CameraGestureSensor caller, long gestureLength) { 
		if (touchFree == 1 & mIsRunning) {
			final long timeCorrectrecieved = System.currentTimeMillis();
			mNumbGesturesReceived ++;
			eachScreenGes++; 
			runOnUiThread(new Runnable() {    
				@Override
				public void run() { 
					if(mCurrentDirection == Direction.Up) {
						mRightGesture++; 
						allGesReceived += "UP";
						//long timeCorrectrecieved = System.currentTimeMillis();
						long timeToComplete = timeCorrectrecieved - timeOnScreen;
						mGestureHistory.add(new GestureResult(mCurrentRound, "UP", allGesReceived,
								timeToComplete, eachScreenGes, eachScreenClick));
						setRandomDirection();
					} else {
						mWrongGesture++; 
						allGesReceived += "UP" + ",";
					} 
				}
			}); 
		}
	}

	@Override
	public void onGestureDown(CameraGestureSensor caller, long gestureLength) { 
		if (touchFree == 1 & mIsRunning) {
			final long timeCorrectrecieved = System.currentTimeMillis();
			mNumbGesturesReceived ++;
			eachScreenGes++;
			runOnUiThread(new Runnable() {    
				@Override
				public void run() {    
					if(mCurrentDirection == Direction.Down) {
						mRightGesture++; 
						allGesReceived += "DOWN";
						//long timeCorrectrecieved = System.currentTimeMillis();
						long timeToComplete = timeCorrectrecieved - timeOnScreen;
						mGestureHistory.add(new GestureResult(mCurrentRound, "DOWN", allGesReceived,
								timeToComplete, eachScreenGes, eachScreenClick));
						setRandomDirection();
					} else {
						mWrongGesture++; 
						allGesReceived += "DOWN" + ",";
					} 
				}
			}); 
		}
	}

	@Override
	public void onGestureLeft(CameraGestureSensor caller, long gestureLength) { 
		if (touchFree == 1 & mIsRunning) {
			final long timeCorrectrecieved = System.currentTimeMillis();
			mNumbGesturesReceived ++;
			eachScreenGes++;
			runOnUiThread(new Runnable() {    
				@Override
				public void run() {   
					if(mCurrentDirection == Direction.Left) {
						mRightGesture++; 
						allGesReceived += "LEFT";
						//long timeCorrectrecieved = System.currentTimeMillis();
						long timeToComplete = timeCorrectrecieved - timeOnScreen;
						mGestureHistory.add(new GestureResult(mCurrentRound, "LEFT", allGesReceived,
								timeToComplete, eachScreenGes, eachScreenClick));
						setRandomDirection();
					} else {
						mWrongGesture++; 
						allGesReceived += "LEFT" + ",";
					} 
				} 
			});

		} 
	}

	@Override
	public void onGestureRight(CameraGestureSensor caller, long gestureLength) { 
		if (touchFree == 1 & mIsRunning) {
			final long timeCorrectrecieved = System.currentTimeMillis();
			mNumbGesturesReceived ++;
			eachScreenGes++; 
			runOnUiThread(new Runnable() {    
				@Override
				public void run() {   
					if(mCurrentDirection == Direction.Right) { 
						mRightGesture++; 
						allGesReceived += "RIGHT";
						//long timeCorrectrecieved = System.currentTimeMillis();
						long timeToComplete = timeCorrectrecieved - timeOnScreen;
						mGestureHistory.add(new GestureResult(mCurrentRound, "RIGHT", allGesReceived,
								timeToComplete, eachScreenGes, eachScreenClick));
						setRandomDirection();
					} else {
						mWrongGesture++; 
						allGesReceived += "RIGHT" + ",";
					} 
				}
			});

		}
	}

	@Override
	public void onSensorClick(ClickSensor caller) { 	
		if (touchFree == 1 & mIsRunning) {   
			final long timeCorrectrecieved = System.currentTimeMillis();
			mNumbClicksReceived ++;
			eachScreenClick++;
			runOnUiThread(new Runnable() {    
				@Override
				public void run() {  
					if(mCurrentDirection == Direction.Click) { 
						mRightGesture++; 
						allGesReceived += "CLICK";
						//long timeCorrectrecieved = System.currentTimeMillis();
						long timeToComplete = timeCorrectrecieved - timeOnScreen;
						mGestureHistory.add(new GestureResult(mCurrentRound, "CLICK", allGesReceived,
								timeToComplete, eachScreenGes, eachScreenClick));
						setRandomDirection();
					} else {
						mWrongGesture++; 
						allGesReceived += "CLICK" + ",";
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