package com.elmz.shelfthing.util;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.elmz.shelfthing.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.elmz.shelfthing.util.Util.makeTag;

public class DrawerActivity extends AppCompatActivity implements
	NavigationView.OnNavigationItemSelectedListener {
	private static final String TAG = makeTag(DrawerActivity.class);
	@BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
	@BindView(R.id.tab_layout) TabLayout mTabLayout;
	@BindView(R.id.navigation) NavigationView mNavigationView;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		mNavigationView.setNavigationItemSelectedListener(this);

		// Use material design toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_close,
			R.string.navigation_drawer_close) {
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.addDrawerListener(mDrawerToggle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public final boolean onOptionsItemSelected(MenuItem item) {
		return onMenuItemSelected(item) ||

			mDrawerToggle.isDrawerIndicatorEnabled() &&
				mDrawerToggle.onOptionsItemSelected(item) ||

			item.getItemId() == android.R.id.home &&
				popBackStack() ||

			super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		if (item.isCheckable()) {
			item.setChecked(true);
		}
		mDrawerLayout.closeDrawer(GravityCompat.START);
		return onMenuItemSelected(item);
	}

	/**
	 * Callback for popping fragment backStack
	 *
	 * @return True if fragment was popped
	 */
	public boolean popBackStack() {
		return getSupportFragmentManager().popBackStackImmediate();
	}

	/**
	 * Wrapper for overriding onOptionsItemSelected
	 *
	 * @param item Selected item
	 * @return True if selection was handled
	 */
	protected boolean onMenuItemSelected(MenuItem item) {
		return onMenuItemSelected(item.getItemId());
	}

	protected boolean onMenuItemSelected(int id) {
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
				mDrawerLayout.closeDrawer(GravityCompat.START);
			} else {
				mDrawerLayout.openDrawer(GravityCompat.START);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			mDrawerLayout.closeDrawer(GravityCompat.START);
			return;
		}
		super.onBackPressed();
	}

	protected void setDrawerIndicatorEnabled(boolean enabled) {
		mDrawerToggle.setDrawerIndicatorEnabled(enabled);
	}

	protected void prepareTabLayout(TabFragment fragment) {
		if (fragment == null) {
			// no need to repeat
			if (mTabLayout.getVisibility() == View.GONE) return;
			// dismantle tabs
			mTabLayout.removeAllTabs();
			mTabLayout.setVisibility(View.GONE);
		} else {
			fragment.bindToTabLayout(mTabLayout);
			mTabLayout.setVisibility(View.VISIBLE);
		}
	}
}
