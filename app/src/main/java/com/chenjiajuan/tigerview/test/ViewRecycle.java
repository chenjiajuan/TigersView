package com.chenjiajuan.tigerview.test;

import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by chenjiajuan on 2018/8/11.
 */

public class ViewRecycle {
    private List<View> views;
    private List<View> emptyViews;
    private GameView gameView;

    public ViewRecycle(GameView gameView){
        this.gameView=gameView;
    }


    public void recycleItems(LinearLayout linearLayout,int firstItem){
        for (int i=0;i<linearLayout.getChildCount();i++){
            recycleView(linearLayout.getChildAt(i),firstItem);
            linearLayout.removeViewAt(i);
        }
    }

    /**
     * 回收view
     * @param view
     * @param index
     */
    public void recycleView(View view,int index){
        int count=gameView.getGameAdapter().getItemsCount();
        while (index<0){
              index=count+index;
              index%=count;
              views=addView(view,views);
        }
    }

    /**
     * 向缓存中添加view
     * @param view
     * @param cache
     * @return
     */
    private List<View> addView(View view,List<View> cache) {
        if (cache==null){
            cache=new LinkedList<>();
        }
        cache.add(view);
        return cache;
    }
    
    public View getEmptyView(){
        return getCachedView(emptyViews);
    }

    private View getCachedView(List<View> emptyViews) {
        if (emptyViews!=null &&emptyViews.size()>0){
            View view=emptyViews.get(0);
            emptyViews.remove(0);
            return view;
        }

        return null;
    }

    public View getItem(){
        return getCachedView(views);
    }
}
