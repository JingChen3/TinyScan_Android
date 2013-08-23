package com.appxy.tinyscan;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import com.appxy.tools.BitmapTools;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Activity_AddPhoto extends Activity{
	
	private Preview preview;	
	private Camera camera;
	
	private Context ctx;
	private ImageView takepicture, save;
	private String photo_path;
	ProgressBar pb;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.preview);
		
		Intent intent = this.getIntent();
		photo_path = intent.getExtras().getString("photo_path");
		pb = (ProgressBar)findViewById(R.id.takephoto_progress);
		
		preview = new Preview(this);
	        
		//((FrameLayout) findViewById(R.id.preview)).addView(preview);
	
		takepicture = (ImageView)findViewById(R.id.takepicture);
		
		save = (ImageView)findViewById(R.id.savetake);
		
		
		save.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			
         		
				File[] mFile = getExternalCacheDir().listFiles();
				if(mFile.length <1){
					return;
				}
				pb.setVisibility(0);
				takepicture.setEnabled(false);
				new Thread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						File[] mFile = getExternalCacheDir().listFiles();
						File mfile = new File(photo_path);
						String[] name3 = mfile.list();	
						BitmapTools.sort(name3);
						
						int id2 = Integer.parseInt(name3[name3.length-1].substring(8, 11)) + 1;
						
						for(int i=0; i<mFile.length; i++){
							String name = mFile[i].getName();
							String name2 = "";
							
							
							if(id2<10){
							   name2 = name.substring(0, 8)+"00"+id2+".jpg";
							}else if(id2<100){
							   name2 = name.substring(0, 8)+"0"+id2+".jpg";
							}else{
							   name2 = name.substring(0, 8)+"0"+id2+".jpg";
							}
							id2++;
							String newFile_path = photo_path+"/" + name2;
							InputStream tempFile;
							try {
								tempFile = new FileInputStream(mFile[i]);	
								File file = new File(newFile_path);
								if(file.exists()){
								}else{
									file.createNewFile();
								}
				                OutputStream newFile = new FileOutputStream(newFile_path); 
				                byte bt[] = new byte[1024]; 
				                int  c; 
				                while((c = tempFile.read(bt)) > 0)  
				                { 
				            	   newFile.write(bt, 0, c); 
				                } 
				                tempFile.close();
				                newFile.close();
							    } catch (FileNotFoundException e) {
								   // TODO Auto-generated catch block
								   e.printStackTrace();
							    } catch (IOException e) {
								   // TODO Auto-generated catch block
								   e.printStackTrace();
							    } 
							
							    Activity_EditPhoto.mlist.add(newFile_path);
							  
						    }
						
						Message message = new Message();   
				    	message.what = 0;   
				    	handler.sendMessage(message);
					   
					}
					
				}).start();
			}
			
		});
		
		takepicture.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				
					
					Camera.Parameters params = camera.getParameters();

		    		List<String> focusModes = params.getSupportedFocusModes();
		    		
		    		if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
		    			takePicture();
		    		}else{
		    			camera.takePicture( shutterCallback,rawCallback,jpegCallback);
		    			
		    		}
				
				
			}
			
		});
		
		
	}
	
	
	Handler handler = new Handler(){   
		public void handleMessage(Message msg) {   
		switch (msg.what) {   
		case 1:   
		
		     break;
		case 0:
			pb.setVisibility(4);
			Toast.makeText(ctx, "Save Success !!", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(Activity_AddPhoto.this, Activity_EditPhoto.class);
			setResult(2, intent);
			finish();

			break;
		}   
		super.handleMessage(msg);   
	}   
	};  
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		camera = getCameraInstance();
		camera.startPreview();
		preview.setCamera(camera);
	}
	
	
	@Override
	protected void onPause() {
		if(camera != null) {
			camera.stopPreview();
			preview.setCamera(null);
			camera.release();
			camera = null;
		}
		File[] mFile = getExternalCacheDir().listFiles();
		for(int i=0; i<mFile.length; i++){
			
			while(!mFile[i].delete()){
				Log.e("File", "delete failue");
			}
			
			Log.e("File", "delete success");
		}
		super.onPause();
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
		    camera.setOneShotPreviewCallback(null);
           if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
				
			}else{
				Toast.makeText(ctx, "SD card not ready",Toast.LENGTH_SHORT).show();
				return;
		   }
           
	       saveTempPhoto(data);
	       camera.startPreview();
		}
	};
	
	   public void saveTempPhoto(byte[] data){
		   
		   Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);	
		   Timestamp time = new Timestamp(System.currentTimeMillis());
		   SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
		   String time2 = sdf.format(time);
		   try {
			File caheDirectory = getExternalCacheDir();
			File tempFile = File.createTempFile(time2, ".jpg", caheDirectory);
			OutputStream out = null;
			out = new BufferedOutputStream(new FileOutputStream(tempFile));
			Matrix matrix = new Matrix();
	        matrix.setRotate(90);
	    
	        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),bitmap.getHeight(), matrix, true);
	     
	        bm.compress(CompressFormat.JPEG, 100 , out);	
			bm.recycle();
			bitmap.recycle();
			out.flush();
			out.close();
		   } catch (IOException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		   } catch(Exception e){
			   Toast.makeText(ctx, "oh no", Toast.LENGTH_SHORT).show();
			   return;
		   }
		   
	    }
	   
	  /*public final class AutoFocusCallback implements android.hardware.Camera.AutoFocusCallback {
	         public void onAutoFocus(boolean focused, Camera camera) {
		       
		          if (focused) {
		            
		           
		          }
	        }
	  };*/
	  
	  
	  public void takePicture() { 
	        // Log.e(TAG, "==takePicture=="); 
	        if (camera != null) { 
	            // ×Ô¶¯¶Ô½¹ 
	            camera.autoFocus(new AutoFocusCallback() { 
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
	    
	  
	  public static Camera getCameraInstance(){
		    Camera c = null;
		    try {
		        c = Camera.open(); // attempt to get a Camera instance
		    }
		    catch (Exception e){
		        // Camera is not available (in use or does not exist)
		    }
		    return c; // returns null if camera is unavailable
		}
	  
	  
	  class Preview extends ViewGroup implements SurfaceHolder.Callback {
		    private final String TAG = "Preview";
		    int width = 0,  height = 0;
		    SurfaceView mSurfaceView;
		    SurfaceHolder mHolder;
		    Size mPreviewSize;
		    List<Size> mSupportedPreviewSizes;
		    List<Size> mSupportedPictureSizes;
		    Camera mCamera;

		    Preview(Context context) {
		        super(context);

		        mSurfaceView = new SurfaceView(context);
		        addView(mSurfaceView);

		     
		        mHolder = mSurfaceView.getHolder();
		        mHolder.addCallback(this);
		        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		    }

		    public void setCamera(Camera camera) {
		        mCamera = camera;
		        if (mCamera != null) {
		            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
		            mSupportedPictureSizes = mCamera.getParameters().getSupportedPictureSizes();
		            requestLayout();
		        }
		    }

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
		        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
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
		        Camera.Parameters parameters = mCamera.getParameters();
		        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
		        if(mSupportedPictureSizes != null){
		        	for(int i=0; i<mSupportedPictureSizes.size(); i++){
		        		int mWidth = mSupportedPictureSizes.get(i).width;
		        		int mHeight = mSupportedPictureSizes.get(i).height;
		        		
		        		if(mWidth <= mPreviewSize.width && mWidth>=width){
		        			width = mWidth;
		        			height = mHeight;
		        		}
		        		
		        	}
		        	
		        }
		        requestLayout();
		        parameters.setPictureSize(width, height);
		        mCamera.setParameters(parameters);
		        mCamera.startPreview();
		    }
	  }

}
