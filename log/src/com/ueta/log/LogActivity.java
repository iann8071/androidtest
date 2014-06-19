package com.ueta.log;

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

import com.ueta.mocklib.IntentUtil;

import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.provider.MediaStore;
import android.util.Log;

public class LogActivity extends Activity {

	public static final String MOCKLIB_LOG_TAG = "com.ueta.log";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File files = new File("/sdcard/downloads/");

		for (File file : files.listFiles()) {
			Log.d(MOCKLIB_LOG_TAG, file.getName());
			getRequestCode("/sdcard/downloads/" + file.getName());
			getResultCode("/sdcard/downloads/" + file.getName());
			getData("/sdcard/downloads/" + file.getName());
		}

	}

	public static int getRequestCode(String fileName) {
		File file = new File(fileName);
		FileInputStream in;
		try {
			in = new FileInputStream(file);
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

	public static int getResultCode(String fileName) {
		File file = new File(fileName);
		FileInputStream in;
		try {
			in = new FileInputStream(file);
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

	public static Intent getData(String fileName) {
		File file = new File(fileName);
		FileInputStream in;
		try {
			in = new FileInputStream(file);
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			byte[] buffer = new byte[4];
			in.read(buffer);
			buffer = new byte[4];
			in.read(buffer);
			int len1 = 0;
			buffer = new byte[1024 * 1024];
			while (true) {
				int len = in.read(buffer);
				if (len < 0) {
					break;
				}
				len1 = len;
				b.write(buffer, 0, len);
			}
			b.close();
			Parcel parcel = Parcel.obtain();
			parcel.unmarshall(buffer, 0, len1);
			parcel.setDataPosition(0);
			Intent intent = new Intent();
			// intent.readFromParcel(parcel);
			log(parcel);
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static int toInt(byte[] bs) {
		return ByteBuffer.wrap(bs).asIntBuffer().get();
	}

	public static void logInt(int code) {
		Log.d(MOCKLIB_LOG_TAG, Integer.valueOf(code).toString());
		return;
	}

	public static void log(Parcel in) {
		if (in == null) {
			Log.d(MOCKLIB_LOG_TAG, "data is null");
			return;
		}

		String mAction = in.readString();
		Uri mData = Uri.CREATOR.createFromParcel(in);
		String mType = in.readString();
		int mFlags = in.readInt();
		String mPackage = in.readString();
		ComponentName mComponent = ComponentName.readFromParcel(in);

		if (in.readInt() != 0) {
			Rect mSourceBounds = Rect.CREATOR.createFromParcel(in);
		}

		int N = in.readInt();
		ArrayList<String> mCategories;
		if (N > 0) {
			mCategories = new ArrayList<String>();
			int i;
			for (i = 0; i < N; i++) {
				mCategories.add(in.readString().intern());
			}
		} else {
			mCategories = null;
		}

		if (in.readInt() != 0) {
			// mSelector = new Intent(in);
		}

		if (in.readInt() != 0) {
			// ClipData mClipData = new ClipData(in);
		}

		try {
			Bundle mExtras = in.readBundle();
			Log.d(MOCKLIB_LOG_TAG,
					"Extras = " + mExtras.toString() + "\n");
		} catch (Exception e) {

		}

		Log.d(MOCKLIB_LOG_TAG, "Action = " + mAction + "\n");

		Log.d(MOCKLIB_LOG_TAG, "Data = " + mData + "\n");

		Log.d(MOCKLIB_LOG_TAG, "Type = " + mType + "\n");

		Log.d(MOCKLIB_LOG_TAG, "Flag = " + mFlags + "\n");

		Log.d(MOCKLIB_LOG_TAG, "Package = " + mPackage + "\n");

		Log.d(MOCKLIB_LOG_TAG,
				"Component = " + mComponent + "\n");
	}
}
