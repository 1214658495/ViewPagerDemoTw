/**
 * 比亚迪股份有限公司
 * 第十五事业部 汽车电子研究所 汽车通讯部
 * 应用开发科
 *
 * @author 蒋娇娇(jiang.jiaojiao2@byd.com)
 * @since 2013-5-8   上午11:11:05
 */
package com.byd.vtdr.utils;

import java.util.UUID;

/**
 * @author jjj1134446
 * 
 */
public class Config {
	public static final int UPDATE_INIT = 1000;
	public static final int UPDATE_SCAAN = 1001;
	public static final int UPDATE_SWITCH_ON = 1002;
	public static final int CONNECT_SUCCESS = 1003;
	public static final int CONNECT_DISMISS = 1004;
	public static final int CONNECT_FAILURE = 1005;
	public static final int NO_BONDED = 1006;
	public static final int CONNECT_STILL = 1007;
	public static final int UPDATE_SCAAN_FINISHED = 1008;
	public static final int UPDATE_SWITCH_OFF = 1009;
	public static final int DOWNLOAD_VALUE = 1010;// 下载进度
	public static final int DOWNLOAD_SUCCESS = 1011;// 下载
	public static final int DOWNLOAD_FAIL = 1012;// 下载新版本出错
	public static final int ERROR = 1013;// 不明错误
	public static final int CLOSE_SOCKET =1014;
	public static String SETTINGS = "settings";
	public static String PASSPORT = "PASSPORT_FILE";
	public static String PASSWORD = "password";
	public static String USERNAME = "username";
	public static String ISPWD_REMEMBER = "isPwd_remember";
	public static String ISLOGIN_AUTO = "isLogin_auto";
	public static String ISCONNECT_AUTO = "isConnect_auto";
	public static String GPS_POINT_TIME = "gps_point_time";
	public final static UUID mUuid = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static final String URL = "http://183.62.214.231:8012/PhoneBulletinService.asmx";
	public static boolean isNeed_pwdDialog = false;
	//public static String nameMatcher = "^[A-Za-z0-9]{6,10}|[\u4e00-\u9fa5]{3,5}+$";
	public static String nameMatcher = "[\u4e00-\u9fa5]";
	public static String pwdMatcher = "^[A-Za-z0-9]{6,14}";
	//public static String digitMatcher = "[0-9]+";
	public static String pwdDigMatcher = "[0-9]{1,8}";
	public static String ENCODE = "GB2312-80";
	public static String ENCODE_UTF8 = "UTF-8";
	public static int WAIT_TIME = 3500;
	public static int BTtype = 1024;
	public static boolean isConnected = false,stopConnectAuto = false;//stopConnectAuto在进入应用时开启蓝牙缓慢时执行
	public static boolean isLoginToCon = false;
	public static boolean isBTenabled = false;
	public static final String addressServer_ip = "218.17.215.39";// 10.9.146.238
	public static final int addressServer_port = 5030;
	public static String LAST_CAR = "last_carmac";
	public static byte[] currentPointTime = { (byte) 0xff, (byte) 0xff, (byte) 0xff,
			(byte) 0xff, (byte) 0xff, (byte) 0xff };
	public static byte[] historyPointTime = { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
	public static boolean Debug_GPS = false;
}
