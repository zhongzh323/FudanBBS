/**
 * 
 */
package com.example.fudanbbs;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author Joseph.Zhong
 *
 */
public class MyFavoriteFragment extends Fragment {

	private ListView listview;
	private ArrayList<HashMap<String, String>> boardlist;
	private FudanBBSApplication currentapplication;
	private HashMap<String, String> cookie;
	private boardlistAsyncTask asynctask;
	private SimpleAdapter adapter;
	private ProgressDialog progressdialog;
	private View view, errorloginview;
	private LinearLayout layout;
	private String TAG = "##################"+this.getClass().getName();

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container, savedInstanceState);

		currentapplication = (FudanBBSApplication) getActivity().getApplication();
		
		layout =  (LinearLayout) inflater.inflate(R.layout.myfavoritefragement, null);
		listview = (ListView) layout.findViewById(R.id.myfavoriteListView);
		errorloginview = inflater.inflate(R.layout.notloginmessage, null);
		layout.addView(errorloginview);
		if(!currentapplication.isCurrentUserGuest()){
    		layout.removeViewInLayout(errorloginview);
    		asynctask = new boardlistAsyncTask();
    		asynctask.execute();

		}else{
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
			layout.removeViewInLayout(errorloginview);
    		asynctask = new boardlistAsyncTask();
    		asynctask.execute();			
		}
	}
	
	
	public class boardlistAsyncTask extends AsyncTask{
    	private int responsecode;
		@Override		
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if(null == progressdialog){
				progressdialog = new ProgressDialog(getActivity());
				progressdialog.setMessage(getString(R.string.loadingboardlist));
				progressdialog.setCancelable(false);
				progressdialog.setCanceledOnTouchOutside(false);
				progressdialog.setProgressStyle(progressdialog.STYLE_SPINNER);	
			}
			progressdialog.show();	
//			if(null == boardlist){
				boardlist = new ArrayList<HashMap<String, String>>();
//			}else{
//				boardlist.clear();
//			}
			Log.v(TAG, "onPreExecute");
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
	    	adapter = new SimpleAdapter(getActivity().getApplicationContext(), boardlist, 
	    			R.layout.allboard, new String[]{"boardtitle", "boarddesc"}, new int[]{R.id.boardtitle, R.id.boarddesc});
	    	listview.setAdapter(adapter);
			listview.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent,
						View view, int position, long id) {
					// TODO Auto-generated method stub
					TextView boardtitle = (TextView) view.findViewById(R.id.boardtitle);
					TextView boarddesc = (TextView) view.findViewById(R.id.boarddesc);
					String boardtitlestring = boardtitle.getText().toString().trim();
					String boarddescstring = boarddesc.getText().toString().trim();
					Intent intent = new Intent();
					intent.setClassName(getActivity(), "com.example.fudanbbs.BoardActivity");
					Bundle bundle = new Bundle();
					String boardURL = "http://bbs.fudan.edu.cn/bbs/doc?board="+boardtitlestring.substring(1, boardtitlestring.length()-1);
					bundle.putString("boardURL", boardURL);
					bundle.putString("boardname", boarddescstring);
					intent.putExtras(bundle);
					startActivity(intent);
				}});	
			if(progressdialog.isShowing()){
				progressdialog.dismiss();
			}
			Log.v(TAG, "onPostExecute");
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub


			cookie = new  HashMap<String, String>();
			cookie = currentapplication.get_cookie();
			Log.v(TAG, "doInBackground");
//			Log.v(TAG, cookie.get("utmpuser").isEmpty()?"cookie is empty":cookie.get("utmpuser"));
			try {
				Document doc = Jsoup.connect("http://bbs.fudan.edu.cn/bbs/fav").timeout(15000).cookies(cookie).get();
				Elements boards = doc.getElementsByTag("brd");
				for(Element board: boards){
					String[] string = new String[2];
					string[0] = board.attr("brd");
					string[1] = board.text();
					Log.v(TAG, string[1]);
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("boardtitle", "["+string[0]+"] ");
					map.put("boarddesc", string[1]);
					boardlist.add(map);
					}
			} catch(SocketTimeoutException e){
				responsecode = 9999;
			}catch(Exception e){
				e.printStackTrace();
			}

			return null;
		}
		
	}

}
