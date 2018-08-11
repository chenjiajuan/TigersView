package com.chenjiajuan.tigerview.test;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by chenjiajuan on 2018/8/11.
 */

public class GameView extends View {
    private static final String TAG="GameView";
    private int itemsWidth;
    private int padding=0;
    private LinearLayout itemsLayout;
    private ViewRecycle viewRecycle=new ViewRecycle(this);
    private GameAdapter gameAdapter;
    private GameScroller gameScroller;
    private int itemHeight;
    private int currentItem=0;
    private int visibleItem=3;
    private int firstItem=0;
    private int scrollingOffset;
    public GameView(Context context) {
        this(context,null);
    }
    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public GameView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gameScroller=new GameScroller(context,scrollingListener);
    }
    private GameScroller.ScrollingListener scrollingListener=new GameScroller.ScrollingListener() {
        @Override
        public void onScroll(int distance) {
            Log.e(TAG,"onScroll distance : "+distance);
            doScroll();

        }

        @Override
        public void onStarted() {
            Log.e(TAG,"onStarted ");

        }

        @Override
        public void onFinished() {
            Log.e(TAG,"onFinished");

        }

        @Override
        public void onJustify() {
            Log.e(TAG,"onJustify");
        }
    };

    private void doScroll() {

    }

    /**
     * 1.创建根view-LinearLayout ，指定其布局方式
     *     1.1当前需要显示的个数 ---可以指定当前显示项，显示多少个；
     * 2.向LinearLayout内添加子view----子View哪里来？
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);
        Log.e(TAG,"onMeasure  widthSize : "+widthSize+" widthMode : "+widthMode +" , heightSize : "+heightSize+" , heightMode : "+heightMode);
        prepareLayoutView();
        int width=calculateLayoutWidth(widthSize,widthMode);
        int height=calculateLayoutHeight(heightSize,heightMode);
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.e(TAG,"onLayout left : "+left+", top : "+top+" ,right : "+right+", bottom : "+bottom);
        itemsWidth=(right-left)-2*padding;
        itemsLayout.layout(0,0,itemsWidth,(bottom-top));
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG," onDraw");
        if (gameAdapter!=null&&gameAdapter.getItemsCount()>0){
            drawItems(canvas);
        }
    }

    private void drawItems(Canvas canvas) {
          canvas.save();
          int top=(currentItem-firstItem)*getItemHeight()+(getItemHeight()-getHeight()/2);
          canvas.translate(padding,-top);
          itemsLayout.draw(canvas);
          canvas.restore();

    }


    //指定Layout的宽度和高度的模式
    private int calculateLayoutWidth(int widthSize,int widthMode) {
        ViewGroup.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                 ViewGroup.LayoutParams.WRAP_CONTENT);
         itemsLayout.setLayoutParams(layoutParams);
         //为itemsLayout指定宽度和默认测量模式
        itemsLayout.measure(MeasureSpec.makeMeasureSpec(widthSize,MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED));
        //计算view的宽度
        int width=itemsLayout.getMeasuredWidth();
        //匹配外部指定的模式
        if (widthMode==MeasureSpec.EXACTLY){
            //若外部指定了GameView是精确值，那么Layout不可超过这个值
            width=widthSize;
        }else {
            //若外部没有指定，则自己计算GameView应该具备的宽度
            width+=2*padding;
            width = Math.max(width, getSuggestedMinimumWidth());
            if (widthMode == MeasureSpec.AT_MOST && widthSize < width) {
                //若mode为wrap_content
                width = widthSize;
            }
        }
        //重置Layout的测量模式
        itemsLayout.measure(MeasureSpec.makeMeasureSpec(width-2*padding,MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED));
        return width;
    }

    /**
     * 计算高度
     *  1.若外部已经指定高度，那么仅能展示那么多
     *  2.若未指定，则计算可见view的总高度
     * @param heightSize
     * @param heightMode
     * @return
     */
    private int calculateLayoutHeight(int heightSize,int heightMode){
        int height;
        if (heightMode==MeasureSpec.EXACTLY){
            height=heightSize;
        }else {
            if (itemsLayout!=null&&itemsLayout.getChildCount()>0){
                itemHeight=itemsLayout.getChildAt(0).getMeasuredHeight();
            }
            //TODO 此处是否还需要减去其他的值 int desired = itemHeight * visibleItems - itemHeight * ITEM_OFFSET_PERCENT / 50;
            height=itemHeight*visibleItem;
            height=Math.max(height,getSuggestedMinimumHeight());
            if (heightMode==MeasureSpec.AT_MOST){
                //AT_MOST，元素最多达到指定大小
                height=Math.min(height,heightSize);
            }
        }
        return height;
    }



    /**
     * 准备Layout
     *   1.若已存在，将view收藏到ViewRecycle内存储起来，下次备用
     *   2.若不存在，创建Layout 为其添加item
     *
     *   3.从adapter内获取到指定index的view
     *   4.添加到Layout
      */
    private void prepareLayoutView() {
        if (itemsLayout!=null){
            //清空view
            viewRecycle.recycleItems(itemsLayout,firstItem);
        }else {
            createLayoutView();
        }
        for (int i=0;i<visibleItem;i++){
            int index =currentItem+i+1;
            View view= getItemView(index);
            if (view!=null){
                itemsLayout.addView(view);
            }
        }
    }

    /**
     * 创建Layout，添加指定个数的view
     */
    private void createLayoutView(){
        itemsLayout=new LinearLayout(getContext());
        itemsLayout.setOrientation(LinearLayout.VERTICAL);
    }

    /**
     * 获取指定下标的view
     *  1.判断下标是否合理
     *  2.若下标不合理，返回一个新的view
     * @return
     */
    public View getItemView(int index) {
        if (gameAdapter==null||gameAdapter.getItemsCount()==0){
            return null;
        }
       int count =getGameAdapter().getItemsCount();
        index%=count;
        return gameAdapter.getItem(index,viewRecycle.getItem(),itemsLayout);
    }
    /**
     * 返回适配器
     * @return
     */
    public GameAdapter getGameAdapter(){
        return  gameAdapter;
    }

    public void setGameAdapter(GameAdapter gameAdapter){
        this.gameAdapter=gameAdapter;
    }

    public void scroll(int scrollCount,int time){
        int offset=scrollCount*getItemHeight();
        gameScroller.scroll(offset,time);

    }

    private int getItemHeight(){
        if (itemHeight!=0){
            return itemHeight;
        }
        if (itemsLayout!=null&&itemsLayout.getChildCount()>0){
            itemHeight=itemsLayout.getChildAt(0).getMeasuredHeight();
            return itemHeight;
        }
        return  getHeight()/visibleItem;
    }
}
