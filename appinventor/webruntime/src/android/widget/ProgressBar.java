package android.widget;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.drawable.Drawable;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;

public class ProgressBar extends View {

  private final DivElement mFill;

  private int mMin = 0;
  private int mMax = 100;
  private int mProgress = 0;

  private boolean mIndeterminate = false;

  private Drawable mProgressDrawable;

  public ProgressBar(Context context) { this(DOM.createDiv()); }
  public ProgressBar(Context context, AttributeSet attrs) { this(DOM.createDiv()); }
  public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr) { this(DOM.createDiv()); }

  public ProgressBar(Element element) {
    super(element);

    mLayoutParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

    mFill = Document.get().createDivElement();
    getElement().appendChild(mFill);

    getElement().getStyle().setDisplay(Style.Display.BLOCK);
    getElement().getStyle().setWidth(100, Style.Unit.PCT);
    getElement().getStyle().setProperty("minHeight", "8px");
    // getElement().getStyle().setProperty("minWidth", "64px");
    getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
    mFill.getStyle().setDisplay(Style.Display.BLOCK);
    mFill.getStyle().setHeight(100, Style.Unit.PCT);

    getElement().getStyle().setBackgroundColor("rgba(0,0,0,.12)");
    mFill.getStyle().setBackgroundColor("dodgerblue");

    // Drawable not functional yet
    mProgressDrawable = new Drawable() { };

    refreshProgress();
  }

  public int getMin() { return mMin; }

  public void setMin(int min) {
    if (min > mMax) min = mMax;
    if (min == mMin) return;
    mMin = min;
    if (mProgress < mMin) mProgress = mMin;
    requestLayout();
    refreshProgress();
  }

  public int getMax() { return mMax; }

  public void setMax(int max) {
    if (max < mMin) max = mMin;
    if (max == mMax) return;
    mMax = max;
    if (mProgress > mMax) mProgress = mMax;
    requestLayout();
    refreshProgress();
  }

  public int getProgress() {
    return mIndeterminate ? 0 : mProgress;
  }

  public void setProgress(int value) {
    setProgress(value, false);
  }

  public void setProgress(int value, boolean animate) {
    int v = value < mMin ? mMin : (value > mMax ? mMax : value);
    if (v != mProgress) {
      mProgress = v;
      refreshProgress();
    }
  }

  public final void incrementProgressBy(int diff) {
    setProgress(mProgress + diff, false);
  }

  public boolean isIndeterminate() { return mIndeterminate; }

  public void setIndeterminate(boolean indeterminate) {
    if (mIndeterminate == indeterminate) return;
    mIndeterminate = indeterminate;
    refreshProgress();
  }

  public Drawable getProgressDrawable() {
    return mProgressDrawable;
  }

  // Original function in Android source code is doRefreshProgress
  private void refreshProgress() {
    if (mIndeterminate) {
      mFill.getStyle().clearWidth();
      return;
    }
    double denom = Math.max(1.0, (double)(mMax - mMin));
    double ratio = ((double)mProgress - (double)mMin) / denom;
    if (ratio < 0) ratio = 0;
    if (ratio > 1) ratio = 1;
    mFill.getStyle().setWidth(ratio * 100.0, Style.Unit.PCT);
  }
}