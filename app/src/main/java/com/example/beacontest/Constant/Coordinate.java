package com.example.beacontest.Constant;

/**
 * 封装一个坐标类
 * x 横坐标
 * y 纵坐标
 * distance 欧式距离
 */
public class Coordinate {
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getEulideanDistance() {
        return EulideanDistance;
    }

    public void setEulideanDistance(double eulideanDistance) {
        EulideanDistance = eulideanDistance;
    }

    private double x;
    private double y;
    private double EulideanDistance;

}
