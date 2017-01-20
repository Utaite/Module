package com.yuyu.module.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yuyu.module.R;
import com.yuyu.module.utils.ChatVO;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final String TAG = ChatAdapter.class.getSimpleName();

    private Context context;
    private ArrayList<ChatVO> vo;

    public ChatAdapter(Context context) {
        this.context = context;
        vo = new ArrayList<>();
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_card_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(context).load(vo.get(position).getPhotoUrl())
                .bitmapTransform(new CropCircleTransformation(context))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.imageView);
        holder.textView.setText(vo.get(position).getEmail() + ": " + vo.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return vo.size();
    }

    public void add(ChatVO chatVO) {
        vo.add(chatVO);
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image_view);
            textView = (TextView) view.findViewById(R.id.text_view);
        }
    }

}