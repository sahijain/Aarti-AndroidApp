package com.app.aarti.lrc.controller;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;
import com.app.aarti.controller.MainActivityController;
import com.app.aarti.lrc.DefaultLrcBuilder;
import com.app.aarti.lrc.ILrcBuilder;
import com.app.aarti.lrc.LrcRow;
import com.app.aarti.ui.Aarti;
import com.app.aarti.ui.AartiPlayerFragment;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LrcController
{
  private Activity mActivity;
  private long mBeginTime = -1L;
  private String mCurrentPlaying;
  private Aarti mCurrentPlayingAarti;
  private AartiPlayerFragment mFragment;
  private int mPalyTimerDuration = 1000;
  private long mStopTime1 = 0L;
  private long mStopTime2 = 0L;
  private TimerTask mTask;
  private Timer mTimer;
  private boolean test = false;
  
  public LrcController(Activity paramActivity, AartiPlayerFragment paramAartiPlayerFragment)
  {
    this.mActivity = paramActivity;
    this.mFragment = paramAartiPlayerFragment;
  }
  
  public void beginLrcPlay()
  {
    if (this.mTimer == null)
    {
      this.mTimer = new Timer();
      this.mTask = new LrcTask();
      this.mTimer.scheduleAtFixedRate(this.mTask, 0L, this.mPalyTimerDuration);
    }
  }
  
  public String getFromAssets(String paramString)
  {
    try
    {
      BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(this.mActivity.getResources().getAssets().open(paramString)));
      Object localObject = "";
      for (;;)
      {
        String str1 = localBufferedReader.readLine();
        if (str1 == null) {
          return localObject;
        }
        if (!str1.trim().equals(""))
        {
          String str2 = localObject + str1 + "\r\n";
          localObject = str2;
        }
      }
      return "";
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public List<LrcRow> initLrcBuilder()
  {
    String str = getFromAssets(this.mCurrentPlayingAarti.getmAartiName() + "_" + this.mCurrentPlaying + "_" + "english.lrc");
    Log.d("Sahi;", "lrc:" + str);
    return new DefaultLrcBuilder().getLrcRows(str);
  }
  
  public void setmCurrentPlaying(String paramString)
  {
    this.mCurrentPlaying = paramString;
  }
  
  public void setmCurrentPlayingPos(Aarti paramAarti)
  {
    this.mCurrentPlayingAarti = paramAarti;
  }
  
  public void stopLrcPlay()
  {
    if (this.mTimer != null)
    {
      this.mTimer.cancel();
      this.mTimer = null;
    }
  }
  
  class LrcTask
    extends TimerTask
  {
    LrcTask() {}
    
    public void run()
    {
      if (LrcController.this.mBeginTime == -1L) {
        LrcController.this.mBeginTime = System.currentTimeMillis();
      }
      final long l = LrcController.this.mFragment.getMainActivityController().getCurrentPosition();
      LrcController.this.mActivity.runOnUiThread(new Runnable()
      {
        public void run()
        {
          LrcController.this.mFragment.seekLrcToTime(l);
        }
      });
    }
  }
}


/* Location:           C:\Users\sahil.jain\Desktop\dex2jar-0.0.9.15\dex2jar-0.0.9.15\Aarti_com.app.aarti_1.0_1-dex2jar.jar
 * Qualified Name:     com.app.aarti.lrc.controller.LrcController
 * JD-Core Version:    0.7.0.1
 */