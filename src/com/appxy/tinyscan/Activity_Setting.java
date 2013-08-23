package com.appxy.tinyscan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.appxy.tools.PageSizeAdapter;
import com.appxy.tools.ProcessAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
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
	String[] sizes2;
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	ArrayList<HashMap<String,Object>> mlist;
	String[] mlist2;
	HashMap<String, Object> hm;
	int mwidth, mheight;
	TelephonyManager tm;
	String info = "";
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
		sizes2 = new String[]{"Letter", "A4", "Legal", "A3", "A5", "Card"};
		mlist = new ArrayList<HashMap<String,Object>>();
		preferences = getSharedPreferences("MyTinyScan", MODE_PRIVATE);
		for(int i =0; i<6; i++){
			hm = new HashMap<String, Object>();
		    hm.put("image", sizes[i]);
		    hm.put("size", sizes2[i]);
		    mlist.add(hm);
		}
		
		mlist2 = new String[]{"Color", "B&W", "Grayscal"};
		back = (ImageView)findViewById(R.id.set_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		size = (TextView)findViewById(R.id.set_size_text);
		size.setOnClickListener(mlistener);
		size2 = (TextView)findViewById(R.id.set_size_text2);
		size2.setText(sizes2[preferences.getInt("pagesize", 0)]+"");
		process = (TextView)findViewById(R.id.set_process_text);
		process.setOnClickListener(mlistener);
		process2 = (TextView)findViewById(R.id.set_process_text2);
		process2.setText(mlist2[preferences.getInt("process", 0)]+"");
		send = (TextView)findViewById(R.id.set_send);
		send.setOnClickListener(mlistener);
		faq = (TextView)findViewById(R.id.set_faq);
		faq.setOnClickListener(mlistener);
		
	}
	
	
	OnClickListener mlistener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.set_size_text:
				initPopuptWindow();
			
				popupWindow.showAtLocation(v,Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
				break;
			case R.id.set_process_text:
				initPopuptWindow2();
				
				popupWindow.showAtLocation(v,Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
				break;
			case R.id.set_send:
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
				startActivity(Intent.createChooser(email, "Choose one to send "));
				
				break;
			case R.id.set_faq:
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
		
		popupWindow = new PopupWindow(popupWindow_view,  mwidth, mheight,true);  
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
	
	public void initPopuptWindow2() {  
		// TODO Auto-generated method stub  
		  
		// 获取自定义布局文件pop.xml的视图 
		if(popupWindow != null){
			popupWindow = null;
		}
		final View popupWindow_view = getLayoutInflater().inflate(R.layout.process_pop, null,  false);  
		
		// 创建PopupWindow实例
		
		popupWindow = new PopupWindow(popupWindow_view,  mwidth, LayoutParams.WRAP_CONTENT,true);  
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		
		ListView lv = (ListView)popupWindow_view.findViewById(R.id.process_list);
		ProcessAdapter adapter = new ProcessAdapter(context, mlist2);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				process2.setText(mlist2[arg2]+"");
				editor = preferences.edit();
		        editor.putInt("process", arg2);
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

}
