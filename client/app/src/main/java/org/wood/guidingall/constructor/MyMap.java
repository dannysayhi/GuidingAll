package org.wood.guidingall.constructor;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.util.Log;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class MyMap{
	private GoogleMap map;
	private Marker markerMe;

	public MyMap(GoogleMap map) {
		this.map = map;
	}

	public void cameraFocusOnLocation(double lat, double lng, int zoom) {
		CameraUpdate cameraUpdate;
		CameraPosition caremaPosition;

		try {
			caremaPosition = new CameraPosition.Builder()
					.target(new LatLng(lat, lng)).zoom(zoom).build();
			cameraUpdate = CameraUpdateFactory
					.newCameraPosition(caremaPosition);
		} catch (Exception e) {
			cameraUpdate = CameraUpdateFactory
					.newCameraPosition(new CameraPosition.Builder()
							.target(new LatLng(0.0, 0.0)).zoom(zoom).build());
		}
		if (map != null)
			map.moveCamera(cameraUpdate);
		else {
//			setUpMapIfNeeded();
			map.moveCamera(cameraUpdate);
		}
	}

	public void onMyLocationChangeEvent() {
		map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
			@Override
			public void onMyLocationChange(Location location) {
				cameraFocusOnLocation(location.getLatitude(), location.getLongitude(), 15);
				Log.i("f", "change");
			}
		});
	}

	public Intent onDirectClick(LatLng myLocation, LatLng targetLocation) {

		// 取得自己位置與使用者輸入位置的緯經度
		double fromLat = myLocation.latitude;
		double fromLng = myLocation.longitude;
		double toLat = targetLocation.latitude;
		double toLng = targetLocation.longitude;

		Intent intent = direct(fromLat, fromLng, toLat, toLng);

		return intent;
	}

	// 開啟Google地圖應用程式來完成導航要求
	private Intent direct(double fromLat, double fromLng, double toLat,
			double toLng) {
		// 設定欲前往的Uri，saddr-出發地緯經度；daddr-目的地緯經度
		String uriStr = String.format(
				"http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", fromLat,
				fromLng, toLat, toLng);

		Intent intent = new Intent();

		// 指定交由Google地圖應用程式接手
		intent.setClassName("com.google.android.apps.maps",
				"com.google.android.maps.MapsActivity");

		// ACTION_VIEW-呈現資料給使用者觀看
		intent.setAction(Intent.ACTION_VIEW);

		// 將Uri資訊附加到Intent物件上
		intent.setData(Uri.parse(uriStr));

		return intent;
	}

	public void showMarkerLocation(double lat, double lng) {
		if (markerMe != null) {
			markerMe.remove();
		}
		MarkerOptions markerOpt = new MarkerOptions();
		markerOpt.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		markerOpt.position(new LatLng(lat, lng));
		markerOpt.visible(true);
		if (map != null)
			markerMe = map.addMarker(markerOpt);
		else {
//			setUpMapIfNeeded();
			markerMe = map.addMarker(markerOpt);
		}
	}
}
