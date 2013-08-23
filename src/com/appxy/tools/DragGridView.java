package com.appxy.tools;
import com.appxy.tinyscan.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.app.Activity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;


public class DragGridView extends GridView {
    
	private WindowManager windowManager;
    private WindowManager.LayoutParams windowParams;
    private ImageView dragImageView;
    private int dragPosition;
    private int dragPoint;
    private int dragOffset;
    private int dragOffsetx;
    private int dragPointx;
    private int upScrollBounce;
    private int downScrollBounce;
 
  
    Context context;
    
    
    private int dropPosition;
	private int holdPosition;
	private int startPosition;

	private int nColumns = 3;


	
	
	private String LastAnimationID;

	private boolean isMoving = false;
 
    public DragGridView(Context context, AttributeSet attrs) { 
        super(context, attrs); 
        this.context = context;
    } 
    
    boolean flag = false;

	
	
	
    @Override 
    public boolean onInterceptTouchEvent(MotionEvent ev) { 
        // 按下 
    	final int x = (int) ev.getX(); 
        final int y = (int) ev.getY();
        
        if (ev.getAction() == MotionEvent.ACTION_DOWN) { 
            
           
            startPosition = dropPosition = dragPosition = pointToPosition(x, y); 
          
            if (dragPosition == AdapterView.INVALID_POSITION) { 
            	
                return super.onInterceptTouchEvent(ev); 
                
            } 
            final ViewGroup itemView = (ViewGroup) getChildAt(dragPosition 
                    - getFirstVisiblePosition()); 
            
		   
            dragPoint = y - itemView.getTop(); 
            dragPointx = x - itemView.getLeft();
            dragOffset = (int) (ev.getRawY() - y); 
            dragOffsetx = (int) (ev.getRawX() - x); 
           ImageView dragger = (ImageView)itemView.findViewById(R.id.listphoto_photo);
           
          
            if(dragger!=null){
          
            	dragger.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					DisplayMetrics metrics = new DisplayMetrics();

					((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
					upScrollBounce = metrics.heightPixels/4;// 取得向上滚动的边际，大概为该控件的1/3 
	                downScrollBounce = metrics.heightPixels*3/4;// 取得向下滚动的边际，大概为该控件的2/3 
	             
	                itemView.destroyDrawingCache();
	                itemView.setDrawingCacheEnabled(true);
	                
	                Bitmap bm = convertViewToBitmap(itemView);
	                //Bitmap bitmap = Bitmap.createBitmap(bm, 8, 8, bm.getWidth()-8, bm.getHeight()-8);
	                startDrag(bm, x, y);    
	                flag = true;
	                hideDropItem();
	                itemView.setVisibility(View.INVISIBLE);	
	               
					isMoving = false;
					return false;
				}
            	
            });
            }
            
           
            }else  if (ev.getAction() == MotionEvent.ACTION_UP){
               if (dragImageView != null && dragPosition != INVALID_POSITION){
            	if(flag){
            	   stopDrag(); 
                   onDrop(x, y); 
                   flag = false;
            	}
               }
            }else  if (ev.getAction() == MotionEvent.ACTION_MOVE){
            	 if (dragImageView != null && dragPosition != INVALID_POSITION){
                       onDrag(x,y); 
				       if(!isMoving )
				            OnMove(x,y);
				       return false;
            	 }
             }
        return super.onInterceptTouchEvent(ev); 
       
       
       
    }
	
	
	
	
 
    @Override 
    public boolean onTouchEvent(MotionEvent ev) { 
        // item的view不为空，且获取的dragPosition有效 
        if (dragImageView != null && dragPosition != INVALID_POSITION) { 
            int action = ev.getAction(); 
            switch (action) { 
            case MotionEvent.ACTION_UP: 
                int upY = (int) ev.getY(); 
                int upX = (int) ev.getX(); 
               
                stopDrag(); 
                onDrop(upX, upY); 
                break; 
            case MotionEvent.ACTION_MOVE: 
                int moveY = (int) ev.getY(); 
                int moveX = (int) ev.getX();
                onDrag(moveX,moveY); 
               
				
				if(!isMoving )
				    OnMove(moveX,moveY);			
				break;
           
            default: 
                break; 
            } 
          
        } 
 
      return super.onTouchEvent(ev); 
     
    }
 
 
    private void startDrag(Bitmap bm, int x, int y) { 
        stopDrag(); 
     
        windowParams = new WindowManager.LayoutParams(); 
        windowParams.gravity = Gravity.TOP | Gravity.LEFT; 
        windowParams.x = x - dragPointx;
        windowParams.y = y - dragPoint + dragOffset-50; 
       
        windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT; 
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT; 
 
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// 不需获取焦点 
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE// 不需接受触摸事件 
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;// 保持设备常开，并保持亮度不变。 
             
 
        windowParams.format = PixelFormat.TRANSLUCENT;// 默认为不透明，这里设成透明效果. 
        windowParams.windowAnimations = 0;// 窗口所使用的动画设置 
 
        ImageView imageView = new ImageView(getContext()); 
        imageView.setImageBitmap(bm); 
        windowManager = (WindowManager) getContext().getSystemService("window"); 
        windowManager.addView(imageView, windowParams); 
        
        dragImageView = imageView; 
        windowManager.updateViewLayout(dragImageView, windowParams);
    } 
 
  
    public void onDrag(int x,int y) { 
      
        if (dragImageView != null) { 
            windowParams.alpha = 0.5f;// 透明度 
            windowParams.y = y - dragPoint + dragOffset;// 移动y值.//记得要加上dragOffset，windowManager计算的是整个屏幕.(标题栏和状态栏都要算上) 
            windowParams.x = x - dragPointx + dragOffsetx;
           
            windowManager.updateViewLayout(dragImageView, windowParams);// 时时移动. 
        } 
    
       doScroller(x,y); 
       
    } 
 
   
 
    public void doScroller(int x,int y) { 
         
       
        if (y < upScrollBounce) { 
           
        	 this.smoothScrollBy(-20, 0);
        }
        else if (y > downScrollBounce) { 
        	this.smoothScrollBy(20, 0);
           
        }
    } 
 
   
    public void stopDrag() { 
        if (dragImageView != null) { 
            windowManager.removeView(dragImageView); 
            dragImageView = null; 
        } 
        
       
    } 
 
   
    public void onDrop(int x,int y) { 
    	final DragGridAdapter adapter = (DragGridAdapter) this.getAdapter();
		adapter.showDropItem(true);
		adapter.notifyDataSetChanged();
    } 
    
    
    public  void OnMove(int x, int y){
		int TempPosition = pointToPosition(x,y);
		
		if(TempPosition != AdapterView.INVALID_POSITION && TempPosition != dragPosition){
			dropPosition = TempPosition;
		}
		if(dragPosition != startPosition)
			dragPosition = startPosition;
		int MoveNum = dropPosition - dragPosition;
		if(dragPosition != startPosition && dragPosition == dropPosition)
			MoveNum = 0;
		if(MoveNum != 0){
			int itemMoveNum = Math.abs(MoveNum);
			float Xoffset,Yoffset;
			for (int i = 0;i < itemMoveNum;i++){
			if(MoveNum > 0){
				holdPosition = dragPosition + 1;
				Xoffset = (dragPosition/nColumns == holdPosition/nColumns) ? (-1) : (nColumns -1);
				Yoffset = (dragPosition/nColumns == holdPosition/nColumns) ? 0 : (-1);
			}else{
				holdPosition = dragPosition - 1;
				Xoffset = (dragPosition/nColumns == holdPosition/nColumns) ? 1 : (-(nColumns-1));
				Yoffset = (dragPosition/nColumns == holdPosition/nColumns) ? 0 : 1;
			}
			
		    ViewGroup moveView = (ViewGroup)findViewWithTag(holdPosition);	
		  
			Animation animation = getMoveAnimation(Xoffset,Yoffset);
			moveView.startAnimation(animation);
			dragPosition = holdPosition;
			if(dragPosition == dropPosition)
				LastAnimationID = animation.toString();
			final DragGridAdapter adapter = (DragGridAdapter)this.getAdapter();
			animation.setAnimationListener(new Animation.AnimationListener() {
					
				@Override
				public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub
					isMoving = true;
				}
					
				@Override
				public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub
						
				}
					
				@Override
				public void onAnimationEnd(Animation animation) {
						// TODO Auto-generated method stub
					String animaionID = animation.toString();
					if(animaionID.equalsIgnoreCase(LastAnimationID)){
						adapter.update(startPosition, dropPosition);
						startPosition = dropPosition;
						isMoving = false;
					}					
				}
			});	
		  }
	   }
	}
    
    
    public Animation getMoveAnimation(float x,float y){
		TranslateAnimation go = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, x, 
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, y);
		go.setFillAfter(true);
		go.setDuration(300);	
		return go;
	}
    
    private void hideDropItem(){
		final DragGridAdapter adapter = (DragGridAdapter)this.getAdapter();
		adapter.showDropItem(false);
		
	}
    
    public static Bitmap convertViewToBitmap(View view){
          view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
          view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
          view.buildDrawingCache();
          Bitmap bitmap = view.getDrawingCache();

          return bitmap;
  }
	
 
} 


