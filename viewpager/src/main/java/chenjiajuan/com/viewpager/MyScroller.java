package chenjiajuan.com.viewpager;

import android.content.Context;
import android.os.SystemClock;

/**
 * Created by chenjiajuan on 2018/8/12.
 */

public class MyScroller  {
    private float startX; //起始x坐标
    private float startY; //起始y坐标
    private int totalTime; //滑动事件
    private int dx;    //x轴滑动量
    private int dy;    //y轴滑动量
    private long startTime; //开始时间
    private boolean isFinish=false;
    private float currX; //每次移动一小段后X的坐标

    public MyScroller(Context context){

    }
    public void startScroll(float startX, float startY, int dx, int dy, int distanceTime) {
        this.startX=startX;
        this.startY=startY;
        this.dx=dx;
        this.dy=dy;
        this.totalTime=distanceTime;
        this.startTime= SystemClock.uptimeMillis(); //记录起始的时间
        this.isFinish=false;
    }

    /**
     * 每一小段的事件
     * 求一小段的距离
     * 求一小段的坐标
     * 求一小段的时间
     * @return true，正在移动， false移动结束
     */
    public boolean computeScrollOffset(){
        if (isFinish){
            return false;
        }
        long endTime=SystemClock.uptimeMillis(); //记录每次结束的时间
        long dTime=endTime-startTime; //计算差值
        if (dTime<totalTime){
            //未移动结束
            //计算时间间隔内的位移量，加上初始值，就是该段时间内的坐标值，可以直接传给scrollTo，移动到指定坐标位置
            float distanceSmallX=dx*dTime/totalTime;
            //计算此时的坐标值，起始值+distanceSmallX
             currX=startX+distanceSmallX;
        }else {
            //移动结束
            isFinish=true;
            currX=startX+dx;
        }
        return true;
    }

    public float getCurrX() {
        return currX;
    }

    public void setCurrX(float currX) {
        this.currX = currX;
    }
}
