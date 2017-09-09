package com.elmz.shelfthing.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
	public static class TabItem {
		public Fragment fragment;
		public String title;
		public TabItem(Fragment fragment, String title) {
			this.fragment = fragment;
			this.title = title;
		}
	}
	private final List<TabItem> mList = new ArrayList<>();

	ViewPagerAdapter(FragmentManager manager) {
		super(manager);
	}

	@Override
	public Fragment getItem(int position) {
		return mList.get(position).fragment;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mList.get(position).title;
	}

	public void setList(TabItem[] items) {
		mList.clear();
		Collections.addAll(mList, items);
	}
}
