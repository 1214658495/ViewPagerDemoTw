package com.bydauto.myviewpager;

public class ServerConfig {
	private static final String SERVER_HOME = "http://192.168.42.4:8080";// ��������ַ
	public static final String APP_INFO_JSON_ADDRESS = SERVER_HOME + "/hm/apk_update.json";// �������洢APP������Ϣ�ļ��ĵ�ַ,
	public static final String APK_NAME = "New_UI.apk";																					// json�ļ��ж����������ļ���url

	public static final String FIRMWARE_INFO_JSON_ADDRESS = SERVER_HOME + "/hm/firmware_update.json";
	public static final String FIRMWARE_NAME = "MainActivity.apk";
//	public static final String HOST = "192.168.42.1";
//	public static final String HOST = "192.168.8.6";
	public static final String HOST = "192.188.8.6";
	public static final int RB_RECORD_VIDEO = 0;
	public static final int RB_LOCK_VIDEO = 1;
	public static final int RB_CAPTURE_PHOTO = 2;
}
