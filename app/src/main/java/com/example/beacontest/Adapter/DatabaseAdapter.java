package com.example.beacontest.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.beacontest.Constant.HistoryPoint;
import com.example.beacontest.R;

import java.util.ArrayList;

import static com.example.beacontest.DM.DM.QuerryDataBase;

public class DatabaseAdapter extends BaseAdapter {
    ArrayList<HistoryPoint> pointList = QuerryDataBase();
    private Context context;
    private LayoutInflater inflater;

    public DatabaseAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return pointList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryPoint point = pointList.get(position);
        ViewHolder viewHolder ;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.listitem_main, null);
            viewHolder.txt_time =  convertView.findViewById(R.id.tv_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //向TextView中插入数据
        viewHolder.txt_time.setText(point.gettime());
        return convertView;
    }
}

