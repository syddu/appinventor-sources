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
  private boolean mIndeterminate = true;
  private int mProgressColor = Component.COLOR_BLUE;
  private int mIndeterminateColor = Component.COLOR_BLUE;
  private ProgressDrawable mProgressDrawable;
  private ProgressDrawable mIndeterminateDrawable;
  private static final String ANIM_NAME = "indeterminateAnimation";
  private static boolean sAnimInjected = false;
  private static void injectAnimCss() {
    if (sAnimInjected) return;
    String css =
        "@keyframes " + ANIM_NAME + "{"
      + "0%{transform:translateX(0) scaleX(0);}"+
        "40%{transform:translateX(0) scaleX(0.4);}"+
        "100%{transform:translateX(100%) scaleX(0.5);}"+
        "}"
      + "@-webkit-keyframes " + ANIM_NAME + "{"
      + "0%{-webkit-transform:translateX(0) scaleX(0);}"+
        "40%{-webkit-transform:translateX(0) scaleX(0.4);}"+
        "100%{-webkit-transform:translateX(100%) scaleX(0.5);}"+
        "}";

    Element styleEl = Document.get().createElement("style");
    styleEl.setAttribute("type", "text/css");
    styleEl.appendChild(Document.get().createTextNode(css));
    Element head = Document.get().getElementsByTagName("head").getItem(0);
    (head != null ? head : Document.get().getBody()).appendChild(styleEl);
    sAnimInjected = true;
  }
  public ProgressBar(Context context) { this(DOM.createDiv()); }
  public ProgressBar(Context context, AttributeSet attrs) { this(DOM.createDiv()); }
  public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr) { this(DOM.createDiv()); }

  public ProgressBar(Element element) {
    super(element);
    injectAnimCss();
    mLayoutParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

    mFill = Document.get().createDivElement();
    getElement().appendChild(mFill);

    getElement().getStyle().setDisplay(Style.Display.BLOCK);
    getElement().getStyle().setWidth(100, Style.Unit.PCT);
    getElement().getStyle().setProperty("minHeight", "4px");
    getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
    mFill.getStyle().setDisplay(Style.Display.BLOCK);
    mFill.getStyle().setHeight(100, Style.Unit.PCT);
    getElement().getStyle().setBackgroundColor("rgba(0,0,0,.12)");
    
    mProgressDrawable = new ProgressDrawable();
    mIndeterminateDrawable = new ProgressDrawable();
    setIndeterminate(true);
    refreshProgress();
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
      refreshProgress();
    }
  }

  public final void incrementProgressBy(int diff) {
    setProgress(mProgress + diff, false);
  }

  public boolean isIndeterminate() { return mIndeterminate; }

  public void setIndeterminate(boolean indeterminate) {
    mIndeterminate = indeterminate;
    updateColor(mIndeterminate ? mIndeterminateDrawable.color : mProgressDrawable.color);
    if (mIndeterminate) {
      mFill.getStyle().setProperty("transform-origin", "0% 50%");
      mFill.getStyle().setProperty("-webkit-transform-origin", "0% 50%");
      mFill.getStyle().setProperty("animation", ANIM_NAME + " 1s infinite linear");
      mFill.getStyle().setProperty("-webkit-animation", ANIM_NAME + " 1s infinite linear");
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

  private void refreshProgress() {
    if (mIndeterminate) {
      mFill.getStyle().setWidth(100, Style.Unit.PCT);
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
  