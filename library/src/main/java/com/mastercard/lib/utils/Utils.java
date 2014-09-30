package com.mastercard.lib.utils;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.os.Environment;
import android.util.Log;

/**
 * This is a utility class
 * 
 * @author sonal.agarwal
 * 
 */
public class Utils {

	/**
	 * This method converts char to String
	 * 
	 * @param arr
	 *            char array
	 * @return converted String from char array
	 */
	public static String someWhatReadable(char[] arr) {
		String str = "";
		for (char c : arr) {
			str += ((int) c) + " ";
		}
		return str;
	}

	/**
	 * This is a Logger method based on loglevel provided
	 * 
	 * @param tag
	 *            log tag
	 * @param txt
	 *            logging message
	 * @param logLevel
	 *            log level either info,debug or error
	 */
	public static void writeToAppLog(String tag, String txt, int logLevel) {
		switch (logLevel) {
		case Macro.INFO_LOG_LEVEL:
			Log.i(tag, txt);
			break;
		case Macro.DEBUG_LOG_LEVEL:
			Log.d(tag, txt);
			break;
		case Macro.ERROR_LOG_LEVEL:
			Log.e(tag, txt);
			break;

		}

	}
	/**
	 * This method reads text from file
	 * @param filename
	 * @return the text from file
	 * @throws Exception
	 */
	public static String getStringFromFile (String filename) throws Exception {

		//Find the directory for the SD Card using the API
		File sdcard = Environment.getExternalStorageDirectory();

		//Get the text file
		File file = new File(sdcard,filename);
		if (!file.exists()) {
			return null;
		}else {
			FileInputStream fin = new FileInputStream(file);
			String ret = convertStreamToString(fin);
			//Make sure you close all streams.
			fin.close();        
			return ret;
		}
	}

	/**
	 * This method converts input stream to string
	 * @param is
	 * @return the converted string
	 */
	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return sb.toString();
	}
}
