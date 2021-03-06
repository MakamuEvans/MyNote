package com.elm.mycheck.login.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.elm.mycheck.login.R;
import com.elm.mycheck.login.model.Reminder;

import static com.elm.mycheck.login.utils.Utils.setupItem;

import java.util.List;

/**
 * Created by elm on 8/11/17.
 */

public class NotificationsAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private boolean mIsTwoWay;
    public List<Reminder> activeReminders;


    public NotificationsAdapter(Context context, boolean mIsTwoWay, List<Reminder> activeReminders) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.mIsTwoWay = mIsTwoWay;
        this.activeReminders = activeReminders;
    }

    @Override
    public int getCount() {
        return activeReminders.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View view;

        view = layoutInflater.inflate(R.layout.two_way_item, container, false);
        System.out.println(activeReminders.get(position));
        if (activeReminders.get(position) != null)
            setupItem(view, activeReminders.get(position));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), activeReminders.get(position).getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }
}
