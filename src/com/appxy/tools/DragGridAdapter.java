package com.appxy.tools;


import java.util.HashMap;
import java.util.List;

import com.appxy.tinyscan.Activity_ListPhotos;
import com.appxy.tinyscan.Activity_Main;
import com.appxy.tinyscan.MyApplication;
import com.appxy.tinyscan.R;
import com.appxy.tools.BitmapWorkerTask.AsyncDrawable;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public  class DragGridAdapter extends ArrayAdapter<String>{
	 Context context;
	 List<String> mlist;
	 HashMap<String,Object> hm;
	 private int holdPosition;
	 private boolean isChanged = false;
	 private boolean ShowItem = false;
	 DragGridAdapter adapter = this;
	 int width, height;
    public DragGridAdapter(Context context,  List<String> objects, int width, int height) {
        super(context, 0, objects);
        this.context =context;
        this.mlist = objects;
        this.width = width;
        this.height = height;
       
    }
  
	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        //if(view == null){
           view = LayoutInflater.from(context).inflate(R.layout.griditem2, null);
           //view.setLayoutParams(new AbsListView.LayoutParams(300, 420));
       // }
        view.setTag(position);
        
        
        
        
        String photo_path = mlist.get(position);
        ImageView imageview = (ImageView)view.findViewById(R.id.listphoto_photo);
        TextView num = (TextView)view.findViewById(R.id.listphoto_text);
        int number = position+1;
        num.setText(number+"");
        ImageView del = (ImageView)view.findViewById(R.id.listphoto_delete);
        del.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				Activity_ListPhotos.idlist.add(mlist.get(position));
				
				Log.e("del", mlist.get(position));
				mlist.remove(position);
				
				notifyDataSetChanged();
				
			}
        	
        });
        
        imageview.setImageResource(R.drawable.logo);
        if(Activity_Main.getBitmapFromMemCache(photo_path) != null){
	    	loadBitmap2(imageview, photo_path);
	    }else{
	        loadBitmap(photo_path,imageview, photo_path);
	    }
        if (isChanged){
		    if (position == holdPosition){
		    	if(!ShowItem){
			        view.setVisibility(View.INVISIBLE);
		    	}
		    }
		}
    
        return view;
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
	
	public void update(int start, int down) { 
		holdPosition = down;
		String startpath = mlist.get(start);
		if(start < down){
		    mlist.add(down + 1, startpath);
		    mlist.remove(start);
		}else{
			 mlist.add(down,startpath);
			 mlist.remove(start+1);
		}
	
		isChanged = true;
        notifyDataSetChanged();
        
    } 
   
	public void showDropItem(boolean showItem){
		this.ShowItem = showItem;		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlist.size();
	}
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
 
}
