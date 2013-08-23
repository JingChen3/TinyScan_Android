/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appxy.tinyscan;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.appxy.tinyscan.Activity_Detect.CloseReceiver;

public class Activity_CameraPreview extends Activity {
	
	private Context context;	
	private Preview mPreview;
	private Camera mCamera;  
	private FrameLayout fl;
	
	
	private ImageView takepicture, single, photos, light;
	private ProgressBar pb;
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private boolean isSingle;
	String path;
	public static int camerawidth, cameraheight;
	int islight = 1;
	CloseReceiver closeReceiver;
	IntentFilter intentFilter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.preview);
        context = this;
        closeReceiver = new CloseReceiver();
        intentFilter = new IntentFilter("com.tinyscan.CloseReceiver");
        registerReceiver(closeReceiver, intentFilter);
        preferences = getSharedPreferences("MyTinyScan", MODE_PRIVATE);	
        editor = preferences.edit();
        isSingle = preferences.getBoolean("single", true);
        fl = (FrameLayout)findViewById(R.id.previewlayout);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        camerawidth = metrics.heightPixels;
        cameraheight = metrics.widthPixels-100;
        mPreview = new Preview(this);
        fl.addView(mPreview);
        mPreview.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (mCamera != null) { 
		            // 自动对焦 
		        	mCamera.autoFocus(new AutoFocusCallback() { 
		                @Override 
		                public void onAutoFocus(boolean success, Camera camera) { 
		                
		                } 
		            }); 
		        } 
			}
        	
        });
        pb = (ProgressBar)findViewById(R.id.takephoto_progress);
     
        photos = (ImageView)findViewById(R.id.photos);
        photos.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		        photoPickerIntent.setType("image/*");
		        startActivityForResult(photoPickerIntent, 1);

			}
        	
        });
        light = (ImageView)findViewById(R.id.light);
        light.setOnClickListener(mlistener);  
        single = (ImageView)findViewById(R.id.single);
        if(isSingle){
        	single.setImageResource(R.drawable.single_selector);
        	photos.setVisibility(0);
        }else{
        	single.setImageResource(R.drawable.nosingle_selector);
        	photos.setVisibility(4);
        }
        single.setOnClickListener(mlistener);     
        takepicture = (ImageView)findViewById(R.id.takepicture);
        takepicture.setOnClickListener(mlistener);  	
    	 
    }
  
    public class CloseReceiver extends BroadcastReceiver
	{ 
        @Override
        public void onReceive(Context context, Intent intent)
        {
        	finish();
        }
    }
   
    
    Handler handler = new Handler(){   
		public void handleMessage(Message msg) {   
		switch (msg.what) {   
		case 1:   
		
		     break;
		case 0:
			finish();
			pb.setVisibility(4);
			Toast.makeText(context, "Save Success !!", Toast.LENGTH_SHORT).show();
			
			break;
		}   
		super.handleMessage(msg);   
	}   
	};  
    
    
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                	
                
                  MyApplication.nowbitmap = decodeFile(context, data.getData());
                   //Bitmap bit2 = null;
                   if(MyApplication.nowbitmap.getWidth() > MyApplication.nowbitmap.getHeight()){
                	   Matrix matrix = new Matrix();
                	   matrix.postRotate(90);
                	   MyApplication.nowbitmap = Bitmap.createBitmap(MyApplication.nowbitmap, 0,0,MyApplication.nowbitmap.getWidth(), MyApplication.nowbitmap.getHeight(), matrix, true);
                   }
                  // saveImage();
                   try {
					 Thread.sleep(300);
				   } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					 e.printStackTrace();
				   }
         		   Intent i = new Intent(context,Activity_Detect.class);             		 
          		   startActivity(i);  
          			
      			   
             	 }
                
               
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    public static Bitmap decodeFile(Context context, Uri data){
        //Decode image size

    	ContentResolver resolver = context.getContentResolver();
    	byte[] mContent = null; 
    	try {
			mContent=readStream(resolver.openInputStream(Uri.parse(data.toString())));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        return getPicFromBytes(mContent); 
            
    }
	
    public static byte[] readStream(InputStream inStream) throws Exception { 
        byte[] buffer = new byte[1024]; 
        int len = -1; 
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(); 
        while ((len = inStream.read(buffer)) != -1) { 
                 outStream.write(buffer, 0, len); 
        } 
        byte[] data = outStream.toByteArray(); 
        outStream.close(); 
        inStream.close(); 
        return data; 
    }
    
    public static Bitmap getPicFromBytes(byte[] bytes) { 
        if (bytes != null) 
        {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
//          BitmapFactory.decodeFile(fileName, o);
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, o);
            //The new size we want to scale to
            final int REQUIRED_SIZE=2048;

            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            if(o.outWidth >=o.outHeight){
            	if(o.outWidth>=REQUIRED_SIZE){
            		scale = Math.round((float)o.outWidth/(float)REQUIRED_SIZE);
            	}
            }else{
            	if(o.outHeight>=REQUIRED_SIZE){
            		scale = Math.round((float)o.outHeight/(float)REQUIRED_SIZE);
            	}
            }
                  
            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
           
            Log.i("GPUImage", "scale = " + scale);
//          return BitmapFactory.decodeFile(fileName, o2);
//            return BitmapFactory.decodeResource(imageView.getContext().getResources(), R.drawable.original, o2);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, o2); 
        }
        return null;
    }	
	
	
	
    
    OnClickListener mlistener = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId()){
			case R.id.takepicture:
				takePicture();
				break;
			case R.id.single:
				if(isSingle){
					single.setImageResource(R.drawable.nosingle_selector);
					editor.putBoolean("single", false);
					isSingle = false;
					photos.setVisibility(4);
				}else{
					single.setImageResource(R.drawable.single_selector);
					editor.putBoolean("single", true);
					isSingle = true;
					photos.setVisibility(0);
				}
				break;
			case R.id.light:
				if(islight == 1){
					light.setImageResource(R.drawable.flash_selector);
					Parameters p = mCamera.getParameters();
					p.setFlashMode(Parameters.FLASH_MODE_ON);
					mCamera.setParameters(p);
					islight = 2;
				}else if(islight == 2){
					light.setImageResource(R.drawable.flash_selector2);
					Parameters p = mCamera.getParameters();
					p.setFlashMode(Parameters.FLASH_MODE_OFF);
					mCamera.setParameters(p);
					islight = 3;
				}else if(islight == 3){
					light.setImageResource(R.drawable.flash_selector3);
					Parameters p = mCamera.getParameters();
					p.setFlashMode(Parameters.FLASH_MODE_AUTO);
					mCamera.setParameters(p);
					
					islight = 1;
				}
				break;
			default:
				break;
			}
		}
    	
    };
    
    public void takePicture() { 
        // Log.e(TAG, "==takePicture=="); 
        if (mCamera != null) { 
            // 自动对焦 
        	mCamera.autoFocus(new AutoFocusCallback() { 
                @Override 
                public void onAutoFocus(boolean success, Camera camera) { 
                   
                  if (success) { 
                	
                    	//camera.takePicture( shutterCallback,rawCallback,jpegCallback);
                    	camera.takePicture( null,null,jpegCallback);
                   } 
                } 
            }); 
        } 
    } 
    
    
    ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			// Log.d(TAG, "onShutter'd");
		}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			// Log.d(TAG, "onPictureTaken - raw");
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
		    //camera.setOneShotPreviewCallback(null);
			
		 
		   MyApplication.nowbitmap  = getPicFromBytes(data);
		   Log.e("sad",MyApplication.nowbitmap.getWidth()+"  "+MyApplication.nowbitmap.getHeight());
		  
           if(MyApplication.nowbitmap .getWidth() > MyApplication.nowbitmap .getHeight()){
        	   Matrix matrix = new Matrix();
        	   matrix.postRotate(90);
        	   MyApplication.nowbitmap = Bitmap.createBitmap(MyApplication.nowbitmap , 0,0,MyApplication.nowbitmap .getWidth(), MyApplication.nowbitmap .getHeight(), matrix, true);
           }
		   Intent i = new Intent(context,Activity_Detect.class);  
		  
  		   startActivity(i); 
		   
   		   //camera.startPreview();
   	    }
	};

    @Override
    protected void onResume() {
        super.onResume();

       
        mCamera = Camera.open();       
        mPreview.setCamera(mCamera);
        if(islight == 1){
        	Parameters p = mCamera.getParameters();
			p.setFlashMode(Parameters.FLASH_MODE_AUTO);
			mCamera.setParameters(p);
			
			
		}else if(islight == 2){
			Parameters p = mCamera.getParameters();
			p.setFlashMode(Parameters.FLASH_MODE_ON);
			mCamera.setParameters(p);
			
			
		}else if(islight == 3){
			Parameters p = mCamera.getParameters();
			p.setFlashMode(Parameters.FLASH_MODE_OFF);
			mCamera.setParameters(p);
		}
        File[] mFile = getExternalCacheDir().listFiles();
		for(int i=0; i<mFile.length; i++){
			
			while(!mFile[i].delete()){
				Log.e("File", "delete failue");
			}
			
			Log.e("File", "delete success");
		}
		
        
    }

    @Override
    protected void onPause() {
        super.onPause();

       
       if (mCamera != null) {
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
  
    }


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.e("preview", "destory");
		super.onDestroy();
		unregisterReceiver(closeReceiver);
	}
    
    

	

	


}


class Preview extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = "Preview";
    int width = 0,  height = 0;
    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize, mPictureSize;
    List<Size> mSupportedPreviewSizes;
    List<Size> mSupportedPictureSizes;
    List<Size> mSize;
    int memorysize;
	int photosize;
    Camera mCamera;

    Preview(Context context) {
        super(context);

        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		
		memorysize = activityManager.getLargeMemoryClass();
        //memorysize = activityManager.getMemoryClass();
		Log.e("sad", memorysize+"");
		photosize = memorysize*300000/8;
		if(photosize<=9000000){
			
		}else{
			photosize = 9000000;
		}
	    Log.e("sad", photosize+"");
        mSize = new ArrayList<Size>();
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedPictureSizes = mCamera.getParameters().getSupportedPictureSizes();
            
            for(int i=0; i<mSupportedPictureSizes.size(); i++){
            	
            	float s = mSupportedPictureSizes.get(i).width/mSupportedPictureSizes.get(i).height;
            	int s2 = mSupportedPictureSizes.get(i).width * mSupportedPictureSizes.get(i).height;
            	
            	if( s == 4/3 && s2<= photosize && mSupportedPictureSizes.get(i).width<=4096 && mSupportedPictureSizes.get(i).height<=4096){
            		
            		mSize.add(mSupportedPictureSizes.get(i));
            	}
            }
            for(int i=0; i<mSize.size();i++){
            	Log.e("sad2",mSize.get(i).width+"  "+mSize.get(i).height);
            }
            Collections.sort(mSize,comparator);
            mPictureSize = mSize.get(mSize.size()-1);
            requestLayout();
        }
    }

    
    Comparator<Size> comparator = new Comparator<Size>(){
		   public int compare(Size s1, Size s2) {
			    
				  return s1.width*s1.height - s2.width*s2.height;
			  
		   }
 };
 
    public void switchCamera(Camera camera) {
       setCamera(camera);
       try {
           camera.setPreviewDisplay(mHolder);
       } catch (IOException exception) {
           Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
       }
       Camera.Parameters parameters = camera.getParameters();
       parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
       requestLayout();

       camera.setParameters(parameters);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
       // final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
       // final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
    	
    	//final int width = Activity_CameraPreview.cameraheight;
        final int height = Activity_CameraPreview.camerawidth;
        final int width = Activity_CameraPreview.camerawidth*mPictureSize.width/mPictureSize.height;
        Log.e("sad",width+" " +height);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
           
        }    
       
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
               previewWidth = mPreviewSize.width;
               previewHeight = mPreviewSize.height;
            	
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            	 
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            	 
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }


    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
    	if(mCamera != null){
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
       
      
        parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
        Log.e("sad", mPreviewSize.width+"  "+mPreviewSize.height);
        Log.e("sad", mPictureSize.width+"  "+mPictureSize.height);
        requestLayout();
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    	}
    }
    
    
   


}
