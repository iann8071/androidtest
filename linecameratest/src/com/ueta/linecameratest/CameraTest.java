package com.ueta.linecameratest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.ueta.mocklib.IntentUtil;
import com.ueta.mocklib.ViewUtil;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Parcel;
import android.test.InstrumentationTestCase;
import android.util.Log;

public class CameraTest extends InstrumentationTestCase {

	Instrumentation inst;
	public static final String cameraPackage = "jp.naver.linecamera.android";
	public static final String cameraActivity1 = "jp.naver.pick.android.camera.activity.CameraActivity";
	public static final String cameraActivity2 = "jp.naver.pick.android.camera.activity.proc.ImageDecoActivityProc";
	public static final String INTERNAL_RESULT_FILE_NAME = "internal_result";
	public static final String EXTERNAL_RESULT_FILE_NAME = "external_result";
	public static final String RESULT_DATA_FIELD_NAME = "mResultData";
	public static final String RESULT_CODE_FIELD_NAME = "mResultCode";
	public static final String FILE_DIR = "/sdcard/SC_TEST_CONTENT";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		inst = getInstrumentation();
	}

	public void test() throws InterruptedException {
		Log.d("com.ueta.test", "");
		Instrumentation.ActivityMonitor monitor = inst.addMonitor(
				cameraActivity1, null, false);
		start();
		Activity currentActivity = inst
				.waitForMonitorWithTimeout(monitor, 5000);

		monitor = inst.addMonitor(cameraActivity2, null, false);
		clickCapturePhoto(currentActivity);
		currentActivity = monitor.waitForActivityWithTimeout(5000);
		inst.removeMonitor(monitor);

		monitor = inst.addMonitor(cameraActivity1, null, false);
		clickSaveButton(currentActivity);
		currentActivity = monitor.waitForActivityWithTimeout(5000);
		saveActivityResult(currentActivity);
		Log.d("com.ueta.test", "finish");
	}

	public static int getRequestCode(boolean internal) {
		File file = new File(FILE_DIR);
		FileInputStream in;
		try {
			in = new FileInputStream(new File(file, INTERNAL_RESULT_FILE_NAME));
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			byte[] buffer = new byte[4];
			int len = in.read(buffer);
			b.write(buffer, 0, len);
			b.close();
			return toInt(buffer);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return -1;
	}
	
	public static Intent getData(boolean internal) {
		File file = new File(FILE_DIR);
		FileInputStream in;
		try {
			if (internal)
				in = new FileInputStream(new File(file,
						INTERNAL_RESULT_FILE_NAME));
			else
				in = new FileInputStream(new File(file,
						EXTERNAL_RESULT_FILE_NAME));
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			byte[] buffer = new byte[4];
			in.read(buffer);
			buffer = new byte[4];
			in.read(buffer);

			buffer = new byte[1024 * 1024];
			while (true) {
				int len = in.read(buffer);
				if (len < 0) {
					break;
				}
				b.write(buffer, 0, len);
			}
			b.close();
			Parcel parcel = Parcel.obtain();
			parcel.unmarshall(buffer, 0, buffer.length);
			parcel.setDataPosition(0);
			Intent intent = new Intent();
			intent.readFromParcel(parcel);
			return intent;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void saveActivityResult(Activity activity) {
		try {
			Field f = Activity.class.getDeclaredField(RESULT_CODE_FIELD_NAME);
			f.setAccessible(true);
			int resultCode = (Integer) f.get(activity);
			f = Activity.class.getDeclaredField(RESULT_DATA_FIELD_NAME);
			f.setAccessible(true);
			Intent data = (Intent) f.get(activity);
			int requestCode = getRequestCode(true);
			saveIntent(requestCode, resultCode, data, false);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveIntent(int requestCode, int resultCode, Intent data,
			boolean internal) {
		try {
			FileOutputStream oos;
			File file = new File(FILE_DIR);
			oos = new FileOutputStream(
					new File(file, EXTERNAL_RESULT_FILE_NAME));
			Parcel out = Parcel.obtain();
			data.writeToParcel(out, 0);

			oos.write(toBytes(requestCode));
			oos.write(toBytes(resultCode));
			oos.write(out.marshall());
			oos.close();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static byte[] toBytes(int a) {
		byte[] bs = new byte[4];
		bs[3] = (byte) (0x000000ff & (a));
		bs[2] = (byte) (0x000000ff & (a >>> 8));
		bs[1] = (byte) (0x000000ff & (a >>> 16));
		bs[0] = (byte) (0x000000ff & (a >>> 24));
		return bs;
	}
	
	private static int toInt(byte[] bs) {
		return ByteBuffer.wrap(bs).asIntBuffer().get();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	private void start() throws InterruptedException {
		Intent intent = getData(true);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClassName(getInstrumentation().getTargetContext(),
				cameraActivity1);
		getInstrumentation().startActivitySync(intent);
		Thread.sleep(5000);
	}

	private void clickCapturePhoto(Activity a) throws InterruptedException {
		ArrayList<Integer> ns = new ArrayList<Integer>();
		ns.add(0);
		ns.add(1);
		ns.add(0);
		ns.add(0);
		ns.add(0);
		ns.add(0);
		ns.add(1);
		ns.add(1);
		ns.add(0);
		ns.add(0);
		ns.add(4);
		ns.add(0);
		ns.add(1);
		ns.add(0);
		ns.add(0);
		ns.add(1);
		ViewUtil.touchViews(a, ns);
		Thread.sleep(10000);
	}

	private void clickSaveButton(Activity a) throws InterruptedException {
		ArrayList<Integer> ns = new ArrayList<Integer>();
		ns.add(0);
		ns.add(1);
		ns.add(0);
		ns.add(5);
		ns.add(0);
		ns.add(1);
		ViewUtil.touchViews(a, ns);
		Thread.sleep(10000);
	}
}
