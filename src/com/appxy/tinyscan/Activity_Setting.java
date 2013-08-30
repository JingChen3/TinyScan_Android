package com.appxy.tinyscan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.appxy.tools.PageSizeAdapter;
import com.appxy.tools.ProcessAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class Activity_Setting extends Activity{
	
	ImageView back;
	
	TextView size, size2,  process, process2, send, faq;
	PopupWindow popupWindow;
	Context context;
	int[] sizes;
	String[] sizes2, processName;
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	ArrayList<HashMap<String,Object>> mlist, mlist2;
	
	HashMap<String, Object> hm;
	int mwidth, mheight;
	TelephonyManager tm;
	String info = "";
	RelativeLayout rl1,rl2,rl3,rl4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting);
		context = this;
		tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();   
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		mwidth = (int)dm.widthPixels*4/5;
		
		mheight = (int)dm.heightPixels*3/5;
		sizes = new int[]{R.drawable.size_letter, R.drawable.size_a4, R.drawable.size_legal, R.drawable.size_a3, R.drawable.size_a5, R.drawable.size_business};
		sizes2 = new String[]{"Letter", "A4", "Legal", "A3", "A5", "Business Card"};
		mlist = new ArrayList<HashMap<String,Object>>();
		preferences = getSharedPreferences("MyTinyScan", MODE_PRIVATE);
		for(int i =0; i<6; i++){
			hm = new HashMap<String, Object>();
		    hm.put("image", sizes[i]);
		    hm.put("size", sizes2[i]);
		    if(i == preferences.getInt("pagesize", 1)){
		    	hm.put("selected", true);	
		    }else{
		    	 hm.put("selected", false);
		    }
		    mlist.add(hm);
		}
		
		processName = new String[]{"Color", "B&W", "Grayscal"};
		mlist2 = new ArrayList<HashMap<String,Object>>();
		for(int i =0; i<3; i++){
			hm = new HashMap<String, Object>();
		    hm.put("name", processName[i]);
		   
		    if(i == preferences.getInt("process", 1)){
		    	hm.put("selected", true);	
		    }else{
		    	hm.put("selected", false);
		    }
		    mlist2.add(hm);
		}
		back = (ImageView)findViewById(R.id.set_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		
		rl1 = (RelativeLayout)findViewById(R.id.set_layout1);
		rl1.setOnClickListener(mlistener);
		rl1.setOnTouchListener(mlistener2);
		rl2 = (RelativeLayout)findViewById(R.id.set_layout2);
		rl2.setOnClickListener(mlistener);
		rl2.setOnTouchListener(mlistener2);
		rl3 = (RelativeLayout)findViewById(R.id.set_layout3);
		rl3.setOnClickListener(mlistener);
		rl3.setOnTouchListener(mlistener2);
	
		
		size2 = (TextView)findViewById(R.id.set_size_text2);
		size2.setText(sizes2[preferences.getInt("pagesize", 0)]+"");
		
		process2 = (TextView)findViewById(R.id.set_process_text2);
		process2.setText(processName[preferences.getInt("process", 0)]+"");
		
		
	}
	
	
	OnClickListener mlistener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.set_layout1:
				//initPopuptWindow();
			
				//popupWindow.showAtLocation(v,Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
				View view = getLayoutInflater().inflate(R.layout.pagesize2, null);
				
				final AlertDialog mDialog = new AlertDialog.Builder(context).setTitle("Default page size").setView(view).setNegativeButton("Cancel", null).create();
				mDialog.show();
				
				ListView lv = (ListView)view.findViewById(R.id.pagesize_list);
				final PageSizeAdapter adapter = new PageSizeAdapter(context, mlist);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						// TODO Auto-generated method stub
						size2.setText(sizes2[arg2]);
						
						for(int i=0; i<6; i++){
							mlist.get(i).put("selected", false);
						}
						mlist.get(arg2).put("selected", true);
						editor = preferences.edit();
				        editor.putInt("pagesize", arg2);
				        editor.commit();
						adapter.notifyDataSetChanged();
						mDialog.dismiss();
					}
					
				});
				break;
			case R.id.set_layout2:
				//initPopuptWindow2();
				
				//popupWindow.showAtLocation(v,Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
				View view2 = getLayoutInflater().inflate(R.layout.process_pop, null,  false);
				final AlertDialog mDialog2 = new AlertDialog.Builder(context).setTitle("Default process").setView(view2).setNegativeButton("Cancel", null).create();
				mDialog2.show();
				
				ListView lv2 = (ListView)view2.findViewById(R.id.process_list);
				final ProcessAdapter adapter2 = new ProcessAdapter(context, mlist2);
				lv2.setAdapter(adapter2);
				lv2.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						// TODO Auto-generated method stub
						process2.setText(processName[arg2]);
						
						for(int i=0; i<3; i++){
							mlist2.get(i).put("selected", false);
						}
						mlist2.get(arg2).put("selected", true);
						editor = preferences.edit();
				        editor.putInt("process", arg2);
				        editor.commit();
						adapter2.notifyDataSetChanged();
						mDialog2.dismiss();
					}
					
				});
				break;
			case R.id.set_layout3:
				fillinfo();
				Intent email = new Intent(android.content.Intent.ACTION_SEND);
				email.setType("plain/text");
				String[] emailReciver = new String[]{"tinyscan.a@appxy.com"};
				String emailSubject = "TinyScan";
				

				//设置邮件默认地址
				email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
				//设置邮件默认标题
				email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);
				//设置要默认发送的内容
				email.putExtra(android.content.Intent.EXTRA_TEXT, info);
				//startActivity(Intent.createChooser(email, "Choose one to send "));
				startActivity(email);
				break;
			
			default:
				break;
			}
		}
		
	};
	
	
	public void initPopuptWindow() {  
		// TODO Auto-generated method stub  
		  
		// 获取自定义布局文件pop.xml的视图 
		if(popupWindow != null){
			popupWindow = null;
		}
		final View popupWindow_view = getLayoutInflater().inflate(R.layout.pagesize_pop, null,  false);  
		
		// 创建PopupWindow实例
		
		popupWindow = new PopupWindow(popupWindow_view,  mwidth,LayoutParams.WRAP_CONTENT,true);  
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		
		ListView lv = (ListView)popupWindow_view.findViewById(R.id.pagesize_list);
		PageSizeAdapter adapter = new PageSizeAdapter(context, mlist);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				size2.setText(sizes2[arg2]);
				editor = preferences.edit();
		        editor.putInt("pagesize", arg2);
		        editor.commit();
				popupWindow.dismiss();
				popupWindow = null;
			}
			
		});
	}
	
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			finish();
			return true;
			}
		return super.onKeyDown(keyCode, event);
	}
	
	public void fillinfo(){
		info = "";
		
		info += "Model : "+android.os.Build.MODEL+"\n";
		info += "Rrelease : "+android.os.Build.VERSION.RELEASE+"\n";
		
		
	}
	
	OnTouchListener mlistener2 = new OnTouchListener(){

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getAction() == MotionEvent.ACTION_DOWN){
				arg0.setBackgroundColor(Color.rgb(223, 223, 223));
			}else if(arg1.getAction() == MotionEvent.ACTION_UP){
				arg0.setBackgroundColor(Color.TRANSPARENT);
			}else if(arg1.getAction() == MotionEvent.ACTION_MOVE){
				arg0.setBackgroundColor(Color.rgb(223, 223, 223));
			}
			return false;
		}
		
	};

}
