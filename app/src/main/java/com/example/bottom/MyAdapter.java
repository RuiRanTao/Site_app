package com.example.bottom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends BaseAdapter {
     private LayoutInflater mInflater;
    private List<Order> list;


    public MyAdapter(Context context , List<Order> list){

        this.mInflater = LayoutInflater.from(context);
        this.list = list;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder.ID = convertView.findViewById(R.id.tv0);
            holder.floor = convertView.findViewById(R.id.tv1);
            holder.place = convertView.findViewById(R.id.tv2);
            holder.num = convertView.findViewById(R.id.tv3);
            holder.val = convertView.findViewById(R.id.tv4);

//            holder.pic = (ImageView)convertView.findViewById(R.id.iv1);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        holder.ID.setText(Integer.toString(list.get(position).ID));
        holder.floor.setText(Integer.toString(list.get(position).floor));
        holder.place.setText(list.get(position).place);
        holder.num.setText(Integer.toString(list.get(position).num));
        holder.val.setText(Integer.toString(list.get(position).val));

//        holder.age.setText("age");
//        holder.pic.setBackgroundResource((Integer)list.get(position).get("pic"));

        return convertView;
    }


    public final class ViewHolder{
        public TextView ID;
        public TextView floor;
        public TextView place;
        public TextView num;
        public TextView val;
    }

}
