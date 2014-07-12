package edu.washington.cs.handsfreegallery;

import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.widget.Toast;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.content.Context;
import android.widget.ImageView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.content.res.TypedArray; 
import org.opencv.android.OpenCVLoader; 
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import android.widget.AdapterView.OnItemClickListener;
import edu.washington.cs.touchfreelibrary.sensors.ClickSensor;
import edu.washington.cs.touchfreelibrary.sensors.CameraGestureSensor;

/** This application allows a user to navigate through a gallery of images 
 *  by using left and right gestures to move from one image to another. 
 *  
 *  @author Krittika D'Silva (krittika.dsilva@gmail.com)
 * */ 
public class MainActivity  extends Activity implements ClickSensor.Listener, CameraGestureSensor.Listener {
	
	/** */
	private CameraGestureSensor mGestureSensor;
	
	/** True if the openCV library has been initiated. 
	 *  False otherwise*/
	private boolean mOpenCVInitiated;
      
	/** Array of images that the user can view. */
	private Integer[] pics = {
    		R.drawable.animal1,
    		R.drawable.animal2,
    		R.drawable.animal3,
    		R.drawable.animal4,
    		R.drawable.animal5,
    		R.drawable.animal6,
    		R.drawable.animal7,
    		R.drawable.animal8,
    		R.drawable.animal9,
    		R.drawable.animal10 };
	
	/** Gallery which displays images. */
    private Gallery ga;
    /** Contains the image */
    private ImageView imageView;
    
    /** Current index of the image within the gallery */
    private int currentView;
    
    /** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
        currentView = 0;
        
        mGestureSensor = new CameraGestureSensor(this);
		mGestureSensor.addGestureListener(this);
		mGestureSensor.enableClickByColor(true);

		mGestureSensor.addClickListener(this);
        
		mOpenCVInitiated = false;
       
        imageView = (ImageView)findViewById(R.id.ImageView01);
        imageView.setImageResource(pics[currentView]);
       
        ga = (Gallery)findViewById(R.id.Gallery01);
        ga.setAdapter(new ImageAdapter(this));
        ga.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View givenView, 
									int index, long arg3) {
				Toast.makeText(getBaseContext(), "A click was recieved!", 
						Toast.LENGTH_SHORT).show();
				imageView.setImageResource(pics[index]); 
				currentView = index;
			} 
        }); 

	}

	/** OpenCV library initialization. */
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
				case LoaderCallbackInterface.SUCCESS: {
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
	
	
	/** Used to show a gallery of images */
    public class ImageAdapter extends BaseAdapter {
    	/** Context in which the image is displayed. */
    	private Context ctx;
    	/** Index of the image. */
    	private int imageBackground;
    	
    	/** Given a context, initializes the image adapter. */
    	public ImageAdapter(Context c) {
			ctx = c;
			TypedArray ta = obtainStyledAttributes(R.styleable.Gallery1);
			imageBackground = ta.getResourceId(R.styleable.Gallery1_android_galleryItemBackground, 1);
			ta.recycle();
		}

    	/** Returns the total number of images available to be shown. */
		@Override
    	public int getCount() { 
    		return pics.length;
    	}
 
		/** Returns the image at the given index. */
    	@Override
    	public View getView(int arg0, View arg1, ViewGroup arg2) {
    		ImageView iv = new ImageView(ctx);
    		iv.setImageResource(pics[arg0]);
    		iv.setScaleType(ImageView.ScaleType.FIT_XY);
    		iv.setLayoutParams(new Gallery.LayoutParams(150,120));
    		iv.setBackgroundResource(imageBackground);
    		return iv;
    	}

    	/**Auto-generated method stub; this method is never used */
		@Override
		public Object getItem(int arg0) { 
			return 0;
		}

		/** Auto-generated method stub; this method is never used */
		@Override
		public long getItemId(int position) { 
			return 0;
		} 
    }
	
	 /** Leftwards gesture detected. If possible, the previous image is shown. */
	@Override
	public void onGestureLeft(CameraGestureSensor caller, long gestureLength) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {   	  
				if (currentView - 1 >= 0)  { 
					ga.setSelection(currentView - 1);
					imageView.setImageResource(pics[currentView - 1]);
					currentView = currentView - 1;
				}
			} 
		});  
	}
	
	 /** Rightwards gesture detected. If possible, the next image is shown. */
	@Override
	public void onGestureRight(CameraGestureSensor caller, long gestureLength) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {  
					if (currentView + 1 < pics.length) { 
						ga.setSelection(currentView + 1);
						imageView.setImageResource(pics[currentView + 1]);	
						currentView = currentView + 1;
					}
				} 
		});  
	}

	/** Called when the a "hover" is detected - shows a simple toast message. */
	@Override
	public void onSensorClick(ClickSensor caller) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getBaseContext(), 
						"A click was recieved!",
						Toast.LENGTH_SHORT).show();
			} 
		});   
	}
 
	  
	/** Called when the activity is resumed. The gesture detector is initialized. */
	@Override
	public void onResume() {
		super.onResume();
		if(!mOpenCVInitiated)
			return; 
		mGestureSensor.start();
	}
	  
	/** Called when the activity is paused. The gesture detector is stopped
	 *  so that gestures are no longer recognized. */
	@Override
	public void onPause() {
		super.onPause();
		if(!mOpenCVInitiated)
			return; 
		mGestureSensor.stop();
	} 
    
	/** Upwards gesture detected. */
	@Override
	public void onGestureUp(CameraGestureSensor caller, long gestureLength) { 
		// No action performed 
	}

	 /** Downwards gesture detected. */
	@Override
	public void onGestureDown(CameraGestureSensor caller, long gestureLength) {  
		// No action performed 
	}
}
