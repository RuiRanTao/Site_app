package com.example.bottom.ui.user;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.bottom.Constants;
import com.example.bottom.Daemon.DaemonOnceUser;
import com.example.bottom.LoginActivity;
import com.example.bottom.MainActivity;
import com.example.bottom.R;
import com.example.bottom.RegisterActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserFragment extends Fragment {

    private TextView name;
    private TextView site_id;
    private Button button_register;
    private Button button_login;
    private Button button_time;

    private EditText editTextTime1;
    private EditText editTextDate1;
    private EditText editTextTime2;
    private EditText editTextDate2;
    DatePickerDialog.OnDateSetListener onDateSetListener1;
    DatePickerDialog.OnDateSetListener onDateSetListener2;

    // 主线程Handler
    // 用于将从服务器获取的消息显示出来
    private Handler mMainHandler;

    String Toast_message;

    // 线程池
// 为了方便展示,此处直接采用线程池进行线程管理,而没有一个个开线程
    public ExecutorService mThreadPool;

    private UserViewModel notificationsViewModel;

    @SuppressLint("HandlerLeak")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(UserViewModel.class);
        View root = inflater.inflate(R.layout.fragment_user, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        button_register = root.findViewById(R.id.button_register);
        button_login = root.findViewById(R.id.button_login);

        button_time = root.findViewById(R.id.button_time);
        name = root.findViewById(R.id.name);
        site_id = root.findViewById(R.id.site_id);
        mThreadPool = Executors.newCachedThreadPool();
        // 实例化主线程,用于更新接收过来的消息
        mMainHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0: {
                        Intent intent = new Intent(getContext(), RegisterActivity.class);
                        startActivity(intent);
                        System.out.println("Login clicked \n");
                    }
                    break;
                    case 1: {
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        format.setTimeZone(TimeZone.getTimeZone("GMT"));
//                        Date date = parseDate("2020-01-01 0:0:2", format);
                        Date date1 = parseDate(Integer.toString(Constants.year)+"-"+Integer.toString(Constants.month)+"-"+
                                Integer.toString(Constants.day)+" "+Integer.toString(Constants.hour_1)+":"+Integer.toString(Constants.minute_1)
                                +":00", format);
                        Constants.time1 = date1.getTime();
                        System.out.println(Integer.toString(Constants.year)+"-"+Integer.toString(Constants.month)+"-"+
                                Integer.toString(Constants.day)+" "+Integer.toString(Constants.hour_1)+":"+Integer.toString(Constants.minute_1)
                                +":00");

                        Date date2 = parseDate(Integer.toString(Constants.year)+"-"+Integer.toString(Constants.month)+"-"+
                                Integer.toString(Constants.day)+" "+Integer.toString(Constants.hour_2)+":"+Integer.toString(Constants.minute_2)
                                +":00", format);
                        System.out.println(Integer.toString(Constants.year)+"-"+Integer.toString(Constants.month)+"-"+
                                Integer.toString(Constants.day)+" "+Integer.toString(Constants.hour_2)+":"+Integer.toString(Constants.minute_2)
                                +":00");
                        Constants.time2 = date2.getTime();
                        Date date_now = new Date();
                        long l = date_now.getTime();
//                          System.out.println(l);
                        if(date2.getTime()<=date1.getTime()){
                            showToast("结束时间不能早于起始时间");
                        }
                        if(date_now.getTime()>=date1.getTime() || date_now.getTime()>=date2.getTime()){
                            showToast("预约时间不能早于现在");
                        }
                        if(date2.getTime()>date1.getTime() && (date_now.getTime()<=date1.getTime() || date_now.getTime()<=date2.getTime())){
                            Constants.query_mode=1;
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    }
                    break;
                    case 2: {
                        Toast toast = Toast.makeText(getContext(), Toast_message, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    break;
                    case 3: {
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                    break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + msg.what);
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

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        // 步骤4:通知主线程,将接收的消息显示到界面
                        Message msg = Message.obtain();
                        msg.what = 3;
                        mMainHandler.sendMessage(msg);
                    }
                });
            }
        });

        button_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        // 步骤4:通知主线程,将接收的消息显示到界面
                        Message msg = Message.obtain();
                        msg.what = 1;
                        mMainHandler.sendMessage(msg);
                    }
                });
            }
        });

        editTextTime1 = root.findViewById(R.id.edit_time_view1);
        editTextTime1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog( getContext() ,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                editTextTime1.setText(hourOfDay + ":"+minute);
                                Constants.hour_1=hourOfDay;
                                Constants.minute_1=minute;
                            }
                        }, hour,minute,true);
                timePickerDialog.setTitle("Choose Time");
                timePickerDialog.show();

            }
        });

        editTextDate1 =  root.findViewById(R.id.edit_date_view1);
        editTextDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year= calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        onDateSetListener1,year,month,day);
                datePickerDialog.show();
            }
        });
        onDateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month =month+1;
                String date = dayOfMonth +"/"+month+"/"+year;
                editTextDate1.setText(date);
                Constants.year=year;
                Constants.month=month;
                Constants.day=dayOfMonth;
            }
        };



        editTextTime2 = root.findViewById(R.id.edit_time_view2);
        editTextTime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                editTextTime2.setText(hourOfDay + ":"+minute);
                                Constants.hour_2=hourOfDay;
                                Constants.minute_2=minute;
                            }
                        }, hour,minute,true);
                timePickerDialog.setTitle("Choose Time");
                timePickerDialog.show();

            }
        });


        Thread daemon = new Thread(new DaemonOnceUser());
        // 必须在start之前设置为后台线程
        daemon.setDaemon(true);
        daemon.start();

        return root;
    }

    public static Date parseDate(String str, DateFormat format) {
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public void showToast(String toast){
        Toast_message = toast;
        Message msg = Message.obtain();
        msg.what = 2;
        mMainHandler.sendMessage(msg);
    }

    public UserFragment() {
        mainActivity = this;
    }

    public static UserFragment getMainActivity() {
        return mainActivity;
    }

    private static UserFragment mainActivity;

    @SuppressLint("SetTextI18n")
    public void function(){
        name.setText( Constants.name);
        if(Constants.order_id ==0){
            site_id.setText("无");
        }else {
            Date dd1=new Date();
            dd1.setTime((Constants.time1-8*3600)*1000);
            SimpleDateFormat sim1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time1=sim1.format(dd1);
            System.out.println(time1);

            Date dd2=new Date();
            dd2.setTime((Constants.time2-8*3600)*1000);
            SimpleDateFormat sim2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time2=sim2.format(dd2);
            System.out.println(time2);
            int b = Constants.order_id/10000;
            int c = Constants.order_id/1000-b*10;
            int d = Constants.order_id%1000;
            site_id.setText("楼层: " + Integer.toString(b) +"区域: "+ Integer.toString(c) + "座位号: " + Integer.toString(d) + "\n时间: " + time1 +" - "+ time2 );
        }
    }

}
