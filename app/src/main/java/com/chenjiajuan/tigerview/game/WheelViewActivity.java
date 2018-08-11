package com.chenjiajuan.tigerview.game;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chenjiajuan.tigerview.R;


public class WheelViewActivity extends Activity {
    private WheelView wheelview1,wheelview2,wheelview3;
    private SlotMachineAdapter slotMachineAdapter;
    private int currentItem1=0;
    private int currentItem2=0;
    private int currentItem3=0;
    private boolean isScroll=false;
    private boolean isScroll2=false;
    private boolean isScroll3=false;
    private  int targetItem1=3;
    private  int targetItem2=3;
    private  int targetItem3=3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel_view2);
        wheelview1=findViewById(R.id.wheelview1);
        wheelview2=findViewById(R.id.wheelview2);
        wheelview3=findViewById(R.id.wheelview3);
        initWheelView1(0,4000);
        initWheelView2(1,4500);
        initWheelView3(2,4000);

    }

    private void initWheelView1(int what,int delayMillis) {
        slotMachineAdapter=new SlotMachineAdapter();
        wheelview1.setViewAdapter(slotMachineAdapter);
        wheelview1.setVisibleItems(1);
        wheelview1.setCyclic(true);
        wheelview1.setEnabled(false);
        wheelview1.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                isScroll=true;

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                isScroll=false;
                currentItem1=wheel.getCurrentItem();
                Log.e("TAG","currentItem1 : "+wheel.getCurrentItem());

            }
        });
    }


    private void initWheelView2(final int what, int delayMillis) {
        slotMachineAdapter=new SlotMachineAdapter();
        wheelview2.setViewAdapter(slotMachineAdapter);
        wheelview2.setVisibleItems(1);
        wheelview2.setCyclic(true);
        wheelview2.setEnabled(false);
        wheelview2.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                isScroll2=true;

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                currentItem2=wheel.getCurrentItem();
                isScroll2=false;
                Log.e("TAG","currentItem2 : "+wheel.getCurrentItem());

            }
        });
    }

    private void initWheelView3(int what,int delayMillis) {
        slotMachineAdapter=new SlotMachineAdapter();
        wheelview3.setViewAdapter(slotMachineAdapter);
        wheelview3.setVisibleItems(1);
        wheelview3.setCyclic(true);
        wheelview3.setEnabled(false);
        wheelview3.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                isScroll3=true;

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                currentItem3=wheel.getCurrentItem();
                isScroll3=false;
                Log.e("TAG","currentItem3 : "+wheel.getCurrentItem());

            }
        });
    }

    private  Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
//                    targetItem1=new Random().nextInt(3)+1;
                    wheelview1.scroll(itemToScroll(currentItem1,targetItem1),3000);
                    break;
                case 1:
//                    targetItem2=new Random().nextInt(3)+1;
                    wheelview2.scroll(itemToScroll(currentItem2,targetItem2),3500);
                    break;
                case 2:
//                    targetItem3=new Random().nextInt(3)+1;
                    wheelview3.scroll(itemToScroll(currentItem3,targetItem3),4000);
                    break;
            }
        }
    };

    public int itemToScroll(int currentItem,int targetItem){
        int itemToScroll=0;
//
        if (currentItem>targetItem){
            itemToScroll= slotMachineAdapter.getItemsCount()-currentItem+targetItem;
        }else {
            itemToScroll=targetItem-currentItem;
        }
        itemToScroll=itemToScroll+(slotMachineAdapter.getItemsCount()*10);
        Log.e("TAG","itemToScroll : "+itemToScroll+" , currentItem : "+currentItem+" , targetItem :"+targetItem);
        return  itemToScroll;
    }

    public void startRun(View view){
        if (isScroll||isScroll2||isScroll3){
            Log.e("TAG","isScroll");
            return;
        }
        handler.sendEmptyMessageDelayed(0, 1000);
        handler.sendEmptyMessageDelayed(1, 1500);
        handler.sendEmptyMessageDelayed(2, 1000);
    }

    private class SlotMachineAdapter implements WheelViewAdapter{

        @Override
        public int getItemsCount() {
            return 4;
        }

        @Override
        public View getItem(int index, View convertView, ViewGroup parent) {
            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = View.inflate(WheelViewActivity.this, R.layout.item_dialog_tiger_img, null);
            }
            ImageView img =  view.findViewById(R.id.iv_dialog_home_tiger);
            switch (index){
                case 0:
                    img.setImageResource(R.mipmap.shopvoucher);
                    break;
                case 1:
                    img.setImageResource(R.mipmap.musicvoiucher);
                    break;
                case 2:
                    img.setImageResource(R.mipmap.datavoucher);
                    break;

                case 3:
                    img.setImageResource(R.mipmap.redpakge);
                    break;
            }

            return view;
        }

        @Override
        public View getEmptyItem(View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }
    }

}
