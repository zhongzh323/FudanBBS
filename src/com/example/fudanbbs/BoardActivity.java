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

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Joseph.Zhong
 *
 */
public class BoardActivity extends Activity {
	private String topicmodeURL, traditionalmodeURL, bid;
	private ArrayList<HashMap<String, String>> topicdata;
	private Bundle bundle;
	private boolean flag;
	private ListView listview;
	private PullToRefreshListView refreshlistview;

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
		bundle = getIntent().getExtras();
		if(null != bundle){
			traditionalmodeURL = bundle.getString("boardURL");		
			Log.v("TAG####################", traditionalmodeURL.toString());
		}
		flag = false;
		topicdata = new ArrayList<HashMap<String, String>>();
		TopicListAsyncTask asynctask = new TopicListAsyncTask();
		asynctask.execute();
		while(!flag){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			continue;
		}
		OnRefreshListener2<ListView> refreshlistener = new OnRefreshListener2<ListView>(){

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				
			}
			
		};
		refreshlistview = (PullToRefreshListView) findViewById(R.id.topiclistview);
		refreshlistview.setMode(Mode.BOTH);
		refreshlistview.setOnRefreshListener(refreshlistener);
		listview = (ListView)refreshlistview.getRefreshableView();
		TopicAdapter topicadapter = new TopicAdapter();
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
	
    public class TopicListAsyncTask extends AsyncTask{
    	private ProgressDialog progressdialog;
    	private HashMap<String, String> cookie;
    	private FudanBBSApplication currentapplication;
    	@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressdialog = new ProgressDialog(BoardActivity.this);
			progressdialog.setMessage(getString(R.string.loading));
			progressdialog.setCancelable(false);
			progressdialog.setCanceledOnTouchOutside(false);
			progressdialog.setProgressStyle(progressdialog.STYLE_SPINNER);		
			progressdialog.show();		
			currentapplication = (FudanBBSApplication)getApplication();
			cookie = new  HashMap<String, String>();
			cookie = currentapplication.get_cookie();			
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(progressdialog.isShowing()){
				progressdialog.dismiss();
			}
		}

		@Override
    	protected Object doInBackground(Object... params) {
    		// TODO Auto-generated method stub
			if(traditionalmodeURL.isEmpty() == false){
				Document doc, doc1;
				try {    
//					String traditionalmodeURL = "http://bbs.fudan.edu.cn/bbs/doc?board=Real_Estate";
					if(cookie.get("utmpuserid")!=null){
						doc = Jsoup.connect(traditionalmodeURL).cookies(cookie).get();
					}else{
						doc = Jsoup.connect(traditionalmodeURL).get();					
					}

					Elements elements = doc.getElementsByTag("brd");
					for(Element element : elements){
						System.out.println(element.attr("bid"));
						bid = element.attr("bid");
						topicmodeURL = "http://bbs.fudan.edu.cn/bbs/tdoc?bid="+bid;
					}
					if(cookie.get("utmpuserid")!=null){
						doc1 = Jsoup.connect(topicmodeURL).cookies(cookie).get();
					}else{
						doc1 = Jsoup.connect(topicmodeURL).get();					
					}
					elements.clear();
					elements = doc1.getElementsByTag("po");
					for(Element ele: elements){
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("m", ele.attr("m"));
						map.put("owner", ele.attr("owner"));
						map.put("time", ele.attr("time"));
						map.put("id", ele.attr("id"));
						map.put("title", ele.text().trim());
						topicdata.add(map);
					}		
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			flag = true;;
    		return null;
    	}
    	
    }

}
