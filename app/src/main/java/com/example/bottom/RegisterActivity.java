package com.example.bottom;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    /**
     * 发送消息到服务器 变量
     */
    // 输出流对象
    OutputStream outputStream;

    /**
     * 接收服务器消息 变量
     */
    // 输入流对象
    InputStream is;
    // 输入流读取器对象
    InputStreamReader isr ;
    BufferedReader br ;

    // 接收服务器发送过来的消息
    String response;

    String Toast_message;



    private Socket socket;
    private Button button_register;
    private EditText Name;
    private EditText Key0;
    private EditText Key1;

    // 主线程Handler
    // 用于将从服务器获取的消息显示出来
    private Handler mMainHandler;

    // 线程池
// 为了方便展示,此处直接采用线程池进行线程管理,而没有一个个开线程
    public ExecutorService mThreadPool;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        button_register = findViewById(R.id.Button_login);
        Name = findViewById(R.id.name);
        Key0 = findViewById(R.id.key0);
        Key1 = findViewById(R.id.key1);

        mThreadPool = Executors.newCachedThreadPool();
        // 实例化主线程,用于更新接收过来的消息
        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0: {
                        System.out.println("Register clicked \n");
                        String name ,key0, key1;
                        name = Name.getText().toString();
                        key0 = Key0.getText().toString();
                        key1 = Key1.getText().toString();
                        int len_name = name.length();
                        int len_key = key0.length();
                        if(!key0.equals(key1)){
                            showToast("两次密码不一致");
                        }
                        if(len_key==0 || len_name==0){
                            showToast("请输入用户名和密码");
                        }
                        if(len_key>0 && len_key<6){
                            showToast("密码太短");
                        }
                        if(key0.equals(key1) && len_key>5){
                            Constants.register_name = name;
                            Constants.register_key = key0;
                            Constants.login_statue = 2;
                            LoginActivity.getMainActivity().function();
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(Constants.register_statue==1){
                                showToast("注册成功");
                            }
                            System.out.println("Register ready \n");
                        }
                        if(Constants.register_statue==1){
                            Constants.register_statue=0;        //注册状态标志位复原
                            Constants.login_statue=1;           //登录状态标志位复原
                            Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    }
                    break;
                    case 1: {
                        Toast toast = Toast.makeText(RegisterActivity.this, Toast_message, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    break;
                }
            }
        };

        button_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            // 步骤4:通知主线程,将接收的消息显示到界面
                            Message msg = Message.obtain();
                            msg.what = 0;
                            mMainHandler.sendMessage(msg);
                        }
                    });
                }
            });


    }


    public void showToast(String toast){
        Toast_message = toast;
        Message msg = Message.obtain();
        msg.what = 1;
        mMainHandler.sendMessage(msg);
    }

    public RegisterActivity() {
        mainActivity = this;
    }

    public static RegisterActivity getMainActivity() {
        return mainActivity;
    }

    private static RegisterActivity mainActivity;

    public void function(){
//        Connect();
//        Disconnect();
    }

}