/**
 * 
 */
package com.example.fudanbbs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.fudanbbs.PostActivity.postAsyncTask;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.R.color;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Joseph.Zhong
 *
 */
public class BoardActivity extends Activity {


	private String TAG = "##################"+this.getClass().getName();
	private String topicmodeURL, traditionalmodeURL, bid, boardname;
	private ArrayList<HashMap<String, String>> topicdata;
	private HashMap<String, String> cookie;
	private FudanBBSApplication currentapplication;
	private Bundle bundle;
	private String nextpageurl;
	private String lastpageurl;
	private boolean lastpage = false;
	private ListView listview;
	private PullToRefreshListView pulltorefreshlistview;
	private OnRefreshListener2<ListView> refreshlistener;
	private TopicAdapter topicadapter;
	private TopicListAsyncTask asynctask;
	private ImageButton IBActionBack, IBActionRefresh;

	static class ViewHolder{
		TextView topicowner;
		TextView topictime;
		TextView topictitle;
		TextView postURL;
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topiclist);
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setIcon(new ColorDrawable(color.transparent));
		
		currentapplication = (FudanBBSApplication)getApplication();	
		
		traditionalmodeURL = new String();
		topicmodeURL = new String();
		bundle = getIntent().getExtras();
		if(null != bundle){
			traditionalmodeURL = bundle.getString("boardURL");	
			boardname = bundle.getString("boardname");
			Log.v("TAG####################", traditionalmodeURL.toString());
		}
		topicdata = new ArrayList<HashMap<String, String>>();
		pulltorefreshlistview = (PullToRefreshListView) findViewById(R.id.topiclistview);
		pulltorefreshlistview.setMode(Mode.BOTH);
		listview = (ListView)pulltorefreshlistview.getRefreshableView();
		topicadapter = new TopicAdapter();

		listview.setAdapter(topicadapter);
		listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id) {
				// TODO Auto-generated method stub			
				TextView tv = (TextView) view.findViewById(R.id.faketext);
        		Intent intent = new Intent();
        		intent.setClassName(getApplicationContext(), "com.example.fudanbbs.PostActivity");
        		Bundle bundle = new Bundle();
        		bundle.putString("postURL", tv.getText().toString().trim());
        		intent.putExtras(bundle);
        		startActivity(intent);						
			}
			
		});

		refreshlistener = new OnRefreshListener2<ListView>(){

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				pulltorefreshlistview.getLoadingLayoutProxy().setRefreshingLabel(getResources()
						.getString(R.string.pull_to_refresh_refreshing_label));
				pulltorefreshlistview.setRefreshing();
				asynctask = new TopicListAsyncTask();
				asynctask.execute("first", topicmodeURL);
				Log.v(TAG, "pull down called");

			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				pulltorefreshlistview.getLoadingLayoutProxy().setRefreshingLabel(getResources()
						.getString(R.string.pull_to_refresh_from_bottom_refreshing_label));
				if(false == lastpage){
    				pulltorefreshlistview.setRefreshing();
    				Log.v(TAG, nextpageurl);
    				asynctask = new TopicListAsyncTask();
    				asynctask.execute("next",nextpageurl);

				}else{
				Toast.makeText(getApplicationContext(),getResources().getString(R.string.lastitem),Toast.LENGTH_SHORT).show();							
			}
			}
			
		};
		asynctask = new TopicListAsyncTask();
		asynctask.execute("first", traditionalmodeURL);
		pulltorefreshlistview.setOnRefreshListener(refreshlistener);

	}	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		getMenuInflater().inflate(R.menu.boardactivitymenu, menu);
        return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
//		return super.onOptionsItemSelected(item);
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			break;
		case R.id.newtopic:
			Bundle bundle = new Bundle();
			bundle.putString("boardname", boardname);
			bundle.putString("bid", bid);
			Intent intent = new Intent();
			intent.setClassName(getApplicationContext(), "com.example.fudanbbs.NewTopicActivity");
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
		return true;
	}	
	
	public class TopicAdapter extends BaseAdapter{
		ViewHolder holder;
		LayoutInflater inflater;
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return topicdata.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			if(null == convertView){
				inflater = LayoutInflater.from(getApplicationContext());
				convertView = inflater.inflate(R.layout.topiclayout, null);
				holder = new ViewHolder();
				holder.topicowner = (TextView) convertView.findViewById(R.id.topicowner);
				holder.topictime = (TextView) convertView.findViewById(R.id.topictime);
				holder.topictitle = (TextView) convertView.findViewById(R.id.topictitle);
				holder.postURL = (TextView) convertView.findViewById(R.id.faketext);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			if(null != topicdata){
				HashMap<String, String> map = topicdata.get(position);
				
				holder.topicowner.setText(map.get("owner"));
				holder.topictime.setText(map.get("time").substring(0, 10)+" "+map.get("time").substring(11, 19));
				holder.topictitle.setText(map.get("title"));	
				holder.postURL.setText("http://bbs.fudan.edu.cn/bbs/tcon?new=1&bid="+bid+"&f="+map.get("id"));
			}

			return convertView;
		}
		
	}
	
    public class TopicListAsyncTask extends AsyncTask<String, Object, Object>{
    	private ProgressDialog progressdialog;

    	@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressdialog = new ProgressDialog(BoardActivity.this);
			progressdialog.setMessage(getString(R.string.loadingpostlist));
			progressdialog.setCancelable(false);
			progressdialog.setCanceledOnTouchOutside(false);
			progressdialog.setProgressStyle(progressdialog.STYLE_SPINNER);		
			progressdialog.show();	
			cookie = new  HashMap<String, String>();
			if(!currentapplication.isCurrentUserGuest()){
				cookie = currentapplication.get_cookie();					
			}


			Log.v(TAG, "onPreExecute");
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			new Handler().postDelayed(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					topicadapter.notifyDataSetChanged();
					pulltorefreshlistview.onRefreshComplete();
				}
				
			}, 0);
			if(progressdialog.isShowing()){
				progressdialog.dismiss();
			}
			Log.v(TAG, "onPostExecute");
		}

		@Override
    	protected Object doInBackground(String... params) {
    		// TODO Auto-generated method stub
			Log.v(TAG, "doInBackground start");		
			HashMap<String, String> map = new HashMap<String, String>();
			String type = params[0];
			String url = params[1];
			if(type.equals("first")){
				if(url.isEmpty() == false){
					Log.v(TAG, "passed url is "+url);
					Document doc;
					if(true == topicmodeURL.isEmpty()){
	    				try {    
	    					if(cookie.get("utmpuser")!=null){
	    						doc = Jsoup.connect(url).cookies(cookie).get();
	    					}else{
	    						doc = Jsoup.connect(url).get();					
	    					}
	    
	    					Elements elements = doc.getElementsByTag("brd");
	    					for(Element element : elements){
	    						System.out.println(element.attr("bid"));
	    						bid = element.attr("bid");
	    						topicmodeURL = "http://bbs.fudan.edu.cn/bbs/tdoc?bid="+bid;
	    						url = topicmodeURL;
	    					}
	    				} catch (IOException e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				}				
					}
				}
			}
			Document doc1;
			try{
				if(cookie.get("utmpuser")!=null){
					doc1 = Jsoup.connect(url).cookies(cookie).get();
				}else{
					doc1 = Jsoup.connect(url).get();					
				}

				Elements elements = doc1.getElementsByTag("brd");
				for(Element ele: elements){
					int total = Integer.valueOf(ele.attr("total"));
					int start = Integer.valueOf(ele.attr("start"));
					if(1 == start){
						lastpage = true;
					}
					int nextstart = start-20>0?start-20:1;
					nextpageurl = topicmodeURL+"&start="+String.valueOf(nextstart);
				}
				
				ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
				elements.clear();
				elements = doc1.getElementsByTag("po");
				
				for(Element ele: elements){
					map = new HashMap<String, String>();
					map.put("m", ele.attr("m"));
					map.put("owner", ele.attr("owner"));
					map.put("time", ele.attr("time"));
					map.put("id", ele.attr("id"));
					map.put("title", ele.text().trim());
					Log.v(TAG, ele.text());
					data.add(map);
				}		
				
				// reverse the arraylist since the order of topic is originally inverted.
				if(null != data){
					for(int i=data.size()-1; i>0; i--){
//						Log.v(TAG, data.get(i).get("title"));
						topicdata.add(data.get(i));
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		Log.v(TAG, "doInBackground end");
		return null;
    	}
    	
    }

}
