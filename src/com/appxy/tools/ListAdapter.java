package com.appxy.tools;


import java.util.List;

import android.R.color;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import android.widget.ImageView;
import android.widget.TextView;

import com.appxy.tinyscan.Activity_Main;
import com.appxy.tinyscan.Activity_EditPhoto;
import com.appxy.tinyscan.MyApplication;
import com.appxy.tinyscan.R;
import com.appxy.tools.BitmapWorkerTask.AsyncDrawable;


public class ListAdapter extends BaseAdapter{

	Context context;
	LayoutInflater inflater;
	//ArrayList<HashMap<String, Object>> mlist;
	List<Photo_info> mlist2;
	private String root_Path = Environment.getExternalStorageDirectory().getPath()+"/MyTinyScan/";
	public final class ListItemView{                     
	    public ImageView image;
	    public TextView name, other;  
	    public LinearLayout ll;
	   // public CheckBox check; 
    }     
	
	
	public ListAdapter(Context context, List<Photo_info> mlist){
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
	public View getView(final int position, View  convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ListItemView  listItemView = null;   
       if (convertView == null) {   
               listItemView = new ListItemView();    
           
               convertView =inflater.inflate(R.layout.listitem, null);   
               listItemView.image = (ImageView)convertView.findViewById(R.id.listitem_image);
               listItemView.name = (TextView)convertView.findViewById(R.id.listitem_name);
               listItemView.other = (TextView)convertView.findViewById(R.id.listitem_other);
             
               listItemView.ll = (LinearLayout)convertView.findViewById(R.id.listitemlinear);
               convertView.setTag(listItemView);   
          
        }else {   
           listItemView = (ListItemView)convertView.getTag();   
       }
       if(mlist2.get(position) != null){
    
        Photo_info minfo = mlist2.get(position);
        String image_Path = root_Path + minfo.getName() +"/"+minfo.getImage_name();
      
        int num  = minfo.getImage_num();
        if(mlist2.get(position).isCheck()){
        	listItemView.ll.setBackgroundColor(Color.rgb(165, 230, 255));
        	
        }else{
        	listItemView.ll.setBackgroundColor(Color.TRANSPARENT);
        }
        
	    String name =  minfo.getName();
	    
	    if(Activity_Main.getBitmapFromMemCache(name) != null){
	    	loadBitmap2(listItemView.image, name);
	    }else{
	    loadBitmap(image_Path,listItemView.image, name);
	    }
	    if(num == 1){
	    	 listItemView.other.setText(minfo.getTime()+"  "+num +" page");
	    }else{
	    	 listItemView.other.setText(minfo.getTime()+"  "+num +" pages");	
	    }
	   
	   
        
        listItemView.name.setText(name);   
         
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
		Bitmap bitmap = Activity_Main.getBitmapFromMemCache(name);
		imageView.setImageBitmap(bitmap);
	}
	
	
	
}
