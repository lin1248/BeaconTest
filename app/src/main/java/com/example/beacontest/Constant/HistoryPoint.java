package com.example.beacontest.Constant;

public class HistoryPoint {
    private String _id;
    private String time;
    private float[] oldX;
    private float[] oldY;
    private int num;

    public String get_id() {
        return _id;
    }
    public void set_id(String _id) {
        this._id = _id;
    }
    public String gettime() {
        return time;
    }
    public void settime(String time) {
        this.time = time;
    }
    public float[] getoldX() {
        return oldX;
    }
    public void setOldX(float[] oldX) {
        this.oldX=oldX;
    }
    public float[] getoldY() {
        return oldY;
    }
    public void setOldY(float[] oldY) {
        this.oldY=oldY;
    }
    public int getNum(){
        return num;
    }
    public void setNum(int num){
        this.num=num;
    }


    public HistoryPoint(String _id, String time, float[] oldX, float[] oldY, int num) {
        super();
        this._id = _id;
        this.time=time;
        this.oldX=oldX;
        this.oldY=oldY;
        this.num=num;
    }
}
