package com.app.aarti.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.app.aarti.R;
import com.app.aarti.ui.Aarti;
import com.app.aarti.ui.Aarti.AartiTypes;
import com.app.aarti.ui.AartiUtils;
import com.app.aarti.ui.AartiViewPager;
import com.app.aarti.ui.MainActivityUi;

public class MainActivityController {

	private final String DIR_PATH = Environment.getExternalStorageDirectory()
			+ "/Aarti/.Images/";
	private final String FEEDBACK_FILE_NAME = "Feedback.png";
	private final Uri FEEDBACK_FILE_URI = Uri.parse(DIR_PATH + "Feedback.png");
	private ArrayList<Aarti> mAartiList = new ArrayList();
	private Activity mActivity;
	boolean mBound = false;
	private ServiceConnection mConnection;
	private MediaPlayerService mService = null;
	private MainActivityUi mUi;

	public MainActivityController(Activity paramActivity) {
		this.mActivity = paramActivity;
	}

	private boolean addToSDCard(Bitmap paramBitmap) {
		Log.d("Sahil", "addBitmapToSdCard()  for key = Feedback.png");
		File localFile1 = new File(this.DIR_PATH);
		if (!localFile1.exists()) {
			localFile1.mkdirs();
		}
		try {
			File localFile2 = new File(localFile1.toString() + File.separator
					+ "Feedback.png");
			localFile2.createNewFile();
			BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(
					new FileOutputStream(localFile2));
			paramBitmap.compress(Bitmap.CompressFormat.PNG, 100,
					localBufferedOutputStream);
			localBufferedOutputStream.flush();
			localBufferedOutputStream.close();
			return true;
		} catch (FileNotFoundException localFileNotFoundException) {
			return false;
		} catch (IOException localIOException) {
		}
		return false;
	}

	private void initAartiList() {
		String[] aartiArray = mActivity.getResources().getStringArray(
				R.array.Aarti_list);
		for (int i = 0; i < aartiArray.length; i++) {
			if (aartiArray[i].equals(AartiUtils.AartiConstants.VISHNU_AARTI)) {
				Aarti aarti = new Aarti(AartiUtils.AartiConstants.VISHNU_AARTI,
						AartiTypes.AARTI_STUTI);
				mAartiList.add(aarti);
			} else if (aartiArray[i]
					.equals(AartiUtils.AartiConstants.GAYATRI_AARTI)) {
				Aarti aarti = new Aarti(
						AartiUtils.AartiConstants.GAYATRI_AARTI,
						AartiTypes.MANTRA);
				mAartiList.add(aarti);
			} else if (aartiArray[i]
					.equals(AartiUtils.AartiConstants.GANESH_AARTI)) {
				Aarti aarti = new Aarti(AartiUtils.AartiConstants.GANESH_AARTI,
						AartiTypes.AARTI_STUTI);
				mAartiList.add(aarti);
			} else if (aartiArray[i]
					.equals(AartiUtils.AartiConstants.HANUMAN_AARTI)) {
				Aarti aarti = new Aarti(
						AartiUtils.AartiConstants.HANUMAN_AARTI,
						AartiTypes.CHALISA_AARTI);
				mAartiList.add(aarti);
			} else if (aartiArray[i]
					.equals(AartiUtils.AartiConstants.SHANKAR_AARTI)) {
				Aarti aarti = new Aarti(
						AartiUtils.AartiConstants.SHANKAR_AARTI,
						AartiTypes.AARTI_MANTRA);
				mAartiList.add(aarti);
			} else if (aartiArray[i]
					.equals(AartiUtils.AartiConstants.DURGA_AARTI)) {
				Aarti aarti = new Aarti(AartiUtils.AartiConstants.DURGA_AARTI,
						AartiTypes.AARTI_MANTRA);
				mAartiList.add(aarti);
			}
		}
	}

	private void initServiceConnection() {
		this.mConnection = new ServiceConnection() {
			public void onServiceConnected(
					ComponentName paramAnonymousComponentName,
					IBinder paramAnonymousIBinder) {
				Log.d("Sahil", "onServiceConnected()");
				MediaPlayerService.LocalBinder localLocalBinder = (MediaPlayerService.LocalBinder) paramAnonymousIBinder;
				MainActivityController.this.mService = localLocalBinder
						.getService();
				MainActivityController.this.mService
						.setAartList(MainActivityController.this.mAartiList);
				MainActivityController.this.mBound = true;
			}

			public void onServiceDisconnected(
					ComponentName paramAnonymousComponentName) {
				Log.d("Sahil", "onServiceDisconnected()");
				MainActivityController.this.mBound = false;
			}
		};
	}

	public void captureScreenShot() {
		AartiViewPager localAartiViewPager = this.mUi.getmViewPager();
		localAartiViewPager.setDrawingCacheEnabled(true);
		addToSDCard(Bitmap.createBitmap(localAartiViewPager.getDrawingCache()));
	}

	public ArrayList<Aarti> getAartiList() {
		return this.mAartiList;
	}

	public int getCurrentPosition() {
		return this.mService.getCurrentPosition();
	}

	public MediaPlayerService.PlayType getPlayType() {
		return this.mService.getPlayType();
	}

	public int getTotalDuration() {
		return this.mService.getTotalDuration();
	}

	public MainActivityUi getUi() {
		return this.mUi;
	}

	public void handleSendFeedback() {

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				captureScreenShot();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

				Intent emailIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				emailIntent.setType("message/rfc822");
				emailIntent.putExtra(Intent.EXTRA_EMAIL,
						new String[] { "sahiljain5040@gmail.com" });
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
				emailIntent.putExtra(Intent.EXTRA_STREAM, FEEDBACK_FILE_URI);
				try {
					mActivity.startActivity(Intent.createChooser(emailIntent,
							"Send mail..."));
				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(mActivity,
							"There are no email clients installed.",
							Toast.LENGTH_SHORT).show();
				}

			}

		}.execute();

	}

	public boolean isPlaying() {
		return this.mService.isPlaying();
	}

	public void onCreate() {
		initAartiList();
		initServiceConnection();
		Intent localIntent = new Intent(this.mActivity,
				MediaPlayerService.class);
		this.mActivity.bindService(localIntent, this.mConnection, 1);
	}

	public void onDestroy() {
	}

	public void onPause() {
	}

	public void onResume() {
		if (this.mService != null) {
			this.mService.onActivityResume();
		}
	}

	public void onStop() {
		this.mService.onActivityStop();
	}

	public void pause() {
		this.mService.pause();
	}

	public void play1(int paramInt1, String paramString, int paramInt2,
			boolean paramBoolean) {
		this.mService.initAndPlay(paramInt1, paramString, paramInt2,
				paramBoolean);
	}

	public void playBellMediaPlayer() {
		this.mService.playBellMediaPlayer();
	}

	public void playOrPause() {
		this.mService.playOrPause();
	}

	public void releaseBellMediaPlayer() {
		if (this.mService != null) {
			this.mService.releaseBellMediaPlayer();
		}
	}

	public void releaseMediaPlayer() {
		this.mService.releaseMediaPlayer();
	}

	public void seekTo(int paramInt) {
		this.mService.seekTo(paramInt);
	}

	public void setHandlerToMediaPlayerService(Handler paramHandler) {
		this.mService.setUiHandler(paramHandler);
	}

	public void setPlayType(MediaPlayerService.PlayType paramPlayType) {
		this.mService.setPlayType(paramPlayType);
	}

	public void setSeekingInProgress(boolean paramBoolean) {
		this.mService.setSeekingInProgress(paramBoolean);
	}

	public void setShouldPlayBell(boolean paramBoolean) {
		this.mService.setShouldPlayBell(paramBoolean);
	}

	public void setUi(MainActivityUi paramMainActivityUi) {
		this.mUi = paramMainActivityUi;
	}

	public void setmAartiList(ArrayList<Aarti> paramArrayList) {
		this.mAartiList = paramArrayList;
	}

	public void stopBellMediaPlayer() {
		if (this.mService != null) {
			this.mService.stopBellMediaPlayer();
		}
	}

	public void unbindFromService() {
		mActivity.unbindService(mConnection);

	}

}
