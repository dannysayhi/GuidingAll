package org.wood.guidingall.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.wood.guidingall.CloudSourcingWeb;
import org.wood.guidingall.lost.ItemLostPost;
import org.wood.guidingall.MainActivity;
import org.wood.guidingall.MapsActivity;
import org.wood.guidingall.R;

public class PlatformFragment extends Fragment {
	Button btnWebView;
	Button btnPostItem;
	Button btnLocationSimulate;


	private View rootView;
	
	/** 
	 * Returns a new instance of this fragment for the given section number. 
	 */  
	public static PlatformFragment newInstance() {

		return new PlatformFragment();
	}
	
	
	public PlatformFragment() {
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.fragment_platform, container, false);

        
		return rootView;
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		btnWebView = (Button)rootView.findViewById(R.id.btnProjectOverview);
		btnPostItem = (Button)rootView.findViewById(R.id.btnPostLostItem);
		btnLocationSimulate = (Button)rootView.findViewById(R.id.btnLocationSimulation);

		btnWebView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getActivity(), CloudSourcingWeb.class);
				startActivity(intent);
			}
		});

		btnPostItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ItemLostPost.class);
				startActivity(intent);
			}
		});

		btnLocationSimulate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MapsActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity)activity).onSectionAttached(MainActivity.MENU_TRACKING);
	}
}
