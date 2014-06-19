package com.ueta.mock;

import com.ueta.mocklib.IntentUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

public class MockActivity extends Activity {
	
	public static final String cameraPackage = "jp.naver.linecamera.android";
	public static final String cameraActivity1 = "jp.naver.pick.android.camera.activity.ProxyActivity";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = IntentUtil.getData(true);
		//intent.setClassName(cameraPackage, cameraActivity1);
		startActivityForResult(intent, IntentUtil.getRequestCode(true));
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentUtil.saveIntent(requestCode, resultCode, intent, false);
		finish();
    }
}
