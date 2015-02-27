/**
 * 
 */
package com.example.fudanbbs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.fudanbbs.BoardActivity.TopicListAsyncTask;
import com.example.fudanbbs.MyFavoriteFragment.boardlistAsyncTask;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

import android.app.Fragment;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Joseph.Zhong
 *
 */
public class MyMailFragment extends Fragment {

	private String TAG = "##################"+this.getClass().getName();
	private ArrayList<HashMap<String, String>> maillist;
	private FudanBBSApplication currentapplication;
	private HashMap<String, String> cookie;
	private ListView listview;
	private PullToRefreshListView pulltorefreshlistview;
	private MyMailAdapter adapter;
	private MyMailAsyncTask asynctask;
	private ProgressDialog progressdialog;
	private View view, errorloginview;
	private LinearLayout layout;
	static class ViewHolder{
		TextView TVmailfrom;
		TextView TVmaildate;
		TextView TVmailcontent;
		TextView TVmailnamefake;
		TextView TVmailreadfake;
		TextView TVmailnumberfake;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container,	savedInstanceState);
		currentapplication = (FudanBBSApplication) getActivity().getApplication();
		layout = (LinearLayout) inflater.inflate(R.layout.mymailfragment, null);
		pulltorefreshlistview = (PullToRefreshListView)layout.findViewById(R.id.maillistview);
		pulltorefreshlistview.setMode(Mode.BOTH);

		adapter = new MyMailAdapter(getActivity());
		maillist = new ArrayList<HashMap<String, String>>();
		listview = pulltorefreshlistview.getRefreshableView();		
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id) {
				// TODO Auto-generated method stub
				TextView TVnamefake = (TextView) view.findViewById(R.id.mailnamefake);
    			TextView TVmailnumberfake = (TextView) view.findViewById(R.id.mailnumberfake);	
    			TextView TVmailcontent = (TextView) view.findViewById(R.id.mailcontent);	
    			TextView TVmailfrom = (TextView) view.findViewById(R.id.mailfrom);	
				String name = TVnamefake.getText().toString().trim();
				String number= TVmailnumberfake.getText().toString().trim();
				String mailcontent = TVmailcontent.getText().toString().trim();
				String mailfrom = TVmailfrom.getText().toString().trim();
				String mailcontentURL = "http://bbs.fudan.edu.cn/bbs/mailcon?f="+name+"&n="+number;
				Intent intent = new Intent();
				intent.setClassName(getActivity(), "com.example.fudanbbs.MailActivity");
				intent.putExtra("URL", new String[]{mailcontentURL, number, mailcontent, mailfrom});
				startActivity(intent);

			}
			
		});
		OnRefreshListener2<ListView> refreshlistener = new OnRefreshListener2<ListView>(){

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
//				pulltorefreshlistview.getLoadingLayoutProxy().setRefreshingLabel(getResources()
//						.getString(R.string.pull_to_refresh_refreshing_label));
//				pulltorefreshlistview.setRefreshing();
//				asynctask = new MyMailAsyncTask();
//				asynctask.execute("first", topicmodeURL);
				Log.v(TAG, "pull down called");				
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
//				pulltorefreshlistview.getLoadingLayoutProxy().setRefreshingLabel(getResources()
//						.getString(R.string.pull_to_refresh_from_bottom_refreshing_label));
//				if(false == lastpage){
//    				pulltorefreshlistview.setRefreshing();
//    				Log.v(TAG, nextpageurl);
//    				asynctask = new MyMailAsyncTask();
//    				asynctask.execute("next",nextpageurl);
//
//				}else{
//				Toast.makeText(getActivity(), getString(R.string.lastitem),Toast.LENGTH_SHORT).show();							
//				}				
			}			
		};

		pulltorefreshlistview.setOnRefreshListener(refreshlistener);
		errorloginview = inflater.inflate(R.layout.notloginmessage, null);
		layout.addView(errorloginview);
		if(!currentapplication.isCurrentUserGuest()){
			errorloginview.setVisibility(View.GONE);
    		asynctask = new MyMailAsyncTask();
    		asynctask.execute();

		}else{
			pulltorefreshlistview.setVisibility(View.GONE);
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
			Log.v(TAG, "on activity result");
			pulltorefreshlistview.setVisibility(View.VISIBLE);
			errorloginview.setVisibility(View.GONE);
    		asynctask = new MyMailAsyncTask();
    		asynctask.execute();			
		}
	}
	
	public class MyMailAdapter extends BaseAdapter{

		private LayoutInflater inflater;
		private Context context;
		private String from, date, content, name, read, number;
		private HashMap<String, String> map;
		private ViewHolder holder;
		private TextPaint tp;
		public MyMailAdapter(Context context) {
//			super(context, resource, objects);
			// TODO Auto-generated constructor stub
			this.context = context;
			this.inflater = inflater.from(context);
			tp = new TextPaint();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
//			return super.getCount();
			return (null != maillist)?maillist.size():0;
		}

		@Override
		public View getView(int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
//			return super.getView(position, convertView, parent);
			if(null == convertView){

    			convertView = inflater.inflate(R.layout.mymailitem, null);
    			holder = new ViewHolder();
    			holder.TVmailfrom = (TextView) convertView.findViewById(R.id.mailfrom);
    			holder.TVmaildate = (TextView) convertView.findViewById(R.id.maildate);
    			holder.TVmailcontent = (TextView) convertView.findViewById(R.id.mailcontent);	
    			holder.TVmailnamefake = (TextView) convertView.findViewById(R.id.mailnamefake);	
    			holder.TVmailreadfake = (TextView) convertView.findViewById(R.id.mailreadfake);	
    			holder.TVmailnumberfake = (TextView) convertView.findViewById(R.id.mailnumberfake);	
    			convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
				
			}
//			Log.v(TAG, "position is "+position);
			map = new HashMap<String, String>();
			map = maillist.get(position);		
			from = new String();
			from = map.get("from").toString();
			holder.TVmailfrom.setText(from);
			date = new String();
			date = map.get("date").toString();
			holder.TVmaildate.setText(date.substring(0, 10)+" "+date.substring(11,19));
			Log.v(TAG, map.get("content").toString());
			content = new String();
			content = map.get("content").toString();
			holder.TVmailcontent.setText(content);
			read = new String();
			read = map.get("r").toString();			
			holder.TVmailreadfake.setText(read);
			holder.TVmailcontent = new TextView(context);
			holder.TVmailcontent = (TextView) convertView.findViewById(R.id.mailcontent);
			Log.v(TAG, "read is "+read);
			if(read.equals("0")){
				Log.v(TAG, "do paint color");

				tp = holder.TVmailcontent.getPaint();
				tp.setFakeBoldText(true);
//				holder.TVmailcontent.setTextColor(getResources().getColor(R.color.red));
				holder.TVmailcontent.setTextColor(Color.RED);
			}else{
					Log.v(TAG, "do paint color");
					tp = new TextPaint();
					tp = holder.TVmailcontent.getPaint();
					tp.setFakeBoldText(false);
//					holder.TVmailcontent.setTextColor(getResources().getColor(R.color.red));
					holder.TVmailcontent.setTextColor(Color.BLACK);
				}				

			Log.v(TAG, "position is "+position);
			name = new String();
			name = map.get("name").toString();
			holder.TVmailnamefake.setText(name);
			number = new String();
			number = map.get("number").toString();
			holder.TVmailnumberfake.setText(number);
			return convertView;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
	}
	public class MyMailAsyncTask extends AsyncTask{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressdialog = new ProgressDialog(getActivity());
			progressdialog.setMessage(getString(R.string.loadingmaillist));
			progressdialog.setCanceledOnTouchOutside(false);
			progressdialog.setProgressStyle(progressdialog.STYLE_SPINNER);
			progressdialog.show();

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
					adapter.notifyDataSetChanged();
					pulltorefreshlistview.onRefreshComplete();
				}
				
			}, 0);
			
			if(progressdialog.isShowing()){
				progressdialog.dismiss();				
			}
		
			Log.v(TAG, "onPostExecute");
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			Log.v(TAG, "doInBackground start");		
			currentapplication = (FudanBBSApplication) getActivity().getApplication();
			cookie = new  HashMap<String, String>();
			cookie = currentapplication.get_cookie();
			Log.v(TAG, "doInBackground");
//			Log.v(TAG, cookie.get("utmpuserid").isEmpty()?"cookie is empty":cookie.get("utmpuserid"));
//			Log.v(TAG+" cookie", cookie.isEmpty()?"cookie is empty":"utmpuserid");
			try{
    			Document doc = Jsoup.connect("http://bbs.fudan.edu.cn/bbs/mail").timeout(15000).cookies(cookie).get();   
    			HashMap<String, String> map;  	
    			Elements elements = doc.getElementsByTag("bbsmail");
    			int mailcount = 0;
    			if(null != elements){
        			for(Element ele: elements){
        				mailcount = Integer.parseInt(ele.attr("total"));
        			}
    			}
    			Elements mails = doc.getElementsByTag("mail");
    			int number = 0; 
    			
    			for(Element mail: mails){
    				map = new HashMap<String, String>();
    				map.put("r", mail.attr("r").toString().trim());
    				map.put("from", mail.attr("from").toString().trim());
    				map.put("date", mail.attr("date").toString().trim());
    				map.put("name", mail.attr("name").toString().trim());
    				map.put("content", mail.text());
    				map.put("number", String.valueOf(mailcount-number));
    				number++;
    				maillist.add(map);
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
