package com.appxy.tools;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager{
	
	private ImageViewTouch image;
	private float oldX;
	public MyViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	
	public MyViewPager(Context context, AttributeSet attrs) {
	super(context, attrs);
	// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
	     
		switch(arg0.getAction()){
		case MotionEvent.ACTION_DOWN:
			oldX = arg0.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			float newX = arg0.getX();
			if(newX >= oldX){
				if(image.canScroll(1)){
					return false;
					
				}else{
					
					return super.onInterceptTouchEvent(arg0);
				}
			}else{
				if(image.canScroll(-1)){
					
					return false;
				}else{
					return super.onInterceptTouchEvent(arg0);
					
				}
			}
			
			
		}
		return super.onInterceptTouchEvent(arg0);
		  
	 }
	
	 
	 public void setImage(ImageViewTouch image){
		 this.image = image;
	 }
	    

}
