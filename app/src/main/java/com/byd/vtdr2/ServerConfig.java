package com.byd.vtdr2;

public class ServerConfig {
	private static final String SERVER_HOME = "http://192.168.42.4:8080";// ��������ַ
	public static final String APP_INFO_JSON_ADDRESS = SERVER_HOME + "/hm/apk_update.json";// �������洢APP������Ϣ�ļ��ĵ�ַ,
	public static final String APK_NAME = "New_UI.apk";																					// json�ļ��ж����������ļ���url

	public static final String FIRMWARE_INFO_JSON_ADDRESS = SERVER_HOME + "/hm/firmware_update.json";
	public static final String FIRMWARE_NAME = "MainActivity.apk";
	//	public static final String VTDRIP = "192.168.8.6";

//	public static final String VTDRIP = "192.168.42.1";
//	public static final String PADIP = "192.168.8.2";
	public static final String VTDRIP = "192.168.195.6";
	public static final String PADIP = "192.168.195.2";
	public static final int RB_RECORD_VIDEO = 0;
	public static final int RB_LOCK_VIDEO = 1;
	public static final int RB_CAPTURE_PHOTO = 2;

	public static final int BYD_CARD_STATE_OK = 0;
	public static final int BYD_CARD_STATE_NOCARD = -1;
	public static final int BYD_CARD_STATE_SMALL_NAND = -2;
	public static final int BYD_CARD_STATE_NOT_MEM = -3;
	public static final int BYD_CARD_STATE_UNINIT = -4;
	public static final int BYD_CARD_STATE_NEED_FORMAT = -5;
	public static final int BYD_CARD_STATE_SETROOT_FAIL = -6;
	public static final int BYD_CARD_STATE_NOT_ENOUGH = -7;
	public static final int BYD_CARD_STATE_WP = -8;

	public static final int REC_CAP_STATE_PREVIEW = 0;
	public static final int REC_CAP_STATE_RECORD = 1;
	public static final int REC_CAP_STATE_PRE_RECORD = 2;
	public static final int REC_CAP_STATE_FOCUS = 3;
	public static final int REC_CAP_STATE_CAPTURE = 4;
	public static final int REC_CAP_STATE_VF = 5;
	public static final int REC_CAP_STATE_TRANSIT_TO_VF = 6;
	public static final int REC_CAP_STATE_RESET = 255;
}
