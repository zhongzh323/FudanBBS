/**
 * 
 */
package com.example.fudanbbs;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.fudanbbs.MyMailFragment.MyMailAsyncTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
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
		
		flag = false;
		mailcontent = new String();
		mailreplycontent = new String();
		getmaidetailasynctask = new getMailDetailAsyncTask();
		getmaidetailasynctask.execute();
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
		
		TVmaildetail.setText(mailcontent);
		BtnReply.setText(getResources().getString(R.string.reply));
		byte BtnType = REPLY;
		OnClickListener listener = new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String string = ((Button)v).getText().toString();
				Log.v(TAG, string+"button clicked");
				if(string.equals(getResources().getString(R.string.reply))){
					flag = false;
					getreplydetailasynctask = new getReplyDetailAsyncTask();
					getreplydetailasynctask.execute();
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
					TVmaildetail.setVisibility(View.GONE);	
					tablelayout.setVisibility(View.VISIBLE);
					ETMailReceiver.setVisibility(View.VISIBLE);
					ETMailReceiver.setText(mailfrom);
					ETMailTitle.setVisibility(View.VISIBLE);
					ETMailTitle.setText(mailtitle);
					ETeditmail.setVisibility(View.VISIBLE);
					ETeditmail.setText("\n\n"+mailreplycontent);

//					ETeditmail.requestFocus();
					ETeditmail.setSelection(0);
					BtnReply.setText(getResources().getString(R.string.send));			
				}else {
					Log.v(TAG, "this is send");
					String replytext =ETeditmail.getText().toString(); 
					if(replytext.equals("\n\n"+mailreplycontent)){
						Toast.makeText(getApplicationContext(), "回复内容不能为空", Toast.LENGTH_LONG).show();
					}else{
						flag = false;
						postreplycontentasynctask = new postReplyContentAsyncTask();
						postreplycontentasynctask.execute(new String[]{mailtitle, mailfrom, replytext});
					}
				}	
			}
			
		};
		BtnReply.setOnClickListener(listener);
	}

	public class getMailDetailAsyncTask extends AsyncTask<Object, Object, Object>{
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
			Log.v(TAG, "onPostExecute");
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			
			currentapplication = (FudanBBSApplication)getApplication();
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
			flag = true;
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
			Log.v(TAG, "onPostExecute");
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			currentapplication = (FudanBBSApplication)getApplication();
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			flag = true;
			return null;
		}
		
	}
	
	public class postReplyContentAsyncTask extends AsyncTask<Object, Object, Object>{
		private ProgressDialog progressdialog;
		private Response res;
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
			Log.v(TAG, "onPreExecute");
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(progressdialog.isShowing()){
				progressdialog.dismiss();
			}
			if(null!= res&& res.statusCode()==200 && res.statusMessage().equals("OK")){
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.sendsucceed), Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.sendfailed), Toast.LENGTH_SHORT).show();				
			}
			Log.v(TAG, "onPostExecute");
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			String[] array = (String[]) params;
			currentapplication = (FudanBBSApplication)getApplication();
			cookie = new  HashMap<String, String>();
			cookie = currentapplication.get_cookie();
			Log.v(TAG, "doInBackground");
			Log.v(TAG+" cookie", cookie.get("utmpuserid"));
			try {
				res = Jsoup.connect("http://bbs.fudan.edu.cn/bbs/sndmail").cookies(cookie).data("text",array[2], "recv", array[1], "title", array[0])
						.method(Method.POST).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			flag = true;
			return null;
		}
		
	}
}
