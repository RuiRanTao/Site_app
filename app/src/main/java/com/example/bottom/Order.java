package com.example.bottom;

/**
 * Created by Jne
 * Date: 2015/1/6.
 */
public class Order {
    public int ID;
    public int floor;
    public String place;
    public int num;
    public int val;


    public Order() {
    }

    public Order(int ID, int floor, String place, int num, int val) {
        this.ID = ID;
        this.floor = floor;
        this.place = place;
        this.num = num;
        this.val = val;
    }
}
