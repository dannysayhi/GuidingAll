package org.wood.guidingall.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.wood.guidingall.MainActivity;
import org.wood.guidingall.R;


public class BeaconScanFragment extends Fragment {

	final private static char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    static boolean isOpened = false;
    static boolean scanable = true;
    private static BeaconScanFragment mFragment;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
/*    private Runnable mLeScanTask = new Runnable() {

        @Override
        public void run() {
            while (scanable) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                try {
                    Thread.sleep(50);
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    };*/

	BluetoothAdapter mBLEAdapter= BluetoothAdapter.getDefaultAdapter();

	View rootView;
	TextView text;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static BeaconScanFragment newInstance() {
        scanable = true;
        if (!isOpened)
            return new BeaconScanFragment();
		return mFragment;
	}


	public BeaconScanFragment() {
        mFragment = this;
        isOpened = true;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_ibeacon_scan, container, false);

        return rootView;
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		text = (TextView)rootView.findViewById(R.id.test);

		final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        HandlerThread handlerThread = new HandlerThread("mLeScanTaskThread");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
//        mHandler.post(mLeScanTask);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity)activity).onSectionAttached(MainActivity.MENU_STORE);
	}

    @Override
    public void onPause() {
        super.onPause();
        scanable = false;
//        mHandler.removeCallbacks(mLeScanTask);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

		int count = 0;
		int rssi_count = 0;
		double ave = 0;

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi,
                                     final byte[] scanRecord) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (scanRecord.length > 30) {
                            	count++;
                                if ((scanRecord[5] == (byte) 0x4c) && (scanRecord[6] == (byte) 0x00) &&
                                        (scanRecord[7] == (byte) 0x02) && (scanRecord[8] == (byte) 0x15)) {
                                    String uuid = IntToHex2(scanRecord[9] & 0xff)
                                            + IntToHex2(scanRecord[10] & 0xff)
                                            + IntToHex2(scanRecord[11] & 0xff)
                                            + IntToHex2(scanRecord[12] & 0xff)
                                            + "-"
                                            + IntToHex2(scanRecord[13] & 0xff)
                                            + IntToHex2(scanRecord[14] & 0xff)
                                            + "-"
                                            + IntToHex2(scanRecord[15] & 0xff)
                                            + IntToHex2(scanRecord[16] & 0xff)
                                            + "-"
                                            + IntToHex2(scanRecord[17] & 0xff)
                                            + IntToHex2(scanRecord[18] & 0xff)
                                            + "-"
                                            + IntToHex2(scanRecord[19] & 0xff)
                                            + IntToHex2(scanRecord[20] & 0xff)
                                            + IntToHex2(scanRecord[21] & 0xff)
                                            + IntToHex2(scanRecord[22] & 0xff)
                                            + IntToHex2(scanRecord[23] & 0xff)
                                            + IntToHex2(scanRecord[24] & 0xff);

                                    String major = IntToHex2(scanRecord[25] & 0xff) + IntToHex2(scanRecord[26] & 0xff);
                                    String minor = IntToHex2(scanRecord[27] & 0xff) + IntToHex2(scanRecord[28] & 0xff);
                                    String txPower = IntToHex2(scanRecord[29] & 0xff);
                                    Log.d("log", "UUID: " + uuid);
                                    Log.d("log", "Major: " + major);
                                    Log.d("log", "Minor: " + minor);
                                    Log.d("log", "RSSI:" + rssi);
                                    Log.d("log", "Device" + device);
                                    Log.d("log", "ROWDATE: " + bytesToHex(scanRecord));

                                    if(count % 11 == 1) {
                                    	rssi_count = rssi;
                                    	count = 1;
                                    }

                                    rssi_count += rssi;
                                    ave = rssi_count / count;

                                    Log.d("log", "count: " + count);
                                    if(count % 11 == 10) {

                                    	Log.d("log", "rssi_total: " + rssi_count);
                                    	Log.d("log", "AVG_RSSI: " + rssi_count / count);


                                    	text.setText(""+ave);
                                    }

                                }
                            } else {
                            }
                        }
                    });
                }
            };


    private String IntToHex2(int Value) {
        char HEX2[] = {Character.forDigit((Value >> 4) & 0x0F, 16),
                Character.forDigit(Value & 0x0F, 16)};
        String Hex2Str = new String(HEX2);
        return Hex2Str.toUpperCase();
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);

    }

}
