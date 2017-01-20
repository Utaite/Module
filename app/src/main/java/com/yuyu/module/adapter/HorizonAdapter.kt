package com.yuyu.module.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.yuyu.module.R
import com.yuyu.module.activity.MainActivity
import com.yuyu.module.utils.HorizonVO

import java.util.ArrayList

class HorizonAdapter(private val context: Context,
                     private val vo: ArrayList<HorizonVO>) : PagerAdapter() {

    private val TAG: String = HorizonAdapter::class.java.simpleName

    override fun getCount() = vo.size

    override fun getItemPosition(obj: Any?) = PagerAdapter.POSITION_NONE

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.item, container, false)
        val txt = view.findViewById(R.id.txt_item) as TextView
        val img = view.findViewById(R.id.img_item) as ImageView

        txt.run {
            text = vo[position].title
            setOnClickListener {
                (context as MainActivity).toast.setTextShow(
                        context.getString(R.string.horizon_text, position + 1))
            }
        }
        img.run {
            setImageResource(vo[position].img)
            setOnClickListener {
                (context as MainActivity).toast.setTextShow(
                        context.getString(R.string.horizon_image, position + 1))
            }
        }

        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, obj: Any) = view == obj

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) = container.removeView(obj as View)

}

/*
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

    private Context context;
    private ArrayList<HorizonVO> vo;

    public HorizonAdapter(Context context, ArrayList<HorizonVO> vo) {
        this.context = context;
        this.vo = vo;
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
        txt.setOnClickListener(v -> ((MainActivity) context).getToast()
        .setTextShow(container.getResources().getString(R.string.horizon_text, (position + 1))));

        ImageView img = (ImageView) view.findViewById(R.id.img_item);
        img.setImageResource(vo.get(position).getImg());
        img.setOnClickListener(v -> ((MainActivity) context).getToast()
        .setTextShow(container.getResources().getString(R.string.horizon_image, (position + 1))));
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
*/
