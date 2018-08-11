package com.chenjiajuan.tigerview.test;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Scroller;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;


/**
 * Created by chenjiajuan on 2018/8/11.
 */

public class GameScroller {
    private  ScrollingListener scrollingListener;
    private Context context;
    private Scroller scroller;
    private int lastScrollY=0;
    private static final  int DEFAULT_SCROLL_TIME=1000;
    public static final int MIN_DELTA_FOR_SCROLLING = 1;
    private MyHandler myHandler;
    public interface ScrollingListener {
        void onScroll(int distance);
        void onStarted();
        void onFinished();
        void onJustify();
    }
    public GameScroller(Context context, ScrollingListener scrollingListener){
         this.context=context;
         this.scrollingListener=scrollingListener;
         this.scroller=new Scroller(context);
         this.myHandler=new MyHandler(this);
    }
    public void scroll(int distance ,int time){
        scroller.forceFinished(true);
        lastScrollY=0;
        scroller.startScroll(0,0,0,distance,time==0? DEFAULT_SCROLL_TIME :time);
        sendNextMessage();
    }

    public void sendNextMessage(){
        myHandler.removeCallbacksAndMessages(null);
        myHandler.sendEmptyMessage(0);

    }



    private static class MyHandler extends Handler {
        private WeakReference<GameScroller> gameScrollerWeakReference;
        public MyHandler(GameScroller gameScroller){
            this.gameScrollerWeakReference=new WeakReference<>(gameScroller);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            GameScroller gameScroller=gameScrollerWeakReference.get();
            gameScroller.scroller.computeScrollOffset();
            int currentY=gameScroller.scroller.getCurrY();
            int data=gameScroller.lastScrollY-currentY;
            gameScroller.lastScrollY=currentY;
            if (data!=0){
                gameScroller.scrollingListener.onScroll(data);
            }

           if (Math.abs(currentY-gameScroller.scroller.getFinalY())<gameScroller.MIN_DELTA_FOR_SCROLLING){
                gameScroller.scroller.forceFinished(true);
           }
           if (gameScroller.scroller.isFinished()){
               gameScroller.scrollingListener.onFinished();
           }else {
               gameScroller.myHandler.sendEmptyMessage(0);
           }


        }
    }
}
