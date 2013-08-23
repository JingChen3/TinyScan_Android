package com.appxy.tools;

import java.util.ArrayList;
import com.appxy.tinyscan.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;

public class FoldernameAdapter extends BaseAdapter{
	
	Context context;
	LayoutInflater inflater;
	ArrayList<String> mlist;
	
	public final class ListItemView{                     
	   
	    public TextView folder_name;  
	  
        
    }   
	
	
	public FoldernameAdapter(Context context, ArrayList<String> mlist){
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
	           
	               convertView =inflater.inflate(R.layout.folder_name, null);   
	               listItemView.folder_name = (TextView)convertView.findViewById(R.id.folder_name);
	              
	               convertView.setTag(listItemView);   
	          
	        }else {   
	           listItemView = (ListItemView)convertView.getTag();   
	        }
	      
	        listItemView.folder_name.setText(mlist.get(arg0));
	        return convertView ; 
	}

}
