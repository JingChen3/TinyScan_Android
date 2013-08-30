package com.appxy.tinyscan;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.extension.GPUImageWrapper;
import jp.co.cyberagent.android.gpuimage.extension.GPUImageWrapper.eBlurSize;
import com.appxy.tools.LibImgFun;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Activity_Process extends Activity{
	
	String path, newpath;
	int[] data;
	Context context;
	ImageViewTouch image;
	Bitmap bitmap, bitmap2;
	
	ImageView color, gray, bw, bw1, bw2, bw3, bw4, bw5, save, oldimageview;
	boolean isRunning = false;
	
	Thread mThread;
	RelativeLayout rl;
	
	boolean isShow = true;
	int oldid = 2;
	int[] mid = {R.drawable.grayscal5,R.drawable.grayscal4,R.drawable.grayscal3,R.drawable.grayscal2,R.drawable.grayscal1};
	int[] mid2 = {R.drawable.grayscal5_2,R.drawable.grayscal4_2,R.drawable.grayscal3_2,R.drawable.grayscal2_2,R.drawable.grayscal1_2};
	Matrix matrix;
	int mwidth, mheight;
	ProgressDialog progressDialog;
    boolean where;
    SharedPreferences preferences;
    int id;
    IntentFilter intentFilter;
    CloseReceiver closeReceiver ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.process_photo);
		context = this;
		closeReceiver = new CloseReceiver();
        intentFilter = new IntentFilter("com.tinyscan.CloseReceiver");
        registerReceiver(closeReceiver, intentFilter);
		preferences = getSharedPreferences("MyTinyScan", MODE_PRIVATE);
		Intent intent = this.getIntent();
		data = intent.getExtras().getIntArray("data");
		where = MyApplication.where;
		Timestamp time = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
		String time2 = sdf.format(time);
		newpath = getExternalCacheDir() +"/" + time2 + ".jpg";
		rl = (RelativeLayout)findViewById(R.id.bw_layout);
		
		
		image = (ImageViewTouch)findViewById(R.id.process_image);
		image.setDisplayType( DisplayType.FIT_IF_BIGGER );
		color = (ImageView)findViewById(R.id.process_color);
		color.setOnClickListener(mListener);
		
		save = (ImageView)findViewById(R.id.process_save);
		save.setOnClickListener(mListener);
		
		bw = (ImageView)findViewById(R.id.process_bw);
		bw.setOnClickListener(mListener);
		gray = (ImageView)findViewById(R.id.process_gray);
		gray.setOnClickListener(mListener);
		
        
		
		
		bw1 = (ImageView)rl.findViewById(R.id.bw1);
		bw2 = (ImageView)rl.findViewById(R.id.bw2);
		bw3 = (ImageView)rl.findViewById(R.id.bw3);
		bw4 = (ImageView)rl.findViewById(R.id.bw4);
		bw5 = (ImageView)rl.findViewById(R.id.bw5);
		bw1.setOnClickListener(mListener2);
		bw2.setOnClickListener(mListener2);
		bw3.setOnClickListener(mListener2);
		bw4.setOnClickListener(mListener2);
		bw5.setOnClickListener(mListener2);
		bw3.setImageResource(R.drawable.grayscal3_2);
		oldimageview = bw3;
		id = preferences.getInt("process", 1);
		if(id != 1){
			 bw.setImageResource(R.drawable.bw);
			 oldimageview.setImageResource(mid[oldid]);	
			 rl.setVisibility(8);
			 isShow = false;
		}
		
		getImage();
	}
	
	public class CloseReceiver extends BroadcastReceiver
	{ 
        @Override
        public void onReceive(Context context, Intent intent)
        {
        	finish();
        }
    }
	
	OnClickListener mListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch(v.getId()){
			case R.id.process_color:
                if(isRunning){
					
				}else{
					    bw.setImageResource(R.drawable.bw);
					    oldimageview.setImageResource(mid[oldid]);	
					    rl.setVisibility(8);
					    isShow = false;
					    progressDialog=ProgressDialog.show(context,"Please wait ","Processing...");
	                	mThread = new Thread(new Runnable(){
	    					

	    					@Override
	    					public void run() {
	    						// TODO Auto-generated method stub
	    						isRunning = true;
	    						bitmap =  BitmapFactory.decodeFile(newpath);
	    						resize();
	    						Message m = new Message();
	    						m.what = 1;
	    						handler.sendMessage(m);
	    					}
	    					
	    				});
	                	mThread.start();
					
				}
				break;
			case R.id.process_bw:
                if(isRunning){
					
				}else{
				if(isShow){
					rl.setVisibility(8);
					bw.setImageResource(R.drawable.bw);
					isShow = false;
				}else{
					rl.setVisibility(0);
					bw.setImageResource(R.drawable.bw2);
					isShow = true;
					oldimageview.setImageResource(mid[oldid]);
					bw3.setImageResource(mid2[2]);
					oldimageview = bw3;
					oldid = 2;
					bw(eBlurSize.BLURSIZE_BASE);
				}
				
				}
				break;
			case R.id.process_gray:
				if(isRunning){
					
				}else{
					    bw.setImageResource(R.drawable.bw);
					    oldimageview.setImageResource(mid[oldid]);
					    rl.setVisibility(8);
					    isShow = false;
					    progressDialog=ProgressDialog.show(context,"Please wait ","Processing...");
	                	mThread = new Thread(new Runnable(){
	    					

	    					@Override
	    					public void run() {
	    						// TODO Auto-generated method stub
	    						isRunning = true;
	    						Bitmap src = BitmapFactory.decodeFile(newpath);
	    						bitmap = GPUImageWrapper.processGrayscale(context, src);
	    						resize();
	    						src.recycle();
	    						Message m = new Message();
	    						m.what = 1;
	    						handler.sendMessage(m);
	    					}
	    					
	    				});
	                	mThread.start();
					
				}

                
				
				break;
			case R.id.process_save:
                if(isRunning){
					
				}else{
					MyApplication.savebitmap = bitmap;
					MyApplication.tempbitmap = bitmap2;
				if(MyApplication.where){
					

					progressDialog=ProgressDialog.show(context,null,"Saving...");
					mThread = new Thread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							
							String photo_path = MyApplication.addpath;
							File mfile = new File(photo_path);
							String[] name3 = mfile.list();	
							String pattern = "[0-9]{18}.jpg";
							List<String> namelist = new ArrayList<String>();
							for(int i=0; i<name3.length; i++){
								if(name3[i].matches(pattern)){
									namelist.add(name3[i]);
								}
							}
							if(namelist.size()>0){
							Collections.sort(namelist,comparator3);
							int id2 = Integer.parseInt(namelist.get(namelist.size()-1).substring(15, 18)) + 1;
							
							String name = namelist.get(0).substring(0,14);
							String name2 = "";
								
								
							if(id2<10){
								 name2 = name.substring(0, 14)+MyApplication.sizeid+"00"+id2+".jpg";
							}else if(id2<100){
								 name2 = name.substring(0, 14)+MyApplication.sizeid+"0"+id2+".jpg";
							}else{
								 name2 = name.substring(0, 14)+MyApplication.sizeid+id2+".jpg";
							}
							
							String newFile_path = photo_path+"/" + name2;
							File newfile = new File(newFile_path);
							 OutputStream out = null;
				     			try {
									out = new BufferedOutputStream(new FileOutputStream(newfile));
									MyApplication.savebitmap.compress(CompressFormat.JPEG, 85, out);	
					     		    out.flush();
					     		    out.close();
					     		    Thread.sleep(1000);
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							   
							    Message m = new Message();   
						    	m.what = 2;   
						    	handler.sendMessage(m);
							   
							}
						}	
					});
					mThread.start();
				
					
				}else{
					Intent intent = new Intent(context, Activity_SavePhoto.class);
					startActivity(intent);
				}
				
				
				}
				break;
			default:
				break;	
			}
			
		}
		
	};
	
	Comparator<String> comparator3 = new Comparator<String>(){
		   public int compare(String s1, String s2) {
			    
				  return s1.substring(s1.length()-7, s1.length()-4).compareTo(s2.substring(s2.length()-7, s2.length()-4));
			  
		   }
     };
	
	OnClickListener mListener2 = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			switch(arg0.getId()){
			case R.id.bw1:
				if(isRunning){
					
				}else{
				oldimageview.setImageResource(mid[oldid]);
				bw1.setImageResource(mid2[0]);
				oldimageview = bw1;
				oldid = 0;
				bw(eBlurSize.BLURSIZE_2);
				}
				break;
			case R.id.bw2:
                if(isRunning){
					
				}else{
					oldimageview.setImageResource(mid[oldid]);
					bw2.setImageResource(mid2[1]);
					oldimageview = bw2;
				oldid = 1;
				bw(eBlurSize.BLURSIZE_1);
				}
				break;
			case R.id.bw3:
                if(isRunning){
					
				}else{
					oldimageview.setImageResource(mid[oldid]);
					bw3.setImageResource(mid2[2]);
					oldimageview = bw3;
				oldid = 2;
				bw(eBlurSize.BLURSIZE_BASE);
				}
				break;
			case R.id.bw4:
                if(isRunning){
					
				}else{
					oldimageview.setImageResource(mid[oldid]);
					bw4.setImageResource(mid2[3]);
					oldimageview = bw4;
				oldid = 3;
				bw(eBlurSize.BLURSIZE_1M);
				}
				break;
			case R.id.bw5:
                if(isRunning){
					
				}else{
					oldimageview.setImageResource(mid[oldid]);
					bw5.setImageResource(mid2[4]);
					oldimageview = bw5;
				oldid = 4;
				bw(eBlurSize.BLURSIZE_2M);
				}
				break;
			default:
				break;
			}
		}
		
	};
	
	
	public void bw(final eBlurSize e){
		
		progressDialog=ProgressDialog.show(context,"Please wait ","Processing...");
				mThread = new Thread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						isRunning = true;
						Bitmap src =  BitmapFactory.decodeFile(newpath);
						bitmap = GPUImageWrapper.processAdaptiveThreshold(context, e, src);
						resize();
						src.recycle();
						Message m = new Message();
						m.what = 1;
						handler.sendMessage(m);
					}
					
				});
				mThread.start();
			
		
	}
	
	public void getImage(){
		if(isRunning){
			
		}else{
			
			
			progressDialog=ProgressDialog.show(context,"Please wait ","Processing...");
			mThread = new Thread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
				    isRunning = true;
					saveImage();
					LibImgFun.Transfer(path,data, newpath, MyApplication.degree);
					bitmap =  BitmapFactory.decodeFile(newpath);
					mwidth = image.getMeasuredWidth()-24;
					mheight = image.getMeasuredHeight();
					
					if(bitmap.getWidth() >= bitmap.getHeight()){
						float scale = (mwidth+0.0f)/bitmap.getWidth();
						if(bitmap.getHeight()*scale > mheight){
							scale = (mheight+0.0f)/bitmap.getHeight();
						}
						matrix = new Matrix();
						matrix.postScale(scale, scale);
						
					}else{
						float scale = (mheight+0.0f)/bitmap.getHeight();
						if(bitmap.getWidth()*scale > mwidth){
							scale = (mwidth+0.0f)/bitmap.getWidth();
						}
						matrix = new Matrix();
						matrix.postScale(scale, scale);
					}
					
					Bitmap src =  BitmapFactory.decodeFile(newpath);
					if(id == 0){
						
					}else if(id == 1){
						bitmap = GPUImageWrapper.processAdaptiveThreshold(context, eBlurSize.BLURSIZE_BASE, src);
					}else{
						
						bitmap = GPUImageWrapper.processGrayscale(context, src);
					}
					
					resize();
					src.recycle();
					Message m = new Message();
					m.what = 0;
					handler.sendMessage(m);
				}
				
			});
			mThread.start();
		}
		
	}
	
	
	Handler handler = new Handler(){   
		public void handleMessage(Message msg) {   
		switch (msg.what) {   
		
		case 0:
			
			progressDialog.dismiss();
			isRunning = false;
			mThread = null;
			
			image.setImageBitmap(bitmap2);
		
			break;
		case 1:
			progressDialog.dismiss();
			isRunning = false;
			mThread = null;
			image.setImageBitmap(bitmap2);
			break;
		case 2:
			progressDialog.dismiss();
			isRunning = false;
			mThread = null;
		
			MyApplication.isUpdate = true;
			MyApplication.isAdd = true;
			Intent intent = new Intent(context, Activity_EditPhoto.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
 			startActivity(intent);
 			
 			break;
	    default:
			break;
		}   
		super.handleMessage(msg);   
	}   
	};  
	
	public void resize(){
		bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			mThread = null;
			if(bitmap !=null && !bitmap.isRecycled()){
				bitmap.recycle();
			}
			bitmap = null;
			if(bitmap2 !=null && !bitmap2.isRecycled()){
				bitmap2.recycle();
			}
			bitmap2 = null;
			
			finish();
			return true;
	    }
		return super.onKeyDown(keyCode, event);
	}


	
	public void saveImage(){

	    Timestamp time = new Timestamp(System.currentTimeMillis());
  		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
  		String time2 = sdf.format(time);
  		File caheDirectory = getExternalCacheDir();	      			 
  		File tempFile = null;
		try {
			
			tempFile = File.createTempFile(time2, ".temp", caheDirectory);
			OutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile));	
			
			MyApplication.nowbitmap.compress(CompressFormat.JPEG, 85 , out);
     		out.flush();
     		out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		path = tempFile.getPath();
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		super.onDestroy();
		unregisterReceiver(closeReceiver);
		if(bitmap !=null && !bitmap.isRecycled()){
			bitmap.recycle();
		}
		bitmap = null;
		
		if(bitmap2 !=null && !bitmap2.isRecycled()){
			bitmap2.recycle();
		}
		bitmap2 = null;
		
	}
	

}
