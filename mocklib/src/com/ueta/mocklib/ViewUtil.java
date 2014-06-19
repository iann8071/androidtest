package com.ueta.mocklib;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Point;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class ViewUtil {
	private static void searchView(View v, ArrayList<Integer> ns) {
		try {
			ViewGroup parent = (ViewGroup) v;
			for (int i = 0; i < parent.getChildCount(); i++) {
				ArrayList<Integer> ns1 = new ArrayList<Integer>();
				ns1.addAll(ns);
				ns1.add(i);
				searchView(parent.getChildAt(i), ns1);
			}
		} catch (ClassCastException e) {
			Log.d("com.ueta.test", v.getClass().toString());
			for (int i = 0; i < ns.size(); i++) {
				Log.d("com.ueta.test", Integer.valueOf(ns.get(i)).toString());
			}
		}
	}

	private static View returnView(ViewGroup v, ArrayList<Integer> ns) {
		ViewGroup parent = v;
		for (int i = 0; i < ns.size(); i++) {
			View child = parent.getChildAt(ns.get(i));
			try {
				parent = (ViewGroup) child;
			} catch (ClassCastException e) {
				Log.d("com.ueta.tests", "child view:" + parent.getId());
				return child;
			}
		}

		Log.d("com.ueta.tests",
				"parent view:" + parent.getClass() + parent.getId());
		return parent;
	}

	public static void touchViews(Activity a, ArrayList<Integer> ns) {
		Window w = a.getWindow();
		final ViewGroup v = (ViewGroup) w.getDecorView();
		final Point p = getAbsoluteCoordinates(v, ns);
		a.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				MotionEvent e = MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis() + 100,
						MotionEvent.ACTION_DOWN, p.x, p.y, 0);
				v.dispatchTouchEvent(e);
				e = MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis() + 100,
						MotionEvent.ACTION_UP, p.x, p.y, 0);
				v.dispatchTouchEvent(e);
			}
		});
	}

	public static Point getAbsoluteCoordinates(ViewGroup v,
			ArrayList<Integer> ns) {
		Point point = new Point();
		point.x = 0;
		point.y = 0;
		ViewGroup parent = v;
		for (int i = 0; i < ns.size(); i++) {
			View child = parent.getChildAt(ns.get(i));
			point.x += child.getLeft();
			point.y += child.getTop();
			try {
				parent = (ViewGroup) child;
			} catch (ClassCastException e) {

			}
		}

		return point;
	}
}
