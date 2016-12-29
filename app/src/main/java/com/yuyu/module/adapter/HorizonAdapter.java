package com.yuyu.module.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuyu.module.R;
import com.yuyu.module.utils.HorizonVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HorizonAdapter extends PagerAdapter {

    private final String TAG = HorizonAdapter.class.getSimpleName();

    private List<HorizonVO> vo = new ArrayList<>(
            Arrays.asList(new HorizonVO(R.drawable.ic_menu_camera, "TEST 1"),
                    new HorizonVO(R.drawable.ic_menu_gallery, "TEST 2"),
                    new HorizonVO(R.drawable.ic_menu_manage, "TEST 3"),
                    new HorizonVO(R.drawable.ic_menu_send, "TEST 4")));
    private LayoutInflater layoutInflater;

    public HorizonAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
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
        View view = layoutInflater.inflate(R.layout.item, container, false);
        TextView txt = (TextView) view.findViewById(R.id.txt_item);
        txt.setText(vo.get(position).getTitle());
        ImageView img = (ImageView) view.findViewById(R.id.img_item);
        img.setImageResource(vo.get(position).getRes());
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
