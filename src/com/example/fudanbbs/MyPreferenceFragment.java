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

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Joseph.Zhong
 *
 */
public class MyPreferenceFragment extends Fragment {

	private String TAG = "##################"+this.getClass().getName();
	private getPreferenceAsyncTask asynctask;
	private HashMap<String, String> map;
	private TextView TVnickname, TVbirthday, TVgender, TVlogincount, TVonlinetime, TVpostcount, TVaccountsince,
	TVlastlogintime, TVIPAddress;
	private ProgressDialog progressdialog;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container, savedInstanceState);
		
		View view  = inflater.inflate(R.layout.mypreference, null);
		progressdialog = new ProgressDialog(getActivity());
		progressdialog.setMessage(getString(R.string.loading));
		progressdialog.setCancelable(false);
		progressdialog.setCanceledOnTouchOutside(false);
		progressdialog.setProgressStyle(progressdialog.STYLE_SPINNER);		
		
		TVnickname = (TextView) view.findViewById(R.id.nickname);
		TVbirthday = (TextView) view.findViewById(R.id.birthday);
		TVgender = (TextView) view.findViewById(R.id.gender);
		TVlogincount = (TextView) view.findViewById(R.id.logincount);
		TVonlinetime = (TextView) view.findViewById(R.id.onlinetime);
		TVpostcount = (TextView) view.findViewById(R.id.postcount);
		TVaccountsince = (TextView) view.findViewById(R.id.accountsince);
		TVlastlogintime = (TextView) view.findViewById(R.id.lastlogintime);
		TVIPAddress = (TextView) view.findViewById(R.id.IPAddress);

		
		new getPreferenceAsyncTask().execute();

		return view;
	}


public class getPreferenceAsyncTask extends AsyncTask{

	private FudanBBSApplication currentapplication;
	private HashMap<String, String> cookie;
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		Log.v(TAG, "onPreExecute");

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
		TVbirthday.setText("19"+map.get("year")+"年"+map.get("month")+"月"+map.get("day")+"日");
		TVgender.setText(map.get("gender").equals("M")?"男":"女");
		TVlogincount.setText(map.get("login"));
		String stay = map.get("stay");
		String onlinetime = String.valueOf(((Integer.valueOf(stay))/60))+"小时"+String.valueOf(((Integer.valueOf(stay))%60))+"分钟";
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
		currentapplication = (FudanBBSApplication) getActivity().getApplication();
		cookie = new  HashMap<String, String>();
		cookie = currentapplication.get_cookie();
		Log.v(TAG, "doInBackground");
		Log.v(TAG+" cookie", cookie.get("utmpuserid"));
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
