package com.app.aarti.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;

import com.app.aarti.R;
import com.app.aarti.controller.MainActivityController;

public class MainActivity extends FragmentActivity {

	private MainActivityController mController;

	private MainActivityUi ui;

	public void onConfigurationChanged(Configuration paramConfiguration) {
		
		super.onConfigurationChanged(paramConfiguration);
		ui.onConfigurationChanged(paramConfiguration);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("Sahil","onCreate() MainActivity Created");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aarti_activity_main_layout);

		mController = new MainActivityController(this);
		mController.onCreate();

		ui = new MainActivityUi(this, mController);
		mController.setUi(ui);

	}	

	protected void onDestroy() {
		Log.d("Sahil", "onDestroy() MainActivity Destroyed");
		super.onDestroy();
	}

	public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
		if (ui.onOptionsItemSelected(paramMenuItem)) {
			return true;
		}
		return super.onOptionsItemSelected(paramMenuItem);
	}

	protected void onPause() {
		Log.d("Sahil", "onPause() MainActivity Paused");
		super.onPause();
	}

	protected void onPostCreate(Bundle paramBundle) {
		super.onPostCreate(paramBundle);
		ui.onPostCreate(paramBundle);
	}

	protected void onResume() {
		Log.d("Sahil", "onResume() MainActivity Resumed");
		super.onResume();
	}

	protected void onStop() {
		Log.d("Sahil", "onStop() MainActivity Stopped");
		super.onStop();
		IntentFilter localIntentFilter = new IntentFilter();
		localIntentFilter.addAction("stop");
		registerReceiver(mReciever, localIntentFilter);
	}

	BroadcastReceiver mReciever = new BroadcastReceiver() {
		
		public void onReceive(Context paramAnonymousContext,
				Intent paramAnonymousIntent) {
			switch (paramAnonymousIntent.getIntExtra(AartiUtils.KEY, 0)) {
			
			case AartiUtils.STOP:
				
				mController.unbindFromService();
				finish();
				break;
			}
			
			
		}
	};
}
