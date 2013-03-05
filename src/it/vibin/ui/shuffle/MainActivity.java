package it.vibin.ui.shuffle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	private VerticalShuffleScrollView	mScrollView;
	private LinearLayout				llMain;
	private LinearLayout				loading;

	private TextView					title;
	private TextView					bottom;
	private int							guideHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		llMain = (LinearLayout) findViewById(R.id.ll_main);
		mScrollView = (VerticalShuffleScrollView) findViewById(R.id.vssv);
		loading = (LinearLayout) findViewById(R.id.loading);
		loading.setVisibility(View.GONE);
		final ObjectAnimator bojAnim = ObjectAnimator.ofFloat(loading, View.ALPHA, 0);
		bojAnim.setDuration(4000);
		bojAnim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				loading.setVisibility(View.GONE);
			}
		});
		title = (TextView) findViewById(R.id.title);
		bottom = (TextView) findViewById(R.id.bottom);
		guideHeight = (int) (50 * Util.getWindowDensity(this));

		mScrollView.initData((int) (Util.getWindowDensity(this) * 50), new ScrollDetectorListener() {
			@Override
			public void onMoveToBottom() {
				loading.setVisibility(View.VISIBLE);
				if (loading.getVisibility() == View.VISIBLE) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							bojAnim.start();
						}
					}, 2000);
				}
			}

			@Override
			public void onHideGuideBar() {
				LayoutParams lp = (LayoutParams) title.getLayoutParams();
				if (lp.topMargin == 0) {
					startSlidingAnimation(guideHeight, 0);
				}
			}

			@Override
			public void onShowGuideBar() {
				LayoutParams lp = (LayoutParams) title.getLayoutParams();
				if (lp.topMargin == -guideHeight) {
					startSlidingAnimation(0, guideHeight);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void setBarHeight(int height) {
		int topMargin = height - guideHeight;
		LayoutParams lp = (LayoutParams) title.getLayoutParams();
		lp.topMargin = topMargin;
		title.setLayoutParams(lp);

		int bottomMargin = topMargin * 52 / 38;
		LayoutParams lp2 = (LayoutParams) bottom.getLayoutParams();
		lp2.bottomMargin = bottomMargin;
		bottom.setLayoutParams(lp2);

	}

	private void startSlidingAnimation(int headHeight, int limit) {
		new SlidingAnimationThread(headHeight, limit).start();
	}

	private class SlidingAnimationThread extends Thread {
		private int		headHeight;
		private int		limit		= 0;
		private boolean	statusShow	= true;

		public SlidingAnimationThread(int headHeight, int limit) {
			this.headHeight = headHeight;
			this.limit = limit;
			if (headHeight < limit) {
				statusShow = true;
			}
			else {
				statusShow = false;
			}
		}

		@Override
		public void run() {
			try {
				int mStep = 0;

				while (true) {
					mStep = Math.abs((int) ((headHeight - limit) / 5));
					mStep = mStep == 0 ? 1 : mStep;
					mStep = 1;
					if (statusShow) {
						headHeight += mStep;
						if (headHeight < limit) {
							handler.sendEmptyMessage(headHeight);
							Thread.sleep(5);
						}
						else {
							handler.sendEmptyMessage(limit);
							break;
						}
					}
					else {
						headHeight -= mStep;
						if (headHeight > limit) {
							handler.sendEmptyMessage(headHeight);
							Thread.sleep(5);
						}
						else {
							handler.sendEmptyMessage(limit);
							break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Handler	handler	= new Handler() {
							@Override
							public void handleMessage(Message msg) {
								super.handleMessage(msg);
								setBarHeight(msg.what);
							}
						};

	}
}
