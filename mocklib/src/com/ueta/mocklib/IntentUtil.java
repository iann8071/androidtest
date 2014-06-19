package com.ueta.mocklib;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class IntentUtil {

	public static final String INTERNAL_RESULT_FILE_NAME = "internal_result";
	public static final String EXTERNAL_RESULT_FILE_NAME = "external_result";
	public static final String FLAG_FILE_NAME = "intent_set";
	public static final String PARENT_ACTIVITY_FIELD_NAME = "mParent";
	public static final String RESULT_DATA_FIELD_NAME = "mResultData";
	public static final String RESULT_CODE_FIELD_NAME = "mResultCode";
	public static final String FILE_DIR = "/sdcard/SC_TEST_CONTENT";
	public static final String MOCK_PACKAGE_NAME = "com.ueta.mock";
	public static final String MOCK_ACTIVITY_NAME = "com.ueta.mock.MockActivity";
	public static final String MOCKLIB_LOG_TAG = "com.ueta.mocklib";
	public static final int RESULT_NONE = -1000;

	public static void setMockParent(Activity child) {
		if (!isIntentSet()) {
			setParent(child, new MyMockParent());
		} else {
			setParent(child, new MyMockParent2(getParent(child)));
		}
	}

	public static Activity getParent(Activity child) {
		Field parent;
		try {
			parent = Activity.class
					.getDeclaredField(PARENT_ACTIVITY_FIELD_NAME);
			parent.setAccessible(true);
			return (Activity) parent.get(child);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void setParent(Activity child, Activity parent) {
		Field parentField;
		try {
			parentField = Activity.class
					.getDeclaredField(PARENT_ACTIVITY_FIELD_NAME);
			parentField.setAccessible(true);
			parentField.set(child, parent);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int getRequestCode(boolean internal) {
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

	public static int getResultCode() {
		File file = new File(FILE_DIR);
		FileInputStream in;
		try {
			in = new FileInputStream(new File(file, EXTERNAL_RESULT_FILE_NAME));
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			byte[] buffer = new byte[4];
			in.read(buffer);
			buffer = new byte[4];
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
			log(intent);
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

	public static Intent getMockIntent() {
		Intent mockIntent = new Intent();
		mockIntent.setAction(Intent.ACTION_MAIN);
		mockIntent.setClassName(MOCK_PACKAGE_NAME, MOCK_ACTIVITY_NAME);
		return mockIntent;
	}

	public static void saveActivityResult(Activity activity) {
		try {
			Field f = Activity.class.getDeclaredField(RESULT_CODE_FIELD_NAME);
			f.setAccessible(true);
			int resultCode = (Integer) f.get(activity);
			f = Activity.class.getDeclaredField("mResultData");
			f.setAccessible(true);
			Intent data = (Intent) f.get(activity);
			int requestCode = getRequestCode(true);
			saveIntent(requestCode, resultCode, data, false);
			createFlag();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createFlag() {
		File file = new File(FILE_DIR, FLAG_FILE_NAME);
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public static void deleteFlag() {
		File file = new File(FILE_DIR, FLAG_FILE_NAME);
		if (file.exists())
			file.delete();
	}

	public static void saveIntent(int requestCode, int resultCode, Intent data,
			boolean internal) {
		log(requestCode);
		log(resultCode);
		log(data);
		try {
			FileOutputStream oos;
			File file = new File(FILE_DIR);
			if (internal) {
				oos = new FileOutputStream(new File(file,
						INTERNAL_RESULT_FILE_NAME));
			} else {
				oos = new FileOutputStream(new File(file,
						EXTERNAL_RESULT_FILE_NAME));
			}
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

	private static boolean isIntentSet() {
		File file = new File(FILE_DIR, FLAG_FILE_NAME);
		return file.exists();
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

	public static void logInt(int code) {
		Log.d(MOCKLIB_LOG_TAG, Integer.valueOf(code).toString());
		return;
	}

	public static void log(Object o) {
		if (o == null) {
			Log.d(MOCKLIB_LOG_TAG, "data is null");
			return;
		}

		for (Field field : o.getClass().getDeclaredFields()) {
			if (Modifier.isPublic(field.getModifiers()))
				continue;

			field.setAccessible(true);
			try {
				Log.d(MOCKLIB_LOG_TAG, field.getName() + " = " + field.get(o)
						+ "\n");
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
