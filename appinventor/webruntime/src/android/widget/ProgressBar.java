package android.widget;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.BlendModeColorFilter;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.google.gwt.dom.client.Document;
import com.google.appinventor.components.runtime.Component;
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
  private int mProgressColor = Component.COLOR_BLUE;
  private int mIndeterminateColor = Component.COLOR_BLUE;
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
    getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
    mFill.getStyle().setDisplay(Style.Display.BLOCK);
    mFill.getStyle().setHeight(100, Style.Unit.PCT);
    getElement().getStyle().setBackgroundColor("rgba(0,0,0,.12)");

    mProgressDrawable = new Drawable() { 
      public void setColorFilter(ColorFilter colorFilter) {
        if (colorFilter instanceof BlendModeColorFilter) {
          setColorFilter(((BlendModeColorFilter) colorFilter).getColor(), PorterDuff.Mode.SRC_IN);
        }
      }
      public void setColorFilter(int color, PorterDuff.Mode mode) {
        if (mIndeterminate) {
          mIndeterminateColor = color;
        } else {
          mProgressColor = color;
        }
        updateColor(color);
      }
    };
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
    updateColor(mIndeterminate ? mIndeterminateColor : mProgressColor);
    refreshProgress();
  }

  public Drawable getProgressDrawable() {
    return mProgressDrawable;
  }

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
  private void updateColor(int color){
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        mFill.getStyle().setBackgroundColor("rgba(" + r + "," + g + "," + b + "," + alpha + ")");
  }
}
  