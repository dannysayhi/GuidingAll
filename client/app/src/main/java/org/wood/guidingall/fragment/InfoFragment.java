package org.wood.guidingall.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.wood.guidingall.MainActivity;
import org.wood.guidingall.R;

public class InfoFragment extends Fragment {
	
	/** 
	 * Returns a new instance of this fragment for the given section number. 
	 */  
	public static InfoFragment newInstance() {

		return new InfoFragment();
	}
	
	
	public InfoFragment() { 
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);
         
        return rootView;
    }
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity)activity).onSectionAttached(MainActivity.MENU_INFORMATION);
	}
}
