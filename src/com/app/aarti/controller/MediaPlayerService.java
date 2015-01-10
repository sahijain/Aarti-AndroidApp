package com.app.aarti.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.app.aarti.R;
import com.app.aarti.ui.Aarti;
import com.app.aarti.ui.AartiUtils;
import com.app.aarti.ui.MainActivity;

public class MediaPlayerService extends Service implements
		MediaPlayer.OnErrorListener {
	private final int NOTIFICATION_ID = 1;
	Thread UiUpdateThread;
	private boolean isSeekingInProgress = false;
	private ArrayList<Aarti> mAartiList;
	private String[] mAartiStringArray;
	private MediaPlayer mBellMediaPlayer;
	private LocalBinder mBinder = new LocalBinder();
	private int mCurrentIndex = -1;
	private String mCurrentlyPlaying;
	private Handler mHandler;
	private MediaPlayer mMediaPlayer;
	private Notification mNotification = null;
	private NotificationManager mNotificationManager;
	private RemoteViews mNotificationTemplate;
	PlayType mPlaytype = PlayType.NONE;

	BroadcastReceiver mReciever = new BroadcastReceiver() {
		public void onReceive(Context paramAnonymousContext,
				Intent paramAnonymousIntent) {
			String str = paramAnonymousIntent.getAction();
			if (str.equals("Play_Pause")) {
				if (MediaPlayerService.this.mMediaPlayer.isPlaying()) {
					MediaPlayerService.this.mNotificationTemplate
							.setImageViewResource(R.id.notification_base_play,
									2130837509);

				} else {
					MediaPlayerService.this.mNotificationTemplate
							.setImageViewResource(R.id.notification_base_play,
									2130837508);
				}
				MediaPlayerService.this.mNotificationManager.notify(
						NOTIFICATION_ID, MediaPlayerService.this.mNotification);
				MediaPlayerService.this.playOrPause();

			} else if (str.equals("Close")) {
				MediaPlayerService.this.releaseMediaPlayer();
				MediaPlayerService.this.releaseBellMediaPlayer();
				MediaPlayerService.this.stopForeground(true);
				MediaPlayerService.this.sendBroadcast(5);
			}

		}
	};
	private boolean shouldPlayBell = false;
	private int storedCurrentPosOnStop;

	private PendingIntent getPendingIntent() {
		return PendingIntent.getActivity(this, 0, new Intent(this,
				MainActivity.class), 0);
	}

	private int getResId(String paramString, Class<?> paramClass) {
		try {
			Field localField = paramClass.getDeclaredField(paramString);
			int i = localField.getInt(localField);
			return i;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return -1;
	}

	private void initCollapsedLayout() {
		this.mNotificationTemplate.setTextViewText(2131099674, "Aarti");
		this.mNotificationTemplate.setTextViewText(
				2131099675,
				((Aarti) this.mAartiList.get(this.mCurrentIndex))
						.getmAartiName()
						+ new StringBuilder(" ").append(this.mCurrentlyPlaying)
								.toString());
	}

	private void initMediaPlayerForBell() {
		int i = getResId("temple_bell_fast", R.raw.class);
		this.mBellMediaPlayer = MediaPlayer.create(getApplicationContext(), i);
		this.mBellMediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					public void onCompletion(
							MediaPlayer paramAnonymousMediaPlayer) {
						if (MediaPlayerService.this.shouldPlayBell()) {
							Log.d("Sahil", "bell media completed,again playing");
							MediaPlayerService.this.mBellMediaPlayer.start();
						}
					}
				});
		if (shouldPlayBell()) {
			this.mBellMediaPlayer.start();
		}
	}

	private void initPlayBackControlBroadcastReciever() {
		IntentFilter localIntentFilter = new IntentFilter();
		localIntentFilter.addAction("Play_Pause");
		localIntentFilter.addAction("Close");
		registerReceiver(this.mReciever, localIntentFilter);
	}

	private void initPlaybackActions() {
		this.mNotificationTemplate.setOnClickPendingIntent(R.id.notification_base_play,
				retreivePlaybackActions(1));
		this.mNotificationTemplate.setOnClickPendingIntent(R.id.notification_base_collapse,
				retreivePlaybackActions(2));
	}

	private void initUIUpdateThread() {

		Thread UiUpdateThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (mMediaPlayer.isPlaying() && !isSeekingInProgress) {
						// Log.d("Sahil", "Success");
						int totalTime = mMediaPlayer.getDuration();
						int currentTime = mMediaPlayer.getCurrentPosition();
						String totalTimeString = MediaPlayerUtility
								.milliSecondsToTimer(totalTime);
						String currentTimeString = MediaPlayerUtility
								.milliSecondsToTimer(currentTime);
						int progress = MediaPlayerUtility
								.getProgressPercentage(currentTime, totalTime);
						Message msg = mHandler.obtainMessage();
						Bundle bundle = new Bundle();
						bundle.putString("current_duration", currentTimeString);
						bundle.putString("total_duration", totalTimeString);
						bundle.putInt("progress", progress);
						msg.what = 23;
						msg.setData(bundle);
						mHandler.sendMessage(msg);
						Thread.sleep(1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		UiUpdateThread.start();

	}

	private final PendingIntent retreivePlaybackActions(final int which) {
		Intent action;
		PendingIntent pendingIntent;

		switch (which) {
		case 1:

			Intent playPasueIntent = new Intent("Play_Pause");
			PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(
					this, 20, playPasueIntent, 0);
			return playPausePendingIntent;

		case 2:
			Intent previousIntent1 = new Intent("Close");
			PendingIntent previousPendingIntent1 = PendingIntent.getBroadcast(
					this, 20, previousIntent1, 0);
			return previousPendingIntent1;

		default:
			break;
		}
		return null;
	}

	private void sendBroadcast(int paramInt) {
		Intent localIntent = new Intent();
		switch (paramInt) {
		case AartiUtils.COMPLETED:
			localIntent.setAction("completed");
			localIntent.putExtra(AartiUtils.KEY, 2);
			sendBroadcast(localIntent);
			return;
		case AartiUtils.PLAY:
			localIntent.setAction("play");
			localIntent.putExtra(AartiUtils.KEY, 3);
			sendBroadcast(localIntent);
			return;
		case AartiUtils.PAUSE:
			localIntent.setAction("pause");
			localIntent.putExtra(AartiUtils.KEY, 4);
			sendBroadcast(localIntent);
			return;

		case AartiUtils.STOP:
			localIntent.setAction("stop");
			localIntent.putExtra(AartiUtils.KEY, 5);
			sendBroadcast(localIntent);
			break;
		}
	}

	private boolean shouldPlayBell() {
		return (this.mMediaPlayer != null) && (this.mMediaPlayer.isPlaying())
				&& (this.shouldPlayBell);
	}

	public int getCurrentPosition() {
		if (this.mMediaPlayer == null) {
			return 0;
		}
		return this.mMediaPlayer.getCurrentPosition();
	}

	public PlayType getPlayType() {
		return this.mPlaytype;
	}

	public int getTotalDuration() {
		return this.mMediaPlayer.getDuration();
	}

	public void initAndPlay(int paramInt1, String paramString, int paramInt2,
			boolean paramBoolean) {
		Log.d("Sahil", "initialising the media player..");
		if (this.mMediaPlayer != null) {
			this.mMediaPlayer.reset();
		}
		this.mCurrentlyPlaying = paramString;
		this.mCurrentIndex = paramInt1;
		int i = getResId(this.mAartiStringArray[paramInt1] + "_"
				+ this.mCurrentlyPlaying, R.raw.class);
		this.mMediaPlayer = MediaPlayer.create(getApplicationContext(), i);
		this.mMediaPlayer.seekTo(paramInt2);
		this.mMediaPlayer.setOnErrorListener(this);
		this.mMediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					public void onCompletion(
							MediaPlayer paramAnonymousMediaPlayer) {
						MediaPlayerService.this.sendBroadcast(2);
						if ((!MediaPlayerService.this.mPlaytype
								.equals(MediaPlayerService.PlayType.NONE))
								&& (MediaPlayerService.this.mPlaytype
										.equals(MediaPlayerService.PlayType.REPEAT_CURRENT))) {
							MediaPlayerService.this.play();
						}
					}
				});
		if (paramBoolean) {
			play();
		}
	}

	public boolean isPlaying() {
		return (this.mMediaPlayer != null) && (this.mMediaPlayer.isPlaying());
	}

	public boolean isPlayingBell() {
		return this.shouldPlayBell;
	}

	public void onActivityResume() {
		stopForeground(true);
		if (this.shouldPlayBell) {
			initMediaPlayerForBell();
		}
		if (this.mMediaPlayer == null) {
			initAndPlay(this.mCurrentIndex, this.mCurrentlyPlaying,
					this.storedCurrentPosOnStop, false);
		}
		initUIUpdateThread();
	}

	public void onActivityStop() {

		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {

			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// Default notfication layout
			mNotificationTemplate = new RemoteViews(getPackageName(),
					R.layout.notification_layout);

			// Set up the content view
			initCollapsedLayout();

			if (AartiUtils.DeviceAPIChecker.hasHoneycomb()) {
				// Notification Builder
				mNotification = new NotificationCompat.Builder(this)
						.setSmallIcon(R.drawable.ic_launcher)
						.setContentIntent(getPendingIntent())
						.setPriority(Notification.PRIORITY_DEFAULT)
						.setContent(mNotificationTemplate).build();
				// Control playback from the notification
				initPlaybackActions();
			} else {

				// FIXME: I do not understand why this happens, but the
				// NotificationCompat
				// API does not work on Gingerbread. Specifically, {@code
				// #mBuilder.setContent()} won't apply the custom RV in
				// Gingerbread.
				// So,
				// until this is fixed I'll just use the old way.
				mNotification = new Notification();
				mNotification.contentView = mNotificationTemplate;
				mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
				mNotification.icon = R.drawable.ic_launcher;
				mNotification.contentIntent = getPendingIntent();

			}

			startForeground(NOTIFICATION_ID, mNotification);
		}
	}

	public IBinder onBind(Intent paramIntent) {
		Log.d("Sahil", "onBind() of service...");
		return this.mBinder;
	}

	public void onCreate() {
		Log.d("Sahil", "onCreate() of service...");
		super.onCreate();
		this.mAartiStringArray = getApplicationContext().getResources()
				.getStringArray(R.array.Aarti_list);
		initPlayBackControlBroadcastReciever();
	}

	public void onDestroy() {
		Log.d("Sahil", "onDestroy() of service...");
		super.onDestroy();
		if (this.mReciever != null) {
			unregisterReceiver(this.mReciever);
		}
		if (this.mMediaPlayer != null) {
			this.mMediaPlayer.release();
			this.mMediaPlayer = null;
		}
		if (this.mBellMediaPlayer != null) {
			this.mBellMediaPlayer.release();
			this.mBellMediaPlayer = null;
		}
	}

	public boolean onError(MediaPlayer paramMediaPlayer, int paramInt1,
			int paramInt2) {
		Log.d("Sahil", "Media Player Error!!!,check this");
		this.mMediaPlayer.reset();
		return false;
	}

	public boolean onUnbind(Intent paramIntent) {
		Log.d("Sahil", "onUnbind() of service...");
		return super.onUnbind(paramIntent);
	}

	public void pause() {
		sendBroadcast(4);
		this.mMediaPlayer.pause();
	}

	public void play() {
		sendBroadcast(3);
		this.mMediaPlayer.start();
		if (this.shouldPlayBell) {
			this.mBellMediaPlayer.start();
		}
		initUIUpdateThread();
	}

	public void playBellMediaPlayer() {
		this.shouldPlayBell = true;
		if (this.mBellMediaPlayer == null) {
			initMediaPlayerForBell();
			return;
		}
		this.mBellMediaPlayer.start();
	}

	public void playOrPause() {
		if (this.mMediaPlayer.isPlaying()) {
			pause();
			return;
		}
		play();
	}

	public void releaseBellMediaPlayer() {
		if (this.mBellMediaPlayer != null) {
			this.mBellMediaPlayer.release();
			this.mBellMediaPlayer = null;
		}
	}

	public void releaseMediaPlayer() {
		Log.d("Sahil", "releasing media player");
		if (this.mMediaPlayer != null) {
			this.mMediaPlayer.release();
			this.mMediaPlayer = null;
		}
	}

	public void seekTo(int paramInt) {
		this.mMediaPlayer.seekTo(paramInt);
	}

	public void setAartList(ArrayList<Aarti> paramArrayList) {
		this.mAartiList = paramArrayList;
	}

	public void setPlayType(PlayType paramPlayType) {
		this.mPlaytype = paramPlayType;
	}

	public void setSeekingInProgress(boolean paramBoolean) {
		this.isSeekingInProgress = paramBoolean;
		if (!paramBoolean) {
			initUIUpdateThread();
		}
	}

	public void setShouldPlayBell(boolean paramBoolean) {
		this.shouldPlayBell = paramBoolean;
	}

	public void setUiHandler(Handler paramHandler) {
		this.mHandler = paramHandler;
	}

	public void stop() {
		this.mMediaPlayer.stop();
	}

	public void stopBellMediaPlayer() {
		this.shouldPlayBell = false;
		this.mBellMediaPlayer.stop();
		releaseBellMediaPlayer();
	}

	public class LocalBinder extends Binder {
		public LocalBinder() {
		}

		MediaPlayerService getService() {
			return MediaPlayerService.this;
		}
	}

	public static enum PlayType {
		REPEAT_CURRENT, NONE;
	}
}
