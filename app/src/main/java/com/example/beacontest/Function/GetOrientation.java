package com.example.beacontest.Function;

import static com.example.beacontest.Constant.TAG.East;
import static com.example.beacontest.Constant.TAG.North;
import static com.example.beacontest.Constant.TAG.Northeast;
import static com.example.beacontest.Constant.TAG.Northwest;
import static com.example.beacontest.Constant.TAG.South;
import static com.example.beacontest.Constant.TAG.Southeast;
import static com.example.beacontest.Constant.TAG.Southwest;
import static com.example.beacontest.Constant.TAG.West;

 public class GetOrientation {
    private String orientation = null;

     public String getStr(int alpha){
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
