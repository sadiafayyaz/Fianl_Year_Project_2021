package com.example.androidcarmanager.Galery_Views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.androidcarmanager.R;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class Gallery_Adatpter extends BaseAdapter {

    Context context;
    List<Gallery_Model> list;
    ArrayList<Gallery_Model> arrayList;

    public Gallery_Adatpter(Context context, List<Gallery_Model> list) {
        this.context = context;
        this.list = list;
//        this.arrayList = arrayList;
        this.arrayList = new ArrayList<Gallery_Model>();
        this.arrayList.addAll(list);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MyHolder {
        //        TextView titleTv;
        ImageView imageView;
        ProgressBar pb;

        MyHolder(View v) {
//            titleTv = (TextView) v.findViewById(R.id.gallery_list_title);

            imageView = (ImageView) v.findViewById(R.id.gallery_list__view_thumbnail);
            pb=v.findViewById(R.id.gallery_list_View_progressbar);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MyHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.gallery_list_views, null);
            holder = new MyHolder(row);
            row.setTag(holder);
        } else {
            holder = (MyHolder) row.getTag();
        }

//        holder.titleTv.setText(list.get(position).getTitle());
        final MyHolder finalHolder = holder;
        Glide.with(context)
                .load(list.get(position).getUrl())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(context, "Failed to load image, try again!", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        finalHolder.pb.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imageView);
        return row;
    }
}