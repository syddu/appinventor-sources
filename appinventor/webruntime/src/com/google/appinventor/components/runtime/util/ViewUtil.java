package com.google.appinventor.components.runtime.util;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.google.appinventor.components.runtime.Component;
import com.google.gwt.dom.client.Style;

public class ViewUtil {

  private static View childContainer;
  
  public static void setBackgroundImage(View view, Drawable drawable) {
    view.setBackgroundDrawable(drawable);
    view.requestLayout();
  }

  public static void setChildHeightForVerticalLayout(View child, int height) {
    // TODO(ewpatton): Real implementation
  }

  public static void setChildWidthForVerticalLayout(View child, int width) {
    // TODO(ewpatton): Real implementation
    if (width > 0) {
      child.getElement().getStyle().setWidth(width, Style.Unit.PX);
    } else if (width == Component.LENGTH_PREFERRED) {
      // TODO
    } else if (width == Component.LENGTH_FILL_PARENT) {
      child.getElement().getStyle().setProperty("width", "100%");
    }
  }

  /**
   * Sets the image for an ImageView.
   */
  public static void setImage(ImageView view, Drawable drawable) {
    view.setImageDrawable(drawable);
    if (drawable != null) {
      view.setAdjustViewBounds(true);
    }
    view.requestLayout();
  }

  public static void setBackgroundDrawable(View view, Drawable drawable) {
    view.setBackgroundDrawable(drawable);
    view.requestLayout();
  }

  public static void setChildContainer(View container) {
    childContainer = container;
  }

  public static View getChildContainer() {
    return childContainer;
  }

  public static void addChild(View child) {
    if (childContainer != null && child != null) {
      childContainer.getElement().appendChild(child.getElement());
    }
  }
}
