package edu.washington.cs.gesturescrollertest;

import android.util.Log;
import android.os.Bundle; 
import java.util.Scanner;
import java.io.IOException;
import java.io.InputStream;
import android.app.Activity;
import android.widget.TextView;
import android.view.WindowManager;
import android.view.ViewTreeObserver;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import android.text.method.ScrollingMovementMethod;
import edu.washington.cs.touchfreelibrary.sensors.CameraGestureSensor;
import edu.washington.cs.touchfreelibrary.touchemulation.GestureScroller;

/** This is the first screen in the TextReader app. 
 *  It only has one function - to let the user scroll through 
 *  a text file by gesturing in front of the camera. 
 *  @author Krittika D'Silva (krittika.dsilva@gmail.com) */

public class MainActivity extends Activity {
	private static final String TAG = "ScrollerActivity";
	
	/** Sensor that detects gestures. Calls the appropriate 
	 *  functions when the motions are recognized. */ 
	private CameraGestureSensor mGestureSensor;
	
	/** Sensor that recognizes scroll gestures. */
	private GestureScroller mGestureScroller;
	/** True if a gesture has been received. */
	private boolean gestureStarted = false;
	
	/** TextView containing the text to be shown. */
	private TextView mScrollingTextBox;
 	
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen1);
		
		mScrollingTextBox = (TextView) findViewById(R.id.scrollingTextBox);
		mScrollingTextBox.setMovementMethod(new ScrollingMovementMethod());
		mTextLoadThread.start();
		
		mGestureSensor = new CameraGestureSensor(MainActivity.this);
		mGestureScroller = new GestureScroller();
		mGestureSensor.addGestureListener(mGestureScroller);
		mGestureScroller.setHorizontalScrollEnabled(false);
		mGestureSensor.enableHorizontalScroll(false);
		mGestureScroller.setInvertVerticalScroll(true);
		
		
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		ViewTreeObserver viewTreeObserver = mScrollingTextBox.getViewTreeObserver();
		if (viewTreeObserver.isAlive()) {
		  viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			  @Override
			  public void onGlobalLayout() {
				  mGestureScroller.setVerticalPointsWithView(mScrollingTextBox, 100);
			  }
		  });
		}
	}
 
	/** Thread containing text displayed on the screen - the text changes
	 *  as the user scrolls. */
	private Thread mTextLoadThread = new Thread() {
		@Override
		public void run() {
			InputStream othelloInput = getResources().openRawResource(R.raw.othello);
			
			try {
				Scanner s = new java.util.Scanner(othelloInput).useDelimiter("\\A");
			    mScrollingTextBox.setText(s.hasNext() ? s.next() : "");
				othelloInput.close();
			} catch (IOException e) {
			}
		}
	};
 
	/** Called when the focus on the window changes - the gesture detector is stopped
	 *  when the window doesn't have focus 
	 *  so that the camera is no longer working to recognize gestures. */
	@Override
	public void onWindowFocusChanged (boolean hasFocus) {
		if(!gestureStarted)
			return;
		
		if(hasFocus)
			mGestureSensor.start();
		else
			mGestureSensor.stop();
	}
	
	/** OpenCV library initialization. */ 
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
	    @Override
	    public void onManagerConnected(int status) {
	        switch (status) {
	            case LoaderCallbackInterface.SUCCESS: {
	                Log.i(TAG, "OpenCV loaded successfully");
	                
	                CameraGestureSensor.loadLibrary();
	                 
	                gestureStarted = true;
	                mGestureSensor.start();
	                mGestureScroller.start();
	            } break;
	            default:
	            {
	                super.onManagerConnected(status);
	            } break;
	        }
	    }
	};
}
