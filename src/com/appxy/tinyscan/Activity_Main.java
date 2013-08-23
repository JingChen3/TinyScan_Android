package com.appxy.tinyscan;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.appxy.tools.BitmapTools;
import com.appxy.tools.GridAdapter;
import com.appxy.tools.ListAdapter;
import com.appxy.tools.Photo_info;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class Activity_Main extends Activity {
	private PopupWindow popupWindow;
	private static Context context;
	private LayoutInflater inflater;
	public static ImageView setlist;
	public static ImageView search;
	public static ImageView takephotos;
	public static ImageView logo;
	private static ImageView back;
	public static ImageView back2;
	public static ImageView edit;
	public static ImageView share;
	public static ImageView delete;
	public static ImageView search_delete;
	public static TextView selecttext;
	
	private static EditText search_text;	
	private LinearLayout listphotos;
	private static RelativeLayout search_layout;
	public static LinearLayout selectline, selectline2;
	public static RelativeLayout mainlayout;
	public static GridAdapter madapter;
	public static ListAdapter madapter2;
	public static int list_type;
	public static int sort_type;
	public static boolean isSearch = false;
	public static boolean isSelect = false;
	public static List<Photo_info> mlist2;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	public static LruCache<String, Bitmap> mMemoryCache;
	public static List<Photo_info> idlist;
	private static String root_Path = Environment.getExternalStorageDirectory().getPath()+"/MyTinyScan/";
	private String root_Path2 = Environment.getExternalStorageDirectory().getPath()+"/MyTinyScan_PDF/";
	private long mExitTime;
	private ProgressDialog progressDialog;
	private Thread mThread;
	private File file;
	private File[] files;
	private String[] name;
	Dialog mdialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_main);
		
		context = this;
		
		idlist = new ArrayList<Photo_info>();
		makefolder();

	    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	    final int cacheSize = maxMemory / 8;
	    
	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {       
	        	return bitmap.getByteCount() / 1024;
	           
	        }
	    };
	 
		inflater = LayoutInflater.from(context);
		listphotos = (LinearLayout)findViewById(R.id.main_linear);
		
		mainlayout = (RelativeLayout)findViewById(R.id.main_layout);
	    selectline = (LinearLayout)findViewById(R.id.selectline);
	    selectline2 = (LinearLayout)findViewById(R.id.selectline2);
		search_text = (EditText)findViewById(R.id.search_text);
		selecttext = (TextView)findViewById(R.id.selecttext);
		search_delete = (ImageView)findViewById(R.id.search_text_delete);
		search_delete.setOnClickListener(myOnClickListener);
		back2 = (ImageView)findViewById(R.id.mainback2);
		back2.setOnClickListener(myOnClickListener);
		edit = (ImageView)findViewById(R.id.mainedit);
		edit.setOnClickListener(myOnClickListener);
		share = (ImageView)findViewById(R.id.mainshare);
		share.setOnClickListener(myOnClickListener);
		delete = (ImageView)findViewById(R.id.maindelete);
		delete.setOnClickListener(myOnClickListener);
		search_layout = (RelativeLayout)findViewById(R.id.search_relative);
		setlist = (ImageView)findViewById(R.id.main_setlist);
		search = (ImageView)findViewById(R.id.main_search);
		back = (ImageView)findViewById(R.id.mainback);
		back.setOnClickListener(myOnClickListener);
		logo = (ImageView)findViewById(R.id.mainlogo);
		search.setOnClickListener(myOnClickListener);
		takephotos = (ImageView)findViewById(R.id.main_takepicture);
		takephotos.setOnClickListener(myOnClickListener);
		setlist.setOnClickListener(myOnClickListener);
		list_folders();
		MyApplication.isUpdate = false;
		
		 
	
	}
	
	
    OnClickListener myOnClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.main_setlist:
				initPopuptWindow();  
				popupWindow.showAsDropDown(v,0,0);
                break;
			case R.id.search_text_delete:
				search_text.setText("");
				break;
			case R.id.mainshare:
				
				progressDialog=ProgressDialog.show(context,null,"Converting to PDF...");
				progressDialog.setCancelable(true);
				//progressDialog.set
				mThread = new Thread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						    File mfile = new File(root_Path2);
				            File[] name5 = mfile.listFiles();
				            for(int i=0; i<name5.length; i++){
				        	   name5[i].delete();
				            }
		     				for(int i=0; i<idlist.size(); i++){
		     					
		     					File file = new File(root_Path + idlist.get(i).getName());
		     					File[] files = file.listFiles();
		     					Rectangle msize = null;
		     					for(int j=0; j<files.length; j++){
		     						int size = Integer.parseInt(files[j].getName().substring(14,15));
		     						switch(size){
		     						case 0:
		     							msize = PageSize.LETTER;
		     							break;
		     						case 1:
		     							msize = PageSize.A4;
		     							break;
		     						case 2:
		     							msize = PageSize.LEGAL;
		     							break;
		     						case 3:
		     							msize = PageSize.A3;
		     							break;
		     						case 4:
		     							msize = PageSize.A5;
		     							break;
		     						case 5:
		     							msize = PageSize.POSTCARD;
		     							break;
		     						default:
		     							break;
		     						}
		     						
		     						Document document = new Document(msize);
		    		     			try {
		    		     				
		    		     				PdfWriter.getInstance(document, new FileOutputStream(root_Path2+files[j].getName()+".pdf"));			
		    		     				document.open();
		     						    Bitmap bitmap = BitmapFactory.decodeFile(files[j].getPath());
		     						    ByteArrayOutputStream stream = new ByteArrayOutputStream();
				     				
		     						    bitmap.compress(Bitmap.CompressFormat.JPEG ,
				     				                        100 , stream);
				     				    Image jpg = Image.getInstance(stream.toByteArray());
				     				
				     				    jpg.scaleToFit(document.getPageSize());
				     				
				     				    //jpg.setAlignment(Image.ALIGN_CENTER);
				     				    jpg.setAbsolutePosition((document.getPageSize().getWidth()-jpg.getScaledWidth())/2, (document.getPageSize().getHeight()-jpg.getScaledHeight())/2);
				     				    document.add(jpg);
				     				    document.newPage();
		     					}catch (DocumentException de) {
				     				System.err.println(de.getMessage());
				     			} catch (IOException ioe) {
				     				System.err.println(ioe.getMessage());
				     			}
		    		     		document.close();
		     				}
		     				
		     			} 
		     			PdfCopyFields copy	= null;
		     			try {
							copy = new PdfCopyFields(new FileOutputStream(root_Path2+"MyTinyScan.pdf"));
							File file = new File(root_Path2);
					        File[] name = file.listFiles();
					        for(int i=0; i<name.length; i++){
					        	PdfReader reader = new PdfReader(name[i].getPath()); 
					        	copy.addDocument(reader);
					        }
					       
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (DocumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}finally{
							 copy.close();
						}

		     			
		     			Message m = new Message();
		     			m.what =3;
		     			handler.sendMessage(m);
					}
					
				});
				mThread.start();
				
				break;
			case R.id.main_takepicture:
				
				
				MyApplication.where = false;
			    Intent intent = new Intent(context, Activity_CameraPreview.class);
				
				startActivity(intent);
				
				break;
			case R.id.main_search:
				search();
				break;	
			case R.id.mainback:
				clear();
				break;
			case R.id.mainback2:
				unselected();
				break;
			case R.id.mainedit:
				if(idlist.size()>1){
					Toast.makeText(context, "Only one that you can rename", Toast.LENGTH_SHORT).show();
					
				}else{
					
				
				final View view = getLayoutInflater().inflate(R.layout.rename, null,  false);  
			    final Dialog alertDialog2 = new AlertDialog.Builder(context).setView(view).create();
			    alertDialog2.show();
			    
			    TextView cancel = (TextView)view.findViewById(R.id.renamecancel);
				cancel.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						
						alertDialog2.dismiss();
					
					}
					
				});
				TextView save = (TextView)view.findViewById(R.id.renamesave);
				EditText et = (EditText)view.findViewById(R.id.renameedit);
				et.setSelectAllOnFocus(true);
				et.setText(idlist.get(0).getName());
				et.requestFocusFromTouch();
				
				save.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						EditText et = (EditText)view.findViewById(R.id.renameedit);
						if(et.getText().toString().equals(idlist.get(0).getName())){
							alertDialog2.dismiss();
							return;
						}
						File file2 = new File(root_Path + et.getText().toString());
						if(file2.exists()){
							Toast.makeText(context, "File Exists !!", Toast.LENGTH_SHORT).show();
							return;
						}
						
						File file = new File(root_Path + idlist.get(0).getName());
						if(file.exists()){
							file.renameTo(new File(root_Path + et.getText().toString()));
						}
						Bitmap bitmap = mMemoryCache.get(idlist.get(0).getName());
						mMemoryCache.remove(idlist.get(0).getName());
						addBitmapToMemoryCache(et.getText().toString(), bitmap);
						mlist2.get(mlist2.indexOf(idlist.get(0))).setName(et.getText().toString());
						if(list_type == 0){
		    			    madapter.notifyDataSetChanged();
		    			}else{
		    				madapter2.notifyDataSetChanged();
		    			}
						
						alertDialog2.dismiss();
						Toast.makeText(context, "Save Success !!", Toast.LENGTH_SHORT).show();
					}
					
				});
				
				 Timer timer = new Timer();   
					timer.schedule(new TimerTask(){   
					 
					           @Override  
					            public void run() {   
					               InputMethodManager m = (InputMethodManager)   
					            		   alertDialog2.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);   
					              m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);   
					               
					           }   
					              
					 }, 100);  
				}
				break;
			case R.id.maindelete:
				Dialog alertDialog = new AlertDialog.Builder(context). 
	            setMessage("Are you sure ?").setPositiveButton("Yes", new DialogInterface.OnClickListener(){
	            	 @Override 
	                 public void onClick(DialogInterface dialog, int which) { 
	            		 
	                     // TODO Auto-generated method stub  
	            		 
	            		 progressDialog=ProgressDialog.show(context,"Please wait ","Deleting...");
	            		 
	            		 new Thread(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								for(int i=0; i<idlist.size(); i++){
			            			
			            			File file = new File(root_Path + idlist.get(i).getName());
			            			if(file.exists()){
			            				if(file.isDirectory()){
			            					File[] mFile = file.listFiles();
			            					for(int j =0; j < mFile.length; j++){
			            						mFile[j].delete();
			            					}
			            					
			            				}
			            			file.delete();
			            			mlist2.remove(idlist.get(i));
			            			mMemoryCache.remove(idlist.get(i).getName());
			            			}
			                  			
			            		}
			            		
			            		Message message = new Message();   
						    	message.what = 0;   
						    	handler.sendMessage(message);
								
							}
	            			 
	            		 }).start();
	            		 
	            		
	 					
	                 } 
	             }).setNegativeButton("No",  new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}
	            	 
	             }).
	            create(); 
	            alertDialog.show(); 
	            break;
            default:
                break;
			}
		}
    	
    };
    
    Handler handler = new Handler(){   
		public void handleMessage(Message msg) {   
		switch (msg.what) {   
		
		case 0:	
			idlist.clear();
			if(list_type == 0){
			    madapter.notifyDataSetChanged();
			}else{
				madapter2.notifyDataSetChanged();
			}
			progressDialog.dismiss();
			unselected();
			
		
			break;
			
		case 1:
			
			if(list_type == 0){
				list_by_grid();
			}else{
				list_by_list();
			}
			progressDialog.dismiss();
			break;
		case 3:
			progressDialog.dismiss();
			mThread = null;
			Toast.makeText(context, "Convert Success!!", Toast.LENGTH_SHORT).show();
			File file = new File(root_Path2+"MyTinyScan.pdf");
			Intent mailIntent=new Intent(Intent.ACTION_SEND);
			mailIntent.putExtra(Intent.EXTRA_SUBJECT, "TinyScan");
			mailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			if (file.getName().endsWith(".gz")) {
				mailIntent.setType("application/x-gzip"); //如果是gz使用gzip的mime
            } else if (file.getName().endsWith(".txt")) {
            	mailIntent.setType("text/plain"); //纯文本则用text/plain的mime
            } else {
            	mailIntent.setType("application/octet-stream"); //其他的均使用流当做二进制数据来发送
            }
            startActivity(Intent.createChooser(mailIntent, "Export"));   
			break;
		default:
			break;
		}   
		super.handleMessage(msg);   
	}   
	};  
	
	
	public void initPopuptWindow() {  
		// TODO Auto-generated method stub  
		  
		// 获取自定义布局文件pop.xml的视图 
		if(popupWindow != null){
			popupWindow = null;
		}
		View popupWindow_view = getLayoutInflater().inflate(R.layout.main_pop, null,  false);  
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int mScreenWidth = displayMetrics.widthPixels;
		int mwidth = mScreenWidth/2;
		// 创建PopupWindow实例
		popupWindow = new PopupWindow(popupWindow_view,  mwidth,LayoutParams.WRAP_CONTENT,true);  
		popupWindow_view.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if(popupWindow !=null && popupWindow.isShowing()){
					popupWindow.dismiss();
					popupWindow = null;
				}
				return false;
			}

			
		});

		LinearLayout mainpopll = (LinearLayout)popupWindow_view.findViewById(R.id.main_pop_linear);
		mainpopll.setOnKeyListener(new OnKeyListener()
		{
			@Override
		    public boolean onKey(View v,  int keyCode, KeyEvent event)
		    {
		        if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK)
		        	
		          
		            popupWindow.dismiss();
		           
		        return false;
		    }

			
			
		});
		
		TextView grid = (TextView)popupWindow_view.findViewById(R.id.mainviewgrid);
		grid.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				if(list_type == 0){
					popupWindow.dismiss();
					popupWindow = null;
				}else{
					list_type = 0;
				list_by_grid();
				editor = preferences.edit();
		        editor.putInt("list_type", 0);
		       
		        editor.commit();
				
				popupWindow.dismiss();
				popupWindow = null;
				}
			}
			
		});
		TextView list = (TextView)popupWindow_view.findViewById(R.id.mainviewlist);
		list.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(list_type == 1){
					popupWindow.dismiss();
					popupWindow = null;
				}else{
					list_type = 1;
				list_by_list();
				editor = preferences.edit();
		        editor.putInt("list_type", 1);
		       
		        editor.commit();
				
				popupWindow.dismiss();
				popupWindow = null;
				}
			}
			
		});
		
		TextView sort_date = (TextView)popupWindow_view.findViewById(R.id.mainorder1);
		sort_date.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Collections.sort(mlist2,comparator);
				if(list_type == 0){
				    madapter.notifyDataSetChanged();
				}else{
					madapter2.notifyDataSetChanged();
				}
				editor = preferences.edit();
		        editor.putInt("sort_type", 0);
		       
		        editor.commit();
				popupWindow.dismiss();
				popupWindow = null;
			}
			
		});
		TextView sort_title = (TextView)popupWindow_view.findViewById(R.id.mainorder2);
		sort_title.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Collections.sort(mlist2,comparator2);
				if(list_type == 0){
				    madapter.notifyDataSetChanged();
				}else{
					madapter2.notifyDataSetChanged();
				}
				editor = preferences.edit();
		        editor.putInt("sort_type", 1);
		       
		        editor.commit();
				popupWindow.dismiss();
				popupWindow = null;
			}
			
		});
		
		TextView setting = (TextView)popupWindow_view.findViewById(R.id.mainset);
		setting.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent= new Intent(context, Activity_Setting.class);
				startActivity(intent);
				popupWindow.dismiss();
				popupWindow = null;
			}
			
		});
		
	}
	
	public void list_folders(){
		
	
		mlist2 = new ArrayList<Photo_info>();
		mlist2.clear();
		preferences = getSharedPreferences("MyTinyScan", MODE_PRIVATE);		
		list_type = preferences.getInt("list_type", 0);		
	    sort_type = preferences.getInt("sort_type", 0);
		files = new File(root_Path).listFiles();		
		for(int i = 0; i < files.length; i++){
			
			File[] mFile2 = files[i].listFiles();
			name = files[i].list();
			BitmapTools.sort(name);
			
		    if(mFile2.length > 0){
			
			 Photo_info minfo = new Photo_info(files[i].getName(),mFile2[0].getName().substring(0, 4)+"-"+mFile2[0].getName().substring(4, 6)+"-"+mFile2[0].getName().substring(6, 8),mFile2.length,name[0], false);
			
			 mlist2.add(minfo);
		   }
		}
		if(sort_type == 0){
			Collections.sort(mlist2,comparator);
		}else{
			Collections.sort(mlist2,comparator2);
		}
		
		if(list_type == 0){
			list_by_grid();
		}else{
			list_by_list();
		}
		
		
	}
	
	public void list_by_grid(){
		listphotos.removeAllViews();
		View mView = inflater.inflate(R.layout.list_gridview, null);
		listphotos.addView(mView);
		GridView mView2 = (GridView)mView.findViewById(R.id.main_grid);
		mView2.setBackgroundResource(R.drawable.main_bg);
		mView2.setNumColumns(2);
		mView2.setSelector(new ColorDrawable(Color.TRANSPARENT));
		madapter = new GridAdapter(context, mlist2);
		mView2.setAdapter(madapter);
		mView2.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(context, Activity_EditPhoto.class);
				
				MyApplication.folder_path = mlist2.get(arg2).getName();
				MyApplication.folder_id = arg2;
				MyApplication.isAdd = false;
				startActivity(intent);
				
			}
			
		});
		
		
		
		mView2.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				 if(isSearch){
						clear();
				 }
				if(mlist2.get(arg2).isCheck()){
					 mlist2.get(arg2).setCheck(false);
				     madapter.notifyDataSetChanged();
				     idlist.remove(mlist2.get(arg2));
				     if(idlist.isEmpty()){
				    	 unselected();
				     }else{
				         selected();
				     }
				}else{
				       mlist2.get(arg2).setCheck(true);
				       madapter.notifyDataSetChanged();
				       Photo_info minfo = mlist2.get(arg2);
				       idlist.add(minfo);
				       selected();
				}
				return true;
			}
			
		});
		
		
		
	}
	
    public void list_by_list(){
    	listphotos.removeAllViews();
    	idlist.clear();
		View mView = inflater.inflate(R.layout.list_listview, null);
		listphotos.addView(mView);
		ListView mView2 = (ListView)mView.findViewById(R.id.main_list);
		mView2.setBackgroundResource(R.drawable.main_bg);		
		madapter2 = new ListAdapter(context, mlist2);
		mView2.setAdapter(madapter2);
		mView2.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(context, Activity_EditPhoto.class);
				
				MyApplication.folder_path = mlist2.get(arg2).getName();
				MyApplication.folder_id = arg2;
				MyApplication.isAdd = false;
				startActivity(intent);
				
			}
			
		});
		
		
		
		mView2.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				 if(isSearch){
						clear();
				 }
				if(mlist2.get(arg2).isCheck()){
					
					 mlist2.get(arg2).setCheck(false);
				     madapter2.notifyDataSetChanged();
				     idlist.remove(mlist2.get(arg2));
				     if(idlist.isEmpty()){
				    	 unselected();
				     }else{
				         selected();
				     }
				}else{
					  
				       mlist2.get(arg2).setCheck(true);
				       madapter2.notifyDataSetChanged();
				       Photo_info minfo = mlist2.get(arg2);
				       idlist.add(minfo);
				       selected();
				}
				return true;
			}
			
		});
		
	}
	
	

	
	
	
	

	public void makefolder(){
		   
		File directory = new File(root_Path);
		
		if(!directory.exists()){  
           directory.mkdir();//没有目录先创建目录  
        }  
		
		File file = new File(root_Path2);
		if(!file.exists()){  
	           file.mkdir();//没有目录先创建目录  
	    }  
		
   }

	static Comparator<Photo_info> comparator = new Comparator<Photo_info>(){
		   public int compare(Photo_info s1, Photo_info s2) {
			 
				  return s2.getImage_name().compareTo(s1.getImage_name());
			  
		   }
     };
     
     static Comparator<Photo_info> comparator2 = new Comparator<Photo_info>(){
		   public int compare(Photo_info s1, Photo_info s2) {
			   String pattern = "New Document\\("+"\\d{1,5}"+"\\)";
			   if(s1.getName().matches(pattern) && s2.getName().matches(pattern)){
				  
				   int num = Integer.parseInt(s1.getName().substring(13, s1.getName().length()-1));
				   int num2 = Integer.parseInt(s2.getName().substring(13, s2.getName().length()-1));
				  
				   return num-num2;
			   }
			   return s1.getName().toLowerCase().compareTo(s2.getName().toLowerCase());
		   }
     };
     
     public void search(){
    	 isSearch = true;
    	 logo.setVisibility(8);
    	 setlist.setVisibility(8);
    	 search.setVisibility(8);
    	 back.setVisibility(0);
    	
    	 search_layout.setVisibility(0);
    	 search_text.requestFocus();
    	
    	 InputMethodManager m = (InputMethodManager)search_text.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);   
	     m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);   
    	 search_text.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				if(arg0.toString().equals("")){
					search_delete.setVisibility(4);
				}else{
					search_delete.setVisibility(0);
				}
				getResults(arg0.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
    		 
    	 });
    	 
    	 
     }
     
     public static void clear(){
    	 logo.setVisibility(0);
    	 setlist.setVisibility(0);
    	 search.setVisibility(0);
    	 back.setVisibility(8);
    	 search_text.setText("");
    	 search_layout.setVisibility(8);
    	 if(((Activity) context).getCurrentFocus() != null){
				InputMethodManager imm = (InputMethodManager)((Activity) context).getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
		 }
    	 
    	mlist2.clear();
  		
  		File[] mFile = new File(root_Path).listFiles();
  		for(int i = 0; i<mFile.length; i++){
  			File[] mFile2 = mFile[i].listFiles();
  		
  			Photo_info minfo = new Photo_info(mFile[i].getName(),mFile2[0].getName().substring(0, 4)+"-"+mFile2[0].getName().substring(5, 6)+"-"+mFile2[0].getName().substring(7, 8),mFile2.length,mFile2[0].getName(), false);
  			mlist2.add(minfo);
  		}
  		if(sort_type == 0){
  			Collections.sort(mlist2,comparator);
  		}else{
  			Collections.sort(mlist2,comparator2);
  		}
  		
  		
  		if(list_type == 0){
  			madapter.notifyDataSetChanged();
  		}else{
  			madapter2.notifyDataSetChanged();
  		}
  		isSearch = false;
     }
     
     public void getResults(String name){
    			
 		mlist2.clear();
 		MyFilter mFilter = new MyFilter(name);
 		File[] mFile = new File(root_Path).listFiles(mFilter);
 		for(int i = 0; i<mFile.length; i++){
 			File[] mFile2 = mFile[i].listFiles();
 		
 			Photo_info minfo = new Photo_info(mFile[i].getName(),mFile2[0].getName().substring(0, 4)+"-"+mFile2[0].getName().substring(5, 6)+"-"+mFile2[0].getName().substring(7, 8),mFile2.length,mFile2[0].getName(), false);
 			mlist2.add(minfo);
 		}
 		if(sort_type == 0){
 			Collections.sort(mlist2,comparator);
 		}else{
 			Collections.sort(mlist2,comparator2);
 		}
 		
 		
 		if(list_type == 0){
 			madapter.notifyDataSetChanged();
 		}else{
 			madapter2.notifyDataSetChanged();
 		}
     }
    
     class MyFilter implements FilenameFilter{  
         private String name;  
         public MyFilter(String name){  
             this.name = name.toLowerCase();  
         }  
         public boolean accept(File dir,String folder_name){  
        	 folder_name = folder_name.toLowerCase();
        	
             return folder_name.contains(name);
         }  
     }  
     
     public  static void selected(){
    	 isSelect = true;
    	
    	 mainlayout.setBackgroundColor(Color.rgb(0, 178, 248));
    	 logo.setVisibility(8);
    	 setlist.setVisibility(8);
    	 search.setVisibility(8);
    	 back2.setVisibility(0);
    	 selecttext.setVisibility(0);
    	 selecttext.setText(""+idlist.size());
    	 takephotos.setVisibility(8);
    	 edit.setVisibility(0);
    	 share.setVisibility(0);
    	 delete.setVisibility(0);
    	 selectline.setVisibility(0);
    	 selectline2.setVisibility(0);
    	
     }
     
     public  static void unselected(){
    	 isSelect = false;
    	 for(int i =0; i<idlist.size(); i++){
    		 mlist2.get(mlist2.indexOf(idlist.get(i))).setCheck(false);
    		
    		 if(list_type == 0){
    			    madapter.notifyDataSetChanged();
    			}else{
    				madapter2.notifyDataSetChanged();
    			}
    	 }
    	 idlist.clear();
    	 mainlayout.setBackgroundColor(Color.rgb(59, 132, 206));
    	 logo.setVisibility(0);
    	 setlist.setVisibility(0);
    	 search.setVisibility(0);
    	 back2.setVisibility(8);
    	 selecttext.setText("");
    	 selecttext.setVisibility(8);
    	 selectline.setVisibility(8);
    	 selectline2.setVisibility(8);
    	 takephotos.setVisibility(0);
    	 edit.setVisibility(8);
    	 share.setVisibility(8);
    	 delete.setVisibility(8);
    	 
     }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			
			 if(isSearch){
				clear();
			 }else if(isSelect){
				unselected();
			 }else{
				 if ((System.currentTimeMillis() - mExitTime) > 1000) {
                   
                     Toast.makeText(this, "Press again to exit!!", Toast.LENGTH_SHORT).show();
                     mExitTime = System.currentTimeMillis();

                    } else {
                    	finish();
                    }
			    }
			return true;
			}
		return super.onKeyDown(keyCode, event);
	}
	
	
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("Main", "destory");
		mMemoryCache.evictAll();
		
	}

	public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	       
	    }
	}

	public static Bitmap getBitmapFromMemCache(String key) {
	    return mMemoryCache.get(key);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(MyApplication.isUpdate){
			
			if(list_type == 0){
				madapter.notifyDataSetChanged();
			}else{
				madapter2.notifyDataSetChanged();
			}
			MyApplication.isUpdate = false;
		}
	}
	
	public static int findIdByName(String name){
		for(int i=0; i<mlist2.size(); i++){
			Photo_info pi = mlist2.get(i);
			if(pi.getName().equals(name)){
				return i;
			}
		}
		return 0;
	}

	  

}
