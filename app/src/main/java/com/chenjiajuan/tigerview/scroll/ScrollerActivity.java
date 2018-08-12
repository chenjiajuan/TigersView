package com.chenjiajuan.tigerview.scroll;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.chenjiajuan.tigerview.R;

/**
 * scrollTo，让view滑动到指定位置
 * scrollBy，增量滑动
 *  两者都是内容滑动，比如LinearLayout内的两个view被滑动，区域仅限于它的内部
 */

public class ScrollerActivity extends Activity {
    private Button btnText;
    private Button btnText2;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroller);
        btnText=findViewById(R.id.btnText);
        btnText2=findViewById(R.id.btnText2);
        layout=findViewById(R.id.layout);
        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               layout.scrollTo(-100,-100);
            }
        });
        btnText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.scrollBy(-100,-100);
            }
        });
    }


}
