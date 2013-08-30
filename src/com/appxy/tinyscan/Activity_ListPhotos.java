package com.appxy.tinyscan;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.appxy.tools.BitmapTools;
import com.appxy.tools.DragGridAdapter;
import com.appxy.tools.DragGridView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_ListPhotos extends Activity{
	
	
	private static List<String> list = null;
    private ImageView back,save;
    
    public static TextView num;
    private DragGridAdapter adapter = null;
    public static List<String> idlist;
    private Context context;
    private DragGridView dragView;
    public static  HashMap<String,Object> hm, hm2;
    private String folder_name, photo_path;
    private ProgressDialog progressDialog;
    boolean isRun = false;
    int clickid2;
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
       
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.photolist);
        context = this;
        list = new ArrayList<String>();
        idlist = new ArrayList<String>();
        hm = new HashMap<String,Object>();
        hm2 = new HashMap<String,Object>();
        Intent intent = this.getIntent();
       
        list = intent.getStringArrayListExtra("mlist");
        //Collections.sort(list);
        
       
        folder_name = intent.getExtras().getString("folder_name");
        photo_path = intent.getExtras().getString("photo_path");
        num = (TextView)findViewById(R.id.photolist_num);
        num.setText(folder_name);
        

        dragView = (DragGridView)findViewById(R.id.drag_grid);
        dragView.setBackgroundResource(R.drawable.main_bg);
       
        dragView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		adapter = new DragGridAdapter(context,list, metrics.widthPixels, metrics.heightPixels);
        dragView.setAdapter(adapter);
       
        back = (ImageView)findViewById(R.id.photolist_back);
        back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
        	
        });
        
       
        
        save = (ImageView)findViewById(R.id.photolist_save);
        save.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(isRun){
					
				}else{
				progressDialog=ProgressDialog.show(context,"Please wait ","Saving...");
				new Thread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						MyApplication.isUpdate = true;
						isRun = true;
						if(Activity_Main.getBitmapFromMemCache(MyApplication.folder_path)!=null){
		        			Activity_Main.mMemoryCache.remove(MyApplication.folder_path);
		        		}
						
					
						for(int i=0; i<idlist.size(); i++){
							
							
							File file = new File(idlist.get(i));
							if(file.exists()){
								if(Activity_Main.mMemoryCache.get(file.getPath()) != null){
									Activity_Main.mMemoryCache.get(file.getPath()).recycle();
									Activity_Main.mMemoryCache.remove(file.getPath());
									
								}
								file.delete();
								Activity_Main.mlist2.get(MyApplication.folder_id).setImage_num(Activity_Main.mlist2.get(MyApplication.folder_id).getImage_num()-1);
							}
							
						}
						if(MyApplication.islistchanged){
						MyApplication.islistchanged = false;
						for(int j=0; j <list.size(); j++){
							
							Activity_Main.mMemoryCache.remove(list.get(j));
							File file = new File(list.get(j));
							file.renameTo(new File(list.get(j)+".temp"));
							
						}
						
						for(int j=0; j <list.size(); j++){
							File file = new File(list.get(j)+".temp");
							String name;
							if(j < 10){
								name = "00"+j;
							}else if(j<100){
								name = "0"+j;
							}else{
								name = ""+j;
							}
							String rename = file.getName().substring(0,15)+ name +".jpg";
							file.renameTo(new File(photo_path+"/"+rename));
							
							
						}
						}
						File mfile = new File(photo_path);
						String[] mfiles = mfile.list();
						if(mfiles.length>0){
					    BitmapTools.sort(mfiles);
						Activity_Main.mlist2.get(MyApplication.folder_id).setImage_name(mfiles[0]);
						Message message = new Message();   
				    	message.what = 0;   
				    	handler.sendMessage(message);
						}else{
							mfile.delete();
							Activity_Main.mlist2.remove(MyApplication.folder_id);
							Message message = new Message();   
					    	message.what = 2;   
					    	handler.sendMessage(message);
						}
						
					}
					
				}).start();
				}
			}
        	
        });
    }
    
    Comparator<String> comparator = new Comparator<String>(){
		   public int compare(String s1, String s2) {
			    
				  return s1.substring(s1.length()-7, s1.length()-4).compareTo(s2.substring(s2.length()-7, s2.length()-4));
			  
		   }
    };
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			MyApplication.isUpdate = false;
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

    Handler handler = new Handler(){   
		public void handleMessage(Message msg) {   
		switch (msg.what) {   
		
		case 0:	
			
			isRun = false;
			progressDialog.dismiss();
			Toast.makeText(Activity_ListPhotos.this, "Save Success !!", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(Activity_ListPhotos.this, Activity_EditPhoto.class);
			idlist.clear();
		
			setResult(1, intent);
			finish();
			break;
		case 1:
			progressDialog.dismiss();
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			adapter = new DragGridAdapter(context,list, metrics.widthPixels, metrics.heightPixels);
	        dragView.setAdapter(adapter);
	        break;
        case 2:	
			
			isRun = false;
			progressDialog.dismiss();
			MyApplication.isUpdate = true;
			Toast.makeText(Activity_ListPhotos.this, "Save Success !!", Toast.LENGTH_SHORT).show();
			Intent intent2 = new Intent(Activity_ListPhotos.this, Activity_Main.class);
			intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			idlist.clear();
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		for(int i=0; i <list.size(); i++){
			Activity_Main.mMemoryCache.remove(list.get(i));
		}
	}
	
    
   
}
