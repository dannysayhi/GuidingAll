package org.wood.guidingall.constructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MyLocation {
	private Context context;
	private Location myLocation = null;
	private LocationManager locationManager;
	private Criteria criteria;
	private String provider;
	private MyToast mToast;

	public MyLocation(Context context) {
		this.context = context;
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		mToast = new MyToast(context);
	}

	public MyLocation(Context context, Location location) {
		this.context = context;
		this.myLocation = location;
		mToast = new MyToast(context);
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
	}

	public Location getLocation() {
		if (myLocationValid())
			return myLocation;
		else
			return null;
	}
	
	public void setLocation(Location location) {
		this.myLocation = location;
	}

	public LatLng getLatLng() {
		if (myLocationValid())
			return new LatLng(myLocation.getLatitude(),
					myLocation.getLongitude());
		else
			return new LatLng(0.0, 0.0);
	}

	public void init() {
		if (initLocationProvider())
			whereAmI();
		else
			mToast.show("定位失敗");
	}

	private boolean initLocationProvider() {
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // 定位經準度：ACCURACY_FINE
		// (良好)、ACCURACY_COARSE
		// (粗略)
		criteria.setAltitudeRequired(false); // 是否需能提供高度資訊：true (需要)、false(不需要)
		criteria.setBearingRequired(false); // 是否需能傳回相對位置：true (需要)、false(不需要)
		criteria.setCostAllowed(true); // 是否允許收費：true (允許)、false(不允許)
		criteria.setSpeedRequired(false); // 是否需能提供速度資訊：true (需要)、false(不需要)
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM); // 電量需求：POWER_HIGH
		// (高電量)、POWER_LOW
		// (低電量)、POWER_MEDIUM
		// (中電量)
		// 依據criteria的規則回傳最適合的定位名稱，
		// true代表只回傳目前可提供的定位名稱
		provider = locationManager.getBestProvider(criteria, true);

		return isProviderEnabled(provider);
	}

	public void updateLocation() {
		// 建議要取得位置資訊時都讓系統重新擷取最適當的定位方式
		String provider = locationManager.getBestProvider(criteria, true);
		int minTime = 10000;// ms
		int minDist = 3;// meter
		// 可以設定經過多少毫秒或多少公尺後透過LocationListener來監控自己位置是否改變
		locationManager.requestLocationUpdates(provider, minTime, minDist,
				locationListener);

		myLocation = locationManager.getLastKnownLocation(provider);
	}

	public void whereAmI() {
		int minTime = 5000;// ms
		int minDist = 3;// meter
		if (provider != null) {
			locationManager.addGpsStatusListener(gpsListener);
			locationManager.requestLocationUpdates(provider, minTime, minDist,
					locationListener);
			myLocation = locationManager.getLastKnownLocation(provider);
		}
	}

	public boolean isProviderEnabled(String provider) {
		if(provider != null) {
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				mToast.show("GPS定位成功");
				return true;
			} else if (locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				mToast.show("網路定位成功");
				return true;
			} else if (locationManager
					.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
				mToast.show("PASSIVE定位成功");
			} else {
				mToast.show("定位成功");
			}
		}
		return false;
	}

	public double getLatitude() {
		if (myLocationValid())
			return getLocation().getLatitude();
		else
			return 0.0;
	}

	public double getLongitude() {
		if (myLocationValid())
			return getLocation().getLongitude();
		else
			return 0.0;
	}

	public String getAddressLine(LatLng targetLatLng) {
		Geocoder geocoder = new Geocoder(context, Locale.TAIWAN);
		String returnAddress = "";
		List<Address> lstAddress = null;
		try {
			lstAddress = geocoder.getFromLocation(targetLatLng.latitude,
					targetLatLng.longitude, 1);
			returnAddress = lstAddress.get(0).getAddressLine(0);
		} catch (IOException e) {
			e.printStackTrace();
			return "無法取得地址";
		} catch (Exception e) {
			e.printStackTrace();
			return "無法取得地址";
		}

		return returnAddress;
	}

	public void removeUpdates() {
		locationManager.removeUpdates(locationListener);
		locationManager.removeGpsStatusListener(gpsListener);
	}

	public double distanceBetween(LatLng latlng) {
		float[] results = new float[1];
		// 計算自己位置與使用者輸入地點，此2點間的距離(公尺)，結果會存入results[0]
		try {
			Location.distanceBetween(getLatitude(), getLongitude(),
					latlng.latitude, latlng.longitude, results);
		} catch (Exception e) {
			return Double.NaN;
		}

		return results[0];
	}

	public double distanceBetween(String locationName) {
		float[] results = new float[1];
		if (inputValid(locationName)) {
			// 計算自己位置與使用者輸入地點，此2點間的距離(公尺)，結果會存入results[0]
			try {
				Location.distanceBetween(getLatitude(), getLongitude(),
						getAddress(locationName).getLatitude(),
						getAddress(locationName).getLongitude(), results);
			} catch (Exception e) {
				return Double.NaN;
			}
		} else
			return Double.NaN;

		return results[0];
	}

	// 將使用者輸入的地名或地址轉成Address物件
	public Address getAddress(String locationName) {
		// 建立Geocoder物件
		Geocoder geocoder = new Geocoder(context);
		List<Address> addressList = null;

		try {
			// 解譯地名/地址後可能產生多筆位置資訊，但限定回傳1筆
			if (inputValid(locationName))
				addressList = geocoder.getFromLocationName(locationName, 1);
			else
				return null;
		} catch (IOException e) {
			Log.e(getClass().getName(), e.toString());
			return null;
		}
		if (addressList == null || addressList.isEmpty()) {
			return null;
		} else {
			// 因為當初限定只回傳1筆，所以只要取得第1個Address物件即可
			return addressList.get(0);
		}
	}

	public JSONObject getLocationInfo(String address) {
		HttpGet httpGet = new HttpGet(
				"http://maps.google.com/maps/api/geocode/json?address="
						+ address + "&ka&sensor=false");
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();
		JSONObject jsonObject = new JSONObject();

		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		try {
			jsonObject = new JSONObject(stringBuilder.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonObject;
	}

	public LatLng getGeoPoint(JSONObject jsonObject) {

		Double lon = new Double(0);
		Double lat = new Double(0);

		try {

			lon = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lng");

			lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lat");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new LatLng(lat, lon);

	}

	private LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			myLocation = location;
		}

		@Override
		// 使用者將定位功能關閉時呼叫
		public void onProviderDisabled(String provider) {
			// mToast.show("定位功能已經關閉");
		}

		@Override
		// 使用者將定位功能開啟時呼叫
		public void onProviderEnabled(String provider) {
			// mToast.show("定位功能已經開啓");
		}

		@Override
		// 定位功能改變時呼叫，provider-定位名稱(例如gps或network)；status-狀態
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			case LocationProvider.AVAILABLE:
				// mToast.show("定位服務可以使用");
				break;
			case LocationProvider.OUT_OF_SERVICE:
				// mToast.show("無法提供定位服務，而且短時間內無法回復");
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				// mToast.show("暫時無法使用定位服務，但應該很快就可回復");
				break;
			}
		}
	};

	private GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
		@Override
		public void onGpsStatusChanged(int event) {
			switch (event) {
			case GpsStatus.GPS_EVENT_STARTED:
				// mToast.show("GPS定位啟動");
				break;
			case GpsStatus.GPS_EVENT_STOPPED:
				// mToast.show("GPS定位結束");
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				// mToast.show("GPS第一次定位");
				break;
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				// mToast.show("GPS定位中 ... ");
				break;
			}
		}
	};

	// 檢查是否已經取得自己位置
	private boolean myLocationValid() {
		if (myLocation == null) {
			mToast.show("尚未取得自己位置");
			return false;
		}
		return true;
	}

	// 檢查是否輸入資料
	private boolean inputValid(String input) {
		if (input == null || input.length() <= 0) {
			Toast.makeText(context, "尚未輸入任何資料", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
}
