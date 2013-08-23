package com.appxy.tools;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appxy.tinyscan.R;

public class PageSizeAdapter extends BaseAdapter{
	
	Context context;
	LayoutInflater inflater;
	ArrayList<HashMap<String, Object>>  mlist;
	HashMap<String, Object> hm;
	
	public final class ListItemView{                     
	   
	    public ImageView image;  
	  
        
    }   
	
	
	public PageSizeAdapter(Context context, ArrayList<HashMap<String, Object>> mlist){
		this.context = context;
		this.mlist = mlist;
		inflater = LayoutInflater.from(context);
	}
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlist.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mlist.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		   ListItemView  listItemView = null;   
	       if (convertView == null) {   
	               listItemView = new ListItemView();    
	           
	               convertView =inflater.inflate(R.layout.pagesize_item, null);   
	               listItemView.image = (ImageView)convertView.findViewById(R.id.pagesize_item_image);
	              
	               convertView.setTag(listItemView);   
	          
	        }else {   
	           listItemView = (ListItemView)convertView.getTag();   
	        }
	       listItemView.image.setImageResource((Integer) mlist.get(arg0).get("image"));
	     
	        return convertView ; 
	}

}