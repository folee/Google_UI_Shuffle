package it.vibin.ui.shuffle;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;

public class Util {

	public Util() {
	}

	public static boolean isJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static boolean isHoneyComb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}
	
	/**
	 * get screen density
	 * 
	 * @param context
	 * @return screen density
	 */
	public static float getWindowDensity(Context context) {
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		float density = metrics.density;
		return density;
	}
}
