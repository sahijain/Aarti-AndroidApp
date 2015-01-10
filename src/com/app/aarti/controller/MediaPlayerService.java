package com.app.aarti.controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.RemoteViews;
import com.app.aarti.R.raw;
import com.app.aarti.ui.Aarti;
import com.app.aarti.ui.AartiUtils;
import com.app.aarti.ui.AartiUtils.DeviceAPIChecker;
import com.app.aarti.ui.MainActivity;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class MediaPlayerService
  extends Service
  implements MediaPlayer.OnErrorListener
{
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
  BroadcastReceiver mReciever = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      String str = paramAnonymousIntent.getAction();
      if (str.equals("Play_Pause")) {
        if (MediaPlayerService.this.mMediaPlayer.isPlaying())
        {
          MediaPlayerService.this.mNotificationTemplate.setImageViewResource(2131099676, 2130837509);
          MediaPlayerService.this.mNotificationManager.notify(1, MediaPlayerService.this.mNotification);
          MediaPlayerService.this.playOrPause();
        }
      }
      while (!str.equals("Close")) {
        for (;;)
        {
          return;
          MediaPlayerService.this.mNotificationTemplate.setImageViewResource(2131099676, 2130837508);
        }
      }
      MediaPlayerService.this.releaseMediaPlayer();
      MediaPlayerService.this.releaseBellMediaPlayer();
      MediaPlayerService.this.stopForeground(true);
      MediaPlayerService.this.sendBroadcast(5);
    }
  };
  private boolean shouldPlayBell = false;
  private int storedCurrentPosOnStop;
  
  private PendingIntent getPendingIntent()
  {
    return PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
  }
  
  private int getResId(String paramString, Class<?> paramClass)
  {
    try
    {
      Field localField = paramClass.getDeclaredField(paramString);
      int i = localField.getInt(localField);
      return i;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return -1;
  }
  
  private void initCollapsedLayout()
  {
    this.mNotificationTemplate.setTextViewText(2131099674, "Aarti");
    this.mNotificationTemplate.setTextViewText(2131099675, ((Aarti)this.mAartiList.get(this.mCurrentIndex)).getmAartiName() + new StringBuilder(" ").append(this.mCurrentlyPlaying).toString());
  }
  
  private void initMediaPlayerForBell()
  {
    int i = getResId("temple_bell_fast", R.raw.class);
    this.mBellMediaPlayer = MediaPlayer.create(getApplicationContext(), i);
    this.mBellMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
    {
      public void onCompletion(MediaPlayer paramAnonymousMediaPlayer)
      {
        if (MediaPlayerService.this.shouldPlayBell())
        {
          Log.d("Sahil", "bell media completed,again playing");
          MediaPlayerService.this.mBellMediaPlayer.start();
        }
      }
    });
    if (shouldPlayBell()) {
      this.mBellMediaPlayer.start();
    }
  }
  
  private void initPlayBackControlBroadcastReciever()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("Play_Pause");
    localIntentFilter.addAction("Close");
    registerReceiver(this.mReciever, localIntentFilter);
  }
  
  private void initPlaybackActions()
  {
    this.mNotificationTemplate.setOnClickPendingIntent(2131099676, retreivePlaybackActions(1));
    this.mNotificationTemplate.setOnClickPendingIntent(2131099677, retreivePlaybackActions(2));
  }
  
  private void initUIUpdateThread()
  {
    this.UiUpdateThread = new Thread(new Runnable()
    {
      public void run()
      {
        for (;;)
        {
          try
          {
            if (MediaPlayerService.this.mMediaPlayer.isPlaying())
            {
              boolean bool = MediaPlayerService.this.isSeekingInProgress;
              if (!bool) {
                continue;
              }
            }
          }
          catch (Exception localException)
          {
            int i;
            int j;
            String str1;
            String str2;
            int k;
            Message localMessage;
            Bundle localBundle;
            localException.printStackTrace();
            Log.d("Sahil", "UIUpdate thread terminated with exception");
            continue;
          }
          Log.d("Sahil", "UIUpdate thread terminated");
          return;
          i = MediaPlayerService.this.mMediaPlayer.getDuration();
          j = MediaPlayerService.this.mMediaPlayer.getCurrentPosition();
          str1 = MediaPlayerUtility.milliSecondsToTimer(i);
          str2 = MediaPlayerUtility.milliSecondsToTimer(j);
          k = MediaPlayerUtility.getProgressPercentage(j, i);
          localMessage = MediaPlayerService.this.mHandler.obtainMessage();
          localBundle = new Bundle();
          localBundle.putString("current_duration", str2);
          localBundle.putString("total_duration", str1);
          localBundle.putInt("progress", k);
          localMessage.what = 23;
          localMessage.setData(localBundle);
          MediaPlayerService.this.mHandler.sendMessage(localMessage);
          Thread.sleep(1000L);
        }
      }
    });
    this.UiUpdateThread.start();
  }
  
  private final PendingIntent retreivePlaybackActions(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 1: 
      return PendingIntent.getBroadcast(this, 20, new Intent("Play_Pause"), 0);
    }
    return PendingIntent.getBroadcast(this, 20, new Intent("Close"), 0);
  }
  
  private void sendBroadcast(int paramInt)
  {
    Intent localIntent = new Intent();
    switch (paramInt)
    {
    default: 
      return;
    case 2: 
      localIntent.setAction("completed");
      localIntent.putExtra(AartiUtils.KEY, 2);
      sendBroadcast(localIntent);
      return;
    case 3: 
      localIntent.setAction("play");
      localIntent.putExtra(AartiUtils.KEY, 3);
      sendBroadcast(localIntent);
      return;
    case 4: 
      localIntent.setAction("pause");
      localIntent.putExtra(AartiUtils.KEY, 4);
      sendBroadcast(localIntent);
      return;
    }
    localIntent.setAction("stop");
    localIntent.putExtra(AartiUtils.KEY, 5);
    sendBroadcast(localIntent);
  }
  
  private boolean shouldPlayBell()
  {
    return (this.mMediaPlayer != null) && (this.mMediaPlayer.isPlaying()) && (this.shouldPlayBell);
  }
  
  public int getCurrentPosition()
  {
    if (this.mMediaPlayer == null) {
      return 0;
    }
    return this.mMediaPlayer.getCurrentPosition();
  }
  
  public PlayType getPlayType()
  {
    return this.mPlaytype;
  }
  
  public int getTotalDuration()
  {
    return this.mMediaPlayer.getDuration();
  }
  
  public void initAndPlay(int paramInt1, String paramString, int paramInt2, boolean paramBoolean)
  {
    Log.d("Sahil", "initialising the media player..");
    if (this.mMediaPlayer != null) {
      this.mMediaPlayer.reset();
    }
    this.mCurrentlyPlaying = paramString;
    this.mCurrentIndex = paramInt1;
    int i = getResId(this.mAartiStringArray[paramInt1] + "_" + this.mCurrentlyPlaying, R.raw.class);
    this.mMediaPlayer = MediaPlayer.create(getApplicationContext(), i);
    this.mMediaPlayer.seekTo(paramInt2);
    this.mMediaPlayer.setOnErrorListener(this);
    this.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
    {
      public void onCompletion(MediaPlayer paramAnonymousMediaPlayer)
      {
        MediaPlayerService.this.sendBroadcast(2);
        if ((!MediaPlayerService.this.mPlaytype.equals(MediaPlayerService.PlayType.NONE)) && (MediaPlayerService.this.mPlaytype.equals(MediaPlayerService.PlayType.REPEAT_CURRENT))) {
          MediaPlayerService.this.play();
        }
      }
    });
    if (paramBoolean) {
      play();
    }
  }
  
  public boolean isPlaying()
  {
    return (this.mMediaPlayer != null) && (this.mMediaPlayer.isPlaying());
  }
  
  public boolean isPlayingBell()
  {
    return this.shouldPlayBell;
  }
  
  public void onActivityResume()
  {
    stopForeground(true);
    if (this.shouldPlayBell) {
      initMediaPlayerForBell();
    }
    if (this.mMediaPlayer == null) {
      initAndPlay(this.mCurrentIndex, this.mCurrentlyPlaying, this.storedCurrentPosOnStop, false);
    }
    initUIUpdateThread();
  }
  
  public void onActivityStop()
  {
    if (this.mMediaPlayer.isPlaying())
    {
      this.mNotificationManager = ((NotificationManager)getSystemService("notification"));
      this.mNotificationTemplate = new RemoteViews(getPackageName(), 2130903043);
      initCollapsedLayout();
      if (AartiUtils.DeviceAPIChecker.hasHoneycomb())
      {
        this.mNotification = new NotificationCompat.Builder(this).setSmallIcon(2130837514).setContentIntent(getPendingIntent()).setPriority(0).setContent(this.mNotificationTemplate).build();
        initPlaybackActions();
      }
      for (;;)
      {
        startForeground(1, this.mNotification);
        return;
        this.mNotification = new Notification();
        this.mNotification.contentView = this.mNotificationTemplate;
        Notification localNotification = this.mNotification;
        localNotification.flags = (0x2 | localNotification.flags);
        this.mNotification.icon = 2130837514;
        this.mNotification.contentIntent = getPendingIntent();
      }
    }
    Log.d("Sahil", "Activity stopped and media not playing so releasing the mediaPlayer assets");
    this.storedCurrentPosOnStop = this.mMediaPlayer.getCurrentPosition();
    releaseMediaPlayer();
    releaseBellMediaPlayer();
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    Log.d("Sahil", "onBind() of service...");
    return this.mBinder;
  }
  
  public void onCreate()
  {
    Log.d("Sahil", "onCreate() of service...");
    super.onCreate();
    this.mAartiStringArray = getApplicationContext().getResources().getStringArray(2131034113);
    initPlayBackControlBroadcastReciever();
  }
  
  public void onDestroy()
  {
    Log.d("Sahil", "onDestroy() of service...");
    super.onDestroy();
    if (this.mReciever != null) {
      unregisterReceiver(this.mReciever);
    }
    if (this.mMediaPlayer != null)
    {
      this.mMediaPlayer.release();
      this.mMediaPlayer = null;
    }
    if (this.mBellMediaPlayer != null)
    {
      this.mBellMediaPlayer.release();
      this.mBellMediaPlayer = null;
    }
  }
  
  public boolean onError(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2)
  {
    Log.d("Sahil", "Media Player Error!!!,check this");
    this.mMediaPlayer.reset();
    return false;
  }
  
  public boolean onUnbind(Intent paramIntent)
  {
    Log.d("Sahil", "onUnbind() of service...");
    return super.onUnbind(paramIntent);
  }
  
  public void pause()
  {
    sendBroadcast(4);
    this.mMediaPlayer.pause();
  }
  
  public void play()
  {
    sendBroadcast(3);
    this.mMediaPlayer.start();
    if (this.shouldPlayBell) {
      this.mBellMediaPlayer.start();
    }
    initUIUpdateThread();
  }
  
  public void playBellMediaPlayer()
  {
    this.shouldPlayBell = true;
    if (this.mBellMediaPlayer == null)
    {
      initMediaPlayerForBell();
      return;
    }
    this.mBellMediaPlayer.start();
  }
  
  public void playOrPause()
  {
    if (this.mMediaPlayer.isPlaying())
    {
      pause();
      return;
    }
    play();
  }
  
  public void releaseBellMediaPlayer()
  {
    if (this.mBellMediaPlayer != null)
    {
      this.mBellMediaPlayer.release();
      this.mBellMediaPlayer = null;
    }
  }
  
  public void releaseMediaPlayer()
  {
    Log.d("Sahil", "releasing media player");
    if (this.mMediaPlayer != null)
    {
      this.mMediaPlayer.release();
      this.mMediaPlayer = null;
    }
  }
  
  public void seekTo(int paramInt)
  {
    this.mMediaPlayer.seekTo(paramInt);
  }
  
  public void setAartList(ArrayList<Aarti> paramArrayList)
  {
    this.mAartiList = paramArrayList;
  }
  
  public void setPlayType(PlayType paramPlayType)
  {
    this.mPlaytype = paramPlayType;
  }
  
  public void setSeekingInProgress(boolean paramBoolean)
  {
    this.isSeekingInProgress = paramBoolean;
    if (!paramBoolean) {
      initUIUpdateThread();
    }
  }
  
  public void setShouldPlayBell(boolean paramBoolean)
  {
    this.shouldPlayBell = paramBoolean;
  }
  
  public void setUiHandler(Handler paramHandler)
  {
    this.mHandler = paramHandler;
  }
  
  public void stop()
  {
    this.mMediaPlayer.stop();
  }
  
  public void stopBellMediaPlayer()
  {
    this.shouldPlayBell = false;
    this.mBellMediaPlayer.stop();
    releaseBellMediaPlayer();
  }
  
  public class LocalBinder
    extends Binder
  {
    public LocalBinder() {}
    
    MediaPlayerService getService()
    {
      return MediaPlayerService.this;
    }
  }
  
  public static enum PlayType
  {
    REPEAT_CURRENT,  NONE;
  }
}


/* Location:           C:\Users\sahil.jain\Desktop\dex2jar-0.0.9.15\dex2jar-0.0.9.15\Aarti_com.app.aarti_1.0_1-dex2jar.jar
 * Qualified Name:     com.app.aarti.controller.MediaPlayerService
 * JD-Core Version:    0.7.0.1
 */