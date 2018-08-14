package com.chenjiajuanpopwindow;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG="MainActivity";

    private Button btnPop,btnPop2,btnPop3;
    private PopupWindow popupWindow;
    private RecyclerView recyclerView;
    private AdapterRecycler adapterRecycler;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPop=findViewById(R.id.btnPop);
        btnPop2=findViewById(R.id.btnPop2);
        btnPop3=findViewById(R.id.btnPop3);
        btnPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow(initData("btnPop"));

            }
        });
        btnPop2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow(initData("btnPop2"));

            }
        });

        btnPop3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow(initData("btnPop3"));
            }
        });

    }

    public List<String> initData(String type){
        List<String> list=new ArrayList<>();
        for (int i=0;i<20;i++){
            int a= (int) (Math.random()*10);
            list.add(type+",i:"+i+", number :"+a);
        }
        return list;

    }

    private void showPopWindow(List<String> list){
        initPopWindow();
        updateList(list);
        if (popupWindow!=null&&!popupWindow.isShowing()){
            popupWindow.showAsDropDown(btnPop);
        }

    }

    private void updateList(List<String> list) {
        adapterRecycler.setList(list);
        adapterRecycler.notifyDataSetChanged();

    }

    private void initPopWindow() {
        if (adapterRecycler==null){
            adapterRecycler=new AdapterRecycler(this);
        }
        if (recyclerView==null){
            recyclerView=new RecyclerView(this);
            recyclerView.addItemDecoration(new ItemDector(10));
            recyclerView.setAdapter(adapterRecycler);
            RecyclerView.LayoutParams params=new RecyclerView.LayoutParams( RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.MATCH_PARENT);
            recyclerView.setLayoutParams(params);
        }
        if (linearLayoutManager==null){
            linearLayoutManager=new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
        if (popupWindow==null){
            popupWindow=new PopupWindow(this);
            popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(300);
            popupWindow.setFocusable(false);
            popupWindow.setBackgroundDrawable(null);
            popupWindow.setContentView(recyclerView);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setAnimationStyle(R.style.style_pop);

        }
    }

    public void dismissPop(){
        if (popupWindow!=null&&popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }

    private  class  ItemDector extends RecyclerView.ItemDecoration{
        private int space;
        public  ItemDector(int space){
           this.space=space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom=space;
        }
    }
}
