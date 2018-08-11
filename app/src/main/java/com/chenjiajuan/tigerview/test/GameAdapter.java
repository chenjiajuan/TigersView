package com.chenjiajuan.tigerview.test;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by chenjiajuan on 2018/8/11.
 */

public interface GameAdapter {
    public int getItemsCount();

    public View getItem(int index, View convertView, ViewGroup parent);

    public View getEmptyItem(View convertView, ViewGroup parent);
}
