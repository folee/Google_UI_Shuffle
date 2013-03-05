package it.vibin.ui.shuffle;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class VerticalShuffleScrollView extends ScrollView implements ScrollHandlerInterface {
	private final String			TAG					= VerticalShuffleScrollView.class.getSimpleName();

	private ScrollerHandler			mScrollerHandler;
	private ScrollDetectorListener	listener;
	private LinearLayout			childLayout;

	private final int				SCROLL_STATE_CHECK	= 0;

	private int						currentScrollY		= 0;
	private int						footHeight;
	// Sliding distance and coordinate
	private float					xDistance, yDistance, xLast, yLast;
	private int						scrollYLast;
	private boolean					flag				= false;

	public VerticalShuffleScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mScrollerHandler = new ScrollerHandler(this, true);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		checkScrollView();
	}
	
	private void checkScrollView(){
		if (!LinearLayout.class.isInstance(getChildAt(0))) {
			throw new IllegalArgumentException(
					"This ScrollView must contains a LinearLayout!");
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		if (mScrollerHandler != null) {
			mScrollerHandler.onScrollChange();
		}
	}

	@Override
	public void saveState(Bundle args) {
		// delegate to scrollHandler object
		if (mScrollerHandler != null) {
			mScrollerHandler.saveState(args);
		}

	}

	@Override
	public void restoreState(Bundle args) {
		// delegate to scrollHandler object
		if (mScrollerHandler != null) {
			mScrollerHandler.restoreState(args);
		}

	}

	@Override
	public boolean didAniamtionPlayedOnChild(int position) {
		// delegate to scrollHandler object
		if (mScrollerHandler != null) {
			return mScrollerHandler.didAniamtionPlayedOnChild(position);
		}
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();

			scrollYLast = this.getScrollY();
			flag = true;
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();

			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;

			if (xDistance > yDistance) {
				return false;
			}
			break;
		}

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		super.computeScroll();

		if (flag) {
			int currentY = this.getScrollY();
			if (currentY > scrollYLast) {
				flag = false;
				listener.onHideGuideBar();
			}
			else if (currentY < scrollYLast) {
				flag = false;
				listener.onShowGuideBar();
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			currentScrollY = getScrollY();
			Log.i(TAG, "current y: " + currentScrollY);
			handler.sendEmptyMessageDelayed(SCROLL_STATE_CHECK, 200);
			break;
		default:
		}
		return super.onTouchEvent(ev);
	}

	public void initData(int footHeight, ScrollDetectorListener listener) {
		this.footHeight = footHeight;
		this.listener = listener;

		childLayout = (LinearLayout) getChildAt(0);
	}

	Handler	handler	= new Handler() {
						@Override
						public void handleMessage(Message msg) {
							super.handleMessage(msg);

							switch (msg.what) {
							case SCROLL_STATE_CHECK:
								if (currentScrollY != getScrollY()) {
									currentScrollY = getScrollY();
									// Log.i(TAG, "current y: " +
									// currentScrollY);
									handler.sendEmptyMessageDelayed(SCROLL_STATE_CHECK, 200);
								}
								else {
									int h = childLayout.getHeight() - (currentScrollY + getHeight());

									if (h < footHeight) {
										Log.e(TAG, "Already to the bottom, h=" + h);
										listener.onMoveToBottom();
										if (h < 0) {
											VerticalShuffleScrollView.this.scrollTo(0, childLayout.getHeight()
													- getHeight());
										}
									}
									else {
										Log.e(TAG, "Sliding stop");
										if (currentScrollY < 0) {
											VerticalShuffleScrollView.this.scrollTo(0, 0);
										}
									}
								}
								break;
							}
						}
					};

}