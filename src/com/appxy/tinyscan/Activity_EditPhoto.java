package com.appxy.tinyscan;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.appxy.tools.BitmapTools;
import com.appxy.tools.FoldernameAdapter;
import com.appxy.tools.MyViewPager;
import com.appxy.tools.Photo_info;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class Activity_EditPhoto extends Activity{
	
	public MyViewPager mPager;			
	private Context context;
	private String root_Path = Environment.getExternalStorageDirectory().getPath()+"/MyTinyScan/";
	private String root_Path2 = Environment.getExternalStorageDirectory().getPath()+"/MyTinyScan_PDF/";
	private String photo_path;	
	private String folder_name;	
	private PopupWindow popupWindow;
	private int num = 1;	
	public ImageViewTouch imgview;  
    private ImageView back, list, takephoto, delete, rotate,  move, share;
    private TextView documentname, other;
    private ProgressBar  pb;  
    ProgressDialog progressDialog;
    private File[] files;  
    public static  List<String> mlist;
   
   Thread mThread;
    private float degree = 0f;
   
    Bitmap bm;
    float oldX, oldY;
    Matrix imageMatrix;
    ImageViewTouch image;
    ArrayList<String> namelist;
    int mwidth, mheight;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit_photo);
		context = this;
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mwidth = metrics.widthPixels;
		mheight = metrics.heightPixels - 200;
		
		mlist = new ArrayList<String>();
		
	
		mPager = (MyViewPager)findViewById(R.id.edit_photo_viewpagger);
		mPager.setOffscreenPageLimit(2);
		
		back = (ImageView)findViewById(R.id.edit_photo_back);
		back.setOnClickListener(mlistener2);
		
		list = (ImageView)findViewById(R.id.edit_photo_list);
		list.setOnClickListener(mlistener2);
		
		takephoto = (ImageView)findViewById(R.id.edit_photo_takephoto);
		takephoto.setOnClickListener(mlistener2);
		
		delete = (ImageView)findViewById(R.id.edit_photo_delete);
		delete.setOnClickListener(mlistener2);
		
		pb = (ProgressBar)findViewById(R.id.edit_photo_pb);
		share = (ImageView)findViewById(R.id.edit_photo_share);
		share.setOnClickListener(mlistener2);
		rotate = (ImageView)findViewById(R.id.edit_photo_rotate);		
		rotate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				pb.setVisibility(0);
				View view = mPager.findViewWithTag(mlist.get(num-1));
				image = (ImageViewTouch)view.findViewById(R.id.photo);
				
				
				new Thread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						Matrix imageMatrix = new Matrix();
						imageMatrix.postRotate(90);
						Log.e("sad", degree+90+"");
						
						
						Bitmap bitmap2 = BitmapFactory.decodeFile(mlist.get(num-1));
						
						Bitmap bm2 = Bitmap.createBitmap(bitmap2, 0, 0, bitmap2.getWidth(), bitmap2.getHeight(), imageMatrix, true);
						bitmap2.recycle();
						File file = new File(mlist.get(num-1));
						OutputStream out = null;
                        try {
							out = new BufferedOutputStream(new FileOutputStream(file));
							bm2.compress(CompressFormat.JPEG, 85, out);
							out.flush();
							out.close();
							
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
                        bm2.recycle();
                        Bitmap bitmap = BitmapFactory.decodeFile(mlist.get(num-1));
                        bm = BitmapTools.resizeImage2(bitmap,mwidth-80, mheight-80);
                        bitmap.recycle();
						Message m = new Message();
						m.what = 1;
						handler.sendMessage(m);
					}
					
				}).start();
				
			}
			
		});
		
		move = (ImageView)findViewById(R.id.edit_photo_move);
		move.setOnClickListener(mlistener2);
		
		photo_path = root_Path + MyApplication.folder_path;
		
	
		folder_name = MyApplication.folder_path;
		documentname = (TextView)findViewById(R.id.edit_photo_name);
		
		other = (TextView)findViewById(R.id.edit_photo_pagenum);
		documentname.setText(folder_name);
		File mFile = new File(photo_path);
		files = mFile.listFiles();
		
		for(int i=0; i<files.length; i++){
		   
			mlist.add(files[i].getPath());
		}
		Collections.sort(mlist,comparator);
		other.setText(num +"/"+mlist.size());
		mPager.setAdapter(mAdapter);
	    
		mPager.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				
				
				num = arg0+1;
				other.setText(num +"/"+mlist.size());				
				degree = 0f;
				Log.e("sad", mlist.get(num-1));
			}
	    	
	    });
	    
	    
	    
	}
	
	Comparator<String> comparator = new Comparator<String>(){
		   public int compare(String s1, String s2) {
			    
				  return s1.substring(s1.length()-7, s1.length()-4).compareTo(s2.substring(s2.length()-7, s2.length()-4));
			  
		   }
    };
	
	
	Handler handler = new Handler(){   
		public void handleMessage(Message msg) {   
		switch (msg.what) {   
	
		case 1:
			pb.setVisibility(4);
			image.setImageBitmap(bm);			
			degree+=90f;
     	
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
    
	
	OnClickListener mlistener2 = new OnClickListener(){
		
		

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			switch(arg0.getId()){
			case R.id.edit_photo_back:
				
				finish();
				break;
			case R.id.edit_photo_list:
				Intent intent2 = new Intent(context, Activity_ListPhotos.class);
				intent2.putExtra("folder_name", folder_name);
				intent2.putExtra("photo_path", photo_path);
				intent2.putStringArrayListExtra("mlist", (ArrayList<String>) mlist);				
				startActivityForResult(intent2, 0);
				
				break;
			case R.id.edit_photo_delete:
				Dialog alertDialog = new AlertDialog.Builder(context). 
	            setMessage("Are you sure ?").setPositiveButton("Yes", new DialogInterface.OnClickListener(){
	            	 @Override 
	                 public void onClick(DialogInterface dialog, int which) { 
	            		 
	                     // TODO Auto-generated method stub  
	            		File file = new File(mlist.get(num-1));
	            		file.delete();
	            		MyApplication.isUpdate = true;
	            		
	            		
	            		File mFile = new File(photo_path);
	    				files = mFile.listFiles();
	    				
	    				if(files.length<1){
	    					mFile.delete();
	    					Activity_Main.mlist2.remove(MyApplication.folder_id);
	    					finish();
	    				}else{
	    					
	    					
	    					
	    					
							
							mlist.remove(num-1);
							Activity_Main.mlist2.get(MyApplication.folder_id).setImage_num(mlist.size());
		            		Collections.sort(mlist,comparator);
		            		//Activity_Main.mlist2.get(MyApplication.folder_id).setImage_name(mlist.get(0));
		            		if(num == 1){
		            			if(Activity_Main.mMemoryCache.get(MyApplication.folder_path) != null){
		            				Activity_Main.mMemoryCache.remove(MyApplication.folder_path);
		            				String[] name = mlist.get(0).split("/");
		            				Activity_Main.mlist2.get(MyApplication.folder_id).setName(MyApplication.folder_path);
		            				Activity_Main.mlist2.get(MyApplication.folder_id).setImage_name(name[name.length-1]);
		            			}
		            		}
		            		
		            		mPager.setAdapter(mAdapter);
	    					num = num - 1;
	    					if(num == 0){
	    						num = 1;
	    					}
	    					
	    					mPager.setCurrentItem(num-1);
	    					
		    				other.setText(num +"/"+mlist.size());
	    					
	    				
	    				}
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
			
			case R.id.edit_photo_takephoto:
				MyApplication.where = true;
				MyApplication.addpath = photo_path;
				Intent addintent = new Intent(context, Activity_CameraPreview.class);
				startActivity(addintent);
				
				break;
			
			case R.id.edit_photo_move:
				
				File file = new File(root_Path);
				File[] mFile = file.listFiles();
				namelist = new ArrayList<String>();
				for(int i=0; i <mFile.length; i++){
					if(mFile[i].getName().equals(folder_name)){
						
					}else{
						namelist.add(mFile[i].getName());
					}
				}
				
				if(namelist.size()>0){
				initPopuptWindow();
				int [] pos = new int[2];  
				arg0.getLocationOnScreen(pos);  
				
				popupWindow.showAtLocation(arg0,Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
				}else{
					Toast.makeText(context, "No other documents", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.edit_photo_share:
				
				
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
		     					File file = new File(root_Path + MyApplication.folder_path);
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
		     			PdfCopyFields copy	= null;
		     			try {
							copy = new PdfCopyFields(new FileOutputStream(root_Path2+"MyTinyScan.pdf"));
							File file6 = new File(root_Path2);
					        File[] name = file6.listFiles();
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
			default:
				break;
			}
		}
		
	};
	
	PagerAdapter mAdapter = new PagerAdapter(){

		

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mlist.size();
			
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
		
		@Override
		public void destroyItem(View arg0, int position, Object object) {
			// TODO Auto-generated method stub
			View view = (View)object;
	        ((ViewPager)arg0).removeView(view);
	        view = null;
		}

		@Override
		public Object instantiateItem(View arg0, int position) {
			// TODO Auto-generated method stub
			View view = LayoutInflater.from(context).inflate(R.layout.photo, null);
			view.setTag(mlist.get(position));	
			((ViewPager) arg0).addView(view);
			ImageViewTouch imageView = (ImageViewTouch)view.findViewById(R.id.photo);	
			imageView.setDisplayType( DisplayType.FIT_IF_BIGGER);
			Bitmap bitmap = BitmapFactory.decodeFile(mlist.get(position));
			Bitmap bitmap2 = BitmapTools.resizeImage2(bitmap,mwidth-80, mheight-80);
			
			imageView.setImageBitmap(bitmap2);
			return view;
	    }
     };

	
      
    



	


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		switch(resultCode){
		case 1:
			relist();
			break;
		
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
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
	
	
	public void initPopuptWindow() {  
		// TODO Auto-generated method stub  
		  
		// 获取自定义布局文件pop.xml的视图 
		if(popupWindow != null){
			popupWindow = null;
		}
		final View popupWindow_view = getLayoutInflater().inflate(R.layout.move_photo_pop, null,  false);  
		
		// 创建PopupWindow实例
		DisplayMetrics dm = new DisplayMetrics();   
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float mScreenWidth = dm.widthPixels;
		float mScreenheight = dm.heightPixels;
		int mwidth = (int)mScreenWidth*9/10;
		int mheight = (int)mScreenheight/2;
		if(namelist.size()<=4){
			popupWindow = new PopupWindow(popupWindow_view,  mwidth,LayoutParams.WRAP_CONTENT,true);
		}else{
			popupWindow = new PopupWindow(popupWindow_view,  mwidth,mheight,true);
		}
		 
		
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		ListView mview = (ListView)popupWindow_view.findViewById(R.id.move_photo_list);
		
		FoldernameAdapter adapter = new FoldernameAdapter(context, namelist);
		mview.setAdapter(adapter);
		mview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				
				
				MyApplication.isUpdate = true;
				File file = new File(mlist.get(num-1));
				
				String size = file.getName().substring(14, 15);
				Log.e("sad", size);
				File file2 = new File(root_Path + namelist.get(arg2));			
				String[] name = file2.list();	
				BitmapTools.sort(name);
				String id="";
				int id2 = Integer.parseInt(name[name.length-1].substring(15, 18)) + 1;
				if(id2<10){
					id="00"+id2;
				}else if(id2<100){
					id="0"+id2;
				}else{
					id=""+id2;
				}
				String name2 = root_Path + namelist.get(arg2) + "/" + name[0].substring(0, 14) + size + id + ".jpg";
				file.renameTo(new File(name2));	
				File mfile = new File(photo_path);
				files = mfile.listFiles();
				if(files.length<1){
					mfile.delete();
					Activity_Main.mlist2.remove(MyApplication.folder_id);
					
				}else{
					mlist.remove(num-1);
					Collections.sort(mlist,comparator);
            		//Activity_Main.mlist2.get(MyApplication.folder_id).setImage_name(mlist.get(0));
            		if(num == 1){
            			if(Activity_Main.mMemoryCache.get(MyApplication.folder_path) != null){
            				Activity_Main.mMemoryCache.remove(MyApplication.folder_path);
            				String[] name3 = mlist.get(0).split("/");
            				Activity_Main.mlist2.get(MyApplication.folder_id).setName(MyApplication.folder_path);
            				Activity_Main.mlist2.get(MyApplication.folder_id).setImage_name(name3[name3.length-1]);
            			}
            		}
					Activity_Main.mlist2.get(MyApplication.folder_id).setImage_num(Activity_Main.mlist2.get(MyApplication.folder_id).getImage_num()-1);
				}
				
				MyApplication.folder_path = namelist.get(arg2);
				MyApplication.folder_id = Activity_Main.findIdByName(namelist.get(arg2));
			   
			    Activity_Main.mlist2.get(MyApplication.folder_id).setImage_num(Activity_Main.mlist2.get(MyApplication.folder_id).getImage_num()+1);
				photo_path = root_Path + MyApplication.folder_path;
			    folder_name = MyApplication.folder_path;
				documentname.setText(namelist.get(arg2));
				mlist.clear();
				File mFile = new File(photo_path);
				files = mFile.listFiles();
				
				for(int i=0; i<files.length; i++){
				   
					mlist.add(files[i].getPath());
				}
				Collections.sort(mlist,comparator);			
			    mPager.setAdapter(mAdapter);
			    mPager.setCurrentItem(mlist.size()-1);
				Toast.makeText(context, "Move Success!!", Toast.LENGTH_SHORT).show();
				popupWindow.dismiss();
			}
			
		});
		
		
	}
	
	
	
	public void relist(){
		mlist.clear();
		File mFile = new File(photo_path);
		files = mFile.listFiles();
		
		if(files.length<1){
			mFile.delete();
			finish();
		}else{
		  
			for(int i=0; i<files.length; i++){
				
				mlist.add(files[i].getPath());
			}
			Collections.sort(mlist,comparator);
			mPager.setAdapter(mAdapter);
			num = 1;
			other.setText(num +"/"+mlist.size());
			
		
		}
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(MyApplication.isAdd){
			mPager.setCurrentItem(mlist.size()-1);
			  Activity_Main.mlist2.get(MyApplication.folder_id).setImage_num(Activity_Main.mlist2.get(MyApplication.folder_id).getImage_num()+1);
			  MyApplication.isUpdate = true;
			  MyApplication.isAdd = false;
		}
		
		
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		
		 View view = mPager.findViewWithTag(mlist.get(num-1));
				image = (ImageViewTouch)view.findViewById(R.id.photo);
				mPager.setImage(image);
				
		
		return super.dispatchTouchEvent(ev);
	}
}
