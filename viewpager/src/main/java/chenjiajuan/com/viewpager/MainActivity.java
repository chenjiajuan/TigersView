package chenjiajuan.com.viewpager;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String TAG="MainActivity";
    private MyViewPager viewPager;
    private  int[] a={R.mipmap.idea,R.mipmap.idea2,R.mipmap.idea3,R.mipmap.idea4,R.mipmap.idea5};
    private ArrayList<ImageView> viewArrayList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager=findViewById(R.id.viewPager);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(300,250);
        //添加页面
        for (int i=0;i<a.length;i++){
            ImageView imageView=new ImageView(this);
            imageView.setBackgroundResource(a[i]);
            imageView.setLayoutParams(params);
            viewArrayList.add(imageView);
            viewPager.addView(imageView);
        }
        viewPager.setOnPageChangeListener(new MyViewPager.OnPageChangeListener() {
            @Override
            public void onScrollToPager(int position) {
                Log.e(TAG,"onScrollToPager position :"+position);
            }

            @Override
            public void onStart() {
             Log.e(TAG,"onStart :  time "+ SystemClock.uptimeMillis());
            }

            @Override
            public void onFinish() {
                Log.e(TAG,"onFinish : time "+SystemClock.uptimeMillis());

            }
        });
    }
}
