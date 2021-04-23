package com.example.androidcarmanager.Galery_Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.androidcarmanager.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class List_Adapter extends BaseAdapter {
    Context context;
    List<List_Model> modellist;
    ArrayList<List_Model> arrayList;

    public List_Adapter(Context context, List<List_Model> modellist ) {
        this.context = context;
        this.modellist = modellist;
//        this.arrayList = arrayList;
        this.arrayList = new ArrayList<List_Model>();
        this.arrayList.addAll(modellist);
    }

    @Override
    public int getCount() {
        return modellist.size();
    }

    @Override
    public Object getItem(int position) {
        return modellist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class MyHolder {
        TextView titleTv, dateTv;
        MyHolder(View v) {
            titleTv = (TextView) v.findViewById(R.id.tvlisttitle);
            dateTv = (TextView) v.findViewById(R.id.tvlistdate);
        }
    }


    @Override
    public View getView(final int position, View converView, ViewGroup parent){
        View row = converView;
        MyHolder holder=null;
        if(row==null){
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            row=inflater.inflate(R.layout.listview,null);
            holder = new MyHolder(row);
            row.setTag(holder);
        }else{
            holder = (MyHolder) row.getTag();
        }
        Calendar c=Calendar.getInstance();
        c.setTimeInMillis(modellist.get(position).getDate());
        c.add(Calendar.MONTH,1);
        holder.dateTv.setText(c.get(Calendar.DAY_OF_MONTH)+"/"+c.get(Calendar.MONTH)+"/"+c.get(Calendar.YEAR));
        holder.titleTv.setText(modellist.get(position).getTitle());
        return row;
    }

    //filter
    public void titleDateFilter(Long startDate, Long endDate, String query){
        query = query.toLowerCase(Locale.getDefault());

        modellist.clear();
        if (startDate == null && endDate == null && query.length()==0){
            modellist.addAll(arrayList);
        }
        else {
            for (List_Model model : arrayList){
                if ((model.getTitle().toLowerCase(Locale.getDefault()).contains(query))
                        && ( model.getDate() >= startDate)
                        && ( model.getDate() <= endDate))
                {
                    modellist.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }

}
