package com.appxy.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class CropImageView3 extends View {
	
	
	private float oldX=0;
	private float oldY=0;
	
	
	private final int STATUS_Touch_SINGLE=1;
	private final int STATUS_TOUCH_MULTI_START=2;
	private final int STATUS_TOUCH_MULTI_TOUCHING=3;
	
	private int mStatus=STATUS_Touch_SINGLE;
	

	
	private final int EDGE_LT=1;
	private final int EDGE_RT=2;
	private final int EDGE_LB=3;
	private final int EDGE_RB=4;
	
	private final int EDGE_MOVE_OUT=6;
	private final int EDGE_NONE=7;
	
	public int currentEdge=EDGE_NONE;
	
	protected float oriRationWH=0;
	
	protected FloatDrawable mFloatDrawable;
	
	protected Context mContext;
	
	
	public Point left, top, bottom, right;
	private Bitmap bitmap;
    
	public CropImageView3(Context context) {
		super(context);
		
		init(context);
	}

	public CropImageView3(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		
	}

	public CropImageView3(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
			
	}
	
	public CropImageView3(Context context, Bitmap bitmap,Point left, Point top, Point right, Point bottom) {
		super(context);
		init(context);
        this.bitmap = bitmap;				
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		mFloatDrawable=new FloatDrawable(mContext, left, top, right, bottom);	
		invalidate();	
	}
	

	private void init(Context context)
	{
		this.mContext=context;
		
	}

	
	
	public void setBitmap(Bitmap bitmap,Point left, Point top, Point right, Point bottom)
	{
		
		this.bitmap = bitmap;		
		
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		mFloatDrawable=new FloatDrawable(mContext, left, top, right, bottom);
	
		invalidate();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(event.getPointerCount()>1)
		{
			if(mStatus==STATUS_Touch_SINGLE)
			{
				mStatus=STATUS_TOUCH_MULTI_START;
			}
			else if(mStatus==STATUS_TOUCH_MULTI_START)
			{
				mStatus=STATUS_TOUCH_MULTI_TOUCHING;
			}
		}
		else
		{
			if(mStatus==STATUS_TOUCH_MULTI_START||mStatus==STATUS_TOUCH_MULTI_TOUCHING)
			{
				oldX=event.getX();
				oldY=event.getY();
			}
			
			mStatus=STATUS_Touch_SINGLE;
		}
		


		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				oldX = event.getX();
				oldY = event.getY();
				currentEdge=getTouchEdge((int)oldX, (int)oldY);

	            break;
	            
			case MotionEvent.ACTION_UP:
				
	            break;
	            
			
				
			case MotionEvent.ACTION_POINTER_UP:
				currentEdge=EDGE_NONE;
				break;
	            
			case MotionEvent.ACTION_MOVE:
				
				
				//Log.e("sad1", isRect(mFloatDrawable.getLeft(), mFloatDrawable.getTop(), mFloatDrawable.getRight())+isRect(mFloatDrawable.getTop(), mFloatDrawable.getRight(), mFloatDrawable.getBottom())+isRect(mFloatDrawable.getRight(), mFloatDrawable.getBottom(), mFloatDrawable.getLeft())+isRect(mFloatDrawable.getBottom(), mFloatDrawable.getLeft(), mFloatDrawable.getTop())+"  ");
				if(mStatus==STATUS_TOUCH_MULTI_TOUCHING)
				{
					
				}
				else if(mStatus==STATUS_Touch_SINGLE)
				{
					int dx=(int)(event.getX()-oldX);
					int dy=(int)(event.getY()-oldY);
					
					oldX=event.getX();
					oldY=event.getY();
					
					int width = bitmap.getWidth();
					int height = bitmap.getHeight();
					
					if(!(dx==0&&dy==0))
					{
						switch(currentEdge)
						{
								case EDGE_LT:
									
									int tox = mFloatDrawable.getLeft().x+dx;
									int toy = mFloatDrawable.getLeft().y+dy;
									
									if(tox>0 &&tox<width &&toy>0 &&toy<height ){
										mFloatDrawable.setLeft(new Point(tox,toy));
									}else if(tox>0&&tox<width){
										mFloatDrawable.setLeft(new Point(tox,mFloatDrawable.getLeft().y));
									}else if(toy>0 &&toy<height){
										mFloatDrawable.setLeft(new Point(mFloatDrawable.getLeft().x,toy));
									}
									
									break;
									
								case EDGE_RT:
									int tox2 = mFloatDrawable.getTop().x+dx;
									int toy2 = mFloatDrawable.getTop().y+dy;
									
									if(tox2>0 &&tox2<width &&toy2>0 &&toy2<height ){
										mFloatDrawable.setTop(new Point(tox2,toy2));
									}else if(tox2>0&&tox2<width){
										mFloatDrawable.setTop(new Point(tox2,mFloatDrawable.getTop().y));
									}else if(toy2>0 &&toy2<height){
										mFloatDrawable.setTop(new Point(mFloatDrawable.getTop().x,toy2));
									}
									break;
									
								case EDGE_LB:
									int tox3 = mFloatDrawable.getBottom().x+dx;
									int toy3 = mFloatDrawable.getBottom().y+dy;
									
									if(tox3>0 &&tox3<width &&toy3>0 &&toy3<height ){
										mFloatDrawable.setBottom(new Point(tox3,toy3));
									}else if(tox3>0&&tox3<width){
										mFloatDrawable.setBottom(new Point(tox3,mFloatDrawable.getBottom().y));
									}else if(toy3>0 &&toy3<height){
										mFloatDrawable.setBottom(new Point(mFloatDrawable.getBottom().x,toy3));
									}
									break;
									
								case EDGE_RB:
									int tox4 = mFloatDrawable.getRight().x+dx;
									int toy4 = mFloatDrawable.getRight().y+dy;
									
									if(tox4>0 &&tox4<width &&toy4>0 &&toy4<height ){
										mFloatDrawable.setRight(new Point(tox4,toy4));
									}else if(tox4>0&&tox4<width){
										mFloatDrawable.setRight(new Point(tox4,mFloatDrawable.getRight().y));
									}else if(toy4>0 &&toy4<height){
										mFloatDrawable.setRight(new Point(mFloatDrawable.getRight().x,toy4));
									}
									break;
									
								
								case EDGE_MOVE_OUT:
									break;
						}
						
						invalidate();
					}
				}
	            break;
		}
		
		return true;
	}

    public int getTouchEdge(int eventX,int eventY)
    {
    	
    	Point point = new Point(eventX, eventY);
    	int toleft = getPointtoPoint(point, mFloatDrawable.getLeft());
    	int totop = getPointtoPoint(point, mFloatDrawable.getTop());
    	int toright = getPointtoPoint(point, mFloatDrawable.getRight());
    	int tobottom = getPointtoPoint(point, mFloatDrawable.getBottom());
        if(toleft<totop && toleft<toright && toleft<tobottom){
        	return EDGE_LT;
        }
        if(totop<toleft && totop<toright && totop<tobottom){
        	return EDGE_RT;
        }
        if(toright<totop && toright<toleft && toright<tobottom){
        	return EDGE_RB;
        }
        if(tobottom<totop && tobottom<toright && tobottom<toleft){
        	return EDGE_LB;
        }
       
    	return EDGE_MOVE_OUT;
    }
    
	@Override
	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
		if (bitmap == null) {
            return; // couldn't resolve the URI
        }
		canvas.drawBitmap(bitmap, 0,0, null);
		
        mFloatDrawable.draw(canvas);
	}
	
	public int getPointtoPoint(Point a, Point c){
		int x = Math.abs(a.x - c.x);
    	int y = Math.abs(a.y - c.y);
    	int r = (int) Math.sqrt((x * x) + (y * y));
    	return r;
	}
	
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		 setMeasuredDimension(bitmap.getWidth(),bitmap.getHeight());
	}

	public int[] getPoint() {
		int[] data = new int[8];
		data[0] = mFloatDrawable.getLeft().x;
		data[1] = mFloatDrawable.getLeft().y;
		data[2] = mFloatDrawable.getTop().x;
		data[3] = mFloatDrawable.getTop().y;
		data[4] = mFloatDrawable.getRight().x;
		data[5] = mFloatDrawable.getRight().y;
		data[6] = mFloatDrawable.getBottom().x;
		data[7] = mFloatDrawable.getBottom().y;
		return data;
	}
	
	public void initPoint() {
		mFloatDrawable.setLeft(new Point(0,0));
		mFloatDrawable.setTop(new Point(bitmap.getWidth(),0));
		mFloatDrawable.setRight(new Point(bitmap.getWidth(), bitmap.getHeight()));
		mFloatDrawable.setBottom(new Point(0, bitmap.getHeight()));
		invalidate();
	}
	
	public void initPoint2() {
		mFloatDrawable.setLeft(left);
		mFloatDrawable.setTop(top);
		mFloatDrawable.setRight(right);
		mFloatDrawable.setBottom(bottom);
		invalidate();
	}

	public double isRect(Point p1, Point p2, Point p3) 
	{
	    int x1,y1,x2,y2,x4,y4; 
	    x1 = p1.x;
	    y1 = p1.y;
	    x2 = p2.x;
	    y2 = p2.y;
	    x4 = p3.x;
	    y4 = p3.y;
	   
	    int ma_x = x1 - x2;
	    int ma_y = y1 - y2;
	    int mb_x = x4 - x2;
	    int mb_y = y4 - y2;
	    int v1 = (ma_x * mb_x) + (ma_y * mb_y);
	    double ma_val = Math.sqrt(ma_x*ma_x + ma_y*ma_y);
	    double mb_val = Math.sqrt(mb_x*mb_x + mb_y*mb_y);
	    double cosM = v1 / (ma_val*mb_val);
	    double angleAMB = Math.acos(cosM) * 180 / Math.PI;
	   
	    return angleAMB;
	} 

	
}
