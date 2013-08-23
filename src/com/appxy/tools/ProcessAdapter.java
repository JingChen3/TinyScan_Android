package com.appxy.tools;

import java.util.ArrayList;
import com.appxy.tinyscan.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;

public class ProcessAdapter extends BaseAdapter{
	
	Context context;
	LayoutInflater inflater;
	String[] mlist;
	
	public final class ListItemView{                     
	   
	    public TextView process_name;  
	  
        
    }   
	
	
	public ProcessAdapter(Context context, String[] mlist){
		this.context = context;
		this.mlist = mlist;
		inflater = LayoutInflater.from(context);
	}
	


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlist.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mlist[arg0];
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
	           
	               convertView =inflater.inflate(R.layout.process_item, null);   
	               listItemView.process_name = (TextView)convertView.findViewById(R.id.process_item_text);
	              
	               convertView.setTag(listItemView);   
	          
	        }else {   
	           listItemView = (ListItemView)convertView.getTag();   
	        }
	      
	        listItemView.process_name.setText(mlist[arg0]);
	        return convertView ; 
	}

}
