package it.vibin.ui.shuffle;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
class ScrollerHandler implements OnGlobalLayoutListener, ScrollHandlerInterface {

	private ViewGroup		rootView;
	/**
	 * this variable to keep tracking the animation on each child so we won't
	 * play it twice on the same child . Recall , the animation happened in a
	 * serial order and only once .
	 */
	private int[]			mutuableInteger		= new int[] { INVALID_CHILD_INDEX };

	// this is a helper class that guarantee that only one animation run at a
	// time on it queue
	private AnimationHelper	mAnimationHelper	= new AnimationHelper();

	// flag
	private final boolean	isVerticalScrollView;

	/**
	 * 
	 */
	public ScrollerHandler(View rootView, boolean isVerticalScrollView) {
		if (rootView == null) {
			throw new IllegalArgumentException("Root view can't be null!!");
		}
		if (!(rootView instanceof HorizontalScrollView) && !(rootView instanceof ScrollView)) {
			throw new IllegalArgumentException("This should be a ScollView!");
		}
		this.rootView = (ViewGroup) rootView;
		this.isVerticalScrollView = isVerticalScrollView;
		registerGlobalLayoutListenerOnRootView();
	}

	private void registerGlobalLayoutListenerOnRootView() {
		rootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onGlobalLayout() {
		//this to handle the case if the Child is loaded at runtime .
		if (rootView.getChildCount() == 0) {
			return;
		}
		ViewGroup directChild = (ViewGroup) rootView.getChildAt(0);
		final int childCount = ((ViewGroup) directChild).getChildCount();

		if (childCount == 0) {
			// we would like to keep listening ,perhaps the views will be added
			// at runtime of the child child
			return;
		}
		// now we don't want to keep listening because childs of child is
		// added now so we can start the initial animation .
		if (Util.isJellyBean()) {
			rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
		}
		else {
			rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		}

		// fake scroll
		// this to start the animation on the first visible item(s) in the
		// scroll view i.e will cause the onScrollChange to be invoked
		// (Initially needed)
		if (isVerticalScrollView) {
			rootView.scrollTo(0, 1);// scroll the view in the y-axis
		}
		else {
			// horizontal
			rootView.scrollTo(1, 0); // scroll in the x axis
		}
	}

	/*
	 * package visible only to prevent clients from calling it and bring bugs to
	 * their life
	 */
	void onScrollChange() {
		// this will loop all children
		// if the child top is less or equal the bottom of the this view (
		// scroll view) and didnt played the animation
		// play it
		if (mutuableInteger[0] < 0) {
			mutuableInteger[0] = 0;
		}
		View child;
		boolean flipAnimation = false;
		final int[] location = new int[2];
		final ViewGroup directChild = (ViewGroup) rootView.getChildAt(0);
		final int childCount = ((ViewGroup) directChild).getChildCount();
		for (int i = mutuableInteger[0]; i < childCount; ++i) {
			child = directChild.getChildAt(i);
			if (child != null) {
				child.getLocationOnScreen(location);
				if (animationNeedToBeRunOnChild(location)) {
					++mutuableInteger[0];
					// hide child and don't worry animation have fillAfter = true
					child.setVisibility(View.INVISIBLE);
					playAnimation(child, flipAnimation);

				}
				else {
					// no need to continue checking child
					break;
				}
			}
			flipAnimation = !flipAnimation;
		}

	}

	private boolean animationNeedToBeRunOnChild(int[] childLocationOnScreen) {
		boolean result = false;
		if (isVerticalScrollView) {
			if (childLocationOnScreen[1] <= rootView.getBottom()) {
				result = true;
			}
		}
		else {
			if (childLocationOnScreen[0] <= rootView.getRight()) {
				result = true;
			}
		}

		return result;

	}

	private void playAnimation(View child, boolean flipAnimation) {

		mAnimationHelper.playAnimation(child, R.anim.slide_up, R.animator.rotate_animation);

	}

	// use only when you want to save state for animation
	public void saveState(Bundle args) {
		if (args != null) {

			args.putInt("__mutable_integer__", mutuableInteger[0]);
		}
	}

	public void restoreState(Bundle args) {
		if (args != null) {
			mutuableInteger[0] = args.getInt(BUNDLE_KEY_MUTABLE_INTEGER, INVALID_CHILD_INDEX);
		}
	}

	private static final int	INVALID_CHILD_INDEX			= -1;
	private static final String	BUNDLE_KEY_MUTABLE_INTEGER	= "__mutable_integer__";

	public boolean didAniamtionPlayedOnChild(int position) {
		boolean result = false;

		if (mutuableInteger[0] >= position) {
			result = true;
		}

		return result;
	}

}
