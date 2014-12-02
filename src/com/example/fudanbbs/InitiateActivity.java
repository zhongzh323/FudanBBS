package com.example.fudanbbs;

import android.app.Activity;
import android.os.Bundle;
/**
 * @author Joseph.Zhong
 *
 */
public class InitiateActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		((FudanBBSApplication)getApplication()).initEnv();
//		setContentView(R.layout.)
	}

}
