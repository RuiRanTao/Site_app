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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {


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


    private EditText Name;
    private EditText Key;
    private Socket socket;

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
        setContentView(R.layout.activity_login);

        Button button_login = findViewById(R.id.button_login);
        Name =  (EditText) findViewById(R.id.name);
        Key =  (EditText) findViewById(R.id.key);

        mThreadPool = Executors.newCachedThreadPool();
        // 实例化主线程,用于更新接收过来的消息
        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0: {
                        Constants.login_statue=0;
                        Connect();
                        try {
                            Thread.sleep(500
                            );
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Login clicked "+ Constants.login_statue +"\n");
//                        Disconnect();
                        if(Constants.login_statue==1){
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    }
                    break;
                    case 1: {
                        Toast toast = Toast.makeText(LoginActivity.this, Toast_message, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    break;
                }
            }
        };

        button_login.setOnClickListener(new View.OnClickListener() {
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



//    public void Dst(){
//
//        DatabaseHelper0 helper = new DatabaseHelper0(this);
//        helper.getWritableDatabase();
//        Dao dao = new Dao(getApplicationContext());
//        String sql = "delete from "+ Constants.TABLE_NAME  +" where id > 0";
//        dao.execSQL(sql);
//
//    }
//
    public void Connect(){
        // 利用线程池直接开启一个线程 & 执行该线程
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    String myip = "103.46.128.43";
                    // 创建Socket对象 & 指定服务端的IP 及 端口号
                    socket = new Socket(myip, 10235);

                    // 判断客户端和服务器是否连接成功
                    System.out.println(socket.isConnected());
                    if(socket.isConnected())
                        Constants.Receding = 1;
//                    else
//                        Constants.Receding = 0;
//                        showToast("连接成功");
                    Send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void Send(){
        // 利用线程池直接开启一个线程 & 执行该线程
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                String name ,key;
                String edit;
                try {
//                    if(Constants.login_statue==0){
//                        name = Name.getText().toString();
//                        key = Key.getText().toString();
//                    }
//                    else{
//                        name = Constants.name;
//                        key = Constants.key;
//                    }
                    switch (Constants.login_statue){
                        case 0:{
                            name = Name.getText().toString();
                            key = Key.getText().toString();
                            edit =  "login," +name + "," + key;
                        }break;
                        case 1:{
                            name = Constants.name;
                            key = Constants.key;
                            edit =  "login," +name + "," + key;
                        }break;
                        case 2:{
                            name = Constants.register_name;
                            key = Constants.register_key;
                            edit =  "register," +name + "," + key;
                        }break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + Constants.login_statue);
                    }
                    // 步骤1：从Socket 获得输出流对象OutputStream
                    // 该对象作用：发送数据
                    outputStream = socket.getOutputStream();

//                    String edit = mEdit.getText().toString();

                    // 步骤2：写入需要发送的数据到输出流对象中
                    outputStream.write((edit+"\n").getBytes(StandardCharsets.UTF_8));
                    // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞
                    // 步骤3：发送数据到服务端
                    outputStream.flush();
                    receive();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void receive(){
        try {
            // 步骤1：创建输入流对象InputStream
            is = socket.getInputStream();
            // 步骤2：创建输入流读取器对象 并传入输入流对象
            // 该对象作用：获取服务器返回的数据
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
            while((response = br.readLine())!=null){
                // 步骤4:通知主线程,将接收的消息显示到界面
                Recd();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //*处理接收到的数据*//
    @SuppressLint("SetTextI18n")
    public void Recd() {
        String[] sourceStrArray = response.split(",");
//        int len = sourceStrArray.length;
        System.out.println(sourceStrArray[0]);
        if( sourceStrArray[0].equals("login_ok") ){
            System.out.println(response);
            System.out.println(sourceStrArray[1]+","+sourceStrArray[2]+","+sourceStrArray[3]);
            Constants.name = sourceStrArray[1];
            Constants.key = sourceStrArray[2];
            Constants.order_id = Integer.parseInt(sourceStrArray[3]);
            Constants.login_statue = 1;
            System.out.println(Constants.order_id +Constants.name);
        }
        if( sourceStrArray[0].equals("login_noke") ){           //显示密码错误
            showToast("密码错误");
            System.out.println(response);
        }
        if( sourceStrArray[0].equals("login_no") ){             //显示用户名不存在
            showToast("用户名不存在");
            System.out.println(response);
        }
        if( sourceStrArray[0].equals("register_err") ){         //注册失败，名字已存在
            System.out.println(response);
        }
        if( sourceStrArray[0].equals("register_ok") ){          //注册成功
            System.out.println(response);
            Constants.register_statue=1;
        }

//        Disconnect();
    }

    public void Disconnect(){
        try {
            // 断开 客户端发送到服务器 的连接，即关闭输出流对象OutputStream
            outputStream.close();
            // 断开 服务器发送到客户端 的连接，即关闭输入流读取器对象BufferedReader
            br.close();
            // 最终关闭整个Socket连接
            socket.close();
            // 判断客户端和服务器是否已经断开连接
            System.out.println(socket.isClosed());
//            showToast("数据刷新成功");
//                    btnDisconnect.setBackgroundColor(Color.parseColor("#00B35B"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showToast(String toast){
        Toast_message = toast;
        Message msg = Message.obtain();
        msg.what = 1;
        mMainHandler.sendMessage(msg);
    }

    public LoginActivity() {
        mainActivity = this;
    }

    public static LoginActivity getMainActivity() {
        return mainActivity;
    }

    private static LoginActivity mainActivity;

    public void function(){
        Connect();
//        Disconnect();
    }

}