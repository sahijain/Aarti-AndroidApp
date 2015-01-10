package com.app.aarti.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.aarti.R;
import com.app.aarti.controller.MainActivityController;

public class MainActivityUi implements AdapterView.OnItemClickListener {
	
	private final int LIKE_ON_FB = 2;
	private final int RATE_THIS_APP = 0;
	private final int SEND_FEEDBACK = 1;
	private final int SHARE = 3;
	private String[] mAartilist;
	private Activity mActivity;
	private RelativeLayout mBaseLayout;
	private String mCurrentTitle;
	private String mDefaultTitle;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private MainActivityController mMainActivityController;
	private AartiViewPager mViewPager;

	public MainActivityUi(Activity paramActivity,
			MainActivityController paramMainActivityController) {
		
		mActivity = paramActivity;
		mMainActivityController = paramMainActivityController;
		initUi();
	}

	private void initNavigationDrawer() {
		mDrawerLayout = (DrawerLayout) mActivity
				.findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) mActivity.findViewById(R.id.left_drawer);
		final String[] array = mActivity.getResources().getStringArray(
				R.array.drwawer_list);
		TypedArray drawerIcons = mActivity.getResources().obtainTypedArray(
				R.array.drawer_icons);

		ListViewAdapter drawerListAdapter = new ListViewAdapter(array,
				drawerIcons);
		mDrawerList.setAdapter(drawerListAdapter);
		mDrawerList.setOnItemClickListener(this);

		mDrawerToggle = new ActionBarDrawerToggle(mActivity, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				mActivity.getActionBar().setTitle(mCurrentTitle);
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				mActivity.getActionBar().setTitle(mDefaultTitle);
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mActivity.getActionBar().setDisplayHomeAsUpEnabled(true);
		mActivity.getActionBar().setHomeButtonEnabled(true);
	}

	private void initUi() {
		mCurrentTitle = mDefaultTitle = (String) mActivity.getTitle();
		mBaseLayout = (RelativeLayout) mActivity
				.findViewById(R.id.parent_layout);
		mViewPager = (AartiViewPager) mBaseLayout
				.findViewById(R.id.aarti_viewpager);
		initNavigationDrawer();
		Resources res = mActivity.getResources();
		mAartilist = res.getStringArray(R.array.Aarti_list);

		mViewPager.setAdapter(new ViewPagerAdapter());
		mViewPager.setPageMargin(30);
	}

	public AartiViewPager getmViewPager() {
		
		return mViewPager;
	}

	public void onConfigurationChanged(Configuration paramConfiguration) {
		mDrawerToggle.onConfigurationChanged(paramConfiguration);
		
	}

	public void onItemClick(AdapterView<?> paramAdapterView, View paramView,
			int paramInt, long paramLong) {
		this.mDrawerLayout.closeDrawer(this.mDrawerList);
		switch (paramInt) {
		case RATE_THIS_APP:
			break;
			
		case SEND_FEEDBACK:
			mMainActivityController.handleSendFeedback();
			break;
			
		case LIKE_ON_FB:
			break;
			
		case SHARE:
			break;
			
		default:
			break;
		}
		
	}

	public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
		return mDrawerToggle.onOptionsItemSelected(paramMenuItem);
	}

	protected void onPostCreate(Bundle paramBundle) {
		mDrawerToggle.syncState();
	}

	public void setmCurrentTitle(String paramString) {
		mCurrentTitle = paramString;
	}

	private class ListViewAdapter extends BaseAdapter {

		private String[] mListStringArray;
		private TypedArray mListIconArray;

		public ListViewAdapter(String[] array, TypedArray drawerIcons) {
			mListStringArray = array;
			mListIconArray = drawerIcons;
		}

		@Override
		public int getCount() {
			return mListStringArray.length;
		}

		@Override
		public Object getItem(int arg0) {
			return mListStringArray[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View ConvertView, ViewGroup container) {
			if (ConvertView == null) {
				LayoutInflater inflater = mActivity.getLayoutInflater();
				ConvertView = inflater.inflate(
						R.layout.drawer_list_item_layout, container, false);

			}

			ImageView image = (ImageView) ConvertView.findViewById(R.id.icon);
			image.setImageDrawable(mListIconArray.getDrawable(position));

			TextView tv = (TextView) ConvertView.findViewById(R.id.title);
			tv.setText(mListStringArray[position]);

			return ConvertView;
		}
	}

	private class ViewPagerAdapter extends PagerAdapter implements
			View.OnClickListener {

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			LayoutInflater inflater = mActivity.getLayoutInflater();
			View view = inflater.inflate(R.layout.viewpager_item_layout,
					container, false);
			ImageView image = (ImageView) view.findViewById(R.id.item_image);

			Button button1 = (Button) view.findViewById(R.id.button1);
			button1.setTag(position);
			button1.setOnClickListener(this);
			Button button2 = (Button) view.findViewById(R.id.button2);
			button2.setTag(position);
			button2.setOnClickListener(this);
			Button button3 = (Button) view.findViewById(R.id.button3);
			button3.setTag(position);
			button3.setOnClickListener(this);

			Aarti aarti = mMainActivityController.getAartiList().get(position);

			switch (aarti.getmAartiTypes()) {
			case AARTI_MANTRA:
				button1.setText("aarti");
				button2.setVisibility(View.GONE);
				button3.setText("mantra");
				break;

			case AARTI_STUTI:
				button1.setText("aarti");
				button2.setVisibility(View.GONE);
				button3.setText("stuti");
				break;

			case CHALISA_AARTI:
				button1.setText("aarti");
				button2.setVisibility(View.GONE);
				button3.setText("chalisa");
				break;

			case MANTRA:
				button1.setVisibility(View.GONE);
				button2.setText("mantra");
				button3.setVisibility(View.GONE);
				break;

			default:
				break;
			}

			image.setTag(position);
			container.addView(view);
			mViewPager.setObjectForPosition(view, position);
			return view;
		}

		@Override
		public int getCount() {
			return mAartilist.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mViewPager.findViewFromObject(position));
		}

		@Override
		public void onClick(View view) {

			int position = (Integer) view.getTag();
			
			FragmentManager fragmentManager = ((FragmentActivity) mActivity)
					.getSupportFragmentManager();
			FragmentTransaction fgTransaction = fragmentManager
					.beginTransaction();
			fgTransaction.replace(mBaseLayout.getId(), new AartiPlayerFragment(
					position, mMainActivityController, ((Button) view)
							.getText().toString()));
			fgTransaction.addToBackStack(null);
			fgTransaction.commit();

		}

	}

}
