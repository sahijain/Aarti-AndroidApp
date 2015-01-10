package com.app.aarti.controller;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.app.aarti.ui.Aarti;
import com.app.aarti.ui.Aarti.AartiTypes;
import com.app.aarti.ui.AartiViewPager;
import com.app.aarti.ui.MainActivityUi;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivityController
{
  private final String DIR_PATH = Environment.getExternalStorageDirectory() + "/Aarti/.Images/";
  private final String FEEDBACK_FILE_NAME = "Feedback.png";
  private final Uri FEEDBACK_FILE_URI = Uri.parse(this.DIR_PATH + "Feedback.png");
  private ArrayList<Aarti> mAartiList = new ArrayList();
  private Activity mActivity;
  boolean mBound = false;
  private ServiceConnection mConnection;
  private MediaPlayerService mService = null;
  private MainActivityUi mUi;
  
  public MainActivityController(Activity paramActivity)
  {
    this.mActivity = paramActivity;
  }
  
  private boolean addToSDCard(Bitmap paramBitmap)
  {
    Log.d("Sahil", "addBitmapToSdCard()  for key = Feedback.png");
    File localFile1 = new File(this.DIR_PATH);
    if (!localFile1.exists()) {
      localFile1.mkdirs();
    }
    try
    {
      File localFile2 = new File(localFile1.toString() + File.separator + "Feedback.png");
      localFile2.createNewFile();
      BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(localFile2));
      paramBitmap.compress(Bitmap.CompressFormat.PNG, 100, localBufferedOutputStream);
      localBufferedOutputStream.flush();
      localBufferedOutputStream.close();
      return true;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      return false;
    }
    catch (IOException localIOException) {}
    return false;
  }
  
  private void initAartiList()
  {
    String[] arrayOfString = this.mActivity.getResources().getStringArray(2131034113);
    int i = 0;
    if (i >= arrayOfString.length) {
      return;
    }
    if (arrayOfString[i].equals("vishnu"))
    {
      Aarti localAarti1 = new Aarti("vishnu", Aarti.AartiTypes.AARTI_STUTI);
      this.mAartiList.add(localAarti1);
    }
    for (;;)
    {
      i++;
      break;
      if (arrayOfString[i].equals("gayatri"))
      {
        Aarti localAarti2 = new Aarti("gayatri", Aarti.AartiTypes.MANTRA);
        this.mAartiList.add(localAarti2);
      }
      else if (arrayOfString[i].equals("ganesh"))
      {
        Aarti localAarti3 = new Aarti("ganesh", Aarti.AartiTypes.AARTI_STUTI);
        this.mAartiList.add(localAarti3);
      }
      else if (arrayOfString[i].equals("hanuman"))
      {
        Aarti localAarti4 = new Aarti("hanuman", Aarti.AartiTypes.CHALISA_AARTI);
        this.mAartiList.add(localAarti4);
      }
      else if (arrayOfString[i].equals("shiv"))
      {
        Aarti localAarti5 = new Aarti("shiv", Aarti.AartiTypes.AARTI_MANTRA);
        this.mAartiList.add(localAarti5);
      }
      else if (arrayOfString[i].equals("durga"))
      {
        Aarti localAarti6 = new Aarti("durga", Aarti.AartiTypes.AARTI_MANTRA);
        this.mAartiList.add(localAarti6);
      }
    }
  }
  
  private void initServiceConnection()
  {
    this.mConnection = new ServiceConnection()
    {
      public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
      {
        Log.d("Sahil", "onServiceConnected()");
        MediaPlayerService.LocalBinder localLocalBinder = (MediaPlayerService.LocalBinder)paramAnonymousIBinder;
        MainActivityController.this.mService = localLocalBinder.getService();
        MainActivityController.this.mService.setAartList(MainActivityController.this.mAartiList);
        MainActivityController.this.mBound = true;
      }
      
      public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
      {
        Log.d("Sahil", "onServiceDisconnected()");
        MainActivityController.this.mBound = false;
      }
    };
  }
  
  public void captureScreenShot()
  {
    AartiViewPager localAartiViewPager = this.mUi.getmViewPager();
    localAartiViewPager.setDrawingCacheEnabled(true);
    addToSDCard(Bitmap.createBitmap(localAartiViewPager.getDrawingCache()));
  }
  
  public ArrayList<Aarti> getAartiList()
  {
    return this.mAartiList;
  }
  
  public int getCurrentPosition()
  {
    return this.mService.getCurrentPosition();
  }
  
  public MediaPlayerService.PlayType getPlayType()
  {
    return this.mService.getPlayType();
  }
  
  public int getTotalDuration()
  {
    return this.mService.getTotalDuration();
  }
  
  public MainActivityUi getUi()
  {
    return this.mUi;
  }
  
  public void handleSendFeedback()
  {
    new AsyncTask()
    {
      protected Void doInBackground(Void... paramAnonymousVarArgs)
      {
        MainActivityController.this.captureScreenShot();
        return null;
      }
      
      protected void onPostExecute(Void paramAnonymousVoid)
      {
        Intent localIntent = new Intent("android.intent.action.SEND");
        localIntent.setType("message/rfc822");
        localIntent.putExtra("android.intent.extra.EMAIL", new String[] { "sahiljain5040@gmail.com" });
        localIntent.putExtra("android.intent.extra.SUBJECT", "Feedback");
        localIntent.putExtra("android.intent.extra.STREAM", MainActivityController.this.FEEDBACK_FILE_URI);
        try
        {
          MainActivityController.this.mActivity.startActivity(Intent.createChooser(localIntent, "Send mail..."));
          return;
        }
        catch (ActivityNotFoundException localActivityNotFoundException)
        {
          Toast.makeText(MainActivityController.this.mActivity, "There are no email clients installed.", 0).show();
        }
      }
    }.execute(new Void[0]);
  }
  
  public boolean isPlaying()
  {
    return this.mService.isPlaying();
  }
  
  public void onCreate()
  {
    initAartiList();
    initServiceConnection();
    Intent localIntent = new Intent(this.mActivity, MediaPlayerService.class);
    this.mActivity.bindService(localIntent, this.mConnection, 1);
  }
  
  public void onDestroy() {}
  
  public void onPause() {}
  
  public void onResume()
  {
    if (this.mService != null) {
      this.mService.onActivityResume();
    }
  }
  
  public void onStop()
  {
    this.mService.onActivityStop();
  }
  
  public void pause()
  {
    this.mService.pause();
  }
  
  public void play1(int paramInt1, String paramString, int paramInt2, boolean paramBoolean)
  {
    this.mService.initAndPlay(paramInt1, paramString, paramInt2, paramBoolean);
  }
  
  public void playBellMediaPlayer()
  {
    this.mService.playBellMediaPlayer();
  }
  
  public void playOrPause()
  {
    this.mService.playOrPause();
  }
  
  public void releaseBellMediaPlayer()
  {
    if (this.mService != null) {
      this.mService.releaseBellMediaPlayer();
    }
  }
  
  public void releaseMediaPlayer()
  {
    this.mService.releaseMediaPlayer();
  }
  
  public void seekTo(int paramInt)
  {
    this.mService.seekTo(paramInt);
  }
  
  public void setHandlerToMediaPlayerService(Handler paramHandler)
  {
    this.mService.setUiHandler(paramHandler);
  }
  
  public void setPlayType(MediaPlayerService.PlayType paramPlayType)
  {
    this.mService.setPlayType(paramPlayType);
  }
  
  public void setSeekingInProgress(boolean paramBoolean)
  {
    this.mService.setSeekingInProgress(paramBoolean);
  }
  
  public void setShouldPlayBell(boolean paramBoolean)
  {
    this.mService.setShouldPlayBell(paramBoolean);
  }
  
  public void setUi(MainActivityUi paramMainActivityUi)
  {
    this.mUi = paramMainActivityUi;
  }
  
  public void setmAartiList(ArrayList<Aarti> paramArrayList)
  {
    this.mAartiList = paramArrayList;
  }
  
  public void stopBellMediaPlayer()
  {
    if (this.mService != null) {
      this.mService.stopBellMediaPlayer();
    }
  }
  
  public void unbindFromService()
  {
    this.mActivity.unbindService(this.mConnection);
  }
}


/* Location:           C:\Users\sahil.jain\Desktop\dex2jar-0.0.9.15\dex2jar-0.0.9.15\Aarti_com.app.aarti_1.0_1-dex2jar.jar
 * Qualified Name:     com.app.aarti.controller.MainActivityController
 * JD-Core Version:    0.7.0.1
 */