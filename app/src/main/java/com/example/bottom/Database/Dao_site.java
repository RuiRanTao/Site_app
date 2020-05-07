package com.example.bottom.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.bottom.Constants;
import com.example.bottom.Order;

import java.util.ArrayList;
import java.util.List;

public class Dao_site {

    private final DatabaseHelper_site helper;
    private static final String TAG = "DatabaseHelper";
    private final String[] ORDER_COLUMNS = new String[] {"ID", "floor", "place","num","val"};


    public Dao_site(Context context){

        helper = new DatabaseHelper_site(context);

    }

    //        Dao dao = new Dao(getApplicationContext());
    //        dao.insert();

    public void insert(){
        SQLiteDatabase  db = helper.getWritableDatabase();
        String sql = "insert into " + Constants.SITE_TABLE_NAME + "(floor,place,num,val) values(?,?,?,?,?)";
        db.execSQL(sql,new Object[]{10011,1,"A",100,1});
    }

    /**
     * 执行自定义SQL语句
     */
    public void execSQL(String sql) {
        SQLiteDatabase db = null;

        try {
            if (sql.contains("select")){
//                Toast.makeText(context, "Sorry，还没处理select语句", Toast.LENGTH_SHORT).show();
            }else if (sql.contains("insert") || sql.contains("update") || sql.contains("delete")){
                db = helper.getWritableDatabase();
                db.beginTransaction();
                db.execSQL(sql);
                db.setTransactionSuccessful();
//                Toast.makeText(context, "执行SQL语句成功", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
//            Toast.makeText(context, "执行出错，请检查SQL语句", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }


    /**
     * 查询数据库中所有数据
     * @return
     */
    public List<Order> getAllDate(){
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = helper.getReadableDatabase();
            // select * from Orders
            cursor = db.query(Constants.SITE_TABLE_NAME, ORDER_COLUMNS, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                List<Order> orderList = new ArrayList<Order>(cursor.getCount());
                while (cursor.moveToNext()) {
                    orderList.add(parseOrder(cursor));
                }
                return orderList;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return null;
    }

        /**
     * 将查找到的数据转换成Order类
     */
    private Order parseOrder(Cursor cursor){
        Order order = new Order();
        order.ID = (cursor.getInt(cursor.getColumnIndex("ID")));
        order.floor = (cursor.getInt(cursor.getColumnIndex("floor")));
        order.place = (cursor.getString(cursor.getColumnIndex("place")));
        order.num = (cursor.getInt(cursor.getColumnIndex("num")));
        order.val = (cursor.getInt(cursor.getColumnIndex("val")));
        return order;
    }

    public void delete(){

    }

    public void update(){

    }

    public void query(){

    }



}
