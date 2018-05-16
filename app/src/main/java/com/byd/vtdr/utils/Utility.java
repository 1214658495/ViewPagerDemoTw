package com.byd.vtdr.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.byd.vtdr.BuildConfig;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
	public static final String INTENT_DATA = "intentdata";
	public static final String LOG_DEBUG = "Debug";
	public static final String EXTRA_TIME = "EXTRA_TIME";
	public static String PAGE_KEY = "com.sinde.OKkey";
	public static String REGISTER_ACTION = "REGISTER_ACTION";
	public static String LOGIN_ACTION = "LOGIN_ACTION";
	public static String CONTROL_ACTION = "CONTROL_ACTION";
	public static String CHANGE_PWD_ACTION = "CHANGE_PWD_ACTION";
	public static String REGISTER_LOGIN_ACTION = "REGISTER_LOGIN_ACTION";
	public static String EMPOWER_LOGIN_ACTION = "EMPOWER_LOGIN_ACTION";
	public static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	/**************************************************/
//	public static final String UPDATE_DIR = "http://i.byd.com.cn:8099/BYDi/";// 正式版地址i.byd.com.cn
	public static final String UPDATE_DIR = "http://121.15.172.125:8080/BYDi/";// 测试版地址
	// 下载服务器根目录http://i.byd.com.cn:8099/BYDi/
//	public static final String UPDATE_JSONVER = "bluetoothkey.json";
	public static final String UPDATE_JSONVER = "vtdr.json";
//	public static final String PACKAGE = "com.byd.mybluetoothkey";
//	public static final String PACKAGE = "com.bydauto.vtdr";
//	public static final String PACKAGE = "com.byd.vtdr";
	public static final String PACKAGE = BuildConfig.APPLICATION_ID ;

	public static void log(final Class<?> classObject, final String logString) {
		Log.d(LOG_DEBUG,
				String.format("%s %s", classObject.getSimpleName(), logString));
	}

	public static int getVerCode(Context ctx) {
		int verCode = -1;
		try {
			verCode = ctx.getPackageManager().getPackageInfo(PACKAGE, 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.e("ver_err", e.getMessage());
		}
		return verCode;
	}

	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"com.bydauto.vtdr", 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e("ver_err", e.getMessage());
		}
		return verName;

	}

	/**
	 * 判断是否有空格
	 * 
	 * @param str
	 * @return
	 */
	public static boolean checkBlank(String str) {
		Pattern pattern = Pattern.compile("[\\s]+");
		Matcher matcher = pattern.matcher(str);
		boolean flag = false;
		while (matcher.find()) {
			flag = true;
		}
		return flag;
	}

	public static boolean isGpsEnabled(LocationManager locationManager) {
		boolean isOpenGPS = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isOpenNetwork = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (isOpenGPS || isOpenNetwork) {
			return true;
		}
		return false;
	}

	/**
	 * 将json字符串转换成封装对象
	 * 
	 * @param jsonStr
	 * @return
	 */
//	public UpdateInfo praseUpdateInfo(String jsonStr) {
//		Type listType = new TypeToken<ArrayList<UpdateInfo>>() {
//		}.getType();
//		Gson gson = new Gson();
//		ArrayList<UpdateInfo> list = gson.fromJson(jsonStr, listType);
//		if (list != null) {
//			return list.get(0);
//		} else {
//			return null;
//		}
//	}

	/**
	 * 获取网址内容
	 * 
//	 * @param url
	 * @return
	 * @throws Exception
	 */
	/*public static String getContent(final String url) {
		StringBuilder sb = new StringBuilder();
		try {
			HttpClient client = new DefaultHttpClient();
			HttpParams httpParams = client.getParams();
			// 设置网络超时参数
			HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
			HttpConnectionParams.setSoTimeout(httpParams, 5000);
			HttpResponse response = client.execute(new HttpGet(url));
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(entity.getContent(),  "GB2312"
								*//*"UTF-8"*//*), 8192);

				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				reader.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("getContent(url)", e.toString());
		}
		return sb.toString();
	}*/

	public static void log(final Class<?> classObject, final String logString,
                           final Exception exception) {
		Log.e(LOG_DEBUG,
				String.format("%s %s", classObject.getSimpleName(), logString),
				exception);
	}

	/**
	 * 创建日志
	 * 
	 * @param class1
	 * @param where
	 * @param data
	 * @param e
	 */
	public static void creatLog(final Class<?> class1, String where,
                                Object data, Exception e) {
		if (e != null) {
			Log.e(class1.getSimpleName() + where + "--->", "erro \n" + e);
		} else if (e == null) {
			Log.d(class1.getSimpleName() + "  " + where + "--->", data + "");
		}
	}

	/**
	 * 创建Toast
	 * 
	 * @param c
	 * @param text
	 * @param showToastTime
	 */
	public static void creatToast(final Context c, final String text,
                                  final int showToastTime) {
		Toast.makeText(c, text, showToastTime).show();
	}

	/**
	 * 获取wifiManager
	 * 
	 * @param c
	 * @return
	 */
	public static WifiManager getWifiManager(Context c) {
		return (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
	}

	/**
	 * 获取BluetoothAdapter
	 * 
	 * @return
	 */
	public static BluetoothAdapter getDefaultAdapter() {
		return BluetoothAdapter.getDefaultAdapter();
	}

	/**
	 * 开启蓝牙
	 */
//	public static void startBluetooth() {
//		BluetoothAdapter btAdapter = getDefaultAdapter();
//		if (btAdapter != null && !btAdapter.isEnabled()) {
//			btAdapter.enable();
//
//		}
//	}

//	public static void resetBTstate(boolean preBTstate) {
//		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
//		if (btAdapter != null) {
//			if (btAdapter.isEnabled() && !preBTstate) {
//				btAdapter.disable();
//			} else if (!btAdapter.isEnabled() && preBTstate) {
//				btAdapter.enable();
//			}
//		}
//
//	}

	/**
	 * 四个字节组合成一个int
	 * 
	 * @param byte01
	 * @param byte02
	 * @param byte03
	 * @param byte04
	 * @return
	 */
	/*public static int convertByteToInt(final byte byte01, final byte byte02,
			final byte byte03, final byte byte04) {
		final ByteBuffer byteBuffer = ByteBuffer.allocate(4);
		byteBuffer.put(byte01);
		byteBuffer.put(byte02);
		byteBuffer.put(byte03);
		byteBuffer.put(byte04);
		return byteBuffer.getInt(0);
	}

	public static long convertByteToLong(final byte byte01, final byte byte02,
			final byte byte03, final byte byte04, final byte byte05,
			final byte byte06, final byte byte07, final byte byte08) {
		final ByteBuffer byteBuffer = ByteBuffer.allocate(8);
		byteBuffer.put(byte01);
		byteBuffer.put(byte02);
		byteBuffer.put(byte03);
		byteBuffer.put(byte04);
		byteBuffer.put(byte05);
		byteBuffer.put(byte06);
		byteBuffer.put(byte07);
		byteBuffer.put(byte08);
		return byteBuffer.getLong(0);
	}

	public static String convertByteArrayToHEXString(final byte[] byteArray,
                                                     final int length) {
		final StringBuffer hexStringBuffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			final int intValue = byteArray[i] & 0xff;
			if (intValue < 0x10) {
				hexStringBuffer.append("0");
			}
			hexStringBuffer.append(Integer.toHexString(intValue));
			hexStringBuffer.append(" ");
		}
		return hexStringBuffer.toString();
	}

	public static String unescape(String string) {
		while (true) {
			final int tokenIndex = string.indexOf("&#");
			if (tokenIndex < 0) {
				break;
			}
			final int valueIndex = string.indexOf(";", tokenIndex + 2);
			if (valueIndex < 0) {
				break;
			}
			try {
				string = string.substring(0, tokenIndex)
						+ (char) (Integer.parseInt(string.substring(
								tokenIndex + 2, valueIndex)))
						+ string.substring(valueIndex + 1);
			} catch (final Exception e) {
				return string;
			}
		}
		string = string.replace("&quot;", "\"");
		string = string.replace("&lt;", "<");
		string = string.replace("&gt;", ">");
		string = string.replace("&amp;", "&");
		return string;
	}

	public static void writeAll(final File file, final byte[] byteArray)
			throws IOException {
		final FileOutputStream fileOutputStream = new FileOutputStream(file);
		fileOutputStream.write(byteArray);
		fileOutputStream.flush();
		fileOutputStream.close();
	}

	public static Intent creatBroadCastReceiverIntent(final String action,
                                                      final String data) {
		Intent intent = new Intent(action);
		intent.putExtra(INTENT_DATA, data);
		return intent;
	}

	public static Intent creatBroadCastReceiverIntent(final String action,
                                                      final Parcelable parcelable) {
		Intent intent = new Intent(action);
		intent.putExtra(INTENT_DATA, parcelable);
		return intent;
	}

	public static boolean isNetworkAvailable(Context cxt) {
		ConnectivityManager cm = (ConnectivityManager) cxt
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null && cm.getActiveNetworkInfo() != null) {
			return cm.getActiveNetworkInfo().isAvailable();
		} else {
			return false;
		}
	}

	public static JSONObject createJSonObject(final String jsonString) {
		JSONObject jsonObject = null;
		if (jsonString.equalsIgnoreCase("")) {
			try {
				jsonObject = new JSONObject(jsonString);
				return jsonObject;
			} catch (Exception e) {
				creatLog(Utility.class, "creatJsonObject",
						"jsonString don't match json", e);
				return new JSONObject();
			}
		} else {
			jsonObject = new JSONObject();
			return jsonObject;
		}
	}

	public static JSONArray creatJsonArray(final String jsonArrayString) {
		JSONArray jsonArray = null;
		if (jsonArrayString.equalsIgnoreCase("")) {
			try {
				jsonArray = new JSONArray(jsonArrayString);
				return jsonArray;
			} catch (Exception e) {
				creatLog(Utility.class, "creatJsonArray",
						"jsonArrayString don't match JsonArray", e);
				return new JSONArray();
			}
		} else {
			jsonArray = new JSONArray();
			return jsonArray;
		}
	}

	public static String subString(final String dataString, final int startSub,
                                   final int endSub) {
		return dataString.substring(startSub, endSub);
	}

	public static String addPointString(final String dataString,
                                        final int startSub, final int endSub) {
		return subString(dataString, startSub, endSub) + ".....";
	}

	public static byte[] longToByte(long s) {

		byte[] targets = new byte[8];

		for (int i = 0; i < 8; i++) {

			int offset = (targets.length - 1 - i) * 8;

			targets[i] = (byte) ((s >>> offset) & 0xff);

		}

		return targets;

	}

	public static long byteTolong(byte[] bytes) {

		long longvalue = 0;

		for (int i = 0; i < bytes.length; i++) {

			longvalue += ((long) (bytes[i] & 0xFF)) << (8 * (7 - i));

		}

		return longvalue;

	}

	*//**
	 * 
	 * @param bytesTime
	 *            []--{Y,M,D,H,Min,S}
	 * @return
	 *//*
	public static int bytesTimeToInt(byte[] bytesTime) {
		int intTime = 0;
		if (bytesTime != null && bytesTime.length == 6) {
			String Year = String.valueOf(bytesTime[0] + 2000);
			String Month = String.valueOf(bytesTime[1]);
			String Date = String.valueOf(bytesTime[2]);
			String Hour = String.valueOf(bytesTime[3]);
			String Min = String.valueOf(bytesTime[4]);
			String Second = String.valueOf(bytesTime[5]);
			String strDate = Year.concat("-").concat(Month).concat("-")
					.concat(Date).concat(" ").concat(Hour).concat(":")
					.concat(Min).concat(":").concat(Second);
			try {
				java.util.Date date = sdf.parse(strDate);
				intTime = (int) (date.getTime() / 1000);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return intTime;
	}

	public static int byteToUnsignedValue(byte value) {
		int result = 0;
		if (value < 0) {
			result = (int) (value + 256);
		} else {
			result = (int) (value);
		}
		return result;
	}
	
	public static void restartAPP(Activity context)
	{
		context.finish();
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		context.startActivity(intent);  
	}
	
	*//**检测是否含有谷歌短信应用
	 * @return
	 *//*
	public static boolean isGoogleSMS(Context cxt){
		PackageManager pm = cxt.getPackageManager();
		List<PackageInfo> list1 = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
		for(PackageInfo pi:list1){
			if(pi.packageName.equalsIgnoreCase("com.google.android.apps.messaging")){
				return true;
			}
		}
		return false;
	}*/
}
