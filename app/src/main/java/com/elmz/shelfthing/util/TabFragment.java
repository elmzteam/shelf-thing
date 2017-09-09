package com.elmz.shelfthing.util;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elmz.shelfthing.R;

public abstract class TabFragment extends Fragment {
	private ViewPagerAdapter mAdapter;
	private ViewPager mViewPager;
	private ViewPagerAdapter.TabItem[] mItems = getItems();
	private TabLayout toBind;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new ViewPagerAdapter(getChildFragmentManager());
		mAdapter.setList(mItems);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.viewpager, container, false);
		mViewPager = (ViewPager) rootView;
		mViewPager.setAdapter(mAdapter);
		if (toBind != null) {
			bindToTabLayout(toBind);
		}
		return rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mViewPager = null;
	}

	public abstract ViewPagerAdapter.TabItem[] getItems();

	public void bindToTabLayout(TabLayout tabLayout) {
		if (mViewPager == null) {
			toBind = tabLayout;
			return;
		}
		tabLayout.setupWithViewPager(mViewPager);
		toBind = null;
	}
}