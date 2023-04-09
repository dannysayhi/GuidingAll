package org.wood.guidingall.tools;

public class Config {
	public final static String LOCAL_PC_IP = "192.168.0.2";
    public final static String LOCAL_dannyPC_IP = "192.168.0.3";
	public final static String FCU_WNLAB_IP = "140.134.26.137";
	public final static int SOCKET_PORT = 7777;

    public static final int MAJOR_LOCATION = 1;
    public static final int MAJOR_ITEM = 2;
	
    public static final String BT1_UUID = "303831AA-B644-4520-8F0C-720EAF059935";
    public static final String BT2_UUID = "303831AB-B644-4520-8F0C-720EAF059935";   //FCU_TEST2: 20:CD:39:B0:A0:5F
    public static final String BT3_UUID = "303831AC-B644-4520-8F0C-720EAF059935";   //FCU_TEST3: 20:CD:39:B1:00:15
    public static final String BT4_UUID = "C6148613-C039-436F-9D11-543ACA73FBB8";

    public static final String BT5_UUID = "74278bda-b644-4520-8f0c-720eaf059935";   //FCU_TEST5: D0:39:72:D4:11:F1
    public static final String BT6_UUID = "74279bda-B644-4520-8f0c-720EAF059935";

    //MAJOR: 1 for store, 2 for item
    public static final int BT1_MAJOR = 1;     //TYPE: store
    public static final int BT2_MAJOR = 1;     //TYPE: store
    public static final int BT3_MAJOR = 1;     //TYPE: store
    public static final int BT4_MAJOR = 2;     //TYPE: item

    public static final int BT1_MINOR = 249;   //HEX: 00F9 TYPE: store
    public static final int BT2_MINOR = 250;   //HEX: 00FA TYPE: store
    public static final int BT3_MINOR = 251;   //HEX: 00FB TYPE:                    store
    public static final int BT4_MINOR = 203;   //HEX: 00CB TYPE: item

    public static final int BT5_MAJOR = MAJOR_ITEM;     //TYPE: item
    public static final int BT6_MAJOR = MAJOR_ITEM;     //TYPE: item

    public static final String BT1_PLACE = "GUCCI";		//249
    public static final String BT2_PLACE = "PRADA";     //250
    public static final String BT3_PLACE = "Dior";      //251
    public static final String BT4_PLACE = "Bag";

    public static final String BT5_ITEM = "亮黃錢包";      //251 corner1
    public static final String BT6_ITEM = "小孩";      //corner09

}
