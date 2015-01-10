package com.app.aarti.controller;

public class MediaPlayerUtility
{
  public static int getProgressPercentage(long paramLong1, long paramLong2)
  {
    Double.valueOf(0.0D);
    long l1 = (int)(paramLong1 / 1000L);
    long l2 = (int)(paramLong2 / 1000L);
    return Double.valueOf(100.0D * (l1 / l2)).intValue();
  }
  
  public static String milliSecondsToTimer(long paramLong)
  {
    String str1 = "";
    int i = (int)(paramLong / 3600000L);
    int j = (int)(paramLong % 3600000L) / 60000;
    int k = (int)(paramLong % 3600000L % 60000L / 1000L);
    if (i > 0) {
      str1 = i + ":";
    }
    if (k < 10) {}
    for (String str2 = "0" + k;; str2 = k) {
      return str1 + j + ":" + str2;
    }
  }
  
  public static int progressToTimer(int paramInt1, int paramInt2)
  {
    int i = paramInt2 / 1000;
    return 1000 * (int)(paramInt1 / 100.0D * i);
  }
}


/* Location:           C:\Users\sahil.jain\Desktop\dex2jar-0.0.9.15\dex2jar-0.0.9.15\Aarti_com.app.aarti_1.0_1-dex2jar.jar
 * Qualified Name:     com.app.aarti.controller.MediaPlayerUtility
 * JD-Core Version:    0.7.0.1
 */