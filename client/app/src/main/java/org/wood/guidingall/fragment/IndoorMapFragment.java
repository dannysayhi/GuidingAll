package org.wood.guidingall.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sails.engine.LocationRegion;
import com.sails.engine.PathRoutingManager;
import com.sails.engine.SAILS;
import com.sails.engine.SAILSMapView;
import com.sails.engine.core.model.GeoPoint;
import com.sails.engine.overlay.Marker;

import org.wood.guidingall.MainActivity;
import org.wood.guidingall.R;
import org.wood.guidingall.tools.User;
import org.wood.guidingall.tools.Config;
import org.wood.guidingall.tools.JSON;

import java.util.List;


public class IndoorMapFragment extends Fragment implements SensorEventListener {

    static IndoorMapFragment mFragment;
    static boolean isOpened = false;
    static int destinationDrawable = 0;
    static int fragmentPosition = 0;
    static SAILS mSails;
    static SAILSMapView mSailsMapView;
    ImageView zoomIn;
    ImageView zoomOut;
    ImageView lockCenter;
    ImageView compassImageView;
    ImageView dialogShow;
    Button endRouteButton;
    TextView distanceView;

    Vibrator mVibrator;
    ProgressDialog loadingDialog;

    // record the compass picture angle turned
    float currentDegree = 0f;

    // device sensor manager
    SensorManager mSensorManager;


    private View rootView;

    private static User user;


    public static IndoorMapFragment newInstance(int fragmentPosition, User user) {

        IndoorMapFragment.user = user;

        if (!isOpened || (IndoorMapFragment.fragmentPosition != fragmentPosition)) {
            return new IndoorMapFragment();
        }
        else {
            IndoorMapFragment.fragmentPosition = fragmentPosition;
            return mFragment;

        }
    }

    public IndoorMapFragment() {
        isOpened = true;
        mFragment = this;

        destinationDrawable = R.drawable.map_destination;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map_guide, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        zoomIn = (ImageView)rootView.findViewById(R.id.zoomin);
        zoomOut = (ImageView)rootView.findViewById(R.id.zoomout);
        compassImageView = (ImageView)rootView.findViewById(R.id.iv_compass_point);
        lockCenter = (ImageView)rootView.findViewById(R.id.lockcenter);
        dialogShow = (ImageView)rootView.findViewById(R.id.dialogShow);
        endRouteButton = (Button)rootView.findViewById(R.id.stopRoute);

        distanceView = (TextView) rootView.findViewById(R.id.distanceView);

        mVibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);

        zoomIn.setOnClickListener(controlListener);
        zoomOut.setOnClickListener(controlListener);
        lockCenter.setOnClickListener(controlListener);

        endRouteButton.setOnClickListener(controlListener);


        zoomIn.setVisibility(View.INVISIBLE);
        zoomOut.setVisibility(View.INVISIBLE);
        dialogShow.setVisibility(View.INVISIBLE);

        endRouteButton.setVisibility(View.INVISIBLE);


        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        LocationRegion.FONT_LANGUAGE = LocationRegion.NORMAL;

        //new a SAILS engine.
        mSails = new SAILS(getActivity());
        MainActivity.mSails = mSails;
        //set location mode.
        mSails.setMode(SAILS.BLE_GFP_IMU);
        //set floor number sort rule from descending to ascending.
        mSails.setReverseFloorList(true);
        //create location change call back.
        mSails.setOnLocationChangeEventListener(new SAILS.OnLocationChangeEventListener() {
            @Override
            public void OnLocationChange() {

                if (mSailsMapView.isCenterLock() && !mSailsMapView.isInLocationFloor() && !mSails.getFloor().equals("") && mSails.isLocationFix()) {
                    //set the map that currently location engine recognize.
                    mSailsMapView.loadCurrentLocationFloorMap();
                    mSailsMapView.getMapViewPosition().setZoomLevel((byte) 19);
                    Toast t = Toast.makeText(getActivity().getBaseContext(), mSails.getFloorDescription(mSails.getFloor()), Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

        //new and insert a SAILS MapView from layout resource.
        mSailsMapView = new SAILSMapView(getActivity());
        MainActivity.mSailsMapView = mSailsMapView;
        ((FrameLayout) rootView.findViewById(R.id.SAILSMap)).addView(mSailsMapView);
        //configure SAILS map after map preparation finish.
        mSailsMapView.post(new Runnable() {
            @Override
            public void run() {


                loadingDialog = new ProgressDialog(getActivity());
                loadingDialog.setTitle(getString(R.string.loadingMap));
                loadingDialog.setMessage(getString(R.string.wait));
                loadingDialog.setCancelable(false);
                loadingDialog.setIndeterminate(true);
                loadingDialog.show();

                //please change token and building id to your own building project in cloud.
                String token = Config.MY_TOKEN;
                String buildingId = Config.BUILDING_ID;

                mSails.loadCloudBuilding(token, buildingId, new SAILS.OnFinishCallback() {
                    @Override
                    public void onSuccess(String response) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mapViewInitial();
                                routingInitial();
                                loadingDialog.dismiss();
                                getAlertDialog(getString(R.string.loadingSuccess), "Loading the map successfully").show();
                            }
                        });

                    }

                    @Override
                    public void onFailed(String response) {
                        loadingDialog.dismiss();
                        Toast t = Toast.makeText(getActivity().getBaseContext(), "Loading failed, check your network configuration", Toast.LENGTH_SHORT);
                        t.show();
                    }
                });
            }
        });

        //design some action in mode change call back.
        mSailsMapView.setOnModeChangedListener(new SAILSMapView.OnModeChangedListener() {
            @Override
            public void onModeChanged(int mode) {
                if (((mode & SAILSMapView.LOCATION_CENTER_LOCK) == SAILSMapView.LOCATION_CENTER_LOCK) && ((mode & SAILSMapView.FOLLOW_PHONE_HEADING) == SAILSMapView.FOLLOW_PHONE_HEADING)) {
                    lockCenter.setImageDrawable(getResources().getDrawable(R.drawable.center3));
                } else if ((mode & SAILSMapView.LOCATION_CENTER_LOCK) == SAILSMapView.LOCATION_CENTER_LOCK) {
                    lockCenter.setImageDrawable(getResources().getDrawable(R.drawable.center2));
                } else {
                    lockCenter.setImageDrawable(getResources().getDrawable(R.drawable.center1));
                }
            }
        });
    }

    AlertDialog getAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    void mapViewInitial() {

        //establish a connection of SAILS engine into SAILS MapView.
        mSailsMapView.setSAILSEngine(mSails);

        //set location pointer icon.
        mSailsMapView.setLocationMarker(R.drawable.circle, R.drawable.arrow, null, 35);

        //set location marker visible.
        mSailsMapView.setLocatorMarkerVisible(true);

        //load first floor map in package.
        mSailsMapView.loadFloorMap(mSails.getFloorNameList().get(0));


        //Auto Adjust suitable map zoom level and position to best view position.
        mSailsMapView.autoSetMapZoomAndView();

        //set location region click call back.
        mSailsMapView.setOnRegionClickListener(new SAILSMapView.OnRegionClickListener() {
            @Override
            public void onClick(List<LocationRegion> locationRegions) {

                LocationRegion lr = locationRegions.get(0);
                //begin to routing
                if (mSails.isLocationEngineStarted()) {
                    //set routing start point to current user location.
                    mSailsMapView.getRoutingManager().setStartRegion(PathRoutingManager.MY_LOCATION);

                    //set routing end point marker icon.
                    mSailsMapView.getRoutingManager().setTargetMakerDrawable(Marker.boundCenterBottom(getResources().getDrawable(R.drawable.destination)));

                    //set routing path's color.
                    mSailsMapView.getRoutingManager().getPathPaint().setColor(0xFF35b3e5);



                } else {
                    mSailsMapView.getRoutingManager().setTargetMakerDrawable(Marker.boundCenterBottom(getResources().getDrawable(destinationDrawable)));
                    mSailsMapView.getRoutingManager().getPathPaint().setColor(0xFF85b038);
                }

                //set routing end point location.
                mSailsMapView.getRoutingManager().setTargetRegion(lr);

                //begin to route.
                if (mSailsMapView.getRoutingManager().enableHandler()) {
                    endRouteButton.setVisibility(View.VISIBLE);
                }

            }
        });

        //set location region long click call back.
        mSailsMapView.setOnRegionLongClickListener(new SAILSMapView.OnRegionLongClickListener() {
            @Override
            public void onLongClick(List<LocationRegion> locationRegions) {

                if (mSails.isLocationEngineStarted())
                    return;

                mVibrator.vibrate(70);
                mSailsMapView.getMarkerManager().clear();
                mSailsMapView.getRoutingManager().setStartRegion(locationRegions.get(0));
                mSailsMapView.getMarkerManager().setLocationRegionMarker(locationRegions.get(0), Marker.boundCenter(getResources().getDrawable(R.drawable.start_point)));

            }
        });

    }

    void routingInitial() {
        mSailsMapView.getRoutingManager().setStartMakerDrawable(Marker.boundCenter(getResources().getDrawable(R.drawable.start_point)));
        mSailsMapView.getRoutingManager().setTargetMakerDrawable(Marker.boundCenterBottom(getResources().getDrawable(destinationDrawable)));

        mSailsMapView.getRoutingManager().setOnRoutingUpdateListener(new PathRoutingManager.OnRoutingUpdateListener() {
            @Override
            public void onArrived(LocationRegion targetRegion) {
                Toast.makeText(getActivity().getApplication(), "Arrive.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRouteSuccess() {
                List<GeoPoint> gplist = mSailsMapView.getRoutingManager().getCurrentFloorRoutingPathNodes();
                mSailsMapView.autoSetMapZoomAndView(gplist);
            }

            @Override
            public void onRouteFail() {
                Toast.makeText(getActivity().getApplication(), "Route Fail.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPathDrawFinish() {
            }

            @Override
            public void onTotalDistanceRefresh(int distance) {
                distanceView.setText("Total distance: " + Integer.toString(distance) + " (m)");
            }

            @Override
            public void onReachNearestTransferDistanceRefresh(int distance, int nodeType) {

            }

            @Override
            public void onSwitchFloorInfoRefresh(List<PathRoutingManager.SwitchFloorInfo> infoList, int nearestIndex) {

                //set markers for every transfer location
                for (PathRoutingManager.SwitchFloorInfo mS : infoList) {
                    if (mS.direction != PathRoutingManager.SwitchFloorInfo.GO_TARGET)
                        mSailsMapView.getMarkerManager().setLocationRegionMarker(mS.fromBelongsRegion, Marker.boundCenter(getResources().getDrawable(R.drawable.transfer_point)));
                }

                //when location engine not turn,there is no current switch floor info.
                if (nearestIndex == -1)
                    return;

            }
        });
    }

    View.OnClickListener controlListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == zoomIn) {
                //set map zoomin function.
                mSailsMapView.zoomIn();
            } else if (v == zoomOut) {
                //set map zoomout function.
                mSailsMapView.zoomOut();
            } else if (v == lockCenter) {
                String label = ((MainActivity)getActivity()).getNearestLocation();

                if(label.length() > 0 && !label.equals(JSON.KEY_UNKNOWN)) {
                    mSailsMapView.getMarkerManager().clear();
                    mSailsMapView.getRoutingManager().setStartRegion(mSails.findRegionByLabel(label).get(0));
                    mSailsMapView.getMarkerManager().setLocationRegionMarker(mSails.findRegionByLabel(label).get(0), Marker.boundCenter(getResources().getDrawable(R.drawable.start_point)));
                    Toast.makeText(getActivity(), "You're now in: " + label, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Can not position, please check your bluetooth configuration", Toast.LENGTH_SHORT).show();
                }
            } else if (v == endRouteButton) {
                //end route.
                mSailsMapView.getRoutingManager().disableHandler();

                //remove pinMarker.
                mSailsMapView.getPinMarkerManager().clear();

                endRouteButton.setVisibility(View.INVISIBLE);

            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        fragmentPosition = mFragment.getArguments().getInt("FragmentPosition");
        ((MainActivity)activity).onSectionAttached(fragmentPosition);


    }

    @Override
    public void onResume() {
        super.onResume();
        mSailsMapView.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

        showActionBar(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSailsMapView.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);

        showActionBar(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        compassImageView.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @SuppressWarnings("ConstantConditions")
    void showActionBar(boolean enable) {
        if(enable)
            getActivity().getActionBar().show();
        else
            getActivity().getActionBar().hide();
    }
}
