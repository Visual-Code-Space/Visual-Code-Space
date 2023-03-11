package com.raredev.vcspace.util;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;

public class ViewUtils {
  public static void expand(final View v) {
    int matchParentMeasureSpec =
        View.MeasureSpec.makeMeasureSpec(
            ((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
    int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
    final int targetHeight = v.getMeasuredHeight();

    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    v.getLayoutParams().height = 1;
    v.setVisibility(View.VISIBLE);
    Animation a =
        new Animation() {
          @Override
          protected void applyTransformation(float interpolatedTime, Transformation t) {
            v.getLayoutParams().height =
                interpolatedTime == 1
                    ? ViewGroup.LayoutParams.WRAP_CONTENT
                    : (int) (targetHeight * interpolatedTime);
            v.requestLayout();
          }

          @Override
          public boolean willChangeBounds() {
            return true;
          }
        };

    // Expansion speed of 1dp/ms
    a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density) / 2);
    v.startAnimation(a);
  }

  public static void collapse(final View v) {
    final int initialHeight = v.getMeasuredHeight();

    Animation a =
        new Animation() {
          @Override
          protected void applyTransformation(float interpolatedTime, Transformation t) {
            if (interpolatedTime == 1) {
              v.setVisibility(View.GONE);
            } else {
              v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
              v.requestLayout();
            }
          }

          @Override
          public boolean willChangeBounds() {
            return true;
          }
        };

    // Collapse speed of 1dp/ms
    a.setDuration(
        (int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density) / 2);
    v.startAnimation(a);
  }

  public static boolean isExpanded(View v) {
    return v.getVisibility() == View.VISIBLE;
  }

  public static boolean isCollapsed(View v) {
    return v.getVisibility() == View.GONE;
  }

  public static void rotateChevron(boolean isOpen, ImageView chevronView) {
    float startRotation = isOpen ? -90f : 0f;
    float endRotation = isOpen ? 0f : -90f;

    RotateAnimation rotateAnimation =
        new RotateAnimation(
            startRotation,
            endRotation,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f);
    rotateAnimation.setDuration(200);
    rotateAnimation.setFillAfter(true);
    chevronView.startAnimation(rotateAnimation);
  }
}
