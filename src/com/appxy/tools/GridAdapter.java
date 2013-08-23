package com.appxy.tools;


import java.util.List;

import com.appxy.tinyscan.Activity_Main;
import com.appxy.tinyscan.MyApplication;
import com.appxy.tinyscan.R;
import com.appxy.tools.BitmapWorkerTask.AsyncDrawable;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter{
	
	Context context;
	LayoutInflater inflater;
	//ArrayList<HashMap<String, Object>> mlist;
	List<Photo_info> mlist2;
	
	private String root_Path = Environment.getExternalStorageDirectory().getPath()+"/MyTinyScan/";
	
	public final class ListItemView{                     
	    public ImageView image;
	    public TextView folder_name, creat_time, page_num;  
	    public LinearLayout rl, rl2;
        
    }     
	
	
	public GridAdapter(Context context, List<Photo_info> mlist){
		this.context = context;
		this.mlist2 = mlist;
		
		inflater = LayoutInflater.from(context);
		
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlist2.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View  convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ListItemView  listItemView = null;   
       if (convertView == null) {   
               listItemView = new ListItemView();    
           
               convertView =inflater.inflate(R.layout.griditem, null);   
               listItemView.image = (ImageView)convertView.findViewById(R.id.griditem_image);
               listItemView.folder_name = (TextView)convertView.findViewById(R.id.griditem_folder);
               listItemView.creat_time = (TextView)convertView.findViewById(R.id.griditem_time);
               listItemView.rl = ( LinearLayout)convertView.findViewById(R.id.griditem_relative);
               listItemView.rl2 = ( LinearLayout)convertView.findViewById(R.id.griditem_relative2);
               listItemView.page_num = (TextView)convertView.findViewById(R.id.griditem_num);
               convertView.setTag(listItemView);   
          
        }else {   
           listItemView = (ListItemView)convertView.getTag();   
        }
       if(mlist2.get(position) != null){
        Photo_info minfo = mlist2.get(position);
      
        String image_Path =  root_Path + minfo.getName() +"/"+minfo.getImage_name();
      
        int num  = minfo.getImage_num();
       
        
        if(minfo.isCheck()){
        	listItemView.rl2.setBackgroundColor(Color.rgb(56, 152, 250));
        	//listItemView.rl.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        	listItemView.rl.setBackgroundColor(Color.TRANSPARENT);
        	
        }else{
        	//listItemView.rl2.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        	listItemView.rl2.setBackgroundColor(Color.TRANSPARENT);
        	
        	 if(num > 1){
     	    	listItemView.rl.setBackgroundResource(R.drawable.photo_bg);
     	    	
     	    }else{
     	    	//listItemView.rl.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
     	    	//listItemView.rl.setBackgroundColor(Color.TRANSPARENT);
     	    	listItemView.rl.setBackgroundResource(R.drawable.photo_bg2);
     	    	
     	    }
        }
	    String name = minfo.getName();
	   
	    if(Activity_Main.getBitmapFromMemCache(name) != null){
	    	loadBitmap2(listItemView.image, name);
	    }else{
	    loadBitmap(image_Path,listItemView.image, name);
	    }
	    listItemView.page_num.setText(num +"pages");
	    
	   
        listItemView.folder_name.setText(name);   
        listItemView.creat_time.setText(minfo.getTime());  
       }
        return convertView ; 
    }
	
	
	
	
	public void loadBitmap(String path, ImageView imageView, String name) {
	    if (BitmapWorkerTask.cancelPotentialWork(path, imageView)) {
	        final BitmapWorkerTask task = new BitmapWorkerTask(imageView, name);
	        Bitmap bm = BitmapTools.decodeBitmapFromResource(context.getResources(), R.drawable.white, 200, 240);
	        final AsyncDrawable asyncDrawable =
	                new AsyncDrawable(context.getResources(), bm, task);
	        imageView.setImageDrawable(asyncDrawable);
	        task.execute(path);
	    }
	}
	
	public void loadBitmap2(ImageView imageView, String name){
		Bitmap bitmap =Activity_Main.getBitmapFromMemCache(name);
		imageView.setImageBitmap(bitmap);
	}
	

}
