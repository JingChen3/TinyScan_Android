package com.appxy.tinyscan;



import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import jp.co.cyberagent.android.gpuimage.extension.GPUImageWrapper;

import com.appxy.tinyscan.Activity_CameraPreview.CloseReceiver;
import com.appxy.tools.BitmapTools;
import com.appxy.tools.CropImageView3;
import com.appxy.tools.LibImgFun;
import com.appxy.tools.PageSizeAdapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class Activity_Detect extends Activity{
	
	Point left, top, right, bottom;
	int[] srcData, data;
	String path;
	
	
	Context context;
	CropImageView3 mCropImage;
	LinearLayout ll;
	ImageView full, cancel, save, rotate;
	boolean isfull = false;
	int degree = 0;
	PopupWindow popupWindow;
	TextView sizeText;
	ImageView sizeImage;
	boolean isRunning = false;
	Thread mThread;
	float scale1 = 1f;
	Bitmap bitmap;
	ProgressDialog progressDialog;
	ArrayList<HashMap<String,Object>> mlist;
	HashMap<String, Object> hm;
	int[] sizes;
	String[] sizes2;
	SharedPreferences preferences;
	CloseReceiver closeReceiver;
	IntentFilter intentFilter;
	int id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cropimage);
		context = this;
		closeReceiver = new CloseReceiver();
        intentFilter = new IntentFilter("com.tinyscan.CloseReceiver");
        registerReceiver(closeReceiver, intentFilter);
		mlist = new ArrayList<HashMap<String,Object>>();
		preferences = getSharedPreferences("MyTinyScan", MODE_PRIVATE);
		id = preferences.getInt("pagesize", 0);
		MyApplication.sizeid = id;
		sizes = new int[]{R.drawable.size_letter, R.drawable.size_a4, R.drawable.size_legal, R.drawable.size_a3, R.drawable.size_a5, R.drawable.size_business};
		sizes2 = new String[]{"Letter", "A4", "Legal", "A3", "A5", "Card"};
		for(int i =0; i<6; i++){
			hm = new HashMap<String, Object>();
		    hm.put("image", sizes[i]);
		    hm.put("size", sizes2[i]);
		    mlist.add(hm);
		}
		
		data = new int[8];
        
		ll = (LinearLayout)findViewById(R.id.detect_image_layout);
		
		sizeText = (TextView)findViewById(R.id.detect_sizeText);
		sizeText.setText(sizes2[id]);
		sizeImage = (ImageView)findViewById(R.id.detect_sizeImage);
		sizeImage.setOnClickListener(mListener);
		full = (ImageView)findViewById(R.id.detect_full);
		full.setOnClickListener(mListener);
		cancel = (ImageView)findViewById(R.id.detect_cancel);
		cancel.setOnClickListener(mListener);
		rotate = (ImageView)findViewById(R.id.detect_rotate);
		rotate.setOnClickListener(mListener);
		save = (ImageView)findViewById(R.id.detect_save);
		save.setOnClickListener(mListener);
       
		getCropImage();
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
			if(isRunning){
				
			}else{
				switch(v.getId()){
				case R.id.detect_full:
					if(isfull){
						full.setImageResource(R.drawable.full_selector);
						mCropImage.initPoint2();
						isfull = false;
					}else{
						full.setImageResource(R.drawable.fit_selector);
						mCropImage.initPoint();
						isfull = true;
					}
					
					break;
				case R.id.detect_rotate:
					degree = degree%360;
					float s = (ll.getMeasuredWidth()-0.0f)/bitmap.getHeight();
					if(degree == 0 || degree == 180){
						
						
						ValueAnimator anim = ObjectAnimator.ofFloat(mCropImage, "rotation", degree,degree+90);
						ValueAnimator anim2 = ObjectAnimator.ofFloat(mCropImage, "scaleX", 1f,s);
						ValueAnimator anim3 = ObjectAnimator.ofFloat(mCropImage, "scaleY", 1f,s);
						AnimatorSet animatorSet = new AnimatorSet();  
						animatorSet.play(anim).with(anim2).with(anim3);
						animatorSet.start(); 
					}else{
						
						
						ValueAnimator anim = ObjectAnimator.ofFloat(mCropImage, "rotation", degree,degree+90);
						ValueAnimator anim2 = ObjectAnimator.ofFloat(mCropImage, "scaleX", s,1f);
						ValueAnimator anim3 = ObjectAnimator.ofFloat(mCropImage, "scaleY", s,1f);
						AnimatorSet animatorSet = new AnimatorSet();  
						animatorSet.play(anim).with(anim2).with(anim3);
						animatorSet.start(); 
						
					}
					
					
					degree+=90;
					break;
				case R.id.detect_cancel:
					finish();
					break;
				case R.id.detect_save:
						      				
					 
	         			
					int[] newData = mCropImage.getPoint();
					for(int i =0; i<newData.length; i++){
						newData[i] = (int) (newData[i]/scale1);
					}
					Intent i = new Intent(context, Activity_Process.class);
					i.putExtra("path", path);
					i.putExtra("data", newData);
					MyApplication.degree = degree%360;
					startActivity(i);
					break;
				case R.id.detect_sizeImage:
					initPopuptWindow();
					
					popupWindow.showAtLocation(v,Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
					break;
				
				default:
					break;
				}
			}
			
		}
		
	};
	
	
	
	
	public double isRect(Point p1, Point p2, Point p3) 
	{
	    int x1,y1,x2,y2,x4,y4; 
	    x1 = p1.x;
	    y1 = p1.y;
	    x2 = p2.x;
	    y2 = p2.y;
	    x4 = p3.x;
	    y4 = p3.y;
	   
	    int ma_x = x1 - x2;
	    int ma_y = y1 - y2;
	    int mb_x = x4 - x2;
	    int mb_y = y4 - y2;
	    int v1 = (ma_x * mb_x) + (ma_y * mb_y);
	    double ma_val = Math.sqrt(ma_x*ma_x + ma_y*ma_y);
	    double mb_val = Math.sqrt(mb_x*mb_x + mb_y*mb_y);
	    double cosM = v1 / (ma_val*mb_val);
	    double angleAMB = Math.acos(cosM) * 180 / Math.PI;
	   
	    return angleAMB;
	}





	public void getCropImage(){
		
		if(isRunning){
			
		}else{
			progressDialog=ProgressDialog.show(context,"Please wait ","Loading image and then detect borders...");
			
			mThread = new Thread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					isRunning = true;
					
					
					try {
						
						Timestamp time = new Timestamp(System.currentTimeMillis());
		          		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
		          		String time2 = sdf.format(time);
		          		File caheDirectory = getExternalCacheDir();
						File tempFile = File.createTempFile( time2, ".temp", caheDirectory);
					    OutputStream out = null;
	         			out = new BufferedOutputStream(new FileOutputStream(tempFile));			
	         			Bitmap bm = BitmapTools.resizeImage(MyApplication.nowbitmap);
	         			
	         			Bitmap bm2 = GPUImageWrapper.processRGBClosing(context, 4, bm);
	         		    Bitmap bm3 = GPUImageWrapper.processSharpen(context, 4.0f, bm2);
	         		   
	         		    Bitmap bm4 = GPUImageWrapper.processCannyEdgeDetection(context, bm3);
	         		    bm4.compress(CompressFormat.JPEG, 85, out);	
	         		    out.flush();
	         		    out.close();
	         		    out = null;
	         		    bm.recycle();
	         		    bm2.recycle();
	         		    bm3.recycle();	         		 
	         		    bm4.recycle();
	         		    bm =null;
	         		    bm2 =null;
	         		    bm3 =null;
	         		    bm4 =null;
                        Thread.sleep(2000);
                       
	         		    String path = tempFile.getPath();
	         		    srcData = LibImgFun.ImgFunInt(path);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}catch(Exception e){
						e.printStackTrace();
						
					}
					
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
			isRunning =false;
			
			float scale = MyApplication.nowbitmap.getWidth()/320.0f;
			
			
			
            if(srcData[0]==0 && srcData[1]==0 && srcData[2]==0 && srcData[3]==0 && srcData[4]==0 && srcData[5]==0 && srcData[6]==0 && srcData[7]==0){
				data[0] = 0;
				data[1] = 0;
				data[2] = MyApplication.nowbitmap.getWidth();
				data[3] = 0;
				data[4] = MyApplication.nowbitmap.getWidth();
				data[5] = MyApplication.nowbitmap.getHeight();
				data[6] = 0;
				data[7] = MyApplication.nowbitmap.getHeight();
			}else{
				for(int i =0; i<8; i++){
					data[i] = (int) (srcData[i] * scale);
					
				}
			}
            for(int i =0; i<8; i++){
			
				 Log.e("sad", data[i]+" ");
			}
           
			left = new Point(data[0], data[1]);
			top = new Point(data[2], data[3]);
			right = new Point(data[4], data[5]);
			bottom = new Point(data[6], data[7]);
			
			if(MyApplication.nowbitmap.getHeight() >ll.getMeasuredHeight() || MyApplication.nowbitmap.getWidth() >ll.getMeasuredWidth()){
				
				
				scale1 = (ll.getMeasuredHeight()-0.0f)/MyApplication.nowbitmap.getHeight();
				if(MyApplication.nowbitmap.getWidth()*scale1 > ll.getMeasuredWidth()){
					scale1 = (ll.getMeasuredWidth()-0.0f)/MyApplication.nowbitmap.getWidth();
				}
				Matrix matrix = new Matrix();
				matrix.postScale(scale1, scale1);
			    bitmap = Bitmap.createBitmap(MyApplication.nowbitmap,0, 0, MyApplication.nowbitmap.getWidth(), MyApplication.nowbitmap.getHeight(),matrix, true);
			    for(int i =0; i<8; i++){
					data[i] = (int) (data[i] * scale1);
					
				}
			    left = new Point(data[0], data[1]);
				top = new Point(data[2], data[3]);
				right = new Point(data[4], data[5]);
				bottom = new Point(data[6], data[7]);
			mCropImage = new CropImageView3(context, bitmap, left, top, right, bottom);
			
			ll.addView(mCropImage);
			
			/*if(MyApplication.nowbitmap.getHeight() > (ll.getMeasuredHeight()-100.0f)){
			float s = (ll.getMeasuredHeight()-100.0f)/MyApplication.nowbitmap.getHeight();
			if(MyApplication.nowbitmap.getWidth()*s > ll.getMeasuredWidth()){
				s = (ll.getMeasuredWidth()-100.0f)/MyApplication.nowbitmap.getWidth();
			}*/
			
			//ValueAnimator anim = ObjectAnimator.ofFloat(mCropImage, "scaleX", 1f,scale1);
			//ValueAnimator anim2 = ObjectAnimator.ofFloat(mCropImage, "scaleY", 1f,scale1);
			//AnimatorSet animatorSet = new AnimatorSet();  
			//animatorSet.play(anim).with(anim2);
			//animatorSet.start(); 
			}else{
				bitmap = MyApplication.nowbitmap;
				mCropImage = new CropImageView3(context, bitmap, left, top, right, bottom);
				ll.addView(mCropImage);
			}
			
			break;
		
	    default:
	    	break;
		}   
		super.handleMessage(msg);   
	  }   
    };
    
    
    
    public void initPopuptWindow() {  
		// TODO Auto-generated method stub  
		  
		// ��ȡ�Զ��岼���ļ�pop.xml����ͼ 
		if(popupWindow != null){
			popupWindow = null;
		}
		final View popupWindow_view = getLayoutInflater().inflate(R.layout.pagesize_pop, null,  false);  
		
		// ����PopupWindowʵ��
		DisplayMetrics dm = new DisplayMetrics();   
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float mScreenWidth = dm.widthPixels;
		int mwidth = (int)mScreenWidth*4/5;
		float mScreenHeight = dm.heightPixels;
		int mheight = (int)mScreenHeight*3/5;
		popupWindow = new PopupWindow(popupWindow_view,  mwidth, mheight,true);  
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		
		ListView lv = (ListView)popupWindow_view.findViewById(R.id.pagesize_list);
		PageSizeAdapter adapter = new PageSizeAdapter(context, mlist);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				sizeText.setText(sizes2[arg2]);
				MyApplication.sizeid = arg2;
				popupWindow.dismiss();
				popupWindow = null;
			}
			
		});
	}





	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(MyApplication.nowbitmap != null && !MyApplication.nowbitmap.isRecycled()){
				MyApplication.nowbitmap.recycle();
				MyApplication.nowbitmap = null;
			}
			if(bitmap != null && !bitmap.isRecycled()){
				bitmap.recycle();
				bitmap = null;
			}
			finish();
			return true;
	    }
		return super.onKeyDown(keyCode, event);
		
	}





	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		super.onDestroy();
		unregisterReceiver(closeReceiver);
		if(MyApplication.nowbitmap != null && !MyApplication.nowbitmap.isRecycled()){
			MyApplication.nowbitmap.recycle();
			MyApplication.nowbitmap = null;
		}
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
		}
		
		
	}





	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mThread = null;
		super.onPause();
	}
	
	


	

}