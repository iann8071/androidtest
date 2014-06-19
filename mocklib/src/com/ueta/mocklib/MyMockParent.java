package com.ueta.mocklib;

import android.app.Activity;
import android.content.Intent;

public class MyMockParent extends Activity {

	@Override
	public void startActivityFromChild(Activity child, Intent intent,
			int requestCode) {
		IntentUtil
				.saveIntent(requestCode, IntentUtil.RESULT_NONE, intent, true);
	}
}
