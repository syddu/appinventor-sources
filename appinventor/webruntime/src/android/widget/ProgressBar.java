// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2023-2025 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package android.widget;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.BlendModeColorFilter;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.R;

import com.google.gwt.dom.client.Document;
import com.google.appinventor.components.runtime.Component;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;

public class ProgressBar extends View {

  private final DivElement mFill;
  private final boolean mIsHorizontal;

  private int mMin = 0;
  private int mMax = 100;
  private int mProgress = 0;
  private boolean mIndeterminate = true;
  private int mProgressColor = Component.COLOR_BLUE;
  private int mIndeterminateColor = Component.COLOR_BLUE;
  private ProgressDrawable mProgressDrawable;
  private ProgressDrawable mIndeterminateDrawable;
  private static final String ANIM_NAME = "indeterminateAnimation";
  private static final String CIRCULAR_ANIM_NAME = "indeterminateCircularAnimation";
  private static final String PROGRESS_TRANSITION = "width 200ms linear";
  private static boolean sAnimInjected = false;
  private static void injectAnimCss() {
    if (sAnimInjected) return;
    String css =
        "@keyframes " + ANIM_NAME + "{"
      + "0%{transform:translateX(0) scaleX(0);}"
      + "40%{transform:translateX(0) scaleX(0.4);}"
      + "100%{transform:translateX(100%) scaleX(0.5);}"
      + "}"
      + "@-webkit-keyframes " + ANIM_NAME + "{"
      + "0%{-webkit-transform:translateX(0) scaleX(0);}"
      + "40%{-webkit-transform:translateX(0) scaleX(0.4);}"
      + "100%{-webkit-transform:translateX(100%) scaleX(0.5);}"
      + "}"
      + "@keyframes " + CIRCULAR_ANIM_NAME + "{"
      + "0%{transform:rotate(0deg);}"
      + "100%{transform:rotate(360deg);}"
      + "}"
      + "@-webkit-keyframes " + CIRCULAR_ANIM_NAME + "{"
      + "0%{-webkit-transform:rotate(0deg);}"
      + "100%{-webkit-transform:rotate(360deg);}"
      + "}";

    Element styleEl = Document.get().createElement("style");
    styleEl.setAttribute("type", "text/css");
    styleEl.appendChild(Document.get().createTextNode(css));
    Element head = Document.get().getElementsByTagName("head").getItem(0);
    (head != null ? head : Document.get().getBody()).appendChild(styleEl);
    sAnimInjected = true;
  }
  public ProgressBar(Context context) {
    this(context, null);
  }

  public ProgressBar(Context context, AttributeSet attrs) {
    this(context, attrs, R.attr.progressBarStyle);
  }

  public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
    this(DOM.createDiv(), defStyleAttr);
  }

  public ProgressBar(Element element, int defStyleAttr) {
    super(element);
    mIsHorizontal = (defStyleAttr == R.attr.progressBarStyleHorizontal);

    injectAnimCss();
    mLayoutParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
    mFill = Document.get().createDivElement();
    getElement().appendChild(mFill);

    if (mIsHorizontal) {
      initHorizontalStyles();
    } else {
      initCircularStyles();
    }


    
    mProgressDrawable = new ProgressDrawable();
    mIndeterminateDrawable = new ProgressDrawable();
    setIndeterminate(true);
    refreshProgress();
  }

  private void initHorizontalStyles() {
    getElement().getStyle().setDisplay(Style.Display.BLOCK);
    getElement().getStyle().setWidth(100, Style.Unit.PCT);
    getElement().getStyle().setProperty("minHeight", "4px");
    getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
    mFill.getStyle().setDisplay(Style.Display.BLOCK);
    mFill.getStyle().setHeight(100, Style.Unit.PCT);
    getElement().getStyle().setBackgroundColor("rgba(0,0,0,.12)");
  }

  private void initCircularStyles(){
    getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
    getElement().getStyle().setWidth(24, Style.Unit.PX);
    getElement().getStyle().setHeight(24, Style.Unit.PX);
    getElement().getStyle().setProperty("boxSizing", "border-box");
    getElement().getStyle().setBackgroundColor("transparent");

    mFill.getStyle().setDisplay(Style.Display.BLOCK);
    mFill.getStyle().setWidth(100, Style.Unit.PCT);
    mFill.getStyle().setHeight(100, Style.Unit.PCT);
    mFill.getStyle().setProperty("boxSizing", "border-box");
    mFill.getStyle().setProperty("borderRadius", "50%");
    mFill.getStyle().setProperty("borderStyle", "solid");
    mFill.getStyle().setProperty("borderWidth", "3px");
    mFill.getStyle().setProperty("borderColor", "rgba(0,0,0,.12)");
    mFill.getStyle().setProperty("borderTopColor", "rgba(0,0,0,.54)");
  }

  class ProgressDrawable extends Drawable {
      public void setColorFilter(ColorFilter colorFilter) {
        if (colorFilter instanceof BlendModeColorFilter) {
          setColorFilter(((BlendModeColorFilter) colorFilter).getColor(), PorterDuff.Mode.SRC_IN);
        }
      }
      private int color;
      public void setColorFilter(int color, PorterDuff.Mode mode) {
        this.color = color;
        updateColor(mIndeterminate ? mIndeterminateDrawable.color : mProgressDrawable.color);
      }
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
      refreshProgress(animate);
    }
  }

  public final void incrementProgressBy(int diff) {
    setProgress(mProgress + diff, true);
  }

  public boolean isIndeterminate() { return mIndeterminate; }

  public void setIndeterminate(boolean indeterminate) {
    mIndeterminate = indeterminate;
    updateColor(mIndeterminate ? mIndeterminateDrawable.color : mProgressDrawable.color);
    if (mIndeterminate) {
      if (mIsHorizontal) {
        mFill.getStyle().setProperty("transform-origin", "0% 50%");
        mFill.getStyle().setProperty("-webkit-transform-origin", "0% 50%");
        mFill.getStyle().setProperty("animation", ANIM_NAME + " 1s infinite linear");
        mFill.getStyle().setProperty("-webkit-animation", ANIM_NAME + " 1s infinite linear");
      } else {
        mFill.getStyle().setProperty("transform-origin", "50% 50%");
        mFill.getStyle().setProperty("-webkit-transform-origin", "50% 50%");
        mFill.getStyle().setProperty("animation", CIRCULAR_ANIM_NAME + " 1s infinite linear");
        mFill.getStyle().setProperty("-webkit-animation", CIRCULAR_ANIM_NAME + " 1s infinite linear");
      }
    } else {
      mFill.getStyle().clearProperty("animation");
      mFill.getStyle().clearProperty("-webkit-animation");
      mFill.getStyle().clearProperty("transform-origin");
      mFill.getStyle().clearProperty("-webkit-transform-origin");
      mFill.getStyle().clearProperty("transform");
    }
    refreshProgress();
  }

  public Drawable getProgressDrawable() {
    return mProgressDrawable;
  }

  public Drawable getIndeterminateDrawable() {
    return mIndeterminateDrawable;
  }
  private void animateProgress(double ratio, boolean animate) {
    if (!mIsHorizontal) {
      return;
    }
    if (animate) {
      mFill.getStyle().setProperty("transition", PROGRESS_TRANSITION);
      mFill.getStyle().setProperty("-webkit-transition", PROGRESS_TRANSITION);
    } else {
      mFill.getStyle().clearProperty("transition");
      mFill.getStyle().clearProperty("-webkit-transition");
    }
    mFill.getStyle().setWidth(ratio * 100.0, Style.Unit.PCT);
  }
  private void refreshProgress() {
    refreshProgress(false);
  }

  private void refreshProgress(boolean animate) {
    if (!mIsHorizontal) {
      return;
    }
    if (mIndeterminate) {
      mFill.getStyle().clearProperty("transition");
      mFill.getStyle().clearProperty("-webkit-transition");
      mFill.getStyle().setWidth(100, Style.Unit.PCT);
      return;
    }
    double denom = Math.max(1.0, (double)(mMax - mMin));
    double ratio = ((double)mProgress - (double)mMin) / denom;
    if (ratio < 0) ratio = 0;
    if (ratio > 1) ratio = 1;
    animateProgress(ratio, animate);
  }
  private void updateColor(int color){
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        String rgba = "rgba(" + r + "," + g + "," + b + "," + alpha + ")";
        if (mIsHorizontal){
          mFill.getStyle().setBackgroundColor(rgba);
        } else {
          mFill.getStyle().setProperty("borderTopColor", rgba);
        }
  }
}