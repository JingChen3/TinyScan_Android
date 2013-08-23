package com.appxy.tinyscan;

import com.appxy.tinyscan.MyApplication;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.BoringLayout.Metrics;

public class MyApplication extends Application {
   
    //public static LruCache<String, Bitmap> mMemoryCache;
    public static Bitmap nowbitmap;
    public static Bitmap savebitmap;
    public static Bitmap tempbitmap;
    public static boolean where = false;
  
    public static boolean isUpdate = false;
    
    public static boolean isAdd = false;
    public static String addpath;
    public static String folder_path;
    public static int folder_id;
    public static int degree;
    public static int sizeid = 0;
    public void onCreate(){
        super.onCreate();
       
        nowbitmap = null;
        savebitmap = null;
        tempbitmap = null;
      
        // Get max available VM memory, exceeding this amount will throw an
	    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
	    // int in its constructor.
	    /*final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

	    // Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 8;
	    
	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	        	
	        	return bitmap.getByteCount() / 1024;
	           
	        }
	    };*/
	   
	    
    }

   
   /* public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}

	public static Bitmap getBitmapFromMemCache(String key) {
	    return mMemoryCache.get(key);
	}*/
}
