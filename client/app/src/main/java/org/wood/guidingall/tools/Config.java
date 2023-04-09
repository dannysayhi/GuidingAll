package org.wood.guidingall.tools;

public class Config {
    private static final String dannyPC_LOCAL_IP = "192.168.0.3";
	private static final String FCU_WNLAB_LOCAL_IP = "192.168.0.2";
    private static final String FCU_WNLAB_IP = "140.134.26.137";
    private static final String LOCALHOST = "192.168.0.7";
    public static final int MAJOR_LOCATION = 1;
    public static final int MAJOR_ITEM = 2;

    public static String IPADDRESS = LOCALHOST;

	public static final int SOCKET_PORT = 7777;

    private static final String FCU_BUILDINGS_ID = "5405920d1ff15731210001f3";
    private static final String DEPARTMENT_BUILDINGS_ID = "5432562fe60f1890240002a9";
    public static final String MY_TOKEN = "11368c21cf464c1aa587b7ede79aab8b";
    public static final String BUILDING_ID = FCU_BUILDINGS_ID;
    public static final String LABEL_A_STAIR_DOWN = "A stair down";
    public static final String LABEL_B_STAIR_DOWN = "B stair down";
    public static final String LABEL_C_STAIR_DOWN = "C stair down";

    public static final String BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";

    public static final String BT1_UUID = "303831AA-B644-4520-8F0C-720EAF059935";
    public static final String BT2_UUID = "303831AB-B644-4520-8F0C-720EAF059935";   //FCU_TEST2: 20:CD:39:B0:A0:5F
    public static final String BT3_UUID = "303831AC-B644-4520-8F0C-720EAF059935";   //FCU_TEST3: 20:CD:39:B1:00:15
    public static final String BT4_UUID = "fa794de0-23f0-41e5-b8c6-ae7a6728fe1b";   //FCU_TEST4: 7C:66:9D:9A:1A:F1


    public static final String BT5_UUID = "74278bda-b644-4520-8f0c-720eaf059935";   //FCU_TEST5: D0:39:72:D4:11:F1
    public static final String BT6_UUID = "74279bda-b644-4520-8f0c-720eaf059935";

    //MAJOR: 1 for store, 2 for item
    public static final int BT1_MAJOR = MAJOR_LOCATION;     //TYPE: store
    public static final int BT2_MAJOR = MAJOR_LOCATION;     //TYPE: store
    public static final int BT3_MAJOR = MAJOR_LOCATION;     //TYPE: store
    public static final int BT4_MAJOR = MAJOR_ITEM;     //TYPE: item

    public static final int BT5_MAJOR = MAJOR_ITEM;     //TYPE: item
    public static final int BT6_MAJOR = MAJOR_ITEM;     //TYPE: item

    public static final int BT1_MINOR = 249;   //HEX: 00F9 TYPE: store
    public static final int BT2_MINOR = 250;   //HEX: 00FA TYPE: store
    public static final int BT3_MINOR = 251;   //HEX: 00FB TYPE: store
    public static final int BT4_MINOR = 203;   //HEX: 00CB TYPE: item

    public static final int BT5_MINOR = 252;   //HEX: 00CB TYPE: item
    public static final int BT6_MINOR = 253;

    public static final String BT1_PLACE = "無線網路實驗室 - Wireless Network Lab";     //GuidingAll : 1    corner 4
    public static final String BT2_PLACE = "第三國際會議廳 - 3th International Conference Hall";     //250 GuidingAll : 2 corner 5
    public static final String BT3_PLACE = "專題研究室 - Topics Research";      //251 GuidingAll : 3    corner 8

    public static final String BT5_ITEM = "小芳";      //251 corner1
    public static final String BT6_ITEM = "深藍錢包";      //corner09
}
