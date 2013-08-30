package com.appxy.tinyscan;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collections;

import com.appxy.tools.Photo_info;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Activity_SavePhoto extends Activity{
	
	ImageView image,save, delete;
	EditText title;
	String path = Environment.getExternalStorageDirectory()+"/MyTinyScan";
	Context context;
	ProgressDialog progressDialog;
	Thread mThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.save_photo);
		context = this;
		image = (ImageView)findViewById(R.id.save_photo_image);
		image.setImageBitmap(MyApplication.tempbitmap);
		
		
		
		if(ExistSDCard()){

			File file = new File(path);
			if(file.exists()){
				
			}else{
				file.mkdir();
				
			}
		}
		
		title = (EditText)findViewById(R.id.save_photo_text5);
		delete = (ImageView)findViewById(R.id.save_text_delete);
		title.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				if(arg0.toString().equals("")){
					delete.setVisibility(4);
				}else{
					delete.setVisibility(0);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
		});
		int i = 1;
		File mfile = new File(path + "/" +"New Document");
		if(mfile.exists()){
			while(new File(path+"/"+"New Document("+i+")").exists()){
				i+=1;
			}
			title.setText("New Document("+i+")");
		}else{
			title.setText("New Document");
		}
		
		
		title.requestFocus();
		
		delete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				title.setText("");
			}
			
		});
		save = (ImageView)findViewById(R.id.save_photo_save);
		save.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(getCurrentFocus() != null){
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			    }
				if(title.getText().toString().equals("")){
					Toast.makeText(context, "Document name is required !!", Toast.LENGTH_SHORT).show();
				}else{
				if(ExistSDCard()){
					
					
	          		final File newfolder = new File(path+"/"+title.getText().toString());
	          		if(newfolder.exists()){
	          			Toast.makeText(context, "File already exists !!", Toast.LENGTH_SHORT).show();
	          			
	          		}else{
	          		newfolder.mkdir();
	          		
	          		progressDialog=ProgressDialog.show(context,"Please wait ","Saving...");
	          		mThread = new Thread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Timestamp time = new Timestamp(System.currentTimeMillis());
			          		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
			          		String time2 = sdf.format(time);
			          		String name = time2.substring(0,14)+MyApplication.sizeid+"000.jpg";
			          		File newfile = new File(newfolder.getPath()+"/"+name);
			          		
						    OutputStream out = null;
			     			try {
								out = new BufferedOutputStream(new FileOutputStream(newfile));
			     			
								MyApplication.savebitmap.compress(CompressFormat.JPEG, 100, out);	
				     		    out.flush();
				     		    out.close();
				     		    Photo_info minfo = new Photo_info(newfolder.getName(),newfile.getName().substring(0, 4)+"-"+newfile.getName().substring(4, 6)+"-"+newfile.getName().substring(6, 8),1,newfile.getName(), false);
				     		    Activity_Main.mlist2.add(minfo);
				     		   if(Activity_Main.sort_type == 0){
				     			  Collections.sort(Activity_Main.mlist2, Activity_Main.comparator);
				     			}else{
				     				Collections.sort(Activity_Main.mlist2, Activity_Main.comparator2);
				     			}
				     		    
				     		    MyApplication.folder_path = newfolder.getName();
				     		    MyApplication.folder_id = Activity_Main.mlist2.indexOf(minfo);
				     		    MyApplication.isUpdate = true;
				     		    Thread.sleep(500);
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
			     			m.what = 0;
			     			handler.sendMessage(m);
						}
	          		
	          		});
	          		
	          		mThread.start();	
	          		}
	          		
				}else{
					Toast.makeText(context, "SDCard not ready !!", Toast.LENGTH_SHORT).show();
				}
				}
				
			}
			
		});
	}

	private boolean ExistSDCard() {  
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) 
		{  
		   return true;  
		} else  {
		   return false; 
		}
		}

	Handler handler = new Handler(){   
		public void handleMessage(Message msg) {   
		switch (msg.what) {   
	
		case 0:
			progressDialog.dismiss();
			
		    Intent intent = new Intent();
		    intent.setAction("com.tinyscan.CloseReceiver");
		    sendBroadcast(intent);
			Intent intent2 = new Intent(context, Activity_EditPhoto.class);
			intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
 			startActivity(intent2);
 			finish();
     		break;
     	default:
     	    break;
		}   
		super.handleMessage(msg);   
	  }   
    };

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	

	

	
	

}
