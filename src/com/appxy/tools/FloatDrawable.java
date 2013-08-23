package com.appxy.tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.drawable.Drawable;


public class FloatDrawable extends Drawable {
	
	
	private Point left, top, right, bottom;
	
	private Paint mLinePaint=new Paint();
	{
	    mLinePaint.setARGB(200, 50, 50, 50);
	    mLinePaint.setStrokeWidth(1F);
	    mLinePaint.setStyle(Paint.Style.STROKE);
	    mLinePaint.setAntiAlias(true);
	    mLinePaint.setColor(Color.WHITE);
	}
	
	public FloatDrawable(Context context, Point left, Point top, Point right, Point bottom) {
		super();
		
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		
	}
	
	public void setPoints(Point left, Point top, Point right, Point bottom){
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}
	
	

	@Override
	public void draw(Canvas canvas) {
		
		Path path = new Path();
		
		
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(5);
		path.moveTo(left.x, left.y);
		path.lineTo(top.x, top.y);
		path.lineTo(right.x, right.y);
		path.lineTo(bottom.x,bottom.y);
		path.close();
		
		canvas.drawPath(path, paint);
		paint.setStrokeWidth(2);
		PathEffect effects = new DashPathEffect(new float[]{10,10,10,10},1);  
        paint.setPathEffect(effects); 
        
        Path path2 = new Path();
        path2.moveTo((left.x+top.x)/2, (left.y+top.y)/2);
        path2.lineTo((bottom.x+right.x)/2, (bottom.y+right.y)/2);
        canvas.drawPath(path2, paint);
        
        Path path3 = new Path();
        path3.moveTo(((left.x+top.x)/2+left.x)/2, ((left.y+top.y)/2+left.y)/2);
        path3.lineTo(((bottom.x+right.x)/2+bottom.x)/2, ((bottom.y+right.y)/2+bottom.y)/2);
        canvas.drawPath(path3, paint);
        
        Path path4 = new Path();
        path4.moveTo(((left.x+top.x)/2+top.x)/2, ((left.y+top.y)/2+top.y)/2);
        path4.lineTo(((bottom.x+right.x)/2+right.x)/2, ((bottom.y+right.y)/2+right.y)/2);
        canvas.drawPath(path4, paint);
       
        
        Path path5 = new Path();
        path5.moveTo((left.x+bottom.x)/2, (left.y+bottom.y)/2);
        path5.lineTo((top.x+right.x)/2, (top.y+right.y)/2);
        canvas.drawPath(path5, paint);
        
        Path path6 = new Path();
        path6.moveTo(((left.x+bottom.x)/2+left.x)/2, ((left.y+bottom.y)/2+left.y)/2);
        path6.lineTo(((top.x+right.x)/2+top.x)/2, ((top.y+right.y)/2+top.y)/2);
        canvas.drawPath(path6, paint);
        
        Path path7 = new Path();
        path7.moveTo(((left.x+bottom.x)/2+bottom.x)/2, ((left.y+bottom.y)/2+bottom.y)/2);
        path7.lineTo(((top.x+right.x)/2+right.x)/2, ((top.y+right.y)/2+right.y)/2);
        canvas.drawPath(path7, paint);
        
       
	}

	

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Point getLeft() {
		return left;
	}

	public void setLeft(Point left) {
		this.left = left;
	}

	public Point getTop() {
		return top;
	}

	public void setTop(Point top) {
		this.top = top;
	}

	public Point getRight() {
		return right;
	}

	public void setRight(Point right) {
		this.right = right;
	}

	public Point getBottom() {
		return bottom;
	}

	public void setBottom(Point bottom) {
		this.bottom = bottom;
	}
	
	
	

}
