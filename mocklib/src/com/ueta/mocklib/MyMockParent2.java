package com.ueta.mocklib;

import android.app.Activity;
import android.content.Intent;

public class MyMockParent2 extends Activity {

	Activity realParent = null;

	public MyMockParent2(Activity realParent) {
		this.realParent = realParent;
	}

	@Override
	public void startActivityFromChild(Activity child, Intent intent,
			int requestCode) {
		IntentUtil.deleteFlag();
		IntentUtil.setParent(child, realParent);
		child.startActivityForResult(IntentUtil.getMockIntent(), 0);
	}
}
