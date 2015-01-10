package com.app.aarti.ui;

import android.app.Activity;
import android.content.res.Configuration;
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
		
		mDrawerLayout = ((DrawerLayout) this.mActivity
				.findViewById(2131099660));
		mDrawerList = ((ListView) mActivity.findViewById(2131099663));
		ListViewAdapter localListViewAdapter = new ListViewAdapter(
				mActivity.getResources().getStringArray(2131034114),
				mActivity.getResources().obtainTypedArray(2131034112));
		mDrawerList.setAdapter(localListViewAdapter);
		mDrawerList.setOnItemClickListener(this);
		mDrawerToggle = new ActionBarDrawerToggle(mActivity,
				mDrawerLayout, 2130837513, 2131230732, 2131230733) {
			public void onDrawerClosed(View paramAnonymousView) {
				super.onDrawerClosed(paramAnonymousView);
				mActivity.getActionBar().setTitle(
						mCurrentTitle);
			}

			public void onDrawerOpened(View paramAnonymousView) {
				super.onDrawerOpened(paramAnonymousView);
				MainActivityUi.this.mActivity.getActionBar().setTitle(
						mDefaultTitle);
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mActivity.getActionBar().setDisplayHomeAsUpEnabled(true);
		mActivity.getActionBar().setHomeButtonEnabled(true);
	}

	private void initUi() {
		
		String str = (String) this.mActivity.getTitle();
		mDefaultTitle = str;
		mCurrentTitle = str;
		mBaseLayout = ((RelativeLayout) mActivity
				.findViewById(2131099661));
		mViewPager = ((AartiViewPager) mBaseLayout
				.findViewById(2131099662));
		initNavigationDrawer();
		mAartilist = mActivity.getResources().getStringArray(
				2131034113);
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
			return;
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
		
		private TypedArray mListIconArray;
		private String[] mListStringArray;

		public ListViewAdapter(String[] paramArrayOfString,
				TypedArray paramTypedArray) {
			mListStringArray = paramArrayOfString;
			mListIconArray = paramTypedArray;
		}

		public int getCount() {
			return mListStringArray.length;
		}

		public Object getItem(int paramInt) {
			return mListStringArray[paramInt];
		}

		public long getItemId(int paramInt) {
			return 0L;
		}

		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			if (paramView == null) {
				paramView = mActivity.getLayoutInflater()
						.inflate(2130903042, paramViewGroup, false);
			}
			((ImageView) paramView.findViewById(2131099670))
					.setImageDrawable(mListIconArray.getDrawable(paramInt));
			((TextView) paramView.findViewById(2131099671))
					.setText(mListStringArray[paramInt]);
			return paramView;
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
