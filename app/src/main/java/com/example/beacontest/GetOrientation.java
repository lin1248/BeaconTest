package com.example.beacontest;

import android.text.StaticLayout;

import static com.example.beacontest.TAG.East;
import static com.example.beacontest.TAG.North;
import static com.example.beacontest.TAG.Northeast;
import static com.example.beacontest.TAG.Northwest;
import static com.example.beacontest.TAG.South;
import static com.example.beacontest.TAG.Southeast;
import static com.example.beacontest.TAG.Southwest;
import static com.example.beacontest.TAG.West;

 class GetOrientation {
    private String orientation = null;

     String getStr(int alpha){
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
