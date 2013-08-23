package com.appxy.tools;


 public class LibImgFun {  
 static {   
	    
         System.loadLibrary("ImgFun");   
  
        }   
      
    
    // public static native int[] ImgFunInt(byte[] btArr);
     public static native int[] ImgFunInt(String path);
    // public static native int Transfer(byte[]data, int[]data2, byte[] path, String imagename);
     public static native int Transfer(String path, int[]data, String newpath, int degree);
     
 }