package com.app.aarti.lrc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.List;

public class LrcView
  extends View
  implements ILrcView
{
  public static final int DISPLAY_MODE_NORMAL = 0;
  public static final int DISPLAY_MODE_SCALE = 2;
  public static final int DISPLAY_MODE_SEEK = 1;
  public static final String TAG = "LrcView";
  private int mDisplayMode = 0;
  private int mHignlightRow = 0;
  private int mHignlightRowColor = -256;
  private boolean mIsFirstMove = false;
  private float mLastMotionY;
  private String mLoadingLrcTip = "Downloading lrc...";
  private int mLrcFontSize = 23;
  private List<LrcRow> mLrcRows;
  private ILrcView.LrcViewListener mLrcViewListener;
  private int mMaxLrcFontSize = 35;
  private int mMaxSeekLineTextSize = 18;
  private int mMinLrcFontSize = 15;
  private int mMinSeekFiredOffset = 10;
  private int mMinSeekLineTextSize = 13;
  private int mNormalRowColor = -1;
  private int mPaddingY = 10;
  private Paint mPaint = new Paint(1);
  private PointF mPointerOneLastMotion = new PointF();
  private PointF mPointerTwoLastMotion = new PointF();
  private int mSeekLineColor = -16711681;
  private int mSeekLinePaddingX = 0;
  private int mSeekLineTextColor = -16711681;
  private int mSeekLineTextSize = 15;
  
  public LrcView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mPaint.setTextSize(this.mLrcFontSize);
  }
  
  private void doScale(MotionEvent paramMotionEvent)
  {
    if (this.mDisplayMode == 1)
    {
      this.mDisplayMode = 2;
      Log.d("LrcView", "two move but teaking ...change mode");
      return;
    }
    if (this.mIsFirstMove)
    {
      this.mDisplayMode = 2;
      invalidate();
      this.mIsFirstMove = false;
      setTwoPointerLocation(paramMotionEvent);
    }
    int i = getScale(paramMotionEvent);
    Log.d("LrcView", "scaleSize:" + i);
    if (i != 0)
    {
      setNewFontSize(i);
      invalidate();
    }
    setTwoPointerLocation(paramMotionEvent);
  }
  
  private void doSeek(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getY();
    float f2 = f1 - this.mLastMotionY;
    if (Math.abs(f2) < this.mMinSeekFiredOffset) {
      return;
    }
    this.mDisplayMode = 1;
    int i = Math.abs((int)f2 / this.mLrcFontSize);
    Log.d("LrcView", "move new hightlightrow : " + this.mHignlightRow + " offsetY: " + f2 + " rowOffset:" + i);
    if (f2 < 0.0F) {
      this.mHignlightRow = (i + this.mHignlightRow);
    }
    for (;;)
    {
      this.mHignlightRow = Math.max(0, this.mHignlightRow);
      this.mHignlightRow = Math.min(this.mHignlightRow, -1 + this.mLrcRows.size());
      if (i <= 0) {
        break;
      }
      this.mLastMotionY = f1;
      invalidate();
      return;
      if (f2 > 0.0F) {
        this.mHignlightRow -= i;
      }
    }
  }
  
  private int getScale(MotionEvent paramMotionEvent)
  {
    Log.d("LrcView", "scaleSize getScale");
    float f1 = paramMotionEvent.getX(0);
    float f2 = paramMotionEvent.getY(0);
    float f3 = paramMotionEvent.getX(1);
    float f4 = paramMotionEvent.getY(1);
    float f5 = Math.abs(this.mPointerOneLastMotion.x - this.mPointerTwoLastMotion.x);
    float f6 = Math.abs(f3 - f1);
    float f7 = Math.abs(this.mPointerOneLastMotion.y - this.mPointerTwoLastMotion.y);
    float f8 = Math.abs(f4 - f2);
    float f9 = Math.max(Math.abs(f6 - f5), Math.abs(f8 - f7));
    if (f9 == Math.abs(f6 - f5))
    {
      if (f6 > f5) {}
      for (i = 1;; i = 0)
      {
        Log.d("LrcView", "scaleSize maxOffset:" + f9);
        if (i == 0) {
          break;
        }
        return (int)(f9 / 10.0F);
      }
    }
    if (f8 > f7) {}
    for (int i = 1;; i = 0) {
      break;
    }
    return -(int)(f9 / 10.0F);
  }
  
  private void setNewFontSize(int paramInt)
  {
    this.mLrcFontSize = (paramInt + this.mLrcFontSize);
    this.mSeekLineTextSize = (paramInt + this.mSeekLineTextSize);
    this.mLrcFontSize = Math.max(this.mLrcFontSize, this.mMinLrcFontSize);
    this.mLrcFontSize = Math.min(this.mLrcFontSize, this.mMaxLrcFontSize);
    this.mSeekLineTextSize = Math.max(this.mSeekLineTextSize, this.mMinSeekLineTextSize);
    this.mSeekLineTextSize = Math.min(this.mSeekLineTextSize, this.mMaxSeekLineTextSize);
  }
  
  private void setTwoPointerLocation(MotionEvent paramMotionEvent)
  {
    this.mPointerOneLastMotion.x = paramMotionEvent.getX(0);
    this.mPointerOneLastMotion.y = paramMotionEvent.getY(0);
    this.mPointerTwoLastMotion.x = paramMotionEvent.getX(1);
    this.mPointerTwoLastMotion.y = paramMotionEvent.getY(1);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    int i = getHeight();
    int j = getWidth();
    if ((this.mLrcRows == null) || (this.mLrcRows.size() == 0))
    {
      if (this.mLoadingLrcTip != null)
      {
        this.mPaint.setColor(this.mHignlightRowColor);
        this.mPaint.setTextSize(this.mLrcFontSize);
        this.mPaint.setTextAlign(Paint.Align.CENTER);
        paramCanvas.drawText(this.mLoadingLrcTip, j / 2, i / 2 - this.mLrcFontSize, this.mPaint);
      }
      return;
    }
    int k = j / 2;
    String str = ((LrcRow)this.mLrcRows.get(this.mHignlightRow)).content;
    int m = i / 2 - this.mLrcFontSize;
    this.mPaint.setColor(this.mHignlightRowColor);
    this.mPaint.setTextSize(this.mLrcFontSize);
    this.mPaint.setTextAlign(Paint.Align.CENTER);
    paramCanvas.drawText(str, k, m, this.mPaint);
    if (this.mDisplayMode == 1)
    {
      this.mPaint.setColor(this.mSeekLineColor);
      paramCanvas.drawLine(this.mSeekLinePaddingX, m, j - this.mSeekLinePaddingX, m, this.mPaint);
      this.mPaint.setColor(this.mSeekLineTextColor);
      this.mPaint.setTextSize(this.mSeekLineTextSize);
      this.mPaint.setTextAlign(Paint.Align.LEFT);
      paramCanvas.drawText(((LrcRow)this.mLrcRows.get(this.mHignlightRow)).strTime, 0.0F, m, this.mPaint);
    }
    this.mPaint.setColor(this.mNormalRowColor);
    this.mPaint.setTextSize(this.mLrcFontSize);
    this.mPaint.setTextAlign(Paint.Align.CENTER);
    int n = -1 + this.mHignlightRow;
    int i1 = m - this.mPaddingY - this.mLrcFontSize;
    for (;;)
    {
      if ((i1 <= -this.mLrcFontSize) || (n < 0))
      {
        int i2 = 1 + this.mHignlightRow;
        int i3 = m + this.mPaddingY + this.mLrcFontSize;
        while ((i3 < i) && (i2 < this.mLrcRows.size()))
        {
          paramCanvas.drawText(((LrcRow)this.mLrcRows.get(i2)).content, k, i3, this.mPaint);
          i3 += this.mPaddingY + this.mLrcFontSize;
          i2++;
        }
        break;
      }
      paramCanvas.drawText(((LrcRow)this.mLrcRows.get(n)).content, k, i1, this.mPaint);
      i1 -= this.mPaddingY + this.mLrcFontSize;
      n--;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mLrcRows == null) || (this.mLrcRows.size() == 0)) {
      return super.onTouchEvent(paramMotionEvent);
    }
    switch (paramMotionEvent.getAction())
    {
    }
    for (;;)
    {
      return true;
      Log.d("LrcView", "down,mLastMotionY:" + this.mLastMotionY);
      this.mLastMotionY = paramMotionEvent.getY();
      this.mIsFirstMove = true;
      invalidate();
      continue;
      if (paramMotionEvent.getPointerCount() == 2)
      {
        Log.d("LrcView", "two move");
        doScale(paramMotionEvent);
        return true;
      }
      Log.d("LrcView", "one move");
      if (this.mDisplayMode == 2) {
        return true;
      }
      doSeek(paramMotionEvent);
      continue;
      if (this.mDisplayMode == 1)
      {
        seekLrc(this.mHignlightRow);
        if (this.mLrcViewListener != null) {
          this.mLrcViewListener.onLrcSeeked(((LrcRow)this.mLrcRows.get(this.mHignlightRow)).time, null);
        }
      }
      this.mDisplayMode = 0;
      invalidate();
    }
  }
  
  public void seekLrc(int paramInt)
  {
    if ((this.mLrcRows == null) || (paramInt < 0) || (paramInt > this.mLrcRows.size())) {
      return;
    }
    ((LrcRow)this.mLrcRows.get(paramInt));
    this.mHignlightRow = paramInt;
    invalidate();
  }
  
  public void seekLrcToTime(long paramLong)
  {
    if ((this.mLrcRows == null) || (this.mLrcRows.size() == 0)) {}
    for (;;)
    {
      return;
      if (this.mDisplayMode == 0)
      {
        Log.d("LrcView", "seekLrcToTime:" + paramLong);
        for (int i = 0; i < this.mLrcRows.size(); i++)
        {
          LrcRow localLrcRow1 = (LrcRow)this.mLrcRows.get(i);
          if (i + 1 == this.mLrcRows.size()) {}
          for (LrcRow localLrcRow2 = null; ((paramLong >= localLrcRow1.time) && (localLrcRow2 != null) && (paramLong < localLrcRow2.time)) || ((paramLong > localLrcRow1.time) && (localLrcRow2 == null)); localLrcRow2 = (LrcRow)this.mLrcRows.get(i + 1))
          {
            seekLrc(i);
            return;
          }
        }
      }
    }
  }
  
  public void setListener(ILrcView.LrcViewListener paramLrcViewListener)
  {
    this.mLrcViewListener = paramLrcViewListener;
  }
  
  public void setLoadingTipText(String paramString)
  {
    this.mLoadingLrcTip = paramString;
  }
  
  public void setLrc(List<LrcRow> paramList)
  {
    this.mLrcRows = paramList;
    invalidate();
  }
}


/* Location:           C:\Users\sahil.jain\Desktop\dex2jar-0.0.9.15\dex2jar-0.0.9.15\Aarti_com.app.aarti_1.0_1-dex2jar.jar
 * Qualified Name:     com.app.aarti.lrc.LrcView
 * JD-Core Version:    0.7.0.1
 */