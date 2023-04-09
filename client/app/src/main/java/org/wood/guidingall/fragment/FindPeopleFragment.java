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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wood.guidingall.MainActivity;
import org.wood.guidingall.R;
import org.wood.guidingall.tools.User;
import org.wood.guidingall.adapter.FriendListAdapter;
import org.wood.guidingall.backend.ReceiveMsgSession;
import org.wood.guidingall.tools.Config;
import org.wood.guidingall.tools.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindPeopleFragment extends Fragment implements SensorEventListener {

    static FindPeopleFragment mFragment;


    static boolean isOpened = false;
    static int destinationDrawable = 0;
    static int fragmentPosition = 0;
    static SAILS mSails;
    static SAILSMapView mSailsMapView;
    ImageView zoomIn;
    ImageView zoomOut;
    ImageView lockCenter;
    ImageView dialogShow;
    ImageView compassImageView;
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


    public static FindPeopleFragment newInstance(int fragmentPosition, User user) {
        destinationDrawable = R.drawable.people_destination;
        FindPeopleFragment.user = user;

        if (!isOpened || (FindPeopleFragment.fragmentPosition != fragmentPosition))
            return new FindPeopleFragment();
        else {
            FindPeopleFragment.fragmentPosition = fragmentPosition;
            return mFragment;
        }
    }

    public FindPeopleFragment() {
        isOpened = true;
        mFragment = this;
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
        dialogShow.setOnClickListener(controlListener);

        endRouteButton.setOnClickListener(controlListener);


        zoomIn.setVisibility(View.INVISIBLE);
        zoomOut.setVisibility(View.INVISIBLE);

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

                                showFriendListDialog(getString(R.string.loadingSuccess), getString(R.string.findPeopleMessage));
                            }
                        });

                    }

                    @Override
                    public void onFailed(String response) {
                        loadingDialog.dismiss();
                        Toast t = Toast.makeText(getActivity().getBaseContext(), getString(R.string.loadingFailed), Toast.LENGTH_SHORT);
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
                .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }


    void showFriendListDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        builder.setPositiveButton("User sheet", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                new showUserListDialogAsync().execute();
            }
        });


        builder.create().show();

    };

    void showRequestAlertDialog(final String otherUserName, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        builder.setPositiveButton("Ask immediately!", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                try {
                    JSONObject sendObject;
                    sendObject = new JSONObject();
                    sendObject.put(JSON.KEY_STATUS, JSON.STATUS_REQUEST_MESSAGE);
                    sendObject.put(JSON.KEY_USERNAME, user.getUserName());
                    sendObject.put(JSON.KEY_OTHER_USERNAME, otherUserName);
                    sendObject.put(JSON.KEY_MESSAGE, JSON.KEY_WANT_TO_KNOW_OTHER_USER_LOCATION);
                    sendObject.put(JSON.KEY_NEED_TO_REPLY, true);
                    new showUserRequestDialogAsync().execute(sendObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        builder.create().show();
    };

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
                mSailsMapView.getMarkerManager().clear();
                mSailsMapView.getRoutingManager().setStartRegion(mSails.findRegionByLabel(((MainActivity)getActivity()).getNearestLocation()).get(0));
                mSailsMapView.getMarkerManager().setLocationRegionMarker(mSails.findRegionByLabel(((MainActivity)getActivity()).getNearestLocation()).get(0), Marker.boundCenter(getResources().getDrawable(R.drawable.start_point)));
                Toast.makeText(getActivity(), "You're now in: " + user.getUserLocation(), Toast.LENGTH_SHORT).show();
            } else if (v == endRouteButton) {
                //end route.
                mSailsMapView.getRoutingManager().disableHandler();

                //remove pinMarker.
                mSailsMapView.getPinMarkerManager().clear();

                endRouteButton.setVisibility(View.INVISIBLE);

            } else if (v == dialogShow) {
                showFriendListDialog("Find user", "Do you want to find online user?");
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

    private class showUserListDialogAsync extends AsyncTask<Void, Void, List<HashMap<String, Object>>> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle(getString(R.string.loading));
            pDialog.setMessage("Loading online user data sheet...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected List<HashMap<String, Object>> doInBackground(Void... params) {
            List<HashMap<String, Object>> replyList = new ArrayList<>();
            HashMap<String, Object> replyObjectMap;
            JSONObject sendObject;
            JSONObject receiveObject;

            try {
                sendObject = new JSONObject();
                sendObject.put(JSON.KEY_STATUS, JSON.STATUS_REQUEST_GET_USER_LIST);
                user.send(sendObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String receiveMsg;

            while(true) {
                if (!((receiveMsg = ReceiveMsgSession.findUserReceiveMsg) == null)) {
                    ReceiveMsgSession.findUserReceiveMsg = null;
                    break;
                }
            }

            try {
                receiveObject = new JSONObject(receiveMsg);
                int status = receiveObject.getInt(JSON.KEY_STATUS);
                if(status == JSON.STATUS_REQUEST_GET_USER_LIST) {
                    JSONArray userArray = receiveObject.getJSONArray(JSON.KEY_CONNECTED_USERS);

                    for(int i = 0 ; i < userArray.length() ; i++) {
                        JSONObject detailObject = (JSONObject)userArray.get(i);
                        replyObjectMap = new HashMap<>();
                        replyObjectMap.put(JSON.KEY_USERNAME, detailObject.getString(JSON.KEY_USERNAME));
                        replyObjectMap.put(JSON.KEY_ISFRIEND, detailObject.getBoolean(JSON.KEY_ISFRIEND));
                        replyObjectMap.put(JSON.KEY_LOCATION, detailObject.getString(JSON.KEY_LOCATION));
                        replyList.add(replyObjectMap);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



            return replyList;
        }

        @Override
        protected void onPostExecute(final List<HashMap<String, Object>> replyList) {
            BaseAdapter baseAdapter = new FriendListAdapter(getActivity(), replyList);
            pDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false)
                    .setTitle("Online user")
                    .setAdapter(baseAdapter, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            mSailsMapView.getMarkerManager().clear();
                            mSailsMapView.getRoutingManager().setStartRegion(mSails.findRegionByLabel(((MainActivity) getActivity()).getNearestLocation()).get(0));
                            mSailsMapView.getMarkerManager().setLocationRegionMarker(mSails.findRegionByLabel(((MainActivity) getActivity()).getNearestLocation()).get(0), Marker.boundCenter(getResources().getDrawable(R.drawable.start_point)));

                            String location = (String) replyList.get(which).get(JSON.KEY_LOCATION);
                            boolean isFriend = (boolean) replyList.get(which).get(JSON.KEY_ISFRIEND);

                            if (isFriend) {
                                if (location.equals(JSON.KEY_UNKNOWN)) {
                                    Toast.makeText(getActivity(), "The user is positing please wait!", Toast.LENGTH_SHORT).show();
                                } else {
                                    mSailsMapView.getRoutingManager().setTargetRegion(mSails.findRegionByLabel(location).get(0));
                                    mSailsMapView.getRoutingManager().enableHandler();
                                }
                            } else {
                                String otherUserName = (String) replyList.get(which).get(JSON.KEY_USERNAME);
                                showRequestAlertDialog(otherUserName, "You're not" + otherUserName + "'s friend", "Immediately ask the user location?");
                            }

                        }
                    }).create();

            if(replyList.size() > 0)
                builder.show();
            else
                getAlertDialog(getString(R.string.nothing), "No online user").show();

        }
    }

    private class showUserRequestDialogAsync extends AsyncTask<JSONObject, Void, JSONObject> {
        boolean isAcept;
        boolean needToRelpy;
        String location = "unknown";
        String userName;
        String otherUserName;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            user.send(params[0].toString());
            JSONObject replyObject = null;
            String replyMsg;

            int status = -1;

            while(true) {
                if((replyMsg = ReceiveMsgSession.replyMsg) != null) {
                    ReceiveMsgSession.replyMsg = null;
                    break;
                }
            }

            try {
                replyObject = new JSONObject(replyMsg);
                status = replyObject.getInt(JSON.KEY_STATUS);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            if(status == JSON.STATUS_REPLY_MESSAGE)
                return replyObject;
            else
                return null;

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            String parserResultMsg;

            try {
                isAcept = jsonObject.getBoolean(JSON.KEY_MESSAGE);
                needToRelpy = jsonObject.getBoolean(JSON.KEY_NEED_TO_REPLY);
                location = jsonObject.getString(JSON.KEY_LOCATION);
                userName = jsonObject.getString(JSON.KEY_USERNAME);
                otherUserName = jsonObject.getString(JSON.KEY_OTHER_USERNAME);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(isAcept) {
                parserResultMsg = "Get" + otherUserName + "allowed";
            } else {
                parserResultMsg = otherUserName + "not allowed";
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false)
                    .setTitle(otherUserName + "for your message")
                    .setMessage(parserResultMsg)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            mSailsMapView.getMarkerManager().clear();
                            mSailsMapView.getRoutingManager().setStartRegion(mSails.findRegionByLabel(((MainActivity) getActivity()).getNearestLocation()).get(0));
                            mSailsMapView.getMarkerManager().setLocationRegionMarker(mSails.findRegionByLabel(((MainActivity) getActivity()).getNearestLocation()).get(0), Marker.boundCenter(getResources().getDrawable(R.drawable.start_point)));

                            if(isAcept) {
                                if (location.equals(JSON.KEY_UNKNOWN)) {
                                    Toast.makeText(getActivity(), "The user is positing please wait!", Toast.LENGTH_SHORT).show();
                                } else {
                                    mSailsMapView.getRoutingManager().setTargetRegion(mSails.findRegionByLabel(location).get(0));
                                    mSailsMapView.getRoutingManager().enableHandler();
                                }
                            }
                        }
                    }).create().show();
        }
    }
}
