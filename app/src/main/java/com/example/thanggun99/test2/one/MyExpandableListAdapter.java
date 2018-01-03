package com.example.thanggun99.test2.one;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.thanggun99.test2.App;
import com.example.thanggun99.test2.R;

import butterknife.ButterKnife;

/**
 * Created by thanggun99 on 9/11/17.
 */
public class MyExpandableListAdapter extends BaseExpandableListAdapter implements PinnedHeaderExpListView.PinnedHeaderAdapter,
        AbsListView.OnScrollListener {
    private String[] groups = { "People Names", "Dog Names", "Cat Names", "Fish Names" };
    private String[][] children = {
            { "Arnold", "Barry", "Chuck", "David", "Stas", "Oleg", "Max","Alex","Romeo", "Adolf" },
            { "Ace", "Bandit", "Cha-Cha", "Deuce", "Nokki", "Baron", "Sharik", "Toshka","SObaka","Belka","Strelka","Zhuchka"},
            { "Fluffy", "Snuggles","Cate", "Yasha","Bars" },
            { "Goldy", "Bubbles","Fluffy", "Snuggles","Guffy", "Snoopy" }
    };

    public String getChild(int groupPosition, int childPosition) {
        return children[groupPosition][childPosition];
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        return children[groupPosition].length;
    }

    @SuppressLint("InflateParams")
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child, null);
        }
        TextView textView = ButterKnife.findById(convertView, R.id.tv_title);
        textView.setText(getChild(groupPosition, childPosition));
        return convertView;
    }


    public String getGroup(int groupPosition) {
        return groups[groupPosition];
    }

    public int getGroupCount() {
        return groups.length;
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
        }
        TextView textView = ButterKnife.findById(convertView, R.id.tv_title);
        textView.setText(getGroup(groupPosition));
        return convertView;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void configurePinnedHeader(View v, int position, int alpha) {
        TextView header = ButterKnife.findById(v, R.id.tv_title);
        final String title = getGroup(position);

        header.setText(title);
        int headerColor = ContextCompat.getColor(App.getContext(), R.color.black);
        int headerTextColor = ContextCompat.getColor(App.getContext(), R.color.white);
        if (alpha == 255) {
            header.setBackgroundColor(headerColor);
            header.setTextColor(headerTextColor);
        } else {
            header.setBackgroundColor(Color.argb(alpha,
                    Color.red(headerColor),
                    Color.green(headerColor),
                    Color.blue(headerColor)));
            header.setTextColor(Color.argb(alpha,
                    Color.red(headerTextColor),
                    Color.green(headerTextColor),
                    Color.blue(headerTextColor)));
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view instanceof PinnedHeaderExpListView) {
            ((PinnedHeaderExpListView) view).configureHeaderView(firstVisibleItem);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub

    }

}