package com.app.aarti.lrc;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class LrcRow
  implements Comparable<LrcRow>
{
  public static final String TAG = "LrcRow";
  public String content;
  public String strTime;
  public long time;
  
  public LrcRow() {}
  
  public LrcRow(String paramString1, long paramLong, String paramString2)
  {
    this.strTime = paramString1;
    this.time = paramLong;
    this.content = paramString2;
    Log.d("LrcRow", "strTime:" + paramString1 + " time:" + paramLong + " content:" + paramString2);
  }
  
  public static List<LrcRow> createRows(String paramString)
  {
    for (int i = 0;; i++)
    {
      try
      {
        if ((paramString.indexOf("[") == 0) && (paramString.indexOf("]") == 9))
        {
          int j = paramString.lastIndexOf("]");
          String str1 = paramString.substring(j + 1, paramString.length());
          String[] arrayOfString = paramString.substring(0, j + 1).replace("[", "-").replace("]", "-").split("-");
          localArrayList = new ArrayList();
          int k = arrayOfString.length;
          if (i >= k) {
            break label175;
          }
          String str2 = arrayOfString[i];
          if (str2.trim().length() == 0) {
            continue;
          }
          localArrayList.add(new LrcRow(str2, timeConvert(str2), str1));
        }
      }
      catch (Exception localException)
      {
        Log.e("LrcRow", "createRows exception:" + localException.getMessage());
        return null;
      }
      ArrayList localArrayList = null;
      label175:
      return localArrayList;
    }
  }
  
  private static long timeConvert(String paramString)
  {
    String[] arrayOfString = paramString.replace('.', ':').split(":");
    return 1000 * (60 * Integer.valueOf(arrayOfString[0]).intValue()) + 1000 * Integer.valueOf(arrayOfString[1]).intValue() + Integer.valueOf(arrayOfString[2]).intValue();
  }
  
  public int compareTo(LrcRow paramLrcRow)
  {
    return (int)(this.time - paramLrcRow.time);
  }
}


/* Location:           C:\Users\sahil.jain\Desktop\dex2jar-0.0.9.15\dex2jar-0.0.9.15\Aarti_com.app.aarti_1.0_1-dex2jar.jar
 * Qualified Name:     com.app.aarti.lrc.LrcRow
 * JD-Core Version:    0.7.0.1
 */