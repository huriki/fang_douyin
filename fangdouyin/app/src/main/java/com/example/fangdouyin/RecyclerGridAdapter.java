package com.example.fangdouyin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecyclerGridAdapter extends RecyclerView.Adapter<RecyclerGridAdapter.CustomViewHolder>{
    private List<RecyclerGridBean> datas;
    private Context context;
    private int itemWidth;




    public RecyclerGridAdapter(Context context, List<RecyclerGridBean> datas){
        this.context = context;
        this.datas = datas;
        //计算卡片宽度
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        itemWidth = (screenWidth-3*4)/3;
    }
    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.adapter_recyclegrid,
                parent,false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        RecyclerGridBean bean = datas.get(position);
        holder.likeNumTxt.setText(bean.getLikeNum());
        //设置相应宽度
        holder.imageView.getLayoutParams().height = (int)(1.2*itemWidth);
        holder.imageView.getLayoutParams().width = itemWidth;

        Glide.with(context)
                .load(bean.getImageId())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView likeNumTxt;
        ImageView imageView;
        public CustomViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recyclegrid_image);
            likeNumTxt = itemView.findViewById(R.id.recyclegrid_likeNum);
        }
    }
}
