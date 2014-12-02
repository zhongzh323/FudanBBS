/**
 * 
 */
package com.example.fudanbbs;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author Joseph.Zhong
 *
 */
public class TopicActivity extends Activity {
	private String url = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		url = getIntent().getExtras().getString("topicURL");
		setContentView(R.layout.topiclist);
	}
	
}
