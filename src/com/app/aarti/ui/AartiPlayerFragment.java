package com.app.aarti.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.app.aarti.controller.MainActivityController;
import com.app.aarti.controller.MediaPlayerService.PlayType;
import com.app.aarti.controller.MediaPlayerUtility;
import com.app.aarti.lrc.ILrcView;
import com.app.aarti.lrc.ILrcView.LrcViewListener;
import com.app.aarti.lrc.LrcRow;
import com.app.aarti.lrc.controller.LrcController;
import java.util.ArrayList;
import java.util.List;

public class AartiPlayerFragment
  extends Fragment
  implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, ILrcView.LrcViewListener
{
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
  BroadcastReceiver mReciever = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      switch (paramAnonymousIntent.getIntExtra(AartiUtils.KEY, 0))
      {
      default: 
        return;
      case 2: 
        AartiPlayerFragment.this.mPlayPauseButton.setImageResource(2130837509);
        return;
      case 3: 
        AartiPlayerFragment.this.updateLRCView(true);
        AartiPlayerFragment.this.mPlayPauseButton.setImageResource(2130837508);
        return;
      }
      AartiPlayerFragment.this.updateLRCView(false);
      AartiPlayerFragment.this.mPlayPauseButton.setImageResource(2130837509);
    }
  };
  private ImageButton mRepeatButton;
  private int mTotalDuration;
  private TextView mTotalDurationText;
  private int position;
  
  public AartiPlayerFragment(int paramInt, MainActivityController paramMainActivityController, String paramString)
  {
    this.position = paramInt;
    this.mMainActivityController = paramMainActivityController;
    this.mCurrentlyPlaying = paramString;
  }
  
  private void initHandler()
  {
    this.mHandler = new Handler(new Handler.Callback()
    {
      public boolean handleMessage(Message paramAnonymousMessage)
      {
        switch (paramAnonymousMessage.what)
        {
        }
        for (;;)
        {
          return true;
          Bundle localBundle = paramAnonymousMessage.getData();
          String str1 = localBundle.getString("current_duration");
          String str2 = localBundle.getString("total_duration");
          int i = localBundle.getInt("progress");
          AartiPlayerFragment.this.mCurrentDurationText.setText(str1);
          AartiPlayerFragment.this.mTotalDurationText.setText(str2);
          AartiPlayerFragment.this.mProgressBar.setProgress(i);
        }
      }
    });
    this.mMainActivityController.setHandlerToMediaPlayerService(this.mHandler);
  }
  
  private void initLrcController()
  {
    this.mLrcController = new LrcController(getActivity(), this);
    this.mLrcController.setmCurrentPlayingPos((Aarti)this.mMainActivityController.getAartiList().get(this.position));
    this.mLrcController.setmCurrentPlaying(this.mCurrentlyPlaying);
    List localList = this.mLrcController.initLrcBuilder();
    this.mLrcView.setLrc(localList);
    this.mLrcView.setListener(this);
  }
  
  private void initPlayBackControlBroadcastReciever()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("completed");
    localIntentFilter.addAction("play");
    localIntentFilter.addAction("pause");
    localIntentFilter.addAction("stop");
    getActivity().registerReceiver(this.mReciever, localIntentFilter);
  }
  
  private void updateLRCView(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mLrcController.beginLrcPlay();
      return;
    }
    this.mLrcController.stopLrcPlay();
  }
  
  public MainActivityController getMainActivityController()
  {
    return this.mMainActivityController;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    Log.d("Sahil", "AartiPlayerFragment: onActivityCreated()");
    initLrcController();
    this.mMainActivityController.play1(this.position, this.mCurrentlyPlaying, 0, true);
    this.mMainActivityController.setPlayType(MediaPlayerService.PlayType.NONE);
    initPlayBackControlBroadcastReciever();
  }
  
  public void onAttach(Activity paramActivity)
  {
    Log.d("Sahil", "AartiPlayerFragment: onAttach()Atttaching Fragment to the activity");
    super.onAttach(paramActivity);
    String str = paramActivity.getResources().getStringArray(2131034113)[this.position] + " " + this.mCurrentlyPlaying;
    paramActivity.getActionBar().setTitle(str);
    this.mMainActivityController.getUi().setmCurrentTitle(str);
    this.mMainActivityController.setShouldPlayBell(false);
  }
  
  public void onClick(View paramView)
  {
    if (paramView.equals(this.mPlayPauseButton)) {
      this.mMainActivityController.playOrPause();
    }
    while (!paramView.equals(this.mRepeatButton)) {
      return;
    }
    if (MediaPlayerService.PlayType.NONE == this.mMainActivityController.getPlayType())
    {
      this.mRepeatButton.setImageResource(2130837510);
      this.mMainActivityController.setPlayType(MediaPlayerService.PlayType.REPEAT_CURRENT);
      return;
    }
    this.mRepeatButton.setImageResource(2130837511);
    this.mMainActivityController.setPlayType(MediaPlayerService.PlayType.NONE);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    Log.d("Sahil", "AartiPlayerFragment: onCreate() of Fragment");
    super.onCreate(paramBundle);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    paramMenuInflater.inflate(2131361793, paramMenu);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    Log.d("Sahil", "AartiPlayerFragment: onCreateView() of Fragment");
    View localView = paramLayoutInflater.inflate(2130903041, paramViewGroup, false);
    this.mLrcView = ((ILrcView)localView.findViewById(2131099664));
    this.mCurrentDurationText = ((TextView)localView.findViewById(2131099668));
    this.mTotalDurationText = ((TextView)localView.findViewById(2131099669));
    this.mPlayPauseButton = ((ImageButton)localView.findViewById(2131099666));
    this.mPlayPauseButton.setOnClickListener(this);
    this.mRepeatButton = ((ImageButton)localView.findViewById(2131099667));
    this.mRepeatButton.setOnClickListener(this);
    this.mProgressBar = ((SeekBar)localView.findViewById(16908301));
    this.mProgressBar.setOnSeekBarChangeListener(this);
    setHasOptionsMenu(true);
    return localView;
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    Log.d("Sahil", "AartiPlayerFragment: onDestroy()");
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    Log.d("Sahil", "AartiPlayerFragment: onDestroyView()");
  }
  
  public void onDetach()
  {
    Log.d("Sahil", "AartiPlayerFragment: onDetach()Detaching Fragment from the activity");
    super.onDetach();
    getActivity().getActionBar().setTitle(getActivity().getTitle());
    this.mMainActivityController.releaseMediaPlayer();
    this.mMainActivityController.releaseBellMediaPlayer();
  }
  
  public void onLrcSeeked(long paramLong, LrcRow paramLrcRow)
  {
    String str = MediaPlayerUtility.milliSecondsToTimer(MediaPlayerUtility.progressToTimer(MediaPlayerUtility.getProgressPercentage(paramLong, this.mTotalDuration), this.mTotalDuration));
    Log.d("Sahil", "lrcSeeked to " + str);
    this.mCurrentDurationText.setText(str);
    this.mMainActivityController.seekTo((int)paramLong);
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    }
    for (;;)
    {
      return super.onOptionsItemSelected(paramMenuItem);
      Log.d("Sahil", "bell selected");
      if (paramMenuItem.getTitle().toString().equals("bell_not_enabled"))
      {
        this.mMainActivityController.playBellMediaPlayer();
        paramMenuItem.setTitle("bell_selected");
      }
      else
      {
        this.mMainActivityController.stopBellMediaPlayer();
        paramMenuItem.setTitle("bell_not_enabled");
      }
    }
  }
  
  public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      Log.d("Sahil", "onProgressChanged()progress = " + paramInt);
      this.mProgress = paramInt;
      String str = MediaPlayerUtility.milliSecondsToTimer(MediaPlayerUtility.progressToTimer(paramInt, this.mTotalDuration));
      this.mCurrentDurationText.setText(str);
    }
  }
  
  public void onResume()
  {
    super.onResume();
    Log.d("Sahil", "AartiPlayerFragment: onResume()");
    initPlayBackControlBroadcastReciever();
    initHandler();
    this.mMainActivityController.onResume();
    updateLRCView(true);
    if (this.mMainActivityController.isPlaying())
    {
      this.mPlayPauseButton.setImageResource(2130837508);
      return;
    }
    this.mPlayPauseButton.setImageResource(2130837509);
  }
  
  public void onStartTrackingTouch(SeekBar paramSeekBar)
  {
    Log.d("Sahil", "onStartTrackingTouch()");
    this.mTotalDuration = this.mMainActivityController.getTotalDuration();
    this.mMainActivityController.setSeekingInProgress(true);
  }
  
  public void onStop()
  {
    super.onStop();
    getActivity().unregisterReceiver(this.mReciever);
    Log.d("Sahil", "AartiPlayerFragment: onStop()");
    this.mMainActivityController.setHandlerToMediaPlayerService(null);
    updateLRCView(false);
    this.mMainActivityController.onStop();
  }
  
  public void onStopTrackingTouch(SeekBar paramSeekBar)
  {
    Log.d("Sahil", "onStopTrackingTouch()");
    this.mMainActivityController.seekTo(MediaPlayerUtility.progressToTimer(this.mProgress, this.mTotalDuration));
    this.mMainActivityController.setSeekingInProgress(false);
  }
  
  public void seekLrcToTime(long paramLong)
  {
    this.mLrcView.seekLrcToTime(paramLong);
  }
}


/* Location:           C:\Users\sahil.jain\Desktop\dex2jar-0.0.9.15\dex2jar-0.0.9.15\Aarti_com.app.aarti_1.0_1-dex2jar.jar
 * Qualified Name:     com.app.aarti.ui.AartiPlayerFragment
 * JD-Core Version:    0.7.0.1
 */