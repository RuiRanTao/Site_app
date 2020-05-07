package com.example.bottom.Daemon;

import android.util.Log;

import com.example.bottom.Constants;
import com.example.bottom.ScatterChartActivity;
import com.example.bottom.ui.home.HomeFragment;


public class DaemonOnceScat implements Runnable {

    private static final String TAG = "Background";

    @Override
    public void run() {
        try {
//            while (true) {
            HomeFragment.getMainActivity().function();
                if(Constants.Receding == 1 && Constants.ScatInit==1){
                    ScatterChartActivity.getMainActivity().function();
                    Constants.Receding = 0;
                }
                Log.d(TAG,"Ma后台刷新运行中");
//            }
        } finally {// 后台线程不执行finally子句
            System.out.println("finally ");
        }
    }
}
