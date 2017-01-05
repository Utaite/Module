package com.yuyu.module.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuyu.module.R;
import com.yuyu.module.activity.MainActivity;
import com.yuyu.module.utils.HorizonVO;

import java.util.ArrayList;

public class HorizonAdapter extends PagerAdapter {

    private final String TAG = HorizonAdapter.class.getSimpleName();

    private ArrayList<HorizonVO> vo;
    private Context context;

    public HorizonAdapter(Context context, ArrayList<HorizonVO> vo) {
        this.vo = vo;
        this.context = context;
    }

    @Override
    public int getCount() {
        return vo.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, container, false);
        TextView txt = (TextView) view.findViewById(R.id.txt_item);
        txt.setText(vo.get(position).getTitle());
        txt.setOnClickListener(v -> {
            ((MainActivity) context).getToast().setText(container.getResources().getString(R.string.click_text, (position + 1)));
            ((MainActivity) context).getToast().show();
        });
        ImageView img = (ImageView) view.findViewById(R.id.img_item);
        img.setImageResource(vo.get(position).getImg());
        img.setOnClickListener(v -> {
            ((MainActivity) context).getToast().setText(container.getResources().getString(R.string.click_image, (position + 1)));
            ((MainActivity) context).getToast().show();
        });
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }
}
