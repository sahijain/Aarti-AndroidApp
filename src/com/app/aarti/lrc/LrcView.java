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

public class LrcView extends View implements ILrcView {
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

	public LrcView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		this.mPaint.setTextSize(this.mLrcFontSize);
	}

	private void doScale(MotionEvent paramMotionEvent) {
		if (this.mDisplayMode == 1) {
			this.mDisplayMode = 2;
			Log.d("LrcView", "two move but teaking ...change mode");
			return;
		}
		if (this.mIsFirstMove) {
			this.mDisplayMode = 2;
			invalidate();
			this.mIsFirstMove = false;
			setTwoPointerLocation(paramMotionEvent);
		}
		int i = getScale(paramMotionEvent);
		Log.d("LrcView", "scaleSize:" + i);
		if (i != 0) {
			setNewFontSize(i);
			invalidate();
		}
		setTwoPointerLocation(paramMotionEvent);
	}

	private void doSeek(MotionEvent event) {
		float y = event.getY();
		float offsetY = y - mLastMotionY; // touch offset.
		if (Math.abs(offsetY) < mMinSeekFiredOffset) {
			// move to short ,do not fire seek action
			return;
		}
		mDisplayMode = DISPLAY_MODE_SEEK;
		int rowOffset = Math.abs((int) offsetY / mLrcFontSize); // highlight row
																// offset.
		Log.d(TAG, "move new hightlightrow : " + mHignlightRow + " offsetY: "
				+ offsetY + " rowOffset:" + rowOffset);
		if (offsetY < 0) {
			// finger move up
			mHignlightRow += rowOffset;
		} else if (offsetY > 0) {
			// finger move down
			mHignlightRow -= rowOffset;
		}
		mHignlightRow = Math.max(0, mHignlightRow);
		mHignlightRow = Math.min(mHignlightRow, mLrcRows.size() - 1);

		if (rowOffset > 0) {
			mLastMotionY = y;
			invalidate();
		}
	}

	private int getScale(MotionEvent event) {
		Log.d(TAG, "scaleSize getScale");
		float x0 = event.getX(0);
		float y0 = event.getY(0);
		float x1 = event.getX(1);
		float y1 = event.getY(1);
		float maxOffset = 0; // max offset between x or y axis,used to decide
								// scale size

		boolean zoomin = false;

		float oldXOffset = Math.abs(mPointerOneLastMotion.x
				- mPointerTwoLastMotion.x);
		float newXoffset = Math.abs(x1 - x0);

		float oldYOffset = Math.abs(mPointerOneLastMotion.y
				- mPointerTwoLastMotion.y);
		float newYoffset = Math.abs(y1 - y0);

		maxOffset = Math.max(Math.abs(newXoffset - oldXOffset),
				Math.abs(newYoffset - oldYOffset));
		if (maxOffset == Math.abs(newXoffset - oldXOffset)) {
			zoomin = newXoffset > oldXOffset ? true : false;
		} else {
			zoomin = newYoffset > oldYOffset ? true : false;
		}

		Log.d(TAG, "scaleSize maxOffset:" + maxOffset);

		if (zoomin)
			return (int) (maxOffset / 10);
		else
			return -(int) (maxOffset / 10);
	}

	private void setNewFontSize(int paramInt) {
		this.mLrcFontSize = (paramInt + this.mLrcFontSize);
		this.mSeekLineTextSize = (paramInt + this.mSeekLineTextSize);
		this.mLrcFontSize = Math.max(this.mLrcFontSize, this.mMinLrcFontSize);
		this.mLrcFontSize = Math.min(this.mLrcFontSize, this.mMaxLrcFontSize);
		this.mSeekLineTextSize = Math.max(this.mSeekLineTextSize,
				this.mMinSeekLineTextSize);
		this.mSeekLineTextSize = Math.min(this.mSeekLineTextSize,
				this.mMaxSeekLineTextSize);
	}

	private void setTwoPointerLocation(MotionEvent paramMotionEvent) {
		this.mPointerOneLastMotion.x = paramMotionEvent.getX(0);
		this.mPointerOneLastMotion.y = paramMotionEvent.getY(0);
		this.mPointerTwoLastMotion.x = paramMotionEvent.getX(1);
		this.mPointerTwoLastMotion.y = paramMotionEvent.getY(1);
	}

	protected void onDraw(Canvas paramCanvas) {
		int i = getHeight();
		int j = getWidth();
		if ((this.mLrcRows == null) || (this.mLrcRows.size() == 0)) {
			if (this.mLoadingLrcTip != null) {
				this.mPaint.setColor(this.mHignlightRowColor);
				this.mPaint.setTextSize(this.mLrcFontSize);
				this.mPaint.setTextAlign(Paint.Align.CENTER);
				paramCanvas.drawText(this.mLoadingLrcTip, j / 2, i / 2
						- this.mLrcFontSize, this.mPaint);
			}
			return;
		}
		int k = j / 2;
		String str = ((LrcRow) this.mLrcRows.get(this.mHignlightRow)).content;
		int m = i / 2 - this.mLrcFontSize;
		this.mPaint.setColor(this.mHignlightRowColor);
		this.mPaint.setTextSize(this.mLrcFontSize);
		this.mPaint.setTextAlign(Paint.Align.CENTER);
		paramCanvas.drawText(str, k, m, this.mPaint);
		if (this.mDisplayMode == 1) {
			this.mPaint.setColor(this.mSeekLineColor);
			paramCanvas.drawLine(this.mSeekLinePaddingX, m, j
					- this.mSeekLinePaddingX, m, this.mPaint);
			this.mPaint.setColor(this.mSeekLineTextColor);
			this.mPaint.setTextSize(this.mSeekLineTextSize);
			this.mPaint.setTextAlign(Paint.Align.LEFT);
			paramCanvas.drawText(
					((LrcRow) this.mLrcRows.get(this.mHignlightRow)).strTime,
					0.0F, m, this.mPaint);
		}
		this.mPaint.setColor(this.mNormalRowColor);
		this.mPaint.setTextSize(this.mLrcFontSize);
		this.mPaint.setTextAlign(Paint.Align.CENTER);
		int n = -1 + this.mHignlightRow;
		int i1 = m - this.mPaddingY - this.mLrcFontSize;
		for (;;) {
			if ((i1 <= -this.mLrcFontSize) || (n < 0)) {
				int i2 = 1 + this.mHignlightRow;
				int i3 = m + this.mPaddingY + this.mLrcFontSize;
				while ((i3 < i) && (i2 < this.mLrcRows.size())) {
					paramCanvas.drawText(
							((LrcRow) this.mLrcRows.get(i2)).content, k, i3,
							this.mPaint);
					i3 += this.mPaddingY + this.mLrcFontSize;
					i2++;
				}
				break;
			}
			paramCanvas.drawText(((LrcRow) this.mLrcRows.get(n)).content, k,
					i1, this.mPaint);
			i1 -= this.mPaddingY + this.mLrcFontSize;
			n--;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (mLrcRows == null || mLrcRows.size() == 0) {
			return super.onTouchEvent(event);
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.d(TAG, "down,mLastMotionY:" + mLastMotionY);
			mLastMotionY = event.getY();
			mIsFirstMove = true;
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:

			if (event.getPointerCount() == 2) {
				Log.d(TAG, "two move");
				doScale(event);
				return true;
			}
			Log.d(TAG, "one move");
			// single pointer mode ,seek
			if (mDisplayMode == DISPLAY_MODE_SCALE) {
				// if scaling but pointer become not two ,do nothing.
				return true;
			}

			doSeek(event);
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (mDisplayMode == DISPLAY_MODE_SEEK) {
				seekLrc(mHignlightRow);
				if (this.mLrcViewListener != null) {
					this.mLrcViewListener
							.onLrcSeeked(((LrcRow) this.mLrcRows
									.get(this.mHignlightRow)).time, null);
				}
			}
			mDisplayMode = DISPLAY_MODE_NORMAL;
			invalidate();
			break;
		}
		return true;
	}

	public void seekLrc(int position) {
	    if(mLrcRows == null || position < 0 || position > mLrcRows.size()) {
	        return;
	    }
		LrcRow lrcRow = mLrcRows.get(position);
		mHignlightRow = position;
		invalidate();
		/*if(mLrcViewListener != null){
			mLrcViewListener.onLrcSeeked(position, lrcRow);
		}*/
	}

	public void seekLrcToTime(long time) {
        if(mLrcRows == null || mLrcRows.size() == 0) {
            return;
        }
        
        if(mDisplayMode != DISPLAY_MODE_NORMAL) {
            // touching
            return;
        }
        
        Log.d(TAG, "seekLrcToTime:" + time);
        // find row
        for(int i = 0; i < mLrcRows.size(); i++) {
            LrcRow current = mLrcRows.get(i);
            LrcRow next = i + 1 == mLrcRows.size() ? null : mLrcRows.get(i + 1);
            
            if((time >= current.time && next != null && time < next.time)
                    || (time > current.time && next == null)) {
                seekLrc(i);
                return;
            }
        }
    }

	public void setListener(ILrcView.LrcViewListener paramLrcViewListener) {
		this.mLrcViewListener = paramLrcViewListener;
	}

	public void setLoadingTipText(String paramString) {
		this.mLoadingLrcTip = paramString;
	}

	public void setLrc(List<LrcRow> paramList) {
		this.mLrcRows = paramList;
		invalidate();
	}
}

/*
 * Location:
 * C:\Users\sahil.jain\Desktop\dex2jar-0.0.9.15\dex2jar-0.0.9.15\Aarti_com
 * .app.aarti_1.0_1-dex2jar.jar
 * 
 * Qualified Name: com.app.aarti.lrc.LrcView
 * 
 * JD-Core Version: 0.7.0.1
 */