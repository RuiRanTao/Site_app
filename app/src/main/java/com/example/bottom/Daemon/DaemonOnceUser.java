package com.example.bottom.Daemon;


import com.example.bottom.Constants;
import com.example.bottom.LoginActivity;
import com.example.bottom.ui.home.HomeFragment;
import com.example.bottom.ui.user.UserFragment;

public class DaemonOnceUser implements Runnable {

    private static final String TAG = "Background";

    @Override
    public void run() {
        try {
            if(Constants.login_statue==1){
                LoginActivity.getMainActivity().function();
                Thread.sleep(100);
                Constants.query_mode=3;
                HomeFragment.getMainActivity().function();
                Thread.sleep(500);
                UserFragment.getMainActivity().function();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {// 后台线程不执行finally子句
            System.out.println("finally ");
        }
    }
}
