/**
 * 
 */
package com.example.fudanbbs;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.fudanbbs.MyFavoriteFragment.boardlistAsyncTask;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Joseph.Zhong
 *
 */
public class MyPreferenceFragment extends Fragment {

	private String TAG = "##################"+this.getClass().getName();
	private FudanBBSApplication currentapplication;
	private HashMap<String, String> cookie;
	private getPreferenceAsyncTask asynctask;
	private HashMap<String, String> map;
	private TextView TVnickname, TVbirthday, TVgender, TVlogincount, TVonlinetime, TVpostcount, TVaccountsince,
	TVlastlogintime, TVIPAddress;
	private ProgressDialog progressdialog;
	private View view, errorloginview;
	private LinearLayout layout, mypreferencelayout;	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container, savedInstanceState);
		
		layout  = (LinearLayout) inflater.inflate(R.layout.mypreference, null);
		currentapplication = (FudanBBSApplication) getActivity().getApplication();
		mypreferencelayout = (LinearLayout) layout.findViewById(R.id.mypreferencelayout);
		errorloginview = inflater.inflate(R.layout.notlogin4mypreference, null);
		layout.addView(errorloginview);
		TVnickname = (TextView) layout.findViewById(R.id.nickname);
		TVbirthday = (TextView) layout.findViewById(R.id.birthday);
		TVgender = (TextView) layout.findViewById(R.id.gender);
		TVlogincount = (TextView) layout.findViewById(R.id.logincount);
		TVonlinetime = (TextView) layout.findViewById(R.id.onlinetime);
		TVpostcount = (TextView) layout.findViewById(R.id.postcount);
		TVaccountsince = (TextView) layout.findViewById(R.id.accountsince);
		TVlastlogintime = (TextView) layout.findViewById(R.id.lastlogintime);
		TVIPAddress = (TextView) layout.findViewById(R.id.IPAddress);
		if(!currentapplication.isCurrentUserGuest()){
			errorloginview.setVisibility(View.GONE);
    		asynctask = new getPreferenceAsyncTask();
    		asynctask.execute();

		}else{
			Log.v(TAG, "current user is guest");
//			mypreferencelayout.setVisibility(View.GONE);
			Button BtnGologin = (Button) errorloginview.findViewById(R.id.gologin);
			BtnGologin.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClassName(getActivity(), "com.example.fudanbbs.LoginActivity");
					startActivityForResult(intent, 0);
				}
				
			});

		}		

		return layout;
	}
	
	@Override
	public void onActivityResult(int requestCode,
			int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(data.getBooleanExtra("result", false)){
			errorloginview.setVisibility(View.GONE);
    		asynctask = new getPreferenceAsyncTask();
    		asynctask.execute();			
		}else{
			
		}
	}

public class getPreferenceAsyncTask extends AsyncTask{


	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		Log.v(TAG, "onPreExecute");
		progressdialog = new ProgressDialog(getActivity());
		progressdialog.setMessage(getString(R.string.loadingpreference));
		progressdialog.setCancelable(false);
		progressdialog.setCanceledOnTouchOutside(false);
		progressdialog.setProgressStyle(progressdialog.STYLE_SPINNER);		
		progressdialog.show();		

		if(null == map){
			map = new HashMap<String, String>();
		}else{
			map.clear();			
		}
	}

	@Override
	protected void onPostExecute(Object result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		Log.v(TAG, "onPostExecute");
		TVnickname.setText(map.get("nick"));
		TVbirthday.setText("19"+map.get("year")+", "+map.get("month")+", "+map.get("day")+", ");
		TVgender.setText(map.get("gender").equals("M")?"Male":"Female");
		TVlogincount.setText(map.get("login"));
		String stay = map.get("stay");
		String onlinetime = String.valueOf(((Integer.valueOf(stay))/60))+"hours "+String.valueOf(((Integer.valueOf(stay))%60))+"minutes";
		TVonlinetime.setText(onlinetime);
		TVpostcount.setText(map.get("post"));
		TVaccountsince.setText(map.get("since").substring(0,10)+"  "+map.get("since").substring(11,19));
		TVlastlogintime.setText(map.get("last").substring(0,10)+"  "+map.get("last").substring(11,19));
		TVIPAddress.setText(map.get("host"));
		if(progressdialog.isShowing()){
			progressdialog.dismiss();
		}
	}

	@Override
	protected Object doInBackground(Object... params) {
		// TODO Auto-generated method stub

		cookie = new  HashMap<String, String>();
		cookie = currentapplication.get_cookie();
		Log.v(TAG, "doInBackground");
		Log.v(TAG+" cookie", cookie.get("utmpuser"));
		try {
			Document doc = Jsoup.connect("http://bbs.fudan.edu.cn/bbs/info").timeout(15000).cookies(cookie).get();
			Elements elements = doc.getElementsByTag("bbsinfo");
			for(Element ele: elements){
				map.put("post", ele.attr("post"));
				map.put("login", ele.attr("login"));
				map.put("stay", ele.attr("stay"));
				map.put("since", ele.attr("since"));
				map.put("host", ele.attr("host"));
				map.put("year", ele.attr("year"));
				map.put("month", ele.attr("month"));
				map.put("day", ele.attr("day"));
				map.put("gender", ele.attr("gender"));
				map.put("last", ele.attr("last"));
			}
			Elements elementsnick = doc.getElementsByTag("nick");
			for(Element ele: elementsnick){
				map.put("nick", ele.text());
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
		return null;
	}
	
}

}
