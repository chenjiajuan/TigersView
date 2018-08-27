package com.chenjiajuan.stepview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjiajuan on 2018/8/25.
 */

public class StepView extends View{
    private static final String TAG="StepView";
    private Drawable mCompleteIcon;
    private Drawable mAttentionIcon;
    private Drawable mDefaultIcon;
    private Drawable mUpIcon;
    private float mIconHeight,leftMargin,topMargin,mIconWidth,mLineWidth,mLineHeight;
//    private float mIconHeight=CalcUtils.dp2px(getContext(),25f);
//    private float leftMargin=CalcUtils.dp2px(getContext(),20f);
//    private float topMargin=CalcUtils.dp2px(getContext(),20f);
//    private float mIconWidth=CalcUtils.dp2px(getContext(),25f);
//    private float mLineWidth=CalcUtils.dp2px(getContext(),30f);
//    private float mLineHeight=CalcUtils.dp2px(getContext(),5f);
    private float mTextBotton=CalcUtils.dp2px(getContext(),5f);
    private List<Float> mPointCenterList;
    private float mCenterY; //Y坐标是固定的
    private int setpNum=10;
    private List<StepStatus> mStepStatus;
    private float mLineLeftY=0;
    private float mLineRightY=0;
    private Paint mLineCompletePaint;
    private Paint mLineUnCompletePaint;
    private Paint mTextNumberPaint;
    private int mCompleteLineColor,mUnCompleteLineColor,mCurrentColor;
    private int mTextNumberSize;
    private int currentPosition=0;
    //绘制的总时长，即绘制的次数
    private int mAnimationTime=500;
    //动画执行的间隔，每10秒执行一次
    private int mAnimationInterval=10;
    //每次执行时绘制的长度  （总长度/总时长*执行间隔）
    private  float mAnimationWeight;
    private int mCount=0; //记录绘制次数
    private boolean isAnimate=false;

    public StepView(Context context) {
        this(context,null);
    }

    public StepView(Context context,  AttributeSet attrs) {
        this(context, attrs,0);
    }

    public StepView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs);
    }

    private void initView(Context context,AttributeSet attributeSet) {
        TypedArray typedArray=context.obtainStyledAttributes(attributeSet,R.styleable.StepView);
        mIconHeight=typedArray.getDimension(R.styleable.StepView_iconHeight,25f);
        mIconWidth=typedArray.getDimension(R.styleable.StepView_iconWidth,25f);
        mLineWidth=typedArray.getDimension(R.styleable.StepView_lineWidth,20f);
        mLineHeight=typedArray.getDimension(R.styleable.StepView_lineHeight,5f);
        leftMargin=typedArray.getDimension(R.styleable.StepView_leftPadding,10f);
        topMargin=typedArray.getDimension(R.styleable.StepView_textTopPadding,10f);
        mCompleteLineColor=typedArray.getColor(R.styleable.StepView_completeColor,
                   ContextCompat.getColor(getContext(), R.color.c_41c961));
        mUnCompleteLineColor=typedArray.getColor(R.styleable.StepView_unCompleteColor,
                ContextCompat.getColor(getContext(),R.color.c_999999));
        mCurrentColor=typedArray.getColor(R.styleable.StepView_currentColor,
                ContextCompat.getColor(getContext(),R.color.c_f7b93c));
        mCompleteIcon=getResources().getDrawable(typedArray.getResourceId(R.styleable.StepView_iconComplete,
                R.mipmap.ic_sign_finish));
        mAttentionIcon=getResources().getDrawable(typedArray.getResourceId(R.styleable.StepView_iconCurrent,
                R.mipmap.ic_sign_unfinish));
        mDefaultIcon=getResources().getDrawable(typedArray.getResourceId(R.styleable.StepView_iconUnComplete,
                R.mipmap.ic_sign_unfinish));
        mTextNumberSize=(int) typedArray.getDimension(R.styleable.StepView_textSize,15f);
        mUpIcon=ContextCompat.getDrawable(context,R.mipmap.ic_sign_up);
        mPointCenterList=new ArrayList<>();
        //线条画笔，已完成
        mLineCompletePaint=new Paint();
        mLineCompletePaint.setAntiAlias(true);
        mLineCompletePaint.setColor(mCompleteLineColor);
        mLineCompletePaint.setStrokeWidth(2);
        mLineCompletePaint.setStyle(Paint.Style.FILL);
        //线条画笔，未完成
        mLineUnCompletePaint=new Paint();
        mLineUnCompletePaint.setAntiAlias(true);
        mLineUnCompletePaint.setColor(mUnCompleteLineColor);
        mLineUnCompletePaint.setStrokeWidth(2);
        mLineUnCompletePaint.setStyle(Paint.Style.FILL);

        //文本
        mTextNumberPaint=new Paint();
        mTextNumberPaint.setAntiAlias(true);
        mTextNumberPaint.setColor(mCurrentColor);
        mTextNumberPaint.setStyle(Paint.Style.FILL);
        mTextNumberPaint.setTextSize(mTextNumberSize);

        mAnimationWeight=(mLineWidth/mAnimationTime)*mAnimationInterval;
      



    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize(heightMeasureSpec));
        Log.e(TAG,"onMeasure.........");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e(TAG,"onSizeChanged.........");
        mPointCenterList.clear();
        mCenterY=topMargin+mIconHeight/2;
        mLineLeftY=mCenterY-(mLineHeight/2);
        mLineRightY=mCenterY+(mLineHeight/2);
        float size=mIconWidth/2+leftMargin;
        mPointCenterList.add(size);
        for (int i=1;i<setpNum;i++){
            size=(size+mIconWidth+mLineWidth);
            mPointCenterList.add(size);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isAnimate){
            drawSign(canvas);
        }else {
            drawUnSign(canvas);
        }
    }

    private void drawSign(Canvas canvas){
        for (int i=0;i<mPointCenterList.size();i++){
             //绘制线段
            float lineLeft=mPointCenterList.get(i)+mIconWidth/2;
            if (i<mPointCenterList.size()-1){
                if (mStepStatus.get(i+1).getState()==StepStatus.STATUS_COMPLETED){
                    canvas.drawRect(lineLeft,mLineLeftY,lineLeft+mLineWidth,
                            mLineRightY,mLineCompletePaint);
                }else {
                    if (i==currentPosition-1){ //当前绘制的是下标第3，绘制的曲线在下标第2项后
                        //绿色：起始位置+绘制单位长度*(绘制N次/绘制间隔)
                        Log.e(TAG,"mAnimationWeight : "+mAnimationWeight +" mCount : "+mCount);
                        float right=lineLeft+mAnimationWeight*(mCount/mAnimationInterval);
                        Log.e(TAG,"lineLeft : "+lineLeft+" , right : "+right);
                        canvas.drawRect(lineLeft,mLineLeftY,right,mLineRightY,mLineCompletePaint);
                        //灰色:
                        Log.e(TAG,"right : "+right+" , lineLeft : "+(lineLeft+mLineWidth));
                        canvas.drawRect(right,mLineLeftY,lineLeft+mLineWidth,mLineRightY,mLineUnCompletePaint);
                    }else {
                        if (mStepStatus.get(i).getState()==StepStatus.STATUS_COMPLETED){
                            canvas.drawRect(lineLeft,mLineLeftY,lineLeft+mLineWidth,
                                    mLineRightY,mLineCompletePaint);
                        }else {
                            //其余则直接都是灰色
                            canvas.drawRect(lineLeft,mLineLeftY,lineLeft+mLineWidth,mLineRightY,mLineUnCompletePaint);

                        }
                    }
                }
            }
            //绘制图标
            float currentXPosition=mPointCenterList.get(i);
            StepStatus stepStatus=mStepStatus.get(i);
            Rect rect=new Rect((int)(currentXPosition-mIconWidth/2),
                    (int) (mCenterY-mIconHeight/2) ,
                    (int) (currentXPosition+mIconWidth/2),
                    (int)(mCenterY+mIconHeight/2));
            if (i==currentPosition&&mCount==mAnimationTime){
                mCompleteIcon.setBounds(rect);
                mCompleteIcon.draw(canvas);
            }else {
                if (stepStatus.getState()==StepStatus.STATUS_COMPLETED){
                    mCompleteIcon.setBounds(rect);
                    mCompleteIcon.draw(canvas);
                    mTextNumberPaint.setColor(mCompleteLineColor);
                }else if (stepStatus.getState()==StepStatus.STATUS_CURRENT){
                    mAttentionIcon.setBounds(rect);
                    mAttentionIcon.draw(canvas);
                }else if (stepStatus.getState()==StepStatus.STATUS_UNDO){
                    mDefaultIcon.setBounds(rect);
                    mDefaultIcon.draw(canvas);
                    mTextNumberPaint.setColor(mUnCompleteLineColor);
                }
            }
            //绘制分数
            canvas.drawText("+"+stepStatus.getNumber(),currentXPosition,
                    mCenterY-mIconHeight/2-mTextBotton,mTextNumberPaint);
        }
        mCount=mCount+mAnimationInterval; //累计绘制次数，需要加上时间间隔
        if (mCount<=mAnimationTime){
            postInvalidate();
        }else {
            isAnimate=false;
            mCount=0;
            mStepStatus.get(currentPosition).setState(StepStatus.STATUS_COMPLETED);
        }

    }

    private void drawUnSign(Canvas canvas) {
        for (int i=0;i<mPointCenterList.size();i++){
            //绘制线段
        float leftX=mPointCenterList.get(i)+mIconWidth/2;
           if (i<mPointCenterList.size()-1){
               //根据当前项的前一项，来决定它的后一条线的颜色，
               // 状态区分颜色，如果是完成转态，则画成绿色
               if (mStepStatus.get(i+1).getState()==StepStatus.STATUS_COMPLETED){
                   canvas.drawRect(leftX,mLineLeftY,leftX+mLineWidth,
                           mLineRightY,mLineCompletePaint);
               }else {
                   canvas.drawRect(leftX,mLineLeftY,leftX+mLineWidth,
                           mLineRightY,mLineUnCompletePaint);
               }
           }
            //绘制图标
            float iconCenterX=mPointCenterList.get(i);
            Rect rect=new Rect((int) (iconCenterX-mIconWidth/2),
                    (int) (mCenterY-mIconHeight/2),
                    (int)(iconCenterX+mIconWidth/2),
                    (int)(mCenterY+mIconHeight/2));
            StepStatus stepStatus=mStepStatus.get(i);
            if (stepStatus.getState()==StepStatus.STATUS_COMPLETED){
                mCompleteIcon.setBounds(rect);
                mCompleteIcon.draw(canvas);
                mTextNumberPaint.setColor(mCompleteLineColor);
            }else if (stepStatus.getState()==StepStatus.STATUS_CURRENT){
                mAttentionIcon.setBounds(rect);
                mAttentionIcon.draw(canvas);
                mTextNumberPaint.setColor(mCurrentColor);
            }else if (stepStatus.getState()==StepStatus.STATUS_UNDO){
                mDefaultIcon.setBounds(rect);
                mDefaultIcon.draw(canvas);
                mTextNumberPaint.setColor(mUnCompleteLineColor);
            }
           //绘制分数
            canvas.drawText("+"+stepStatus.getNumber(),iconCenterX,
                    mCenterY-mIconHeight/2-mTextBotton,mTextNumberPaint);

        }
    }
    public void setStepStatus(List<StepStatus> stepStatus){
        if (stepStatus==null){
            return;
        }
        mStepStatus=stepStatus;
        setpNum=mStepStatus.size();
        postInvalidate();
    }

    public void setCurrentPosition(int currentPosition){
        if (currentPosition>mStepStatus.size()-1){
            return;
        }
        this.currentPosition=currentPosition;
        this.isAnimate=true;

        postInvalidate();
    }
}
