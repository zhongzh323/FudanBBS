/**
 * 
 */
package com.example.fudanbbs;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.fudanbbs.MyMailFragment.MyMailAsyncTask;

import android.R.color;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Joseph.Zhong
 *
 */
public class MailActivity extends Activity {
	private TextView TVmaildetail;
	private EditText ETeditmail, ETMailReceiver, ETMailTitle;
	private TableLayout tablelayout;
	private Button BtnReply;
	private String mailcontent, mailreplycontent;
	private boolean flag;
	private FudanBBSApplication currentapplication;
	private HashMap<String, String> cookie;
	private String url, number, replyurl, mailtitle, mailfrom;
	private final byte REPLY = 1, SEND = 2;
	private getMailDetailAsyncTask getmaidetailasynctask;
	private getReplyDetailAsyncTask getreplydetailasynctask;
	private postReplyContentAsyncTask postreplycontentasynctask;
	private String TAG = "##################"+this.getClass().getName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maillayout);
		
    	ActionBar actionbar = getActionBar();
    	actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME);
    	actionbar.setDisplayHomeAsUpEnabled(true);
    	actionbar.setIcon(new ColorDrawable(color.transparent));
    	actionbar.setTitle(getResources().getString(R.string.newposttitle));
    	
		currentapplication = (FudanBBSApplication)getApplication();
		
		TVmaildetail = (TextView) findViewById(R.id.maildetailcontent);		
		tablelayout = (TableLayout) findViewById(R.id.mailtitletable);
		tablelayout.setVisibility(View.GONE);
		ETMailReceiver = (EditText) findViewById(R.id.mailreceiver);
		ETMailReceiver.setVisibility(View.GONE);
		ETMailTitle = (EditText) findViewById(R.id.mailtitle);
		ETMailTitle.setVisibility(View.GONE);
		ETeditmail = (EditText) findViewById(R.id.maileditcontent);
		ETeditmail.setVisibility(View.GONE);
		BtnReply = (Button) findViewById(R.id.reply);
		Intent intent  = getIntent();
		String[] array = intent.getStringArrayExtra("URL");
		url = array[0];
		number = array[1];
		mailtitle = array[2];
		mailtitle = "Re:"+mailtitle;
		mailfrom = array[3];
		replyurl = "http://bbs.fudan.edu.cn/bbs/pstmail?n="+number;
		mailcontent = new String();
		mailreplycontent = new String();
		getmaidetailasynctask = new getMailDetailAsyncTask();
		getmaidetailasynctask.execute();		

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
//		return super.onOptionsItemSelected(item);
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}	
	
	public class getMailDetailAsyncTask extends AsyncTask<Object, Object, Object>{
		private ProgressDialog progressdialog;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressdialog = new ProgressDialog(MailActivity.this);
			progressdialog.setMessage(getString(R.string.loadingmail));
			progressdialog.setCancelable(false);
			progressdialog.setCanceledOnTouchOutside(false);
			progressdialog.setProgressStyle(progressdialog.STYLE_SPINNER);		
			progressdialog.show();	
			if(mailcontent.length()>0){
				mailcontent = "";
			}
			Log.v(TAG, "onPreExecute");
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(progressdialog.isShowing()){
				progressdialog.dismiss();
			}

			
			TVmaildetail.setText(mailcontent);
			BtnReply.setText(getResources().getString(R.string.reply));
			OnClickListener listener = new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

						getreplydetailasynctask = new getReplyDetailAsyncTask();
						getreplydetailasynctask.execute();

		
					}
				
			};
			BtnReply.setOnClickListener(listener);
			Log.v(TAG, "onPostExecute");
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			cookie = new  HashMap<String, String>();
			cookie = currentapplication.get_cookie();
			Log.v(TAG, "doInBackground");
			Log.v(TAG+" cookie", cookie.get("utmpuserid"));
			try {
				Document doc = Jsoup.connect(url).timeout(15000).cookies(cookie).get();
				Elements elements = doc.getElementsByTag("mail");
				for(Element ele: elements){
					mailcontent = ele.text().toString();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}
	public class getReplyDetailAsyncTask extends AsyncTask{
		private ProgressDialog progressdialog;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressdialog = new ProgressDialog(MailActivity.this);
			progressdialog.setMessage(getString(R.string.loading));
			progressdialog.setCancelable(false);
			progressdialog.setCanceledOnTouchOutside(false);
			progressdialog.setProgressStyle(progressdialog.STYLE_SPINNER);		
			progressdialog.show();	
			if(mailreplycontent.length()>0){
				mailreplycontent = "";
			}
			Log.v(TAG, "onPreExecute");
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(progressdialog.isShowing()){
				progressdialog.dismiss();
			}
			Log.v(TAG, "this is send");
			TVmaildetail.setVisibility(View.GONE);	
			tablelayout.setVisibility(View.VISIBLE);
			ETMailReceiver.setVisibility(View.VISIBLE);
			ETMailReceiver.setText(mailfrom);
			ETMailTitle.setVisibility(View.VISIBLE);
			ETMailTitle.setText(mailtitle);
			ETeditmail.setVisibility(View.VISIBLE);
			ETeditmail.setText("\n\n"+mailreplycontent);

//			ETeditmail.requestFocus();
			ETeditmail.setSelection(0);
			BtnReply.setText(getResources().getString(R.string.send));	
			OnClickListener listener = new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
						String replytext =ETeditmail.getText().toString(); 
						if(replytext.equals("\n\n"+mailreplycontent)){
							Toast.makeText(getApplicationContext(), "回复内容不能为空", Toast.LENGTH_LONG).show();
						}else{
							postreplycontentasynctask = new postReplyContentAsyncTask();
							postreplycontentasynctask.execute(new String[]{mailtitle, mailfrom, replytext});
						}
					}
				
			};
			BtnReply.setOnClickListener(listener);

			Log.v(TAG, "onPostExecute");
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub

			cookie = new  HashMap<String, String>();
			cookie = currentapplication.get_cookie();
			Log.v(TAG, "doInBackground");
			Log.v(TAG+" cookie", cookie.get("utmpuserid"));
			try {
				Document doc = Jsoup.connect(replyurl).timeout(15000).cookies(cookie).get();
				Elements elements = doc.getElementsByTag("m");
				for(Element ele: elements){
					mailreplycontent = ele.text().toString();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		
	}
	
	public class postReplyContentAsyncTask extends AsyncTask<Object, Object, Object>{
		private ProgressDialog progressdialog;
    	private StringBuffer cookiestring;

    	private HttpURLConnection con;
    	private String topictitle, topictext, topiccontent;
    	private int responsecode;
    	private String responsemessage;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressdialog = new ProgressDialog(MailActivity.this);
			progressdialog.setMessage(getString(R.string.postingreplymail));
			progressdialog.setCancelable(false);
			progressdialog.setCanceledOnTouchOutside(false);
			progressdialog.setProgressStyle(progressdialog.STYLE_SPINNER);		
			progressdialog.show();	
			Log.v(TAG, "onPreExecute");
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(progressdialog.isShowing()){
				progressdialog.dismiss();
			}
			switch(responsecode){
			case 200:
				if(responsemessage.equals("OK")){
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.sendsucceed), Toast.LENGTH_SHORT).show();
					finish();
				}else{
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.sendfailed), Toast.LENGTH_SHORT).show();									
				}
				break;
			case 9999:
				Toast.makeText(getApplicationContext(),  getResources().getString(R.string.connectfailed), Toast.LENGTH_LONG).show();
				break;
			}
			Log.v(TAG, "onPostExecute");
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			String[] array = (String[]) params;
			cookie = new  HashMap<String, String>();
			cookie = currentapplication.get_cookie();
			cookiestring = new StringBuffer();
			for(String key :cookie.keySet()){
				cookiestring.append(key).append("=").append(cookie.get(key)).append(";");
			}
			cookiestring.deleteCharAt(cookiestring.length()-1);
			Log.v(TAG, "doInBackground");
			Log.v(TAG+" cookie", cookie.get("utmpuserid"));
			try {
				URL url = new URL("http://bbs.fudan.edu.cn/bbs/sndmail");
				con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("POST");
				con.setConnectTimeout(15000);
  	          	con.setDoInput(true);
  	          	con.setDoOutput(true);
  	          	con.setUseCaches(false);
				con.setRequestProperty("Cookie", cookiestring.toString());
				con.setRequestProperty("Charset", "utf-8");
				con.setRequestProperty("Connection", "keep-alive");
				con.setRequestProperty("Referer", replyurl);
				con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				con.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
				con.connect();
				DataOutputStream ds = new DataOutputStream(con.getOutputStream());
				StringBuffer content = new StringBuffer("recv="+array[1]+"&title="+array[0] +"&text="+array[2]);


				Log.v(TAG, content.toString());
				ds.write(content.toString().getBytes("gb18030"));

				ds.flush();
    	        ds.close();
    	        responsecode= con.getResponseCode();
    	        responsemessage = con.getResponseMessage();
    	        con.disconnect();

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
			
			return null;
		}
		
	}
}
