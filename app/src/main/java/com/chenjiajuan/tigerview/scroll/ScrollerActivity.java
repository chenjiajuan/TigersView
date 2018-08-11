package com.chenjiajuan.tigerview.scroll;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Scroller;

import com.chenjiajuan.tigerview.R;

public class ScrollerActivity extends Activity {
    private Button btnText;
    private ImageView ivCoupon,ivCoupon2;
    private Button btnText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroller);
        btnText=findViewById(R.id.btnText);
        btnText2=findViewById(R.id.btnText2);
        ivCoupon=findViewById(R.id.ivCoupon);
        ivCoupon2=findViewById(R.id.ivCoupon2);
        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ivCoupon.scrollTo(-100,-100);
            }
        });
        btnText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivCoupon2.scrollBy(-100,-100);
            }
        });
    }


}
