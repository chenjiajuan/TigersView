package com.chenjiajuan.stepview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private StepView stepView;
    private List<StepStatus> stepStatusList=new ArrayList<>();
    private int count=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stepView=findViewById(R.id.stepView);
        for ( int i=0;i<6;i++){
            StepStatus stepStatus;
            if (i<3){
                stepStatus=new StepStatus(StepStatus.STATUS_COMPLETED,i);
                stepStatusList.add(stepStatus);
            }else if (i==3){
                stepStatus=new StepStatus(StepStatus.STATUS_CURRENT,i);
                stepStatusList.add(stepStatus);
            }else {
                stepStatus=new StepStatus(StepStatus.STATUS_UNDO,i);
                stepStatusList.add(stepStatus);
            }
        }
        stepView.setStepStatus(stepStatusList);

    }

    public void  OnClick(View view){
        count++;
        stepView.setCurrentPosition(count);
    }
}
