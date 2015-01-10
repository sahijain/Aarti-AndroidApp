package com.app.aarti.lrc;

import java.util.List;

public abstract interface ILrcView
{
  public abstract void seekLrc(int paramInt);
  
  public abstract void seekLrcToTime(long paramLong);
  
  public abstract void setListener(LrcViewListener paramLrcViewListener);
  
  public abstract void setLrc(List<LrcRow> paramList);
  
  public static abstract interface LrcViewListener
  {
    public abstract void onLrcSeeked(long paramLong, LrcRow paramLrcRow);
  }
}


/* Location:           C:\Users\sahil.jain\Desktop\dex2jar-0.0.9.15\dex2jar-0.0.9.15\Aarti_com.app.aarti_1.0_1-dex2jar.jar
 * Qualified Name:     com.app.aarti.lrc.ILrcView
 * JD-Core Version:    0.7.0.1
 */