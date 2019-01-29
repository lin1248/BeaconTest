package com.example.beacontest;

public class GetOrientation {
    String orientation = null;
    private static String North = "北";
    private static String South = "南";
    private static String East = "东";
    private static String West = "西";
    private static String Northeast = "东北";
    private static String Southwest = "西南";
    private static String Southeast= "东南";
    private static String Northwest = "西北";

    protected String getStr(int alpha){
        if((alpha>=0 && alpha<15) || (alpha>345 && alpha<=360)){
            orientation = North;
        }
        else if(alpha>=15 && alpha<=75)
        {
            orientation = Northeast;
        }
        else if(alpha>75 && alpha<105){
            orientation = East;
        }
        else if(alpha>=105 && alpha<=165){
            orientation = Southeast;
        }
        else if(alpha>165 && alpha<195){
            orientation = South;
        }
        else if(alpha>=195 && alpha<=255){
            orientation = Southwest;
        }
        else if(alpha>255 && alpha<285){
            orientation = West;
        }
        else if(alpha>=285 && alpha<=345){
            orientation = Northwest;
        }
        return orientation;
    }
}
