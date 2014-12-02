package com.example.fudanbbs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
/**
 * @author Joseph.Zhong
 *
 */
public class Top10Fragment extends Fragment {
	static final String TAG = "MainActivity";
	private FudanBBSApplication currentApplication;
	Activity ma;
	ArrayList<HashMap<String, String>> top10List;
	SimpleAdapter adapter;
	ProgressDialog progressdialog;
	public Top10Fragment(){
		top10List = new ArrayList<HashMap<String, String>>();

	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		ma = activity;
		Log.v(TAG, "onAttach");
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.top10fragment, container, false);
        ListView top10ListView = (ListView)rootView.findViewById(R.id.top10listview);
         
		currentApplication = (FudanBBSApplication)(ma.getApplication());
		progressdialog = new ProgressDialog(getActivity());
		progressdialog.setMessage(getString(R.string.loading));
		progressdialog.setCancelable(false);
		progressdialog.setCanceledOnTouchOutside(false);
		progressdialog.setProgressStyle(progressdialog.STYLE_SPINNER);		
		progressdialog.show();		
		
    	top10AsycTask task = new top10AsycTask();
    	task.execute();
    	try {
			task.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        for(HashMap<String, String> map: top10List){
        	Log.v(TAG, map.get("title"));
        }

        adapter = new SimpleAdapter(rootView.getContext(), top10List, 
        		R.layout.top10, 
        		new String[]{"owner","board","title"}, 
        		new int[]{R.id.top10owner, R.id.top10board, R.id.top10title});

    	top10ListView.setAdapter(adapter);

        OnItemClickListener listener = new OnItemClickListener(){

			@Override
			public void onItemClick(
					AdapterView<?> parent,
					View view, int position, long id) {
				// TODO Auto-generated method stub
				Log.v("Listview", "position"+position);

				String gid = top10List.get(position).get("gid");
				String board = top10List.get(position).get("board");
				String postURL = "http://bbs.fudan.edu.cn/bbs/tcon?new=1&board="+board+"&f="
						+gid;
				Intent intent = new Intent();
				intent.setClassName(getActivity(), "com.example.fudanbbs.PostActivity");
				Bundle bundle = new Bundle();
				bundle.putString("postURL", postURL);
				intent.putExtras(bundle);
				startActivity(intent);
			}
        	
        };
        top10ListView.setOnItemClickListener(listener);
		progressdialog.dismiss();
		return rootView;
	}
	
    public class top10AsycTask extends AsyncTask<String, Integer, String>{
    	private String cookieValue;
		private String urltop10;
		private String urlrecommendboard;
		private Connection connection;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
//			currentApplication = (FudanBBSApplication)getActivity().getApplication();	
//			cookieValue = currentApplication.get_cookie();
			urltop10 = "http://bbs.fudan.edu.cn/bbs/top10";
			urlrecommendboard = "http://bbs.fudan.edu.cn/bbs/sec";
			connection = Jsoup.connect(urltop10);
			Log.v(TAG, "onPreExecute");
			
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
//			super.onPostExecute(result);
//			Log.v(TAG, "onPostExecute");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.v(TAG, "doInBackground start");
			try {
				Document doc = connection.get();
				Elements ele = doc.getElementsByTag("top");
//				Document doc = Jsoup.parse(url);
//				System.out.println(doc.body());
				for(Element e: ele){
					HashMap<String, String> map = new HashMap();
					map.put("owner",e.attr("owner").toString().trim());
					map.put("board", e.attr("board").toString().trim());
					map.put("gid", e.attr("gid").toString().trim());
					map.put("title", e.text().toString().trim());
					map.put("count", e.attr("count").toString().trim());
					top10List.add(map);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.v(TAG, "doInBackground end");
			return null;
		}
    }
}
