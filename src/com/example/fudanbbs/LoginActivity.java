package com.example.fudanbbs;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @author Joseph.Zhong
 *
 */
public class LoginActivity extends Activity {


	private String username, password;
    private boolean rememberPassword, autoLogin;
    private EditText ETusername, ETpassword;
    private CheckBox CBrememberPassword, CBautoLogin;
    private Button BtnLogin, BtnGuestLogin;
    private TextView TVretrievePassword, TVregister;
    private HashMap <String, String> accountInfo;
    private FudanBBSApplication currentApplication;
    private AlertDialog loginingDialog;

    @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
		moveTaskToBack(false);  
		Intent intent = new Intent();
		intent.putExtra("result", false);
		LoginActivity.this.setResult(0, intent);
		finish();
	}
    
    OnClickListener listener = new OnClickListener(){
    
    	@Override
    	public void onClick(View v) {
    		// TODO Auto-generated method stub
    		switch(v.getId()){
    		case R.id.login:
    			username = ETusername.getText().toString().trim();
    			password = ETpassword.getText().toString().trim();

    			loginingDialog.setCanceledOnTouchOutside(true);    			
    			if(username.isEmpty() || password.isEmpty()){
    				loginingDialog.setMessage(getResources().getString(R.string.accountNotNull));	
    				loginingDialog.show();
    			}else if(!currentApplication.checkNetwork()){
    				loginingDialog.setMessage(getResources().getString(R.string.networkNotAvailable));	
    				loginingDialog.show();
    			}else{			
    				loginTask logintask = new loginTask();
    				logintask.execute();
    			}    	
    			break;

    		case R.id.register:
    			callBrowser2page("http://bbs.fudan.edu.cn/reg.htm");
    			break;
    		}
    	}
	
    };
    


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.login);
//		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logintitlebar);
		loginingDialog = new AlertDialog.Builder(LoginActivity.this).create();
		ETusername = (EditText)findViewById(R.id.username);
		ETpassword = (EditText)findViewById(R.id.password);
		currentApplication = (FudanBBSApplication)getApplication();
		accountInfo = currentApplication.getAccountInfo();
		if(null != accountInfo){
			username = accountInfo.get("username");
			if("true".equals(accountInfo.get("rememberpassword"))){
				rememberPassword = true;
				password = accountInfo.get("password");
			}else{
				rememberPassword = false;
			}
			if("true".equals(accountInfo.get("autologin"))){
				autoLogin = true;
			}else{
				autoLogin = false;
			}
		}

		
		if(null != username){
			ETusername.setText(username);
		}
		if(null != password){
			ETpassword.setText(password);
		}
		
		
		CBrememberPassword = (CheckBox)findViewById(R.id.checkRememberPassword);
		CBautoLogin = (CheckBox)findViewById(R.id.checkAutoLogin);
		BtnLogin = (Button)findViewById(R.id.login);
		BtnLogin.setOnClickListener(listener);
		BtnGuestLogin = (Button)findViewById(R.id.guestLogin);
		BtnGuestLogin.setOnClickListener(listener);
		
		TVregister = (TextView)findViewById(R.id.register);
		TVregister.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		TVregister.setOnClickListener(listener);
//		Toast.makeText(getApplicationContext(), ((FudanBBSApplication)getApplication()).LANDSCAPE, Toast.LENGTH_LONG).show();
	}
	
    
	// call system internet browser to access web page
	public void callBrowser2page(String aWebpage){
		Intent callBrowser = new Intent(Intent.ACTION_VIEW);
		callBrowser.setData(Uri.parse(aWebpage));
		startActivity(callBrowser);
		
	}
	

	// login page Async task class
	public class loginTask extends AsyncTask<Object, Object, Object>{
	//	private TaskItem aTaskItem;
		private int httpResponseCode;
	    private ProgressDialog progressdialog;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressdialog = new ProgressDialog(LoginActivity.this);
			progressdialog.setMessage(getString(R.string.logining));
			progressdialog.setCancelable(false);
			progressdialog.setCanceledOnTouchOutside(false);
			progressdialog.setProgressStyle(progressdialog.STYLE_SPINNER);		
			progressdialog.show();	


		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			accountInfo.put("username", username);
			accountInfo.put("password", password);
			if(progressdialog.isShowing()){
				progressdialog.dismiss();
			}
			switch(httpResponseCode){
    			case 200:
					loginingDialog.setMessage(getResources().getString(R.string.loginSucceed));

					if (CBrememberPassword.isChecked()){
						accountInfo.put("rememberpassword", "true");    						
					}
					if(CBautoLogin.isChecked()){
						accountInfo.put("autologin", "true");
					}
					currentApplication.setCurrentUserGuest(false);
					currentApplication.setCurrentUsername(username);
					currentApplication.saveAccountInfo(accountInfo);
					Toast.makeText(getApplicationContext(), getString(R.string.loginSucceed), Toast.LENGTH_LONG).show();
					Intent intent = new Intent();
					intent.putExtra("result", true);
					LoginActivity.this.setResult(0, intent);
					finish();					
    				break;
    			case 9999:		
    				loginingDialog.setMessage(getResources().getString(R.string.connectfailed));
    				loginingDialog.show();
    				break;
    			case 0:
    				loginingDialog.setMessage(getResources().getString(R.string.loginFailedServerError));
    				loginingDialog.show();
    				break;
    			default:
    				loginingDialog.setMessage(getResources().getString(R.string.loginFailed)+" error code="+httpResponseCode);
    				loginingDialog.show();
    				break;
    			}
		}


		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			URL url;
			HttpURLConnection connection = null;

			try {
				url = new URL("http://bbs.fudan.edu.cn/bbs/login");
				connection = (HttpURLConnection)url.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(20000);
				connection.setInstanceFollowRedirects(false);
				connection.connect();
				
				httpResponseCode = connection.getResponseCode();
				if(null != connection){
					connection.disconnect();
				}
				if( 200 == httpResponseCode){
					String loginurl = "http://bbs.fudan.edu.cn/bbs/login";
					Response res = Jsoup.connect(loginurl).data("id",username,"pw",password)
							.timeout(10000).method(Method.POST).execute();	
					HashMap<String, String> cookie = new HashMap<String, String>();
					cookie.put("utmpuserid", res.cookie("utmpuserid"));
					cookie.put("utmpkey", res.cookie("utmpkey"));
					cookie.put("utmpnum", res.cookie("utmpnum"));
					currentApplication.setCookie(cookie);    
					Log.v("loginActivitycookie", cookie.get("utmpuserid"));
				}

	
			} 
			catch(SocketTimeoutException e){
				httpResponseCode = 9999;
			}
			
			catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			finally{
				if(null != connection){
					connection.disconnect();
				}
			}
			return null;
		}
		
	}

	
}
