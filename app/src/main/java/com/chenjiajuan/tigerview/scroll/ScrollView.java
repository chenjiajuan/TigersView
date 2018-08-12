package com.chenjiajuan.tigerview.scroll;

import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by chenjiajuan on 2018/8/11.
 */

public class ScrollView extends ViewGroup {
    private static final String TAG="ScrollView";
    private Scroller scroller;
    private int leftBorder;
    private int rightBorder;
    private int targetIndex=0;
    public ScrollView(Context context) {
        this(context,null);
    }

    public ScrollView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller=new Scroller(context);
        ViewConfiguration configuration=ViewConfiguration.get(context);
        mTouchSlop= ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        Log.e("TAG","mTouchSlop : "+mTouchSlop);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount=getChildCount();
        for (int i=0;i<childCount;i++){
            View childView=getChildAt(i);
            measureChild(childView,widthMeasureSpec,heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
         if (changed){
          int childCount=getChildCount();
          for (int i=0;i<childCount;i++){
              View childView=getChildAt(i);
              childView.layout(i*childView.getMeasuredWidth(),0,(i+1)*childView.getMeasuredWidth(),childView.getMeasuredHeight());
          }
          leftBorder=getChildAt(0).getLeft();
          rightBorder=getChildAt(getChildCount()-1).getRight();
         }
    }

    private float mXMove=0;
    private float mXLastMove=0;
    private float mXDown=0;
    private int mTouchSlop=0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.e("TAG","onInterceptTouchEvent, event : "+ev.getAction()+" , rax : "+ev.getRawX()+" , ray : "+ev.getRawY());
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                 mXDown=ev.getRawX();
                 mXLastMove=mXDown;
                break;
            case MotionEvent.ACTION_MOVE:
                mXDown=ev.getRawX();
                float diff=Math.abs(mXMove-mXDown);
                mXLastMove=mXMove;
                //如果手指滑动的距离不够大，则不懂
                if (diff>20){
                    //此时不再接受手指滑动事件，将其拦截
                    Log.e("TAG","拦截Touch事件，进行滚动....");
                    return true;
                }
             break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("TAG","onTouchEvent , event : "+event.getAction()+" , rax : "+event.getRawX()+" , ray : "+event.getRawY());
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG,"ScrollX : "+getScrollX()+",ScrollY : "+getScrollY()+", rawX : "+event.getRawY()+" , rawY : "+event.getRawY());
                Log.e("TAG","action move : getScrollX : "+getScrollX()+"");
                mXMove=event.getRawX();
                int scrollX= (int) (mXLastMove-mXMove);
                if (getScrollX()+scrollX<leftBorder){
                    scrollTo(leftBorder,0);
                    return true;
                }else if (getScrollX()+getWidth()+scrollX>rightBorder){
                     scrollTo(rightBorder-getWidth(),0);
                     return true;
                }
                //手指在触摸的时候，微滑动
                scrollBy(scrollX,0);
                mXLastMove=mXMove;
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG,"ScrollX : "+getScrollX()+",ScrollY : "+getScrollY()+", rawX : "+event.getRawY()+" , rawY : "+event.getRawY());
                //手指松开的时候滑
                mXMove=event.getRawX();
                float dx=mXDown-mXLastMove;
                if (getScrollX()>0){
                    if (getScrollX()>getWidth()/4){
                        targetIndex++;
                        dx=targetIndex*getWidth()-getScrollX();
                    }
                }else {
                    if (getScrollX()<(-getWidth()/4)){
                        targetIndex--;
                    }
                }
                scroller.startScroll(getScrollX(),0, (int) dx,0);
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()){
            Log.e("TAG","currX : "+scroller.getCurrX()+" , currY : "+scroller.getCurrY());
            scrollTo(scroller.getCurrX(),scroller.getCurrY());
            invalidate();
        }
    }


}
