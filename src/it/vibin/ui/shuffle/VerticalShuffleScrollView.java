package it.vibin.ui.shuffle;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class VerticalShuffleScrollView extends ScrollView {
	private final String			TAG				= VerticalShuffleScrollView.class.getSimpleName();

	private int						currentScrollY	= 0;
	private int						footHeight;
	private OnScrollStopListener	listener;
	private LinearLayout			childLayout;

	private final static int		SCROLL_JUDGE	= 0;

	// Sliding distance and coordinate
	private float					xDistance, yDistance, xLast, yLast;
	private int						scrollYLast;

	private boolean					flag			= false;

	public VerticalShuffleScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
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
			handler.sendEmptyMessageDelayed(SCROLL_JUDGE, 200);
			break;
		default:
		}
		return super.onTouchEvent(ev);
	}

	public void initData(int footHeight, OnScrollStopListener listener) {
		this.footHeight = footHeight;
		this.listener = listener;

		childLayout = (LinearLayout) getChildAt(0);
	}

	Handler	handler	= new Handler() {
						@Override
						public void handleMessage(Message msg) {
							super.handleMessage(msg);

							switch (msg.what) {
							case SCROLL_JUDGE:
								if (currentScrollY != getScrollY()) {
									currentScrollY = getScrollY();
									// Log.i(TAG, "current y: " +
									// currentScrollY);
									handler.sendEmptyMessageDelayed(0, 200);
								}
								else {
									int h = childLayout.getHeight() - (currentScrollY + getHeight());

									if (h < footHeight) {
										Log.e(TAG, "Already to the bottom, h=" + h);
										listener.onMoveToBottom();
										if (h < 0) {
											VerticalShuffleScrollView.this.scrollTo(0, childLayout.getHeight() - getHeight());
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

	public interface OnScrollStopListener {
		public void onMoveToBottom();

		public void onHideGuideBar();

		public void onShowGuideBar();
	}
}