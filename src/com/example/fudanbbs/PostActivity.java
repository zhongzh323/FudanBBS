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

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Joseph.Zhong
 *
 */
public class PostActivity extends Activity {
	private int textsize;
	private Bundle bundle;
	private String url;
	private String nextpageurl;
	private String lastpageurl;
	private String lastfid;
	private boolean lastpage = false;
	private boolean flag;
	private final String TAG = "#########PostActivity###########";
	private ArrayList<HashMap<String, ArrayList<String []>>> postArrayList;
	private postAdapter postadapter;
	private ListView listview;
	static class ViewHolder{
		public TextView postowner;
		public TextView posttime;
		public TextView posttitle;
		public LinearLayout posttextlayout;
		public LinearLayout postquote;
		public LinearLayout postsignature;
	}

	private PullToRefreshListView pulltorefreshlistview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		bundle = getIntent().getExtras();
		url = bundle.getString("postURL");	
		nextpageurl = "";
		lastfid = "";
		Log.v(TAG, url);
		setContentView(R.layout.postlist);	
		

		OnRefreshListener2<ListView> refreshlistener = new OnRefreshListener2<ListView>(){
			
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				pulltorefreshlistview.getLoadingLayoutProxy().setRefreshingLabel(getResources()
						.getString(R.string.pull_to_refresh_refreshing_label));
				pulltorefreshlistview.setRefreshing();
				postArrayList.clear();
				flag = false;
				postAsyncTask postasynctask = new postAsyncTask();
				postasynctask.execute(url);	
				while(!flag){
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}
				new Handler().postDelayed(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						postadapter.notifyDataSetChanged();
						pulltorefreshlistview.onRefreshComplete();
					}
					
				}, 0);				
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				pulltorefreshlistview.getLoadingLayoutProxy().setRefreshingLabel(getResources()
						.getString(R.string.pull_to_refresh_from_bottom_refreshing_label));
				if(false == lastpage){
    				pulltorefreshlistview.setRefreshing();
    				flag = false;
    				postAsyncTask postasynctask = new postAsyncTask();
    				postasynctask.execute(nextpageurl);
    				while(!flag){
    					try {
    						Thread.sleep(200);
    					} catch (InterruptedException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
    					continue;
    				}	
				}else{
				Toast.makeText(getApplicationContext(),getResources().getString(R.string.lastitem),Toast.LENGTH_SHORT).show();							
			}	
				new Handler().postDelayed(new Runnable(){
				    
					@Override
					public void run() {
						// TODO Auto-generated method stub
							postadapter.notifyDataSetChanged();	
							pulltorefreshlistview.onRefreshComplete();
						
					}				
				}, 0);			
			}
		};
		OnLastItemVisibleListener onlastitemvisiblelistener = new OnLastItemVisibleListener(){

			@Override
			public void onLastItemVisible() {
				// TODO Auto-generated method stub
//				Toast.makeText(getApplicationContext(), R.string.lastitem, Toast.LENGTH_SHORT).show();
			}
			
		};

		flag = false;
		postArrayList = new ArrayList<HashMap<String, ArrayList<String []>>>();
		postAsyncTask postasynctask = new postAsyncTask();
		postasynctask.execute(url);
		while(!flag){
			try {
				Thread.sleep(200);
				Log.v(TAG, "sleep for 200ms");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			continue;
		}
		pulltorefreshlistview = (PullToRefreshListView)findViewById(R.id.postlistview);
		pulltorefreshlistview.setMode(Mode.BOTH);
		pulltorefreshlistview.setOnRefreshListener(refreshlistener);
		pulltorefreshlistview.setOnLastItemVisibleListener(onlastitemvisiblelistener);
		listview = pulltorefreshlistview.getRefreshableView();
		postadapter = new postAdapter(PostActivity.this);
		listview.setAdapter(postadapter);

	}
	public class postAdapter extends BaseAdapter{
		
		private LayoutInflater inflater; 
		private Context context;
		private ViewHolder holder;
		public ArrayList<String> imagearray;
		private String[] temparray;
		private String[] temparrayowner;
		private String[] temparraynick;
		private String currentimage;
		private LinearLayout layout;
		public postAdapter(Context context){
			this.context = context;
			this.inflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return postArrayList.size();
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
				convertView = inflater.inflate(R.layout.postlayout, null);
				holder = new ViewHolder();
				holder.postowner = (TextView) convertView.findViewById(R.id.postowner);
				holder.posttime = (TextView) convertView.findViewById(R.id.posttime);
				holder.posttitle = (TextView) convertView.findViewById(R.id.posttitle);
				holder.posttextlayout = (LinearLayout) convertView.findViewById(R.id.posttextlayout);
				holder.postquote = (LinearLayout) convertView.findViewById(R.id.postquote);
				holder.postsignature = (LinearLayout) convertView.findViewById(R.id.postsignature);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			temparray = new String[2];      //a temporary array to store the temporary string array data
			temparrayowner = postArrayList.get(position).get("owner").get(0);
			temparraynick = postArrayList.get(position).get("nick").get(0);
			holder.postowner.setText(temparrayowner[1]+"["+temparraynick[1]+"]");
			temparray = new String[2];
			temparray = postArrayList.get(position).get("date").get(0);
			holder.posttime.setText(temparray[1]);
			temparray = new String[2];
			temparray = postArrayList.get(position).get("title").get(0);
			holder.posttitle.setText(temparray[1]);
			
			layout = new LinearLayout(context);
			layout = holder.posttextlayout;
			holder.posttextlayout.removeAllViewsInLayout();
			textsize = 18;
			createView(postArrayList.get(position).get("body"), postArrayList.get(position).get("bodyimage"), textsize);
			holder.postquote.removeAllViewsInLayout();
			layout = holder.postquote;
			textsize = 15;
			createView(postArrayList.get(position).get("quote"), null, textsize);
			holder.postsignature.removeAllViewsInLayout();
			layout = holder.postsignature;
			ArrayList<String []> sig = postArrayList.get(position).get("signature");
			if(null != sig){
				createView(sig,postArrayList.get(position).get("signatureimage"), textsize);
			}

			return convertView;
		}
		private void createXImageView(String aURL, ArrayList<String[]> imagelist){
			XImageView xiv = new XImageView(context, aURL, imagelist);
			xiv.setlistener();
			layout.addView(xiv);	
		}
		private void createTextView(String text, int textsize){
			TextView tv = new TextView(context);
			tv.setText(text);
			tv.setTextSize(textsize);
//			Log.v(TAG, text);
			layout.addView(tv);			
		}
		private void createhrefTextView(String aURL){
			TextView hreftv = new TextView(context);
			hreftv.setText(aURL);
			hreftv.setAutoLinkMask(Linkify.ALL);
			hreftv.setMovementMethod(LinkMovementMethod.getInstance());
			layout.addView(hreftv);				
		}
		private void createView(ArrayList<String[]> arraylist, ArrayList<String[]> imagelist, int textsize){
			ArrayList<String[]> templist = new ArrayList<String[]>();
			templist = arraylist;
			for(int i=0; i<templist.size(); i++){
				temparray = new String[2];
				temparray = templist.get(i);
    			switch(temparray[0]){
        			case "text":
        				createTextView(temparray[1], textsize);
        				break;
        			case "imagehref":
        				createXImageView(temparray[1], imagelist);
        				break;
        			case "regularhref":
        				createhrefTextView(temparray[1]);
        				break;
    			}
			}
		}

    	public class XImageView extends ImageView{
    		public String URL;
    		private ArrayList<String> imagearray;
    		private DisplayImageOptions options;
    		private ImageLoader imageLoader;
    		private ImageLoaderConfiguration config;
    		public XImageView(Context context) {
    			super(context);
    			// TODO Auto-generated constructor stub
    		}
    		public XImageView(Context context, String aURL){
    			super(context);
    			// TODO Auto-generated constructor stub    			
    			this.URL = aURL;
    			initImageLoader();
    			imageLoader.displayImage(aURL, this);  		
    		}		
    		public XImageView(Context context, String aURL,
					ArrayList<String[]> imagelist) {
				// TODO Auto-generated constructor stub
    			super(context);
    			this.URL = aURL;
    			imagearray = new ArrayList<String>();
    			for(int i=0; i<imagelist.size(); i++){
    				String [] temp = imagelist.get(i);
    				imagearray.add(temp[1]);
    			}
       			initImageLoader();
    			imageLoader.displayImage(aURL, this);  		
			}
    		public void initImageLoader(){
    			// image loader configuration
    			this.options  = new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(true)
    					.showImageOnLoading(R.drawable.fudanbbsimageloading).resetViewBeforeLoading(true).build();
    			this.config = new ImageLoaderConfiguration.Builder(context).defaultDisplayImageOptions(options)
    					.diskCacheFileNameGenerator(new Md5FileNameGenerator()).threadPriority(Thread.NORM_PRIORITY-2)
    					.writeDebugLogs().build();	
    			imageLoader = ImageLoader.getInstance();
    			imageLoader.init(config);	
    		}
			OnClickListener listener = new OnClickListener(){   			
    		@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			Intent intent = new Intent();
    			intent.setClassName(getApplicationContext(), "com.example.fudanbbs.ImageBrowserActivity");
    			Bundle bundle = new Bundle();
    			bundle.putString("currentimage", URL);
    			bundle.putStringArrayList("imagearray", imagearray);
    			intent.putExtras(bundle);
    			startActivity(intent);
    		}		
    	};
    	public void setlistener(){
    		this.setOnClickListener(listener);
    	}
    	}
	}
	public class postAsyncTask extends AsyncTask<String, Integer, String>{

		private ProgressDialog progressdialog;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressdialog = new ProgressDialog(PostActivity.this);
			progressdialog.setMessage(getString(R.string.loading));
			progressdialog.setCanceledOnTouchOutside(false);
			progressdialog.setProgressStyle(progressdialog.STYLE_SPINNER);
			progressdialog.show();
			Log.v(TAG, "onPreExecute");
			
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if(progressdialog.isShowing()){
				progressdialog.dismiss();				
			}
			
			Log.v(TAG, "onPostExecute");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String url = params[0];
			String bid = null;
			String gid = null;
			String lastfid = null;
			String last = "0";
			try {
				Log.v(TAG, "doInBackground start "+url);
				Document doc = Jsoup.connect(url).timeout(20000).get();
				
				//get  bid for generating the "next page " URL path
				Elements elements = doc.getElementsByTag("bbstcon");
				for(Element e: elements){
    				bid = e.attr("bid").toString().trim();
    				gid = e.attr("gid").toString().trim();
    				if(e.hasAttr("last")){
    					lastpage = true;
    				}
				}
//				if(false == lastpage){
    				//parse each post  
    				Elements ele = doc.getElementsByTag("po");	
    				for(Element e: ele){			
    					HashMap<String, ArrayList<String []>> map = new HashMap<String, ArrayList<String []>>() ;
    					ArrayList<String []> arraygid = new ArrayList<String []>();					
    					ArrayList<String []> arrayfid = new ArrayList<String []>();
    					ArrayList<String []> arrayowner = new ArrayList<String []>();
    					ArrayList<String []> arraynick = new ArrayList<String []>();	
    					ArrayList<String []> arraytitle = new ArrayList<String []>();
    					ArrayList<String []> arraydate = new ArrayList<String []>();	
    					ArrayList<String []> arraybody = new ArrayList<String []>();
    					ArrayList<String []> arrayquote = new ArrayList<String []>();
    					ArrayList<String []> arraysignature = new ArrayList<String []>();	
    					ArrayList<String []> arraybodyimage = new ArrayList<String []>();
    					ArrayList<String []> arraysignatureimage = new ArrayList<String []>();	
    					/*arraystring represents the type and content of each line in html post 
    					arraystring[0] represents the type, say, "text", "href link", "image href link"
    					arraystring[1] represents the content
    					*/	
    					String [] arraystring = new String [2];
    					arraystring[0] = "text";
    					lastfid = arraystring[1] = e.attr("fid").toString().trim();
    					arrayfid.add(arraystring);
    					map.put("fid", arrayfid);
    
    					arraystring = new String [2];
    					arraystring[0] = "text";
    					arraystring[1] = e.getElementsByTag("owner").text().toString().trim();
    					arrayowner.add(arraystring);
    					map.put("owner", arrayowner);
    					arraystring = new String [2];
    					arraystring[0] = "text";
    					arraystring[1] = e.getElementsByTag("nick").text().toString().trim();
    					arraynick.add(arraystring);			
    					map.put("nick", arraynick);
    					arraystring = new String [2];
    					arraystring[0] = "text";
    					arraystring[1] = e.getElementsByTag("title").text().toString().trim();
    					arraytitle.add(arraystring);			
    					map.put("title", arraytitle);	
    					arraystring = new String [2];
    					arraystring[0] = "text";
    					arraystring[1] = e.getElementsByTag("date").text().toString().trim();
    					arraydate.add(arraystring);			
    					map.put("date", arraydate);						
    
    					Elements body = e.getElementsByAttributeValue("m", "t");
    					for(Element body1: body){
    						Elements linesbody = body1.getElementsByTag("p");
    	    					for(Element line: linesbody){
        							arraystring = new String [2];
    	        						if(line.toString().contains("<br />")){
    	        							arraystring[0] = "text";
    	        							arraystring[1] = "";					
    	        						}else if(line.children().hasAttr("href")){
    	        							if(line.children().hasAttr("i")){
    	        								arraystring[0] = "imagehref";
												arraystring[1] = line.children().attr("href").toString().trim();
												arraybodyimage.add(arraystring);
    	        							}else{
    	        								arraystring[0] = "regularhref";
    	        								arraystring[1] = line.children().attr("href").toString().trim();        								
    	        							}
    	        						}
    	        						else{
    	        							arraystring[0] = "text";
    	        							arraystring[1] = line.text().toString().trim();
    	        						}
    	        						arraybody.add(arraystring);	
    	    						}
    						}
    	    					Elements quote = e.getElementsByAttributeValue("m", "q");
    	    					for(Element quote1: quote){
    	    						Elements linequote = quote1.getElementsByTag("p");
    	    	    					for(Element line: linequote){
    	        							arraystring = new String [2];
    	    	        						if(line.toString().contains("<br />")){
    	    	        							arraystring[0] = "text";
    	    	        							arraystring[1] = "";			
    	    	        						}else if(line.children().hasAttr("href")){
    	            								arraystring[0] = "regularhref";
    	            								arraystring[1] = line.children().attr("href").toString().trim();    
    	    	        						}
    	    	        						else{
    	    	        							arraystring[0] = "text";
    	    	        							arraystring[1] = line.text().toString().trim();
    	    	        						}
    	    	        						arrayquote.add(arraystring);
    	    	    						}    	
    	    					}
    	    					Elements signature = e.getElementsByAttributeValue("m", "s");
    	    					for(Element signature1: signature){
    	    						Elements linesignature = signature1.getElementsByTag("p");
    	    	    					for(Element line: linesignature){
    	        							arraystring = new String [2];
    	    	        						if(line.toString().contains("<br />")){
    	    	        							arraystring[0] = "text";
    	    	        							arraystring[1] = "";						
    	    	        						}else if(line.children().hasAttr("href")){
    	    	        							if(line.children().hasAttr("i")){
    	    	        								arraystring[0] = "imagehref";
    	    	        								arraystring[1] = line.children().attr("href").toString().trim();
    	    	    	        						arraysignatureimage.add(arraystring);
    	    	        							}else{
    	    	        								arraystring[0] = "regularhref";
    	    	        								arraystring[1] = line.children().attr("href").toString().trim();        								
    	    	        							}
    	    	        						}
    	    	        						else{
    	    	        							arraystring[0] = "text";
    	    	        							arraystring[1] = line.text().toString().trim();
    	    	        						}
    	    	        						arraysignature.add(arraystring);
    	    	    						}    	
    	    					}    					
    					map.put("body", arraybody);
    					map.put("quote", arrayquote);
    					map.put("signature", arraysignature);
    					map.put("bodyimage", arraybodyimage);
    					map.put("signatureimage", arraysignatureimage);
//    					Log.v(TAG, arraysignatureimage.get(0).toString());
    					postArrayList.add(map);
    				}
    				nextpageurl = "http://bbs.fudan.edu.cn/bbs/tcon?new=1&bid="+bid+"&g="+gid+"&f="+lastfid+"&a=n";
    				Log.v(TAG, "doInBackground end, next page url =" +nextpageurl);
    				flag = true;
	//			}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.pageloadfailed), Toast.LENGTH_LONG).show();
			}
//			Log.v(TAG, postArrayList.toArray().toString());
//			Log.v(TAG, nextpageurl);
			String o = null;
			return o;
		}

	}
	public void printarraylist(){
		int position = 1;
		String[] temparray = new String[2];      //a temporary array to store the temporary string array data
		String[] temparrayowner = postArrayList.get(position).get("owner").get(0);
		String[] temparraynick = postArrayList.get(position).get("nick").get(0);
//		Log.v(TAG, temparrayowner[1]+"["+temparraynick[1]+"]");
		temparray = new String[2];
		temparray = postArrayList.get(position).get("date").get(0);
//		Log.v(TAG, temparray[1]);
		temparray = new String[2];
		temparray = postArrayList.get(position).get("title").get(0);
//		Log.v(TAG, temparray[1]);		
		ArrayList<String[]> tempabodylist = new ArrayList<String[]>();
		tempabodylist = postArrayList.get(position).get("body");
		for(int i=0; i<tempabodylist.size(); i++){
			temparray = new String[2];
			String[] strings = tempabodylist.get(i);
			temparray = strings;
//			Log.v(TAG, temparray[1]);

		}
		
	}
}
