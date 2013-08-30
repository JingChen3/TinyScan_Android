package com.appxy.tools;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;


public class BitmapTools {
	
	
	 public static Bitmap getSDCardImg(String imagePath) {
         BitmapFactory.Options opt = new BitmapFactory.Options();
         opt.inPreferredConfig = Bitmap.Config.RGB_565;
         opt.inPurgeable = true;
         opt.inInputShareable = true;
         // 获取资源图片
         return BitmapFactory.decodeFile(imagePath, opt);
    }
	 
	
	
	 
	 
	 public static Bitmap resizeImage(Bitmap bitmap) {
			
		   
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			
			
			float scale = 320.0f/width;
			
			Matrix matrix = new Matrix();
			
			matrix.postScale(scale, scale);
		
			Bitmap newbitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
					height, matrix, true);
		
			return newbitmap;
		}
	 
	 
	 public static Bitmap resizeImage2(Bitmap bitmap, int width , int height) {
			
			
			int mwidth = bitmap.getWidth();
			int mheight = bitmap.getHeight();
			float scale = 1f;
			float scale1 = 1f;
			float scale2 = 1f;
			scale1 = (width+0.0f)/mwidth;
			scale2 = (height+0.0f)/mheight;
			if(scale1>scale2){
				scale = scale2;
			}else{
				scale = scale1;
			}
			Log.e("sad",scale+"  "+scale1+"   "+scale2);
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			Bitmap newbitmap = Bitmap.createBitmap(bitmap, 0, 0, mwidth, mheight, matrix, true);
			return newbitmap;
			
		}
	 
	 
	 
	 public static Bitmap getPhoto(String path,int reqWidth, int reqHeight){
		 BitmapFactory.Options options = new BitmapFactory.Options();
		 options.inJustDecodeBounds = true;
		 
		 BitmapFactory.decodeFile(path, options);
		 options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		    // Decode bitmap with inSampleSize set
		 options.inJustDecodeBounds = false;
		 try
		  {
		   Bitmap bmp = BitmapFactory.decodeFile(path, options);
		   if(bmp.getWidth()>bmp.getHeight()){
			   Matrix matrix = new Matrix();
			   matrix.postRotate(-90);
			   bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
		   }
		   return bmp;
		  }
		  catch (OutOfMemoryError e)
		  {
			  
		  }
		  return null;
		 
		
	 }
	 
	 public static int calculateInSampleSize(
	            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	    if(width >= height){
	    	inSampleSize = Math.round((float) width / (float) reqWidth);
	    }else{
	    	inSampleSize = Math.round((float) height / (float) reqHeight);
	    }
    
	    /*if (height > reqHeight || width > reqWidth) {

	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);

	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }*/

	    return inSampleSize;
	}
	 
	 public static void sort(String[] str){
			for(int i=0;i<str.length-1;i++){
				String maxStr = str[i];
				int index = i;
				for(int j=i+1;j<str.length;j++){
					
					if(maxStr.substring(maxStr.length()-7, maxStr.length()-4).compareTo(str[j].substring(str[j].length()-7, str[j].length()-4)) > 0){
						maxStr = str[j];
						index = j;
					}
				}
				str[index] = str[i];
				str[i] = maxStr;
			}
      }
	 
	 
	 public static Bitmap decodeBitmapFromResource(Resources res, int resId,
		        int reqWidth, int reqHeight) {

		    // First decode with inJustDecodeBounds=true to check dimensions
		    final BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    BitmapFactory.decodeResource(res, resId, options);

		    // Calculate inSampleSize
		    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		    // Decode bitmap with inSampleSize set
		    options.inJustDecodeBounds = false;
		    
		    return BitmapFactory.decodeResource(res, resId, options);
		}
	
	 

}
