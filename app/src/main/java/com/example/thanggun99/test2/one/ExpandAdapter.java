package com.example.thanggun99.test2.one;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.thanggun99.test2.R;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by thanggun99 on 9/11/17.
 */

public class ExpandAdapter extends BaseExpandableListAdapter {

    private List<String> titleList;
    private HashMap<String, List<String>> childList;

    public ExpandAdapter(List<String> titleList, HashMap<String, List<String>> childList) {
        this.titleList = titleList;
        this.childList = childList;
    }

    @Override
    public int getGroupCount() {
        return titleList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return childList.get(titleList.get(i)).size();
    }

    @Override
    public String getGroup(int i) {
        return titleList.get(i);
    }

    @Override
    public String getChild(int i, int i1) {
        return childList.get(titleList.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_header, null);
        }
        TextView textView = ButterKnife.findById(view, R.id.tv_title);
        textView.setText(getGroup(i));
        return view;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_child, null);
        }
        TextView textView = ButterKnife.findById(view, R.id.tv_title);
        textView.setText(getChild(i, i1));
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
