package com.chenjiajuan.tigerview;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chenjiajuan.tigerview.game.WheelViewActivity;
import com.chenjiajuan.tigerview.game.WheelViewAdapter;
import com.chenjiajuan.tigerview.test.GameAdapter;
import com.chenjiajuan.tigerview.test.GameView;

public class MainActivity extends Activity {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameView=findViewById(R.id.gameView);
        gameView.setGameAdapter(new SlotMachineAdapter());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //gameView.scroll(3,5000);
    }

    private class SlotMachineAdapter implements GameAdapter {

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
                view = View.inflate(MainActivity.this, R.layout.item_dialog_tiger_img, null);
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
    }
}
