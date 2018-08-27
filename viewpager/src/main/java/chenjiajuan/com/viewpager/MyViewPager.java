package chenjiajuan.com.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.ArrayList;

/**
 * Created by chenjiajuan on 2018/8/12.
 */

public class MyViewPager extends ViewGroup {
    private static final String TAG="MyViewPager";
    private GestureDetector gestureDetector;
    private MyScroller myScroller; //滑动辅助器，让滑动平滑

    public MyViewPager(Context context) {
        this(context,null);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gestureDetector=new GestureDetector(new GestureDetectorListener());
        myScroller=new MyScroller(context);

    }

    //自定义view必须写改方法
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e(TAG,"onLayout.......");
        int count=getChildCount();
        int left=getPaddingLeft();
        int right=getPaddingRight();
        int top=getPaddingTop();
        int bottom=getPaddingBottom();
        for (int i=0;i<count;i++){
            View childView=getChildAt(i);
            int width=getWidth();
            int height=getHeight();
            Log.e(TAG,"onLayout : width : "+width+",height : "+height);
            if (i>0){
                left+=width;
            }
            Log.e(TAG,"left : "+left+"，top :"+top+" ,right :"+right+" ,bottom : "+(bottom+height));
            childView.layout(left,top,left+width,bottom+height);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e(TAG,"onMeasure.......");
        int parentWidth=MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight=MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth=MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight=MeasureSpec.getMode(heightMeasureSpec);
        Log.e(TAG,"onMeasure , parentWidth : "+parentWidth+" , parentHeight : "+
                parentHeight+" modeWidth : "+modeWidth+",modeHeight :"+modeHeight);
        for (int i=0;i<getChildCount();i++){
            View view=getChildAt(i);
            LayoutParams params1=view.getLayoutParams();
            int height=params1.width;
            if (height<parentHeight){
                height=parentHeight;
            }
            int width=params1.height;
            if (width<parentWidth){
                width=parentWidth;
            }
            Log.e(TAG,"onMeasure , width : "+width+",height : "+height);
            int spaceWidth=MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);
            int spaceHeight=MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY);
            view.measure(spaceWidth,spaceHeight);
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize(heightMeasureSpec));
    }


    private float startX;
    private float endX;
    private int currentIndex=0; //当前页面
    /**
     * 1.将事件传递给手势识别器,
     * 2.记录按下的位置，移动的偏移量，（中间偏移量，手势识别器在做移动）手指抬起时是否需要切换
     *      (1)getX和getRawX的区别？ view在其父view中的坐标，view在屏幕中的坐标
     *      (2)tempIndex; //临时变量，计算下一个页面，可能越界，计算出值后，需要处理，再滑动
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX=event.getX();
                break;
            case MotionEvent.ACTION_UP:
                endX=event.getX();
                int tempIndex=currentIndex;
                if ((startX-endX)>getWidth()/4){
                    tempIndex++;
                }else if ((startX-endX)<(-getWidth()/4)){
                    tempIndex--;
                }
                scrollToPager(tempIndex);
                break;
        }
        return true ;
    }

    /**
     *  1.修正当前下标
     *       对临时变量itemIndex进行处理
     *        scrollTo(currentIndex*getWidth(),0); 进行移动，但不够平滑
     *  2.滑动界面
     *      scrollTo()滑动到指定的位置
     *     distanceX 计算滑动偏移量:getScrollX()可以得到当前的起始位置，即在手势move的时候，手势识别器微滑动后的值
     *     distanceTime 给定一个滑动的时间 :500
     *     求出单位时间内的距离：分段移动 averageDistance=distanceX/distanceTime
     *   3.此时需要自定义个滑动器，不断计算偏移量
     *    startScroll(startX,startY,dX,dY,time)，culculateScrollOffest计算每次移动的距离
     *     最终还是需要scrollTo去移动到指定坐标，只不过是分段移动增加
     *   4.调用invalidate()，不断刷新页面
     *    （1）onDraw方法执行
     *    （2）computeScroll方法执行 ---在这里再调用invalidate（）直到culculateScrollOffest返回true
     * @param tempIndex
     */

    private int distanceTime=200;

    private  void scrollToPager(int tempIndex){
        if (tempIndex<0){
            tempIndex=getChildCount()-1;
        }
        if (tempIndex>getChildCount()-1){
            tempIndex=0;
        }
        currentIndex=tempIndex;
        if (onPageChangeListener!=null){
            onPageChangeListener.onScrollToPager(currentIndex);
        }
        int distanceX=currentIndex*getWidth()-getScrollX();
        Log.e(TAG,"tempIndex : "+tempIndex+" , distanceX : "+distanceX+  "getScrollX ： "+getScrollX()+",getScrollY :"+getScrollY());
        //这里其实没有任何移动，只是将一些坐标记录下来
        myScroller.startScroll(getScrollX(),getScrollY(),distanceX,0,distanceTime);
//        scrollTo(currentIndex*getWidth(),0);
        if (onPageChangeListener!=null){
            onPageChangeListener.onStart();
        }
         invalidate(); //触发一次绘制，主要是为了触发computeScroll（）

    }


    /**
     * 最终还是scrollTo负责移动view，（每次移动一小段，绘制一次）scroller负责计算，
     * invalidate负责不断触发，直到移动结
     *   invalidate（）--》computeScroll()-》invalidate()
     */
    @Override
    public void computeScroll() {
         scrollView();
    }

    private void  scrollView(){
        if (myScroller.computeScrollOffset()){
            float currX=  myScroller.getCurrX();
            scrollTo((int) currX,0);
            invalidate();
        }else {
            if (onPageChangeListener!=null){
                onPageChangeListener.onFinish();
            }
        }
    }

    /**
     * 手势识别器，处理事件
     *  1.所有的view都有一个方法，可以让内部的内容进行移动
     *    scrollBy() 移动一段位置 scrollTo()移动到指定位置
     *  2.只在x轴上移动，onScroll的scrollBy内distanceY传0
     *      内部调用了 scrollTo(mScrollX + x, mScrollY + y);
     *         x与y是偏移量，而mScrollX和mScrollY是起始值，即上一次被滑动后的位置
     *  3.定义页面切换的规则
     *       （startX-endX)>getWidth()/2 手指向右，内容向左， 正值，切换到下一个页面
     *        currentIndex++；
     *        (startX-endX)<(-getWidth()/2) 手指向左，内容向右，starX比endY小，势必为负值。切换到上一个页面
     *        currentIndex--；
     *
     */
    private class GestureDetectorListener extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.e(TAG,"gestureDetector,onScroll distanceX : "+(int)distanceX+",distanceY : "+(int)distanceY);
            Log.e(TAG,"before scroll X: "+getScaleX()+" , Y : "+getScaleY());
            if (currentIndex==0||currentIndex==(getChildCount()-1)){
                return true;
            }
            scrollBy((int) distanceX,0);
            Log.e(TAG,"after  scroll X: "+getScaleX()+" , Y : "+getScaleY());
            return true;
        }
    }

    public  interface OnPageChangeListener{
        void onScrollToPager(int position);
        void onStart();
        void onFinish();
    }
    private OnPageChangeListener onPageChangeListener;
    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener){
        this.onPageChangeListener=onPageChangeListener;
    }
}
