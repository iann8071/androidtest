package com.ueta.potorotest;

import java.util.ArrayList;

import com.ueta.mocklib.IntentUtil;
import com.ueta.mocklib.ViewUtil;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.InstrumentationTestCase;

public class PotoroTest extends InstrumentationTestCase {

	Instrumentation inst;
	public static final String potoroPackage = "com.potoro.tisong";
	public static final String potoroActivity1 = "com.potoro.tisong.InitMain";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		inst = getInstrumentation();
	}

	public void test() throws InterruptedException {
		Instrumentation.ActivityMonitor monitor = inst.addMonitor(
				potoroActivity1, null, false);
		start();
		Activity currentActivity = inst
				.waitForMonitorWithTimeout(monitor, 5000);

		IntentUtil.setMockParent(currentActivity);
		clickPotoroCameraButton(currentActivity);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	private void start() throws InterruptedException {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClassName(potoroPackage, potoroActivity1);
		inst.startActivitySync(intent);
		Thread.sleep(5000);
	}

	private void clickPotoroCameraButton(Activity a)
			throws InterruptedException {
		ArrayList<Integer> ns = new ArrayList<Integer>();
		ns.add(0);
		ns.add(1);
		ns.add(0);
		ns.add(1);
		ns.add(1);
		ns.add(1);
		ns.add(3);
		ViewUtil.touchViews(a, ns);
		Thread.sleep(10000);
	}
}
