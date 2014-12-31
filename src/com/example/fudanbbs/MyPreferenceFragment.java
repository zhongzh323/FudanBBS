/**
 * 
 */
package com.example.fudanbbs;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @author Joseph.Zhong
 *
 */
public class MyPreferenceFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container, savedInstanceState);
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.mypreference, null);
		return layout;
	}




}
