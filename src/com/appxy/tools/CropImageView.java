package com.appxy.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CropImageView extends ImageView{
	
	Canvas canvas;
	Bitmap bitmap;
	Context context;
	Point left,top,right,bottom;

	public CropImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public CropImageView(Context context, Point left, Point top, Point right, Point bottom, Bitmap bitmap) {
		super(context);
		// TODO Auto-generated constructor stub	
		this.context = context;
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.bitmap = bitmap;
		
		
	}
	
	public CropImageView(Context context, AttributeSet attrs) {
		super(context, attrs);		
	}

	public CropImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
					
	}
	
	

	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		super.setImageBitmap(bm);
		bitmap = bm;
		
	}
	
	
	

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		//super.onDraw(canvas);
		if(bitmap == null){
			return;
		}
		
		
	}

}
