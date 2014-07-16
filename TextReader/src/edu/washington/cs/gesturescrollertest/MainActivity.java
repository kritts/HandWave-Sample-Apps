package edu.washington.cs.gesturescrollertest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;
import edu.washington.cs.touchfreelibrary.sensors.CameraGestureSensor;
import edu.washington.cs.touchfreelibrary.touchemulation.GestureScroller;

public class MainActivity extends Activity {
	private static final String TAG = "ScrollerActivity";
	private CameraGestureSensor mGestureSensor;
	private GestureScroller mGestureScroller;
	
	private boolean gestureStarted = false;
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
	    @Override
	    public void onManagerConnected(int status) {
	        switch (status) {
	            case LoaderCallbackInterface.SUCCESS:
	            {
	                Log.i(TAG, "OpenCV loaded successfully");
	                
	                CameraGestureSensor.loadLibrary();
	                
	                // more initialization steps go here
	                
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
	
	private TextView mScrollingTextBox;
	private Thread mTextLoadThread = new Thread() {
		@Override
		public void run() {
			InputStream othelloInput = getResources().openRawResource(R.raw.recipes);
			
			try {
				Scanner s = new java.util.Scanner(othelloInput).useDelimiter("\\A");
			    mScrollingTextBox.setText(s.hasNext() ? s.next() : "");
				othelloInput.close();
			} catch (IOException e) {
			}
		}
	};
	
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
			  public void onGlobalLayout() 
			  {
				  mGestureScroller.setVerticalPointsWithView(mScrollingTextBox, 100);
			  }
		  });
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scroller, menu);
		return true;
	}
	
	@Override
	public void onWindowFocusChanged (boolean hasFocus) {
		if(!gestureStarted)
			return;
		
		if(hasFocus)
			mGestureSensor.start();
		else
			mGestureSensor.stop();
	}
}
