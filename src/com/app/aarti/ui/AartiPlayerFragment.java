package com.app.aarti.ui;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.app.aarti.R;
import com.app.aarti.controller.MainActivityController;
import com.app.aarti.controller.MediaPlayerService;
import com.app.aarti.controller.MediaPlayerUtility;
import com.app.aarti.lrc.ILrcView;
import com.app.aarti.lrc.LrcRow;
import com.app.aarti.lrc.controller.LrcController;

public class AartiPlayerFragment extends Fragment implements
		SeekBar.OnSeekBarChangeListener, View.OnClickListener,
		ILrcView.LrcViewListener {

	private static final String TAG = "AartiPlayerFragment";
	private TextView mCurrentDurationText;
	private String mCurrentlyPlaying;
	private Handler mHandler;
	private LrcController mLrcController;
	private ILrcView mLrcView;
	private MainActivityController mMainActivityController;
	private ImageButton mPlayPauseButton;
	private int mProgress;
	private SeekBar mProgressBar;

	private ImageButton mRepeatButton;
	private int mTotalDuration;
	private TextView mTotalDurationText;
	private int position;

	BroadcastReceiver mReciever = new BroadcastReceiver() {

		public void onReceive(Context paramAnonymousContext,
				Intent paramAnonymousIntent) {
			switch (paramAnonymousIntent.getIntExtra(AartiUtils.KEY, 0)) {

			case AartiUtils.COMPLETED:
				AartiPlayerFragment.this.mPlayPauseButton
						.setImageResource(R.drawable.ic_action_play);
				break;
			case AartiUtils.PLAY:
				AartiPlayerFragment.this.updateLRCView(true);
				AartiPlayerFragment.this.mPlayPauseButton
						.setImageResource(R.drawable.ic_action_pause);
				break;

			case AartiUtils.PAUSE:
				AartiPlayerFragment.this.updateLRCView(false);
				AartiPlayerFragment.this.mPlayPauseButton
						.setImageResource(R.drawable.ic_action_play);
			}

		}
	};

	public AartiPlayerFragment(int paramInt,
			MainActivityController paramMainActivityController,
			String paramString) {
		this.position = paramInt;
		this.mMainActivityController = paramMainActivityController;
		this.mCurrentlyPlaying = paramString;
	}

	private void initHandler() {
		this.mHandler = new Handler(new Handler.Callback() {
			public boolean handleMessage(Message paramAnonymousMessage) {
				switch (paramAnonymousMessage.what) {

				case 23:
					Bundle localBundle = paramAnonymousMessage.getData();
					String str1 = localBundle.getString("current_duration");
					String str2 = localBundle.getString("total_duration");
					int i = localBundle.getInt("progress");
					AartiPlayerFragment.this.mCurrentDurationText.setText(str1);
					AartiPlayerFragment.this.mTotalDurationText.setText(str2);
					AartiPlayerFragment.this.mProgressBar.setProgress(i);
					return true;

				default:
					return false;
				}

			}
		});
		this.mMainActivityController
				.setHandlerToMediaPlayerService(this.mHandler);
	}

	private void initLrcController() {
		this.mLrcController = new LrcController(getActivity(), this);
		this.mLrcController
				.setmCurrentPlayingPos((Aarti) this.mMainActivityController
						.getAartiList().get(this.position));
		this.mLrcController.setmCurrentPlaying(this.mCurrentlyPlaying);
		List localList = this.mLrcController.initLrcBuilder();
		this.mLrcView.setLrc(localList);
		this.mLrcView.setListener(this);
	}

	private void initPlayBackControlBroadcastReciever() {
		IntentFilter localIntentFilter = new IntentFilter();
		localIntentFilter.addAction("completed");
		localIntentFilter.addAction("play");
		localIntentFilter.addAction("pause");
		localIntentFilter.addAction("stop");
		getActivity().registerReceiver(this.mReciever, localIntentFilter);
	}

	private void updateLRCView(boolean paramBoolean) {
		if (paramBoolean) {
			this.mLrcController.beginLrcPlay();
			return;
		}
		this.mLrcController.stopLrcPlay();
	}

	public MainActivityController getMainActivityController() {
		return this.mMainActivityController;
	}

	public void onActivityCreated(Bundle paramBundle) {
		super.onActivityCreated(paramBundle);
		Log.d("Sahil", "AartiPlayerFragment: onActivityCreated()");
		initLrcController();
		this.mMainActivityController.play1(this.position,
				this.mCurrentlyPlaying, 0, true);
		this.mMainActivityController
				.setPlayType(MediaPlayerService.PlayType.NONE);
		initPlayBackControlBroadcastReciever();
	}

	public void onAttach(Activity paramActivity) {
		Log.d("Sahil",
				"AartiPlayerFragment: onAttach()Atttaching Fragment to the activity");
		super.onAttach(paramActivity);
		String str = paramActivity.getResources().getStringArray(
				R.array.Aarti_list)[this.position]
				+ " " + this.mCurrentlyPlaying;
		paramActivity.getActionBar().setTitle(str);
		this.mMainActivityController.getUi().setmCurrentTitle(str);
		this.mMainActivityController.setShouldPlayBell(false);
	}

	public void onClick(View paramView) {

		if (paramView.equals(this.mPlayPauseButton)) {
			this.mMainActivityController.playOrPause();
		} else if (paramView.equals(this.mRepeatButton)) {
			if (MediaPlayerService.PlayType.NONE == this.mMainActivityController
					.getPlayType()) {
				this.mRepeatButton
						.setImageResource(R.drawable.ic_action_repeat);
				this.mMainActivityController
						.setPlayType(MediaPlayerService.PlayType.REPEAT_CURRENT);
				return;
			} else {
				this.mRepeatButton
						.setImageResource(R.drawable.ic_action_replay);
				this.mMainActivityController
						.setPlayType(MediaPlayerService.PlayType.NONE);
			}
		}

	}

	public void onCreate(Bundle paramBundle) {
		Log.d("Sahil", "AartiPlayerFragment: onCreate() of Fragment");
		super.onCreate(paramBundle);
	}

	public void onCreateOptionsMenu(Menu paramMenu,
			MenuInflater paramMenuInflater) {
		super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
		paramMenuInflater.inflate(R.menu.aarti_player, paramMenu);
	}

	public View onCreateView(LayoutInflater paramLayoutInflater,
			ViewGroup paramViewGroup, Bundle paramBundle) {

		Log.d("Sahil", "AartiPlayerFragment: onCreateView() of Fragment");
		View localView = paramLayoutInflater.inflate(
				R.layout.aarti_player_fragment_layout, paramViewGroup, false);
		this.mLrcView = ((ILrcView) localView.findViewById(R.id.lrcview));
		this.mCurrentDurationText = ((TextView) localView
				.findViewById(R.id.currenttime));
		this.mTotalDurationText = ((TextView) localView
				.findViewById(R.id.totaltime));
		this.mPlayPauseButton = ((ImageButton) localView
				.findViewById(R.id.play_pause));
		this.mPlayPauseButton.setOnClickListener(this);
		this.mRepeatButton = ((ImageButton) localView.findViewById(R.id.repeat));
		this.mRepeatButton.setOnClickListener(this);
		this.mProgressBar = ((SeekBar) localView
				.findViewById(android.R.id.progress));
		this.mProgressBar.setOnSeekBarChangeListener(this);
		setHasOptionsMenu(true);
		return localView;
	}

	public void onDestroy() {
		super.onDestroy();
		Log.d("Sahil", "AartiPlayerFragment: onDestroy()");
	}

	public void onDestroyView() {
		super.onDestroyView();
		Log.d("Sahil", "AartiPlayerFragment: onDestroyView()");
	}

	public void onDetach() {
		Log.d("Sahil",
				"AartiPlayerFragment: onDetach()Detaching Fragment from the activity");
		super.onDetach();
		getActivity().getActionBar().setTitle(getActivity().getTitle());
		this.mMainActivityController.releaseMediaPlayer();
		this.mMainActivityController.releaseBellMediaPlayer();
	}

	public void onLrcSeeked(long paramLong, LrcRow paramLrcRow) {
		String str = MediaPlayerUtility.milliSecondsToTimer(MediaPlayerUtility
				.progressToTimer(MediaPlayerUtility.getProgressPercentage(
						paramLong, this.mTotalDuration), this.mTotalDuration));
		Log.d("Sahil", "lrcSeeked to " + str);
		this.mCurrentDurationText.setText(str);
		this.mMainActivityController.seekTo((int) paramLong);
	}

	public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
		switch (paramMenuItem.getItemId()) {

		case R.id.bell:
			if (paramMenuItem.getTitle().toString().equalsIgnoreCase("bell_not_enabled")) {
				this.mMainActivityController.playBellMediaPlayer();
				paramMenuItem.setTitle("bell_selected");
			} else {
				this.mMainActivityController.stopBellMediaPlayer();
				paramMenuItem.setTitle("bell_not_enabled");
			}
			return true;

		default:
			return super.onOptionsItemSelected(paramMenuItem);
		}

	}

	public void onProgressChanged(SeekBar paramSeekBar, int paramInt,
			boolean paramBoolean) {
		if (paramBoolean) {
			Log.d("Sahil", "onProgressChanged()progress = " + paramInt);
			this.mProgress = paramInt;
			String str = MediaPlayerUtility
					.milliSecondsToTimer(MediaPlayerUtility.progressToTimer(
							paramInt, this.mTotalDuration));
			this.mCurrentDurationText.setText(str);
		}
	}

	public void onResume() {
		super.onResume();
		Log.d("Sahil", "AartiPlayerFragment: onResume()");
		initPlayBackControlBroadcastReciever();
		initHandler();
		this.mMainActivityController.onResume();
		updateLRCView(true);
		if (this.mMainActivityController.isPlaying()) {
			this.mPlayPauseButton.setImageResource(R.drawable.ic_action_pause);

		} else {
			this.mPlayPauseButton.setImageResource(R.drawable.ic_action_play);
		}
	}

	public void onStartTrackingTouch(SeekBar paramSeekBar) {
		Log.d("Sahil", "onStartTrackingTouch()");
		this.mTotalDuration = this.mMainActivityController.getTotalDuration();
		this.mMainActivityController.setSeekingInProgress(true);
	}

	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(this.mReciever);
		Log.d("Sahil", "AartiPlayerFragment: onStop()");
		this.mMainActivityController.setHandlerToMediaPlayerService(null);
		updateLRCView(false);
		this.mMainActivityController.onStop();
	}

	public void onStopTrackingTouch(SeekBar paramSeekBar) {
		Log.d("Sahil", "onStopTrackingTouch()");
		this.mMainActivityController.seekTo(MediaPlayerUtility.progressToTimer(
				this.mProgress, this.mTotalDuration));
		this.mMainActivityController.setSeekingInProgress(false);
	}

	public void seekLrcToTime(long paramLong) {
		this.mLrcView.seekLrcToTime(paramLong);
	}
}
