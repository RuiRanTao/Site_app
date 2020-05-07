package com.example.bottom;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bottom.Database.Dao_site;

import java.util.List;

public class TableActivity extends AppCompatActivity {

    ListView listView;
    public List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        listView = findViewById(R.id.list_view);

        Dao_site dao = new Dao_site(getApplicationContext());
        orderList = dao. getAllDate();

//        final ArrayList<String> fruits = new ArrayList <>();
//
//        fruits.add("Apples");
//        fruits.add("Apricots");
//        fruits.add("Bananas");
//        fruits.add("Blackberry");
//        fruits.add("Blueberry");
//        fruits.add("Cherries");
//        fruits.add("Cranberry");
//        fruits.add("Sugar-apple");
//        fruits.add("Pitaya");
//        fruits.add("Common fig");
//        fruits.add("Cranberry");
//        fruits.add("Jackfruit");
//        fruits.add("Mangos");
//        fruits.add("Papayas");
//        fruits.add("Guava");
//        fruits.add("Jackfruit");
//        fruits.add("Orange");
//        fruits.add("Papaya");
//        fruits.add("Pear");
//        fruits.add("Pineapple");

        MyAdapter adapter = new MyAdapter (this, orderList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"you selected"+orderList.get(position),Toast.LENGTH_SHORT).show();
            }
        });


//        Button button = findViewById(R.id.button_second);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(SecondActivity.this,MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });

//        Thread daemon = new Thread(new DaemonSe());
//        // 必须在start之前设置为后台线程
//        daemon.setDaemon(true);
//        daemon.start();
    }
}