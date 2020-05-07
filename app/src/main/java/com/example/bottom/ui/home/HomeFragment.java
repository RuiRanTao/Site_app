package com.example.bottom.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.bottom.Constants;
import com.example.bottom.Daemon.DaemonOnceHome;
import com.example.bottom.Database.Dao_site;
import com.example.bottom.Database.DatabaseHelper_site;
import com.example.bottom.Order;
import com.example.bottom.R;
import com.example.bottom.Result;
import com.example.bottom.ScatterChartActivity;
import com.example.bottom.TableActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private HomeViewModel homeViewModel;

    /**
     * 主 变量
     */
    public List<Result> Resultlist;

    public List<Order> orderList;

    // 主线程Handler
    // 用于将从服务器获取的消息显示出来
    private Handler mMainHandler;

    // Socket变量
    private Socket socket;

    // 线程池
    // 为了方便展示,此处直接采用线程池进行线程管理,而没有一个个开线程
    public ExecutorService mThreadPool;

    // Socket变量

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

    //
    String Toast_message;

    /**
     * 发送消息到服务器 变量
     */
    // 输出流对象
    OutputStream outputStream;

    /**
     * 按钮 变量
     */
    private TextView text10;
    private TextView text11;
    private TextView text12;
    private TextView text13;
    private TextView text20;
    private TextView text21;
    private TextView text22;
    private TextView text23;
    private TextView text30;
    private TextView text31;
    private TextView text32;
    private TextView text33;
    private TextView text40;
    private TextView text41;
    private TextView text42;
    private TextView text43;
    private TextView login_statue;
    private TextView text_query;
    // 连接 断开连接 发送数据到服务器 的按钮变量
    private Button btnConnect;
    private Button btnDisconnect;
    private Button btnSend;
    private Button btnClear;
    private Button Receive;
    private Button dst;
    private Button button_first;
    private Button Scatter;

    private Button button_choose_floor;
    private Button button_choose_place;
    private Button button_choose_site;
    private Button button_order;
    // 显示接收服务器消息 按钮
    private TextView receive_message;

    // 输入需要发送的消息 输入框
    private EditText mEdit;

    //输入服务器的IP地址
    private EditText ip;

    @SuppressLint("HandlerLeak")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        btnConnect = root.findViewById(R.id.connect);
        button_first = root.findViewById(R.id.button_first);
        button_choose_floor = root.findViewById(R.id.button_choose_floor);
        button_choose_place = root.findViewById(R.id.button_choose_place);
        button_choose_site = root.findViewById(R.id.button_choose_site);
        button_order = root.findViewById(R.id.button_order);

//        dst = (Button) findViewById(R.id.dst);
        Scatter = root.findViewById(R.id.Scatter);
        text10 = root.findViewById(R.id.text10);
        text11 = root.findViewById(R.id.text11);
        text12 = root.findViewById(R.id.text12);
        text13 = root.findViewById(R.id.text13);
        text20 = root.findViewById(R.id.text20);
        text21 = root.findViewById(R.id.text21);
        text22 = root.findViewById(R.id.text22);
        text23 = root.findViewById(R.id.text23);
        text30 = root.findViewById(R.id.text30);
        text31 = root.findViewById(R.id.text31);
        text32 = root.findViewById(R.id.text32);
        text33 = root.findViewById(R.id.text33);
        text40 = root.findViewById(R.id.text40);
        text41 = root.findViewById(R.id.text41);
        text42 = root.findViewById(R.id.text42);
        text43 = root.findViewById(R.id.text43);
        login_statue = root.findViewById(R.id.login_statue);
        text_query = root.findViewById(R.id.text_query);

        // 初始化线程池
        mThreadPool = Executors.newCachedThreadPool();

        // 实例化主线程,用于更新接收过来的消息
        mMainHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0: {
                        Recd();
//                        Disconnect();
                    } break;
                    case 1: {
                        @SuppressLint("HandlerLeak") Toast toast = Toast.makeText(getContext(), Toast_message, Toast.LENGTH_SHORT);
                        toast.show();
                    } break;
                    case 2: {
                        receive_message.setText("");
                    } break;
                    case 3: {
                        Dst();
                    } break;
                    case 4: {
                        Constants.query_mode=0;
                        Connect();  //普通查询
                    } break;
                    case 5: {
                        Send();
                    } break;
                    case 6: {
                        Disconnect();
                    } break;
                    case 7: {
                        Constants.query_mode=2;
                        Connect();  //普通查询
                    } break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + msg.what);
                }
            }
        };

        button_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TableActivity.class);
                startActivity(intent);
//                finish();
            }
        });


        button_choose_floor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.item_statue=1;
                PopupMenu popupMenu=new PopupMenu(getContext(),button_choose_floor);
                popupMenu.inflate(R.menu.popup_floor_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(HomeFragment.this);
            }
        });

        button_choose_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Constants.order_floor!=0){
                    Constants.item_statue=2;
                    PopupMenu popupMenu=new PopupMenu(getContext(),button_choose_place);
                    popupMenu.inflate(R.menu.popup_place_menu);
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener( HomeFragment.this);
                }
            }
        });

        button_choose_site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Constants.order_floor != 0)&&(Constants.order_place != 0)) {

                    Constants.item_statue = 3;
                    PopupMenu popupMenu = new PopupMenu(getContext(), button_choose_site);
                    popupMenu.inflate(R.menu.popup_site_menu);
                    Dao_site dao = new Dao_site(getContext());
                    orderList = dao.getAllDate();
                    int begin=60*(Constants.order_floor-1)+20*(Constants.order_place-1);
                    for (int i = begin ; i < begin+20 ; i++) {
                        int val = orderList.get(i).val;
                        if(val==1){
                            System.out.println("删除item");
                            switch (i-begin+1){
                                case 1:  {popupMenu.getMenu().findItem(R.id.item1).setVisible(false);} break;
                                case 2:  {popupMenu.getMenu().findItem(R.id.item2).setVisible(false);} break;
                                case 3:  {popupMenu.getMenu().findItem(R.id.item3).setVisible(false);} break;
                                case 4:  {popupMenu.getMenu().findItem(R.id.item4).setVisible(false);} break;
                                case 5:  {popupMenu.getMenu().findItem(R.id.item5).setVisible(false);} break;
                                case 6:  {popupMenu.getMenu().findItem(R.id.item6).setVisible(false);} break;
                                case 7:  {popupMenu.getMenu().findItem(R.id.item7).setVisible(false);} break;
                                case 8:  {popupMenu.getMenu().findItem(R.id.item8).setVisible(false);} break;
                                case 9:  {popupMenu.getMenu().findItem(R.id.item9).setVisible(false);} break;
                                case 10: {popupMenu.getMenu().findItem(R.id.item10).setVisible(false);} break;
                                case 11: {popupMenu.getMenu().findItem(R.id.item11).setVisible(false);} break;
                                case 12: {popupMenu.getMenu().findItem(R.id.item12).setVisible(false);} break;
                                case 13: {popupMenu.getMenu().findItem(R.id.item13).setVisible(false);} break;
                                case 14: {popupMenu.getMenu().findItem(R.id.item14).setVisible(false);} break;
                                case 15: {popupMenu.getMenu().findItem(R.id.item15).setVisible(false);} break;
                                case 16: {popupMenu.getMenu().findItem(R.id.item16).setVisible(false);} break;
                                case 17: {popupMenu.getMenu().findItem(R.id.item17).setVisible(false);} break;
                                case 18: {popupMenu.getMenu().findItem(R.id.item18).setVisible(false);} break;
                                case 19: {popupMenu.getMenu().findItem(R.id.item19).setVisible(false);} break;
                                case 20: {popupMenu.getMenu().findItem(R.id.item20).setVisible(false);} break;
                            }
                        }
                    }
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener( HomeFragment.this);
                }
            }
        });

        button_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        // 步骤4:通知主线程,将接收的消息显示到界面
                        Message msg = Message.obtain();
                        msg.what = 7;
                        mMainHandler.sendMessage(msg);
                    }
                });
            }
        });

        Scatter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ScatterChartActivity.class);
                if (i != null) startActivity(i);
//                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
            }
        });

        /**
         * 创建客户端 & 服务器的连接
         */
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        // 步骤4:通知主线程,将接收的消息显示到界面
                        Message msg = Message.obtain();
                        msg.what = 4;
                        mMainHandler.sendMessage(msg);
                    }
                });
            }
        });

//        if(Constants.query_mode==0){
//            Thread daemon = new Thread(new DaemonOnceHome());
//            // 必须在start之前设置为后台线程
//            daemon.setDaemon(true);
//            daemon.start();
//        }

//        if(Constants.query_mode==0){
//            btnConnect.performClick();
//        }

        Connect();

        return root;
    }

    public void Dst(){
        DatabaseHelper_site helper = new DatabaseHelper_site(getContext());
        helper.getWritableDatabase();
        Dao_site dao = new Dao_site(getContext());
        String sql = "delete from "+ Constants.SITE_TABLE_NAME  +" where id > 0";
        dao.execSQL(sql);
//        for(int fl = 1; fl < 5; fl = fl+1){
//            for(int pl = 1; pl < 4; pl = pl+1){
//                for(int n = 1; n < 21; n = n+1){
//                    //    for(int v = 0; v < 2; v = v+1){
//                    int v=0;
//                    int id = n + 1000*pl +10000*fl;
//                    sql = "insert into " + Constants.TABLE_NAME +
//                            "(ID,floor,place,num,val) values("+id+","+ fl +","+ pl +","+ n +","+ v +")";
//                    dao.execSQL(sql);
//                }
//            }
//        }
    }

    public void Connect(  ){
        // 利用线程池直接开启一个线程 & 执行该线程
        mThreadPool.execute(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                try {
                    if (Constants.login_statue == 1) {
                        login_statue.setText("您已登录，" + Constants.name);
                    }
                    else {
                        login_statue.setText("您还未登录,请登录！");
                    }

                    if (Constants.query_mode == 0) {
                        button_choose_floor.setVisibility(View.INVISIBLE);
                        button_choose_place.setVisibility(View.INVISIBLE);
                        button_choose_site.setVisibility(View.INVISIBLE);
                        button_order.setVisibility(View.INVISIBLE);
                    }else if (Constants.query_mode == 1){
                        button_choose_floor.setVisibility(View.VISIBLE);
                        button_choose_place.setVisibility(View.VISIBLE);
                        button_choose_site.setVisibility(View.VISIBLE);
                        button_order.setVisibility(View.VISIBLE);
                    }
                    System.out.println(Constants.login_statue);

//                    String myip = ip.getText().toString();
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

                try {
                    // 步骤1：从Socket 获得输出流对象OutputStream
                    // 该对象作用：发送数据
                    outputStream = socket.getOutputStream();

//                    String edit = mEdit.getText().toString();
                    String edit = null;
                    if (Constants.query_mode == 0) {
                        edit = "1,1,1,1,1";
                    }else if(Constants.query_mode ==1){
                        edit = "query_order" +","+ Constants.time1/1000 +","+ Constants.time2/1000;
                    }else if(Constants.query_mode ==2){
                        edit = "order" + "," + Constants.order_floor + "," + Constants .order_place+ "," + Constants.order_site + ","
                                + Constants.time1/1000 + "," + Constants.time2/1000 + ","+Constants.name;
                    }else{
                        edit = "query_user" + "," + Constants.name;
                    }
//                    Constants.query_mode=0;

                    // 步骤2：写入需要发送的数据到输出流对象中
                    outputStream.write((edit + "\n").getBytes(StandardCharsets.UTF_8));
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
//                Message msg = Message.obtain();
//                msg.what = 0;
//                mMainHandler.sendMessage(msg);
                Recd();
                Disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //*处理接收到的数据*//
    @SuppressLint("SetTextI18n")
    public void Recd() {
        List<Result> Results = new ArrayList<>(4);
        for ( int i = 0; i < 4; i++) {
            Results.add(parse());
        }
        DatabaseHelper_site helper = new DatabaseHelper_site(getContext());
        helper.getWritableDatabase();
        Dao_site dao = new Dao_site(getContext());
        String sql = "delete from "+ Constants.SITE_TABLE_NAME  +" where id > 0";
        dao.execSQL(sql);
        String[] sourceStrArray = response.split(",");

        if(sourceStrArray[0].equals("query_ok")){                   //普通或预约查询成功
            for (int i = 1; i < sourceStrArray.length; i++) {
                int a = Integer.parseInt(sourceStrArray[i]);
                int b = a/100000;
                int c = a/10000-b*10;
                int d = a%10000/10;
                int f = a%2;
                int Id=10000*b+1000*c+d;
                sql = "insert into " + Constants.SITE_TABLE_NAME +
                        "(ID,floor,place,num,val) values("+Id+","+ b +","+ c +","+ d +","+ f +")";
                //                sql = "UPDATE "+Constants.TABLE_NAME+" set val = "+f+" where ID="+Id;
                dao.execSQL(sql);
                if(c==1 && f==1){ Results.get(b-1).t++;Results.get(b-1).at++;}
                if(c==1 && f==0){ Results.get(b-1).f++;Results.get(b-1).af++;}
                if(c==2 && f==1){ Results.get(b-1).t++;Results.get(b-1).bt++;}
                if(c==2 && f==0){ Results.get(b-1).f++;Results.get(b-1).bf++;}
                if(c==3 && f==1){ Results.get(b-1).t++;Results.get(b-1).ct++;}
                if(c==3 && f==0){ Results.get(b-1).f++;Results.get(b-1).cf++;}
            }

            text10.setText(Results.get(0).t +" / 60");
            text11.setText(Results.get(0).at +" / 20");
            text12.setText(Results.get(0).bt +" / 20");
            text13.setText(Results.get(0).ct +" / 20");
            text20.setText(Results.get(1).t +" / 60");
            text21.setText(Results.get(1).at +" / 20");
            text22.setText(Results.get(1).bt +" / 20");
            text23.setText(Results.get(1).ct +" / 20");
            text30.setText(Results.get(2).t +" / 60");
            text31.setText(Results.get(2).at +" / 20");
            text32.setText(Results.get(2).bt +" / 20");
            text33.setText(Results.get(2).ct +" / 20");
            text40.setText(Results.get(3).t +" / 60");
            text41.setText(Results.get(3).at +" / 20");
            text42.setText(Results.get(3).bt +" / 20");
            text43.setText(Results.get(3).ct +" / 20");
        }
        if(sourceStrArray[0].equals("order_ok")){
            showToast("预约成功");
        }
        if(sourceStrArray[0].equals("query_user_ok")){
            Constants.order_id = Integer.parseInt(sourceStrArray[1]);
            Constants.time1 = Integer.parseInt(sourceStrArray[2]);
            Constants.time2 = Integer.parseInt(sourceStrArray[3]);
            System.out.println("Time: "+Constants.time1);
            System.out.println("Time: "+Constants.time2);
            Constants.query_mode=0;
        }
        System.out.println("刷新接收完成");
    }

    private static Result parse() {
        Result order = new Result();
        order.t = 0;
        order.f = 0;
        order.at = 0;
        order.af = 0;
        order.bt = 0;
        order.bf = 0;
        order.ct = 0;
        order.cf = 0;
        return order;
    }

    @SuppressLint("SetTextI18n")
    public void Disconnect(){
        try {
            // 断开 客户端发送到服务器 的连接，即关闭输出流对象OutputStream
            outputStream.close();

            // 断开 服务器发送到客户端 的连接，即关闭输入流读取器对象BufferedReader
            br.close();

            // 最终关闭整个Socket连接
            socket.close();
            if (Constants.query_mode == 0) {
                showToast("当前座位状态查询成功");
                text_query.setText("当前座位状态");
            }else if (Constants.query_mode == 1){
                showToast("预约座位状态查询成功");
                text_query.setText(Constants.year+"-"+Constants.month+"-"+Constants.day+" "+Constants.hour_1
                        +":"+Constants.minute_1+" 至 "+Constants.hour_2+":"+Constants.minute_2 + " 预约的座位状态");
            }else{
//                showToast("预约成功");
                ;
            }

            // 判断客户端和服务器是否已经断开连接
            System.out.println(socket.isClosed());
//            btnDisconnect.setBackgroundColor(Color.parseColor("#00B35B"));
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

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:{
                switch (Constants.item_statue){
                    case 1:{
                        button_choose_floor.setText("一楼");
                        Constants.order_floor= 1;
                    }break;
                    case 2: {
                        Constants.order_place = 1;
                        button_choose_place.setText("A区");
                    }break;
                    case 3: {
                        Constants.order_site = 1;
                        button_choose_site.setText("1");
                    }break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + Constants.item_statue);
                }
            }return true;
            case R.id.item2:{
                switch (Constants.item_statue){
                    case 1:{
                        Constants.order_floor= 2;
                        button_choose_floor.setText("二楼");
                    }break;
                    case 2: {
                        Constants.order_place = 2;
                        button_choose_place.setText("B区");
                    }break;
                    case 3: {
                        Constants.order_site = 2;
                        button_choose_site.setText("2");
                    }break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + Constants.item_statue);
                }
            }return true;
            case R.id.item3:{
                switch (Constants.item_statue){
                    case 1:{
                        Constants.order_floor= 3;
                        button_choose_floor.setText("三楼");
                    }break;
                    case 2: {
                        Constants.order_place = 3;
                        button_choose_place.setText("C区");
                    }break;
                    case 3: {
                        Constants.order_site = 3;
                        button_choose_site.setText("3");
                    }break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + Constants.item_statue);
                }
            } return true;
            case R.id.item4:{
                switch (Constants.item_statue){
                    case 1:{
                        Constants.order_floor= 4;
                        button_choose_floor.setText("四楼");
                    }break;
                    case 3: {
                        Constants.order_site = 4;
                        button_choose_site.setText("4");
                    }break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + Constants.item_statue);
                }
            } return true;
            case R.id.item5:{
                Constants.order_site = 5;
                button_choose_site.setText("5");
            }return true;
            case R.id.item6:{
                Constants.order_site = 6;
                button_choose_site.setText("6");
            }return true;
            case R.id.item7:{
                Constants.order_site = 7;
                button_choose_site.setText("7");
            }return true;
            case R.id.item8:{
                Constants.order_site = 8;
                button_choose_site.setText("8");
            } return true;
            case R.id.item9:{
                Constants.order_site = 9;
                button_choose_site.setText("9");
            } return true;
            case R.id.item10:{
                Constants.order_site = 10;
                button_choose_site.setText("10");
            } return true;
            case R.id.item11:{
                Constants.order_site = 11;
                button_choose_site.setText("11");
            } return true;
            case R.id.item12:{
                Constants.order_site = 12;
                button_choose_site.setText("12");
            } return true;
            case R.id.item13:{
                Constants.order_site = 13;
                button_choose_site.setText("13");
            } return true;
            case R.id.item14:{
                Constants.order_site = 14;
                button_choose_site.setText("14");
            } return true;
            case R.id.item15:{
                Constants.order_site = 15;
                button_choose_site.setText("15");
            } return true;
            case R.id.item16:{
                Constants.order_site = 16;
                button_choose_site.setText("16");
            }return true;
            case R.id.item17:{
                Constants.order_site = 17;
                button_choose_site.setText("17");
            } return true;
            case R.id.item18:{
                Constants.order_site = 18;
                button_choose_site.setText("18");
            }return true;
            case R.id.item19:{
                Constants.order_site = 19;
                button_choose_site.setText("19");
            }return true;
            case R.id.item20:{
                Constants.order_site = 20;
                button_choose_site.setText("20");
//                Toast.makeText(this, "Item 20 selected", Toast.LENGTH_SHORT).show();
            }return true;
            default:
                return false;
        }
    }

    public HomeFragment() {
        mainActivity = this;
    }

    public static HomeFragment getMainActivity() {
        return mainActivity;
    }

    private static HomeFragment mainActivity;

    @SuppressLint("SetTextI18n")
    public void function(){
//        Constants.query_mode=0;
        Connect();

//            if (Constants.login_statue == 1)
//            {
//                login_statue.setText("您已登录，" + Constants.name);
//            }
//            else
//            {
//                login_statue.setText("您还未登录,请登录！");
//            }

    }

}


