package it.vibin.ui.shuffle.google;

import it.vibin.ui.shuffle.R;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

public class QuickReturnFragment extends Fragment implements ObservableScrollView.Callbacks {
	private static final int		STATE_ONSCREEN	= 0;
	private static final int		STATE_OFFSCREEN	= 1;
	private static final int		STATE_RETURNING	= 2;

	private TextView				mQuickReturnView;
	private View					mPlaceholderView;
	private ObservableScrollView	mObservableScrollView;
	private int						mMinRawY		= 0;
	private int						mState			= STATE_ONSCREEN;
	private int						mQuickReturnHeight;
	private int						mCachedVerticalScrollRange;

	public QuickReturnFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_content, container, false);

		mObservableScrollView = (ObservableScrollView) rootView.findViewById(R.id.scroll_view);
		mObservableScrollView.setCallbacks(this);

		mQuickReturnView = (TextView) rootView.findViewById(R.id.sticky);
		mQuickReturnView.setText(R.string.quick_return_item);
		mPlaceholderView = rootView.findViewById(R.id.placeholder);

		mObservableScrollView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						onScrollChanged();
						mCachedVerticalScrollRange = mObservableScrollView.computeVerticalScrollRange();
						mQuickReturnHeight = mQuickReturnView.getHeight();
					}
				});

		return rootView;
	}

	@Override
	public void onScrollChanged() {
		int rawY = mPlaceholderView.getTop()
				- Math.min(mCachedVerticalScrollRange - mObservableScrollView.getHeight(),
						mObservableScrollView.getScrollY());
		int translationY = 0;

		switch (mState) {
		case STATE_OFFSCREEN:
			if (rawY <= mMinRawY) {
				mMinRawY = rawY;
			}
			else {
				mState = STATE_RETURNING;
			}
			translationY = rawY;
			break;

		case STATE_ONSCREEN:
			if (rawY < -mQuickReturnHeight) {
				mState = STATE_OFFSCREEN;
				mMinRawY = rawY;
			}
			translationY = rawY;
			break;

		case STATE_RETURNING:
			translationY = (rawY - mMinRawY) - mQuickReturnHeight;
			if (translationY > 0) {
				translationY = 0;
				mMinRawY = rawY - mQuickReturnHeight;
			}

			if (rawY > 0) {
				mState = STATE_ONSCREEN;
				translationY = rawY;
			}

			if (translationY < -mQuickReturnHeight) {
				mState = STATE_OFFSCREEN;
				mMinRawY = rawY;
			}
			break;
		}

		mQuickReturnView.setTranslationY(translationY);
	}
}
