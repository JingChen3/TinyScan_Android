package com.appxy.tools;

import java.util.ArrayList;
import java.util.HashMap;

import com.appxy.tinyscan.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import android.widget.TextView;

public class ProcessAdapter extends BaseAdapter{
	
	Context context;
	LayoutInflater inflater;
	ArrayList<HashMap<String,Object>> mlist;
	
	public final class ListItemView{                     
	   
	    public TextView process_name;  
	    public RadioButton rb;
        
    }   
	
	
	public ProcessAdapter(Context context, ArrayList<HashMap<String,Object>> mlist){
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
	           
	               convertView =inflater.inflate(R.layout.process_item, null);   
	               listItemView.process_name = (TextView)convertView.findViewById(R.id.process_item_text);
	               listItemView.rb = (RadioButton)convertView.findViewById(R.id.process_item_rb);
	               convertView.setTag(listItemView);   
	          
	        }else {   
	           listItemView = (ListItemView)convertView.getTag();   
	        }
	      
	        listItemView.process_name.setText((String) mlist.get(arg0).get("name"));
	       
	        listItemView.rb.setChecked((Boolean) mlist.get(arg0).get("selected"));
	       
	        return convertView ; 
	}

}
